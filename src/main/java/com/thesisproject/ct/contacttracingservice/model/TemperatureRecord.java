package com.thesisproject.ct.contacttracingservice.model;

import java.time.LocalDateTime;
import java.util.UUID;

import com.thesisproject.ct.contacttracingservice.entity.TemperatureRecordEntity;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TemperatureRecord {
	private UUID recordId;
	private UUID userProfileId;
	private Double temperature;
	private LocalDateTime recordDate;
	private String areaCode;
	private boolean detection;
	
	public TemperatureRecord(TemperatureRecordEntity entity) {
		this.recordId = entity.getRecordId();
		this.userProfileId = entity.getUserProfileId();
		this.temperature = entity.getTemperature();
		this.recordDate = entity.getRecordDate();
		this.areaCode = entity.getAreaCode();
		this.detection = entity.isDetection();
	}
}
