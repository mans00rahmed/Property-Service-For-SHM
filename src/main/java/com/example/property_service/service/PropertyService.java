package com.example.property_service.service;

import com.example.property_service.dto.CreatePropertyRequest;
import com.example.property_service.dto.PropertyResponse;
import com.example.property_service.entity.Property;
import com.example.property_service.exception.PropertyNotFoundException;
import com.example.property_service.repository.PropertyRepository;
import com.sigma.smarthome.propertyservice.dto.PropertyUpdateRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PropertyService {

    private final PropertyRepository propertyRepository;

    public PropertyService(PropertyRepository propertyRepository) {
        this.propertyRepository = propertyRepository;
    }
    public void validatePropertyExists(UUID id) {
        boolean exists = propertyRepository.existsById(id);

        if (!exists) {
            throw new PropertyNotFoundException(id);
        }
    }
    

    public PropertyResponse createProperty(CreatePropertyRequest request, UUID managerId) {
        Property property = new Property();
        property.setAddress(request.getAddress());
        property.setPropertyType(request.getPropertyType());
        property.setManagerId(managerId);
        property.setCreatedAt(LocalDateTime.now());

        Property saved = propertyRepository.save(property);

        return new PropertyResponse(
                saved.getId(),
                saved.getAddress(),
                saved.getPropertyType(),
                saved.getManagerId()
        );
    }

    public PropertyResponse updateProperty(UUID id, PropertyUpdateRequest request) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new PropertyNotFoundException(id));

        property.setAddress(request.getAddress());
        property.setPropertyType(request.getPropertyType());

        Property saved = propertyRepository.save(property);

        return new PropertyResponse(
                saved.getId(),
                saved.getAddress(),
                saved.getPropertyType(),
                saved.getManagerId()
        );
    }

    @Transactional
    public void deleteProperty(String id) {
        UUID propertyId = UUID.fromString(id);

        if (!propertyRepository.existsById(propertyId)) {
            throw new PropertyNotFoundException(propertyId);
        }

        propertyRepository.deleteById(propertyId);
    }

    public List<PropertyResponse> getPropertiesForManager(UUID managerId) {
        return propertyRepository.findByManagerId(managerId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<PropertyResponse> getAllProperties() {
        return propertyRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private PropertyResponse mapToResponse(Property property) {
        return new PropertyResponse(
                property.getId(),
                property.getAddress(),
                property.getPropertyType(),
                property.getManagerId()
        );
    }

    public List<UUID> getPropertyIdsByManager(UUID managerId) {
        return propertyRepository.findPropertyIdsByManagerId(managerId);
    }

 
}