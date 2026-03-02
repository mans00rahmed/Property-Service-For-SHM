package com.example.property_service.dto;

import jakarta.validation.constraints.NotBlank;

public class CreatePropertyRequest {

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "Property type is required")
    private String propertyType;

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