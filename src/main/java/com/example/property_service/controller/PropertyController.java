package com.example.property_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PropertyController {

    @GetMapping("/properties/test")
    public String testPropertyService() {
        return "Property Service is working";
    }
}
