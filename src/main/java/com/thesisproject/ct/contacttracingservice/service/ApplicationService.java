package com.thesisproject.ct.contacttracingservice.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.thesisproject.ct.contacttracingservice.entity.ApplicationVariableEntity;
import com.thesisproject.ct.contacttracingservice.error.BadRequestException;
import com.thesisproject.ct.contacttracingservice.model.ApplicationVariable;
import com.thesisproject.ct.contacttracingservice.model.DetectionVariable;
import com.thesisproject.ct.contacttracingservice.repository.ApplicationVariableRepository;

@Service
public class ApplicationService {
	@Autowired
	private ApplicationVariableRepository applicationVariableRepository;
	
	@Value("${cts.detection.temperature}")
	private String detectionTemperature;
	
	@Value("${cts.clinic.number}")
	private String clinicContactNumber;
	
	@Value("${cts.clinic.email}")
	private String clinicEmail;
	
	@Value("${cts.admin.number}")
	private String adminContactNumber;
	
	@Value("${cts.admin.email}")
	private String adminEmail;
	
	public List<ApplicationVariable> getApplicationVariables(String variableGroup) {
		return applicationVariableRepository.findAllByVariableGroupAndEnabled(variableGroup, true)
									   .stream()
									   .map(ApplicationVariable::new)
									   .collect(Collectors.toList());
	}
	
	public Map<String, String> getApplicationVariablesKeyValue(String variableGroup) {
		return applicationVariableRepository.findAllByVariableGroupAndEnabled(variableGroup, true)
									   .stream()
									   .collect(Collectors.toMap(ApplicationVariableEntity::getCode, ApplicationVariableEntity::getDescription));
	}
	
	public ApplicationVariable addApplicationVariable(ApplicationVariable applicationVariable) {
		return Optional.ofNullable(applicationVariable)
					   .map(ApplicationVariableEntity::new)
					   .map(entity -> {entity.setVariableId(null); return entity;})
					   .map(applicationVariableRepository::saveAndFlush)
					   .map(ApplicationVariable::new)
					   .orElseThrow(BadRequestException::new);
	}
	
	public void deleteApplicationVariable(ApplicationVariable applicationVariable) {
		Optional.ofNullable(applicationVariable)
					   .map(ApplicationVariable::getVariableId)
					   .ifPresent(applicationVariableRepository::deleteById);
	}
	
	@Transactional
	public DetectionVariable updateDetectionVariables(DetectionVariable detectionVariable) {
		
		List<ApplicationVariableEntity> entityList = new ArrayList<>();
		ApplicationVariableEntity entity = new ApplicationVariableEntity();
		entity.setCode("clinicEmail");
		entity.setDescription(detectionVariable.getClinicEmail());
		entity.setEnabled(true);
		entity.setVariableGroup("DETECTION VARIABLE");
		entityList.add(entity);
		
		entity = new ApplicationVariableEntity();
		entity.setCode("clinicContactNumber");
		entity.setDescription(detectionVariable.getClinicContactNumber());
		entity.setEnabled(true);
		entity.setVariableGroup("DETECTION VARIABLE");
		entityList.add(entity);
		
		entity = new ApplicationVariableEntity();
		entity.setCode("adminEmail");
		entity.setDescription(detectionVariable.getAdminEmail());
		entity.setEnabled(true);
		entity.setVariableGroup("DETECTION VARIABLE");
		entityList.add(entity);
		
		entity = new ApplicationVariableEntity();
		entity.setCode("adminContactNumber");
		entity.setDescription(detectionVariable.getAdminContactNumber());
		entity.setEnabled(true);
		entity.setVariableGroup("DETECTION VARIABLE");
		entityList.add(entity);
		
		entity = new ApplicationVariableEntity();
		entity.setCode("detectionTemperature");
		entity.setDescription(String.valueOf(detectionVariable.getDetectionTemperature()));
		entity.setEnabled(true);
		entity.setVariableGroup("DETECTION VARIABLE");
		entityList.add(entity);
		
		applicationVariableRepository.deleteAllByVariableGroup("DETECTION VARIABLE");
		applicationVariableRepository.saveAll(entityList);
		return detectionVariable;
	}
	
