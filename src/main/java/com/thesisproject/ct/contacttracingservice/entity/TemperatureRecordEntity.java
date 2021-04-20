package com.thesisproject.ct.contacttracingservice.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import com.thesisproject.ct.contacttracingservice.model.TemperatureRecord;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "public", name="tbl_temperature_record")
@Data
@NoArgsConstructor
public class TemperatureRecordEntity {
	
	@Id
	@GeneratedValue(generator = "uuidGenerator")
	@GenericGenerator(name = "uuidGenerator", strategy = "uuid2")
	@Column(name = "rec_id")
	private UUID recordId;
	
	@Column(name = "user_profile_id", nullable = false)
	private UUID userProfileId;
	
	@Column(name = "temperature", nullable = false)
	private Double temperature;
	
	@Column(name = "record_date", nullable = false)
	private LocalDateTime recordDate;
	
	@Column(name = "area_code", nullable = false)
	private String areaCode;
	
	@Column(name = "detection", nullable = false)
	private boolean detection;
	
	@CreatedDate
	@CreationTimestamp
	private LocalDateTime createdDate;
	
	@CreatedBy
	private String createdBy;
	
	@LastModifiedDate
	@UpdateTimestamp
	private LocalDateTime lastModifiedDate;
	
	@LastModifiedBy
	private String lastModifiedBy;
	
	public TemperatureRecordEntity(TemperatureRecord temperatureRecord) {
		this.recordId = temperatureRecord.getRecordId();
		this.userProfileId = temperatureRecord.getUserProfileId();
		this.temperature = temperatureRecord.getTemperature();
		this.recordDate = temperatureRecord.getRecordDate();
		this.areaCode = temperatureRecord.getAreaCode();
		this.detection = temperatureRecord.isDetection();
	}
}
