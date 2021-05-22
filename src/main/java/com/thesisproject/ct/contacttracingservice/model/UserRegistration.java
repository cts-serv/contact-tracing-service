package com.thesisproject.ct.contacttracingservice.model;

import java.util.UUID;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.thesisproject.ct.contacttracingservice.entity.UserProfileEntity;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserRegistration {
	
	private UUID userProfileId;
	
	@Pattern(regexp = "^[a-zA-Z]{2,50}$", message = "Invalid first name.")
	private String firstName;
	
	@Pattern(regexp = "^[a-zA-Z]{2,50}$", message = "Invalid last name.")
	private String lastName;
	
	@Size(min = 1, message = "Invalid email.")
	@Email(message = "Invalid email.")
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
