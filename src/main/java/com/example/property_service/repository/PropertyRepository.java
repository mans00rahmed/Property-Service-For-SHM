package com.example.property_service.repository;

import com.example.property_service.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface PropertyRepository extends JpaRepository<Property, UUID> {
	List<Property> findByManagerId(UUID managerId);
    @Query("select p.id from Property p where p.managerId = :managerId")
    List<UUID> findPropertyIdsByManagerId(UUID managerId);
	
}