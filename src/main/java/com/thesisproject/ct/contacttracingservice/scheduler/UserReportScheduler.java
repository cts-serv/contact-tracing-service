package com.thesisproject.ct.contacttracingservice.scheduler;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.thesisproject.ct.contacttracingservice.model.UserProfile;
import com.thesisproject.ct.contacttracingservice.service.EmailService;
import com.thesisproject.ct.contacttracingservice.service.SmsService;
import com.thesisproject.ct.contacttracingservice.service.UserService;

@Component
@EnableAsync
public class UserReportScheduler {
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private SmsService smsService;
	
	@Scheduled(cron = "0 0 8 * * ?")
	public void sendUserRecordReports() {
		List<UserProfile> userProfileList = userService.getUserProfiles(null);
		emailService.sendUserProfilesReport(userProfileList);
		smsService.sendUserActivityReportSms(userProfileList);
		
	}
}
