package com.example.property_service.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.sigma.smarthome.propertyservice.dto.PropertyUpdateRequest;

import jakarta.validation.Valid;

@RestController
public class PropertyController {

    @GetMapping("/properties/test")
    public String testPropertyService() {
        return "Property Service is working";
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProperty(
            @PathVariable UUID id,
            @Valid @RequestBody PropertyUpdateRequest request
    ) {
        return ResponseEntity.ok().build(); // temporary
    }
}
