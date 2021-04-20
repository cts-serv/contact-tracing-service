package com.thesisproject.ct.contacttracingservice.model;

import java.util.UUID;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Size;

import com.thesisproject.ct.contacttracingservice.entity.ApplicationVariableEntity;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ApplicationVariable {
	
	private UUID variableId;
	
	@Size(min = 2, max = 50, message = "Please enter value with 2 to 50 characters.")
	private String variableGroup;
	
	@Size(min = 4, max = 4, message = "Please enter a 4-character code.")
	private String code;
	
	@Size(min = 2, max = 50, message = "Please enter value with 2 to 50 characters.")
	private String description;
	
	@AssertTrue(message = "Please enter true.")
	private boolean enabled;
	
	public ApplicationVariable(ApplicationVariableEntity entity) {
		this.variableId = entity.getVariableId();
		this.variableGroup = entity.getVariableGroup();
		this.code = entity.getCode();
		this.description = entity.getDescription();
		this.enabled = entity.isEnabled();
	}
}
