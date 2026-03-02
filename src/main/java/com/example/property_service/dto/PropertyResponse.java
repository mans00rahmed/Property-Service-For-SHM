package com.example.property_service.dto;

import java.util.UUID;

public class PropertyResponse {

    private UUID propertyId;
    private String address;
    private String propertyType;
    private UUID ownerId;

    public PropertyResponse(UUID propertyId, String address, String propertyType, UUID ownerId) {
        this.propertyId = propertyId;
        this.address = address;
        this.propertyType = propertyType;
        this.ownerId = ownerId;
    }

    public UUID getPropertyId() {
        return propertyId;
    }

    public String getAddress() {
        return address;
    }

    public String getPropertyType() {
        return propertyType;
    }

    public UUID getOwnerId() {
        return ownerId;
    }
}