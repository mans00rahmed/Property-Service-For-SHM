package com.example.property_service.entity;


import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "properties",
        indexes = {
                @Index(name = "idx_manager_id", columnList = "managerId")
        })
public class Property {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "VARCHAR(36)")
    private UUID id;

    @Column(nullable = false, length = 500)
    private String address;

    @Column(nullable = false, length = 100)
    private String propertyType;

    @Column(nullable = false)
    private UUID managerId;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // getters and setters
}
