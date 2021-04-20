package com.thesisproject.ct.contacttracingservice.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.thesisproject.ct.contacttracingservice.service.EmailService;

@Component
@EnableAsync
public class UserReportScheduler {
	
	@Autowired
	private EmailService emailService;
	
	@Scheduled(cron = "@daily")
	public void sendUserRecordReports() {
		emailService.sendUserProfilesReport();
	}
}
