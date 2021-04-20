package com.thesisproject.ct.contacttracingservice.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.thesisproject.ct.contacttracingservice.entity.TemperatureRecordEntity;

@Repository
public interface TemperatureRecordRepository extends JpaRepository<TemperatureRecordEntity, UUID>{
	public List<TemperatureRecordEntity> findAllByUserProfileId(UUID userProfileId);
	public List<TemperatureRecordEntity> findAllByDetection(boolean detection);
}
