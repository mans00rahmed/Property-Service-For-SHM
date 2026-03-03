package com.example.property_service.controller;

import com.example.property_service.entity.Property;
import com.example.property_service.repository.PropertyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PropertyControllerTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	PropertyRepository propertyRepository;

	private final UUID MANAGER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

	private UUID existingPropertyId;

	@BeforeEach
	void setup() {
		propertyRepository.deleteAll();

		Property p = new Property();
		p.setAddress("1 Main Street");
		p.setPropertyType("Apartment");
		p.setManagerId(MANAGER_ID);

		existingPropertyId = propertyRepository.save(p).getId();
	}

	// -----------------------
	// SMPM-18: GET /properties
	// -----------------------

	@Test
	void getProperties_noAuth_returns401() throws Exception {
		mockMvc.perform(get("/properties"))
		.andExpect(status().isUnauthorized());
	}

	@Test
	void getProperties_asManager_returns200() throws Exception {
		mockMvc.perform(get("/properties")
				.with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic("manager", "password")))
		.andExpect(status().isOk())
		.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
	}

	@Test
	void getProperties_asStaff_returns200() throws Exception {
		mockMvc.perform(get("/properties")
				.with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic("staff", "password")))
		.andExpect(status().isOk())
		.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
	}

	// -----------------------
	// SMPM-16: POST /properties
	// -----------------------

	@Test
	void createProperty_asManager_returns201() throws Exception {
		String body = """
				{"address":"2 Main Street","propertyType":"House"}
				""";

		mockMvc.perform(post("/properties")
				.with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic("manager", "password"))
				.contentType(MediaType.APPLICATION_JSON)
				.content(body))
		.andExpect(status().isCreated())
		.andExpect(jsonPath("$.address").value("2 Main Street"))
		.andExpect(jsonPath("$.propertyType").value("House"));
	}

	@Test
	void createProperty_missingAddress_returns400() throws Exception {
		String body = """
				{"propertyType":"Apartment"}
				""";

		mockMvc.perform(post("/properties")
				.with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic("manager", "password"))
				.contentType(MediaType.APPLICATION_JSON)
				.content(body))
		.andExpect(status().isBadRequest());
	}

	// -----------------------
	// SMPM-79: DELETE /properties/{id}
	// -----------------------

	@Test
	void deleteProperty_asManager_returns204() throws Exception {
		mockMvc.perform(delete("/properties/" + existingPropertyId)
				.with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic("manager", "password")))
		.andExpect(status().isNoContent());
	}

	@Test
	void deleteProperty_asStaff_returns403() throws Exception {
		mockMvc.perform(delete("/properties/" + existingPropertyId)
				.with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic("staff", "password")))
		.andExpect(status().isForbidden());
	}

	@Test
	void deleteProperty_nonExisting_returns404() throws Exception {
		UUID randomId = UUID.randomUUID();

		mockMvc.perform(delete("/properties/" + randomId)
				.with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic("manager", "password")))
		.andExpect(status().isNotFound());
	}

	// -----------------------
	// FR-4: PUT /properties/{id}
	// -----------------------

	@Test
	void updateProperty_asManager_returns200_andUpdatesFields() throws Exception {
		String body = """
				{"address":"99 New Address","propertyType":"House"}
				""";

		mockMvc.perform(put("/properties/" + existingPropertyId)
				.with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic("manager", "password"))
				.contentType(MediaType.APPLICATION_JSON)
				.content(body))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.propertyId").value(existingPropertyId.toString()))
		.andExpect(jsonPath("$.address").value("99 New Address"))
		.andExpect(jsonPath("$.propertyType").value("House"));

		// testing database persistence (so it's not just returning values)
		Property updated = propertyRepository.findById(existingPropertyId).orElseThrow();
		org.junit.jupiter.api.Assertions.assertEquals("99 New Address", updated.getAddress());
		org.junit.jupiter.api.Assertions.assertEquals("House", updated.getPropertyType());
	}

	@Test
	void updateProperty_nonExisting_returns404() throws Exception {
		UUID randomId = UUID.randomUUID();

		String body = """
				{"address":"Doesn't Matter","propertyType":"House"}
				""";

		mockMvc.perform(put("/properties/" + randomId)
				.with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic("manager", "password"))
				.contentType(MediaType.APPLICATION_JSON)
				.content(body))
		.andExpect(status().isNotFound());
	}

	@Test
	void updateProperty_missingAddress_returns400() throws Exception {
		String body = """
				{"propertyType":"House"}
				""";

		mockMvc.perform(put("/properties/" + existingPropertyId)
				.with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic("manager", "password"))
				.contentType(MediaType.APPLICATION_JSON)
				.content(body))
		.andExpect(status().isBadRequest());
	}

}
