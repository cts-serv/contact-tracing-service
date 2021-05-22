package com.thesisproject.ct.contacttracingservice.model;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Pattern;
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
	
	@Pattern(regexp = "^([a-zA-Z\\s]{2,50}|)$", message = "Invalid middle name.")
	private String middleName;
	
	@Pattern(regexp = "^[0-9]{6,6}$", message = "Invalid id number.")
	private String idNumber;
	
	@Pattern(regexp = "^[0-9]{10,10}$", message = "Invalid contact number.")
	private String contactNumber;
	
	@Pattern(regexp = "^[0-9]{6,6}$", message = "Invalid OTP.")
	private String otp;
	
	@Size(min = 2, max = 50, message = "Invalid position.")
	private String position;
	
	@Size(min = 2, max = 50, message = "Invalid department.")
	private String department;
	
	@Size(min = 2, max = 50, message = "Invalid process.")
	private String process;
	
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
