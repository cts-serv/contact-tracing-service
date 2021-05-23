package com.thesisproject.ct.contacttracingservice.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.thesisproject.ct.contacttracingservice.model.ApplicationVariable;
import com.thesisproject.ct.contacttracingservice.service.ApplicationService;

@RestController
@RequestMapping("/api/application")
public class ApplicationController {
	
	@Autowired
	private ApplicationService applicationService;
	
	@GetMapping(path = "/variables")
	public ResponseEntity<List<ApplicationVariable>> getApplicationVariables(@RequestParam(name = "group") String variableGroup) {
		return ResponseEntity.ok(applicationService.getApplicationVariables(variableGroup));
	}
	
	@PostMapping(path = "/initialize")
	public ResponseEntity<List<ApplicationVariable>> initializeApplication() {
		return ResponseEntity.ok(applicationService.initializeApplication());
	}
	
	@GetMapping(path = "/datetime")
	public String getServerDateTime() {
		return LocalDateTime.now().toString();
	}
}
