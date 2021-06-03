package com.thesisproject.ct.contacttracingservice.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.thesisproject.ct.contacttracingservice.entity.TemperatureRecordEntity;

@Repository
public interface TemperatureRecordRepository extends JpaRepository<TemperatureRecordEntity, UUID>{
	public List<TemperatureRecordEntity> findAllByUserProfileId(UUID userProfileId);
	public List<TemperatureRecordEntity> findAllByDetection(boolean detection);
	public List<TemperatureRecordEntity> findAllByRecordDateBetweenAndUserProfileIdNot(LocalDateTime dateTimeBefore, LocalDateTime dateTimeAfter, UUID userProfileIdNot);
	public List<TemperatureRecordEntity> findAllByRecordDateAndUserProfileIdAndTemperature(LocalDateTime recordDate, UUID userProfileId, Double temperature);
	public void deleteByUserProfileId(UUID userProfileId);
}
