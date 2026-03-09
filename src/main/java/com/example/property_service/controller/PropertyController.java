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

    @GetMapping("/test")
    public String testPropertyService() {
        return "Property Service is working";
    }

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

    @PreAuthorize("hasAnyRole('PROPERTY_MANAGER','MAINTENANCE_STAFF')")
    @GetMapping
    public ResponseEntity<List<PropertyResponse>> getAssignedProperties(Authentication auth) {
        UUID userId = resolveUserId(auth);

        if (hasRole(auth, "ROLE_PROPERTY_MANAGER")) {
            return ResponseEntity.ok(propertyService.getPropertiesForManager(userId));
        }

        if (hasRole(auth, "ROLE_MAINTENANCE_STAFF")) {
            return ResponseEntity.ok(propertyService.getAllProperties());
        }

        return ResponseEntity.status(403).build();
    }

    @PreAuthorize("hasRole('PROPERTY_MANAGER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProperty(@PathVariable String id) {
        propertyService.deleteProperty(id);
        return ResponseEntity.noContent().build();
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

    private UUID resolveUserId(Authentication auth) {
        try {
            return UUID.fromString(auth.getName());
        } catch (Exception ex) {
            throw new IllegalArgumentException("Invalid authenticated user id in JWT subject.");
        }
    }

    private boolean hasRole(Authentication auth, String role) {
        return auth.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals(role));
    }
}