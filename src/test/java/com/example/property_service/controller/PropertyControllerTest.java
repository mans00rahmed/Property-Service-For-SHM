package com.example.property_service.controller;

import com.example.property_service.entity.Property;
import com.example.property_service.repository.PropertyRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PropertyControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    PropertyRepository propertyRepository;

    private static final UUID MANAGER_ID =
            UUID.fromString("11111111-1111-1111-1111-111111111111");

    private static final UUID STAFF_ID =
            UUID.fromString("22222222-2222-2222-2222-222222222222");

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
        mockMvc.perform(get("/api/v1/properties"))
        	.andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(
            username = "11111111-1111-1111-1111-111111111111",
            roles = "PROPERTY_MANAGER"
    )
    void getProperties_asManager_returns200() throws Exception {
        mockMvc.perform(get("/api/v1/properties"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(
            username = "22222222-2222-2222-2222-222222222222",
            roles = "MAINTENANCE_STAFF"
    )
    void getProperties_asStaff_returns200() throws Exception {
        mockMvc.perform(get("/api/v1/properties"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    // -----------------------
    // SMPM-16: POST /properties
    // -----------------------

    @Test
    @WithMockUser(
            username = "11111111-1111-1111-1111-111111111111",
            roles = "PROPERTY_MANAGER"
    )
    void createProperty_asManager_returns201() throws Exception {
        String body = """
                {
                  "address":"2 Main Street",
                  "propertyType":"House"
                }
                """;

        mockMvc.perform(post("/api/v1/properties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.address").value("2 Main Street"))
                .andExpect(jsonPath("$.propertyType").value("House"))
                .andExpect(jsonPath("$.managerId").value(MANAGER_ID.toString()));
    }

    @Test
    @WithMockUser(
            username = "11111111-1111-1111-1111-111111111111",
            roles = "PROPERTY_MANAGER"
    )
    void createProperty_missingAddress_returns400() throws Exception {
        String body = """
                {
                  "propertyType":"Apartment"
                }
                """;

        mockMvc.perform(post("/api/v1/properties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    // -----------------------
    // SMPM-79: DELETE /properties/{id}
    // -----------------------

    @Test
    @WithMockUser(
            username = "11111111-1111-1111-1111-111111111111",
            roles = "PROPERTY_MANAGER"
    )
    void deleteProperty_asManager_returns204() throws Exception {
        mockMvc.perform(delete("/api/v1/properties/" + existingPropertyId))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(
            username = "22222222-2222-2222-2222-222222222222",
            roles = "MAINTENANCE_STAFF"
    )
    void deleteProperty_asStaff_returns403() throws Exception {
        mockMvc.perform(delete("/api/v1/properties/" + existingPropertyId))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(
            username = "11111111-1111-1111-1111-111111111111",
            roles = "PROPERTY_MANAGER"
    )
    void deleteProperty_nonExisting_returns404() throws Exception {
        UUID randomId = UUID.randomUUID();

        mockMvc.perform(delete("/api/v1/properties/" + randomId))
                .andExpect(status().isNotFound());
    }

    // -----------------------
    // FR-4: PUT /properties/{id}
    // -----------------------

    @Test
    @WithMockUser(
            username = "11111111-1111-1111-1111-111111111111",
            roles = "PROPERTY_MANAGER"
    )
    void updateProperty_asManager_returns200_andUpdatesFields() throws Exception {
        String body = """
                {
                  "address":"99 New Address",
                  "propertyType":"House"
                }
                """;

        mockMvc.perform(put("/api/v1/properties/" + existingPropertyId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.propertyId").value(existingPropertyId.toString()))
                .andExpect(jsonPath("$.address").value("99 New Address"))
                .andExpect(jsonPath("$.propertyType").value("House"))
                .andExpect(jsonPath("$.managerId").value(MANAGER_ID.toString()));

        Property updated = propertyRepository.findById(existingPropertyId).orElseThrow();
        Assertions.assertEquals("99 New Address", updated.getAddress());
        Assertions.assertEquals("House", updated.getPropertyType());
    }

    @Test
    @WithMockUser(
            username = "11111111-1111-1111-1111-111111111111",
            roles = "PROPERTY_MANAGER"
    )
    void updateProperty_nonExisting_returns404() throws Exception {
        UUID randomId = UUID.randomUUID();

        String body = """
                {
                  "address":"Doesn't Matter",
                  "propertyType":"House"
                }
                """;

        mockMvc.perform(put("/api/v1/properties/" + randomId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(
            username = "11111111-1111-1111-1111-111111111111",
            roles = "PROPERTY_MANAGER"
    )
    void updateProperty_missingAddress_returns400() throws Exception {
        String body = """
                {
                  "propertyType":"House"
                }
                """;

        mockMvc.perform(put("/api/v1/properties/" + existingPropertyId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }
}