package com.example.property_service.controller;

import com.example.property_service.dto.CreatePropertyRequest;
import com.example.property_service.dto.PropertyResponse;
import com.example.property_service.service.PropertyService;
import com.sigma.smarthome.propertyservice.dto.PropertyUpdateRequest;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/properties")
public class PropertyController {

    private final PropertyService propertyService;

    public PropertyController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    // keep your old test endpoint
    @GetMapping("/test")
    public String testPropertyService() {
        return "Property Service is working";
    }

    @PreAuthorize("hasRole('PROPERTY_MANAGER')")
    @PostMapping
    public ResponseEntity<PropertyResponse> createProperty(@Valid @RequestBody CreatePropertyRequest request) {

        // TEMP (for now): hardcoded managerId
        // Next step later: extract this from JWT / SecurityContext
        UUID managerId = UUID.fromString("00000000-0000-0000-0000-000000000001");

        PropertyResponse created = propertyService.createProperty(request, managerId);

        return ResponseEntity
                .created(URI.create("/properties/" + created.getPropertyId()))
                .body(created);
    }

    @PreAuthorize("hasRole('PROPERTY_MANAGER')")
    @PutMapping("/{id}")
    public ResponseEntity<PropertyResponse> updateProperty(
            @PathVariable UUID id,
            @Valid @RequestBody PropertyUpdateRequest request
    ) {
        PropertyResponse updated = propertyService.updateProperty(id, request);
        return ResponseEntity.ok(updated);
    }
}