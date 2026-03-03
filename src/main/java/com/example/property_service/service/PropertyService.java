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
                .map(p -> new PropertyResponse(
                        p.getId(),
                        p.getAddress(),
                        p.getPropertyType(),
                        p.getManagerId()
                ))
                .toList();
    }
}