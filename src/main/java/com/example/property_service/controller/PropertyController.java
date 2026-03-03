package com.example.property_service.controller;

import com.example.property_service.dto.CreatePropertyRequest;
import com.example.property_service.dto.PropertyResponse;
import com.example.property_service.service.PropertyService;
import com.sigma.smarthome.propertyservice.dto.PropertyUpdateRequest;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
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

    /**
     * Create property (POST /properties)
     * Only PROPERTY_MANAGER can create a property.
     */
    @PreAuthorize("hasRole('PROPERTY_MANAGER')")
    @PostMapping
    public ResponseEntity<PropertyResponse> createProperty(
            @Valid @RequestBody CreatePropertyRequest request,
            Authentication auth
    ) {
        UUID ownerId = resolveUserId(auth);

        PropertyResponse created = propertyService.createProperty(request, ownerId);

        URI location = URI.create("/properties/" + created.getPropertyId());
        return ResponseEntity.created(location).body(created);
    }

    /**
     * View assigned properties (GET /properties)
     * PROPERTY_MANAGER + MAINTENANCE_STAFF can view.
     */
    @PreAuthorize("hasAnyRole('PROPERTY_MANAGER','MAINTENANCE_STAFF')")
    @GetMapping
    public ResponseEntity<List<PropertyResponse>> getAssignedProperties(Authentication auth) {
        UUID userId = resolveUserId(auth);
        return ResponseEntity.ok(propertyService.getPropertiesForManager(userId));
    }

    /**
     * Delete property (DELETE /properties/{id})
     * Only PROPERTY_MANAGER can delete.
     */
    @PreAuthorize("hasRole('PROPERTY_MANAGER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProperty(@PathVariable String id) {
        propertyService.deleteProperty(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Update property (PUT /properties/{id})
     * Only PROPERTY_MANAGER can update.
     */
    @PreAuthorize("hasRole('PROPERTY_MANAGER')")
    @PutMapping("/{id}")
    public ResponseEntity<PropertyResponse> updateProperty(
            @PathVariable UUID id,
            @Valid @RequestBody PropertyUpdateRequest request
    ) {
        PropertyResponse updated = propertyService.updateProperty(id, request);
        return ResponseEntity.ok(updated);
    }

    /**
     * Temporary mapping used for local demo/testing.
     * Replace later with real JWT subject -> UUID mapping.
     */
    private UUID resolveUserId(Authentication auth) {
        String username = auth.getName();

        if ("manager".equals(username)) {
            return UUID.fromString("00000000-0000-0000-0000-000000000001");
        }

        if ("staff".equals(username)) {
            return UUID.fromString("00000000-0000-0000-0000-000000000002");
        }

        return UUID.fromString("00000000-0000-0000-0000-000000000002");
    }
}