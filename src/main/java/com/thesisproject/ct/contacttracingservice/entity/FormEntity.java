package com.thesisproject.ct.contacttracingservice.entity;

import java.time.LocalDateTime;
import java.util.Optional;
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

import com.thesisproject.ct.contacttracingservice.model.Form;
import com.thesisproject.ct.contacttracingservice.model.UserRegistration;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(schema="public", name="tbl_form")
@Data
@NoArgsConstructor
public class FormEntity {
	
	@Id
	@GeneratedValue(generator = "uuidGenerator")
	@GenericGenerator(name = "uuidGenerator", strategy = "uuid2")
	@Column(name = "form_id")
	private UUID formId;
	
	@Column(name = "user_profile_id", nullable = true)
	private UUID userProfileId;
	
	@Column(name = "status", nullable = false)
	private String status;
	
	@Column(name = "submitted_dt", nullable = true)
	private LocalDateTime submittedDate;
	
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
	
	public FormEntity(Form form) {
		this.formId = form.getFormId();
		this.userProfileId = Optional.of(form.getUserRegistration()).map(UserRegistration::getUserProfileId).orElse(null);
		this.status = form.getStatus();
		this.createdDate = form.getCreatedDate();
		this.submittedDate = form.getSubmittedDate();
	}
}
