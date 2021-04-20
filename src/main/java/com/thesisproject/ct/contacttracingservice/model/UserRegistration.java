package com.thesisproject.ct.contacttracingservice.model;

import java.util.UUID;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

import com.thesisproject.ct.contacttracingservice.entity.UserProfileEntity;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserRegistration {
	
	private UUID userProfileId;
	
	@Size(min = 2, max = 50, message = "Please enter value with 2 to 50 characters.")
	private String firstName;
	
	@Size(min = 2, max = 50, message = "Please enter value with 2 to 50 characters.")
	private String lastName;
	
	@Size(min = 1, message = "Please enter a valid email")
	@Email(message = "Please enter a valid email")
	private String email;
	
	public UserRegistration(UUID userProfileId) {
		this.userProfileId = userProfileId;
	}
	
	public UserRegistration(UserProfileEntity entity) {
		this.userProfileId  = entity.getUserProfileId();
		this.firstName = entity.getFirstName();
		this.lastName = entity.getLastName();
		this.email = entity.getEmail();
	}
}
