package com.thesisproject.ct.contacttracingservice.model;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Size;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.thesisproject.ct.contacttracingservice.entity.UserProfileEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class UserProfile extends UserRegistration{
	
	@Size(min = 2, max = 50, message = "Please enter value with 2 to 50 characters.")
	private String middleName;
	
	@Size(min = 6, max = 6, message = "Please enter a valid ID number.")
	private String idNumber;
	
	@Size(min = 10, max = 10, message = "Please enter a valid contact number.")
	private String contactNumber;
	
	@Size(min = 6, max = 6, message = "Invalid OTP")
	private String otp;
	
	@Size(min = 2, max = 50, message = "Please enter a valid position.")
	private String position;
	
	@Size(min = 2, max = 50, message = "Please enter a valid department.")
	private String department;
	
	@AssertTrue(message = "Please accept user agreement and data privacy agreement.")
	private boolean userAgreementAccepted;
	
	@JsonIgnore
	private String lastTemperatureRecord;
	
	private List<TemperatureRecord> temperatureRecords;
	private MultipartFile imageFile;
	
	public UserProfile(UserProfileEntity entity) {
		super(entity);
		this.middleName = entity.getMiddleName();
		this.idNumber = entity.getIdNumber();
		this.contactNumber = entity.getContactNumber();
		this.otp = entity.getOtp();
		this.position = entity.getPosition();
		this.department = entity.getDepartment();
		this.userAgreementAccepted = entity.isUserAgreementAccepted();
		this.temperatureRecords = new ArrayList<>();
	}
}
