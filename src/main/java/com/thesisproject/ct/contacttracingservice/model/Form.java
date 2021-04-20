package com.thesisproject.ct.contacttracingservice.model;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import com.thesisproject.ct.contacttracingservice.entity.FormEntity;
import com.thesisproject.ct.contacttracingservice.enums.FormStatus;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Form {
	private UUID formId;
	private String status;
	private LocalDateTime createdDate;
	private LocalDateTime submittedDate;
	private UserRegistration userRegistration;
	
	public Form(FormEntity entity) {
		this.formId = entity.getFormId();
		this.status = entity.getStatus();
		this.createdDate = entity.getCreatedDate();
		this.submittedDate = entity.getSubmittedDate();
		this.userRegistration = Optional.ofNullable(entity.getUserProfileId()).map(UserRegistration::new).orElse(null);
	}
	
	public Form(UserRegistration userRegistration) {
		this.createdDate = LocalDateTime.now();
		this.status = FormStatus.CREATED.name();
		this.userRegistration = userRegistration;
	}
}
