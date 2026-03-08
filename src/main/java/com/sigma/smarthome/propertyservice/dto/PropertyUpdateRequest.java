package com.sigma.smarthome.propertyservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PropertyUpdateRequest {

//	Update requests
	@NotBlank(message = "address is required")
	@Size(max = 500, message = "address must be 500 characters or less")
	private String address;
	
	@NotBlank(message = "address is required")
	@Size(max = 100, message = "propertyType must be 100 characters or less")
	private String propertyType;
	
	public PropertyUpdateRequest() {}
	
	public PropertyUpdateRequest(String address, String propertyType) {
		this.address = address;
		this.propertyType = propertyType;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPropertyType() {
		return propertyType;
	}

	public void setPropertyType(String propertyType) {
		this.propertyType = propertyType;
	}
	
}
