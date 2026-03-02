package com.example.property_service.controller;

import com.example.property_service.dto.CreatePropertyRequest;
import com.example.property_service.dto.PropertyResponse;
import com.example.property_service.service.PropertyService;
import org.springframework.security.core.Authentication;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;


@RestController
@RequestMapping("/properties")
public class PropertyController {

    private final PropertyService propertyService;

    public PropertyController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }
    
    
//    @PreAuthorize("hasAnyRole('PROPERTY_MANAGER','MAINTENANCE_STAFF')")
//    @GetMapping
//    public ResponseEntity<List<PropertyResponse>> getPropertiesForCurrentUser() {
//        UUID managerId = UUID.fromString("00000000-0000-0000-0000-000000000001");
//        return ResponseEntity.ok(propertyService.getPropertiesForManager(managerId));
//    }
    
    @PreAuthorize("hasRole('PROPERTY_MANAGER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProperty(@PathVariable String id) {

        propertyService.deleteProperty(id);

        return ResponseEntity.noContent().build();
    }

    // keep your old test endpoint
    @GetMapping("/test")
    public String testPropertyService() {
        return "Property Service is working";
    }
    @PreAuthorize("hasAnyRole('PROPERTY_MANAGER','MAINTENANCE_STAFF')")
    @GetMapping
    public ResponseEntity<List<PropertyResponse>> getAssignedProperties(Authentication auth) {

        UUID userId = auth.getName().equals("manager")
                ? UUID.fromString("00000000-0000-0000-0000-000000000001")
                : UUID.fromString("00000000-0000-0000-0000-000000000002");

        return ResponseEntity.ok(propertyService.getPropertiesForManager(userId));
    }
}