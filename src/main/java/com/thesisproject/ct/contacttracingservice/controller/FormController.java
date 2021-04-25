package com.thesisproject.ct.contacttracingservice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thesisproject.ct.contacttracingservice.model.Form;
import com.thesisproject.ct.contacttracingservice.service.FormService;

@RestController
@RequestMapping("/api/forms")
public class FormController {
	
	@Autowired
	private FormService formService;
	
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Form>> getForms() {
		return ResponseEntity.ok(formService.getForms());
	}
}
