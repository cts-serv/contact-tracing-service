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
import org.thymeleaf.util.StringUtils;

import com.thesisproject.ct.contacttracingservice.model.ApplicationVariable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "public", name="tbl_app_variable")
@Data
@NoArgsConstructor
public class ApplicationVariableEntity {
	
	@Id
	@GeneratedValue(generator = "uuid-generator")
	@GenericGenerator(name = "uuid-generator", strategy = "uuid2")
	@Column(name = "var_id")
	private UUID variableId;
	
	@Column(name = "var_group")
	private String variableGroup;
	
	@Column(name = "var_code")
	private String code;
	
	@Column(name ="var_desc")
	private String description;
	
	@Column(name = "var_enabled")
	private boolean enabled;
	
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
	
	public ApplicationVariableEntity(ApplicationVariable applicationVariable) {
		this.variableId = applicationVariable.getVariableId();
		this.variableGroup = applicationVariable.getVariableGroup();
		this.code = applicationVariable.getCode().toUpperCase();
		this.description = StringUtils.capitalize(applicationVariable.getDescription());
		this.enabled = applicationVariable.isEnabled();
	}
}
