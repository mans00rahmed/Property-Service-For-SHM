package com.example.property_service.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "properties",
        indexes = {
                @Index(name = "idx_manager_id", columnList = "manager_id")
        }
)
public class Property {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "VARCHAR(36)")
    private UUID id;

    @Column(nullable = false, length = 500)
    private String address;

    @Column(nullable = false, length = 100)
    private String propertyType;

    @Column(nullable = false, name = "manager_id", columnDefinition = "VARCHAR(36)")
    private UUID managerId;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(String propertyType) {
        this.propertyType = propertyType;
    }

    public UUID getManagerId() {
        return managerId;
    }

    public void setManagerId(UUID managerId) {
        this.managerId = managerId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}