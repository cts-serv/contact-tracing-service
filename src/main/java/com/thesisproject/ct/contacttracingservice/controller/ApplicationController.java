package com.thesisproject.ct.contacttracingservice.controller;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.thesisproject.ct.contacttracingservice.enums.DetectionTableHeaders;
import com.thesisproject.ct.contacttracingservice.model.ApplicationVariable;
import com.thesisproject.ct.contacttracingservice.model.Detection;
import com.thesisproject.ct.contacttracingservice.service.ApplicationService;
import com.thesisproject.ct.contacttracingservice.service.UserService;

@RestController
@RequestMapping("/api/application")
public class ApplicationController {
	
	@Autowired
	private ApplicationService applicationService;
	
	@Autowired
	private UserService userService;
	
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
	
	@GetMapping(path = "/detectionhistory/download")
	public ResponseEntity<byte[]> downloadDetectionHistory() throws FileNotFoundException, IOException {
		byte[] responseByteArray = null;
		File file = new File("detection report.csv");
		List<Detection> detections = userService.getDetections();
		try(FileOutputStream fos = new FileOutputStream(file); ByteArrayOutputStream out = new ByteArrayOutputStream(); CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out), CSVFormat.DEFAULT.withHeader(DetectionTableHeaders.class))) {
			for (Detection detection : detections) {
							List<String> fields = Arrays.asList("#" + detection.getUserProfile().getIdNumber(), 
									detection.getUserProfile().getFirstName(),
									detection.getUserProfile().getMiddleName(), 
									detection.getUserProfile().getLastName(),
									detection.getUserProfile().getContactNumber(), 
									detection.getUserProfile().getDepartment(), 
									detection.getUserProfile().getPosition(),
									detection.getUserProfile().getProcess(),
									String.valueOf(detection.getTemperatureRecord().getTemperature()),
									String.valueOf(detection.getTemperatureRecord().getRecordDate()));
							csvPrinter.printRecord(fields);
			}
			
			csvPrinter.flush();
			responseByteArray = out.toByteArray();
			fos.close();
			FileUtils.forceDelete(file);
		}
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"").body(responseByteArray);
	}
}