	public List<ApplicationVariable> initializeApplication() {
		Map<String, String> validPositionMap = new HashMap<>();
		validPositionMap.put("INSP", "Inspector");
		validPositionMap.put("MNTN", "Maintenance");
		validPositionMap.put("PMST", "PM Staff");
		validPositionMap.put("PDST", "PD Staff");
		validPositionMap.put("MNGR", "Manager");
		validPositionMap.put("OPTR", "Operator");
		validPositionMap.put("CNTR", "Contractor");
		validPositionMap.put("QCST", "QC Staff");
		validPositionMap.put("PCST", "PC Staff");
		validPositionMap.put("ENST", "Engineering Staff");
		validPositionMap.put("ENGR", "Service Engineer");
		
		Map<String, String> validDepartmentMap = new HashMap<>();
		validDepartmentMap.put("PRMN", "Production Maintenance");
		validDepartmentMap.put("PRDN", "Production");
		validDepartmentMap.put("QTCT", "Quality Control");
		validDepartmentMap.put("QTAS", "Quality Assurance");
		validDepartmentMap.put("PRCT", "Production Control");
		validDepartmentMap.put("VSTR", "Visitor");
		validDepartmentMap.put("OPCT", "Operation Control");
		validDepartmentMap.put("MNCT", "Manufacturing Control");

		Map<String, String> validAreaCodeMap = new HashMap<>();
		validAreaCodeMap.put("0001", "Area 0001");
		validAreaCodeMap.put("0002", "Area 0002");
		validAreaCodeMap.put("0003", "Area 0003");
		validAreaCodeMap.put("0004", "Area 0004");
		
		Map<String, String> detectionVariablesMap = new HashMap<>();
		detectionVariablesMap.put("detectionTemperature", this.detectionTemperature);
		detectionVariablesMap.put("clinicContactNumber", this.clinicContactNumber);
		detectionVariablesMap.put("clinicEmail", this.clinicEmail);
		detectionVariablesMap.put("adminContactNumber", this.adminContactNumber);
		detectionVariablesMap.put("adminEmail", this.adminEmail);
		
		applicationVariableRepository.deleteAll();
		validPositionMap.forEach((key, value) -> {
			ApplicationVariableEntity var = new ApplicationVariableEntity();
			var.setEnabled(true);
			var.setVariableGroup("POSITION");
			var.setCode(key);
			var.setDescription(value);
			applicationVariableRepository.save(var);
		});
		validDepartmentMap.forEach((key, value) -> {
			ApplicationVariableEntity var = new ApplicationVariableEntity();
			var.setEnabled(true);
			var.setVariableGroup("DEPARTMENT");
			var.setCode(key);
			var.setDescription(value);
			applicationVariableRepository.save(var);
		});
		detectionVariablesMap.forEach((key, value) -> {
			ApplicationVariableEntity var = new ApplicationVariableEntity();
			var.setEnabled(true);
			var.setVariableGroup("DETECTION VARIABLE");
			var.setCode(key);
			var.setDescription(value);
			applicationVariableRepository.save(var);
		});
		validAreaCodeMap.forEach((key, value) -> {
			ApplicationVariableEntity var = new ApplicationVariableEntity();
			var.setEnabled(true);
			var.setVariableGroup("AREA CODE");
			var.setCode(key);
			var.setDescription(value);
			applicationVariableRepository.save(var);
		});
		
		return applicationVariableRepository.findAll()
									   .stream()
									   .map(ApplicationVariable::new)
									   .collect(Collectors.toList());
	}
	
	public void resetApplication() {
	}
}
