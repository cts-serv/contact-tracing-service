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
	
	@Value("${cts.detection.contactthreshold}")
	private String detectionContactThreshold;
	
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
	
	public ApplicationVariable getApplicationVariable(String code) {
		return Optional.ofNullable(applicationVariableRepository.findOneByCode(code))
					   .map(ApplicationVariable::new)
					   .orElse(null);			
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
		
		entity = new ApplicationVariableEntity();
		entity.setCode("detectionContactThreshold");
		entity.setDescription(String.valueOf(detectionVariable.getDetectionContactThreshold()));
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
		validDepartmentMap.put("PC1", "PC 1");
		validDepartmentMap.put("PC2", "PC 2");
		validDepartmentMap.put("PC3", "PC 3");
		validDepartmentMap.put("PC4", "PC 4");
		
		
		validDepartmentMap.put("FDASSY", "FD THD/SOP ASSY");
		validDepartmentMap.put("FDEND", "FD THD/SOP END");
		
		
		validDepartmentMap.put("DEPT1", "PC1");
		validDepartmentMap.put("DEPT2", "PC2");
		validDepartmentMap.put("DEPT3", "PC3");
		validDepartmentMap.put("DEPT4", "PC4");
		validDepartmentMap.put("DEPT5", "PD THD/SOP ASSY");
		validDepartmentMap.put("DEPT6", "PD THD/SOP END");
		validDepartmentMap.put("DEPT7", "PD SOP WIDE ALL");
		validDepartmentMap.put("DEPT8", "PD QFP/LAPIS ALL FRONT");
		validDepartmentMap.put("DEPT9", "PD QFP/LAPIS ALL END");
		validDepartmentMap.put("DEPT10", "PD MAP ALL");
		validDepartmentMap.put("DEPT11", "PQD ALL");
		validDepartmentMap.put("DEPT12", "EDS");
		validDepartmentMap.put("DEPT13", "WL CSP/MEMS");
		validDepartmentMap.put("DEPT14", "PM DC/DB");
		validDepartmentMap.put("DEPT15", "PM WB");
		validDepartmentMap.put("DEPT16", "PM MP");
		validDepartmentMap.put("DEPT17", "PM FL");
		validDepartmentMap.put("DEPT18", "PM FL /ATC");
		validDepartmentMap.put("DEPT19", "PM SINGULATION");
		validDepartmentMap.put("DEPT20", "TOOLINGS");
		validDepartmentMap.put("DEPT21", "PM FT");
		validDepartmentMap.put("DEPT22", "PM TP");
		validDepartmentMap.put("DEPT23", "ELEC EQPT ENGG");
		validDepartmentMap.put("DEPT24", "PM EDS/MEMS");
		validDepartmentMap.put("DEPT25", "TE MAINTENANCE");
		validDepartmentMap.put("DEPT26", "TE SUPPORT");
		validDepartmentMap.put("DEPT27", "TE FYI");
		validDepartmentMap.put("DEPT28", "PROCESS QC1");
		validDepartmentMap.put("DEPT29", "PROCESS QC2");
		validDepartmentMap.put("DEPT30", "MECHA EQPTT ENGG");
		validDepartmentMap.put("DEPT31", "PROD ENGG SYS");
		validDepartmentMap.put("DEPT32", "OPERATION CTRL");
		validDepartmentMap.put("DEPT33", "MANUFACTURING CTRL");
		validDepartmentMap.put("DEPT34", "PROCESS ENGG");
		validDepartmentMap.put("DEPT35", "NEW PACKAGE DEV");
		validDepartmentMap.put("DEPT36", "AUTOMOTIVE ENGG");
		validDepartmentMap.put("DEPT37", "TE IMPROVEMENT");
		validDepartmentMap.put("DEPT38", "TEST BOX");
		validDepartmentMap.put("DEPT39", "PCB DESIGN");
		validDepartmentMap.put("DEPT40", "TEST PROBE");
		validDepartmentMap.put("DEPT41", "TEST SOL DEV 1");
		validDepartmentMap.put("DEPT42", "TEST SOL DEV 2");
		validDepartmentMap.put("DEPT43", "TEST SOL DEV SUPP");
		validDepartmentMap.put("DEPT44", "PROJECT CTRL");

		
		
		
		Map<String, String> validProcessMap = new HashMap<>();
		validProcessMap.put("DC", "DC");
		validProcessMap.put("DB", "DB");
		validProcessMap.put("WB", "WB");
		validProcessMap.put("MP", "MP");
		validProcessMap.put("PL", "PL");
		validProcessMap.put("ATC", "ATC");
		validProcessMap.put("FL", "FL");
		validProcessMap.put("FT", "FT");
		validProcessMap.put("TP", "TP");
		validProcessMap.put("SH", "SH");
		validProcessMap.put("PQD", "PQD");
		validProcessMap.put("ENGG", "ENGG");

		Map<String, String> validAreaCodeMap = new HashMap<>();
		validAreaCodeMap.put("0001", "Area 0001");
		validAreaCodeMap.put("0002", "Area 0002");
		validAreaCodeMap.put("0003", "Area 0003");
		validAreaCodeMap.put("0004", "Area 0004");
		
		Map<String, String> detectionVariablesMap = new HashMap<>();
		detectionVariablesMap.put("detectionTemperature", this.detectionTemperature);
		detectionVariablesMap.put("detectionContactThreshold", this.detectionContactThreshold);
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
		validProcessMap.forEach((key, value) -> {
			ApplicationVariableEntity var = new ApplicationVariableEntity();
			var.setEnabled(true);
			var.setVariableGroup("PROCESS");
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
