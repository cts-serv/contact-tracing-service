package com.thesisproject.ct.contacttracingservice.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.thesisproject.ct.contacttracingservice.model.ApplicationVariable;
import com.thesisproject.ct.contacttracingservice.model.SmsObject;
import com.thesisproject.ct.contacttracingservice.model.SmsResponse;
import com.thesisproject.ct.contacttracingservice.model.TemperatureRecord;
import com.thesisproject.ct.contacttracingservice.model.UserProfile;

@Service
public class SmsService {
	
	@Autowired
	private ApplicationService applicationService;
	
	@Autowired
	
	@Value("${semaphore.messages.url}")
	private String semaphoreMessagesUrl;
	
	@Value("${semaphore.otp.url}")
	private String semaphoreOtpUrl;
	
	@Value("${semaphore.apikey}")
	private String semaphoreApiKey;
	
	@Value("${semaphore.sendername}")
	private String senderName;
	
	public SmsResponse sendOTP(UserProfile userProfile) {
		SmsObject sms = new SmsObject();
		sms.setApikey(this.semaphoreApiKey);
		//sms.setSendername(this.senderName);
		sms.setNumber(userProfile.getContactNumber());
		sms.setMessage("The OTP for your Contact Tracing Service registration is: {otp}. Please do not share this with anyone.");


		RestTemplate template = new RestTemplate();
		HttpEntity<SmsObject> httpEntity = new HttpEntity<>(sms);
		List<SmsResponse> response = Arrays.asList(template.postForEntity(semaphoreOtpUrl, httpEntity, SmsResponse[].class).getBody());
		return response.get(0);
	}
	
	public void sendRegistrationCompletionSms(UserProfile userProfile) {
		SmsObject sms = new SmsObject();
		sms.setApikey(this.semaphoreApiKey);
		//sms.setSendername(this.senderName);
		sms.setNumber(userProfile.getContactNumber());
		sms.setMessage("Thank you for completing your registration with CTS. You may now use your personal QR Code to user the contact tracing facilities.\n\nCTS Team");


		RestTemplate template = new RestTemplate();
		HttpEntity<SmsObject> httpEntity = new HttpEntity<>(sms);
		template.postForObject(semaphoreMessagesUrl, httpEntity, String.class);
	}
	
	public void sendDetectionSms(UserProfile userProfile) {
		SmsObject sms = new SmsObject();
		sms.setApikey(this.semaphoreApiKey);
		//sms.setSendername(this.senderName);
		sms.setNumber(userProfile.getContactNumber());
		sms.setMessage("Fever detected, please coordinate with the clinic for further instructions.");
		RestTemplate template = new RestTemplate();
		HttpEntity<SmsObject> httpEntity = new HttpEntity<>(sms);
		template.postForObject(semaphoreMessagesUrl, httpEntity, String.class);
		
		
		Optional.ofNullable(applicationService.getApplicationVariable("adminContactNumber"))
				.map(ApplicationVariable::getDescription)
				.filter(t -> !t.isEmpty())
				.ifPresent(adminContactNumber -> {
					sms.setNumber(adminContactNumber);
					sms.setMessage("A fevered person was detected. Please check detection history section in CTS admin page.");
					HttpEntity<SmsObject> httpEntityAdmin = new HttpEntity<>(sms);
					template.postForObject(semaphoreMessagesUrl, httpEntityAdmin, String.class);
				});
		
		Optional.ofNullable(applicationService.getApplicationVariable("clinicContactNumber"))
				.map(ApplicationVariable::getDescription)
				.filter(t -> !t.isEmpty())
				.ifPresent(clinicContactNumber -> {
					sms.setNumber(clinicContactNumber);
					sms.setMessage("A fevered person was detected. Please check detection history section in CTS admin page.");
					HttpEntity<SmsObject> httpEntityClinic = new HttpEntity<>(sms);
					template.postForObject(semaphoreMessagesUrl, httpEntityClinic, String.class);
				});
		
		
	}
	
	public void sendUserActivityReportSms(List<UserProfile> userProfileList) {
		for (UserProfile userProfile : userProfileList) {
			SmsObject sms = new SmsObject();
			sms.setApikey(this.semaphoreApiKey);
			StringBuilder builder = new StringBuilder();
			if(!userProfile.getTemperatureRecords().isEmpty()) {
				for(TemperatureRecord temperatureRecord : userProfile.getTemperatureRecords()) {
					if(temperatureRecord.getRecordDate().isAfter(LocalDateTime.now().minusDays(1))) {
						builder.append(String.valueOf(temperatureRecord.getRecordDate().getHour()).length() < 2 ? "0" + String.valueOf(temperatureRecord.getRecordDate().getHour()) : String.valueOf(temperatureRecord.getRecordDate().getHour()))
							   .append(String.valueOf(temperatureRecord.getRecordDate().getMinute()).length() < 2 ? "0" + String.valueOf(temperatureRecord.getRecordDate().getMinute()) : String.valueOf(temperatureRecord.getRecordDate().getMinute()))
							   .append("H ")
							   .append(temperatureRecord.getTemperature())
							   .append("C\n");
					}
				}
			}
			if(builder.length() > 1) {
				sms.setNumber(userProfile.getContactNumber());
				sms.setMessage(builder.toString());
				RestTemplate template = new RestTemplate();
				HttpEntity<SmsObject> httpEntity = new HttpEntity<>(sms);
				template.postForObject(semaphoreMessagesUrl, httpEntity, String.class);
			}
		}
	}
}
