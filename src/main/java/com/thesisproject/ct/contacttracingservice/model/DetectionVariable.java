package com.thesisproject.ct.contacttracingservice.model;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DetectionVariable {
	private String clinicEmail;
	private String clinicContactNumber;
	private String adminEmail;
	private String adminContactNumber;
	private Double detectionTemperature;
	
	public DetectionVariable(List<ApplicationVariable> applicationVariable) {
		this.clinicEmail = applicationVariable.stream().filter(var -> "clinicEmail".equals(var.getCode())).findFirst().map(ApplicationVariable::getDescription).orElse(null);
		this.clinicContactNumber = applicationVariable.stream().filter(var -> "clinicContactNumber".equals(var.getCode())).findFirst().map(ApplicationVariable::getDescription).orElse(null);
		this.adminEmail = applicationVariable.stream().filter(var -> "adminEmail".equals(var.getCode())).findFirst().map(ApplicationVariable::getDescription).orElse(null);
		this.adminContactNumber = applicationVariable.stream().filter(var -> "adminContactNumber".equals(var.getCode())).findFirst().map(ApplicationVariable::getDescription).orElse(null);
		this.detectionTemperature = applicationVariable.stream().filter(var -> "detectionTemperature".equals(var.getCode())).findFirst().map(ApplicationVariable::getDescription).map(Double::parseDouble).orElse(null);
	}
}
