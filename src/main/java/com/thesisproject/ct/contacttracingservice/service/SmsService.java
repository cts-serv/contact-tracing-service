package com.thesisproject.ct.contacttracingservice.service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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
		sms.setNumber(userProfile.getContactNumber() + "," + applicationService.getApplicationVariable("adminContactNumber").getDescription() + "," + applicationService.getApplicationVariable("clinicContactNumber").getDescription());
		sms.setMessage("Fever detected, please coordinate with the clinic for further instructions.");
		RestTemplate template = new RestTemplate();
		HttpEntity<SmsObject> httpEntity = new HttpEntity<>(sms);
		template.postForObject(semaphoreMessagesUrl, httpEntity, String.class);
	}
	
	public void sendUserActivityReportSms(List<UserProfile> userProfileList) {
		for (UserProfile userProfile : userProfileList) {
			SmsObject sms = new SmsObject();
			sms.setApikey(this.semaphoreApiKey);
			StringBuilder builder = new StringBuilder();
			if(!userProfile.getTemperatureRecords().isEmpty()) {
				for(TemperatureRecord temperatureRecord : userProfile.getTemperatureRecords()) {
					if(temperatureRecord.getRecordDate().isAfter(LocalDate.now().minusDays(1).atStartOfDay())) {
						builder.append(temperatureRecord.getRecordDate().getHour())
							   .append(temperatureRecord.getRecordDate().getMinute())
							   .append("H ")
							   .append(temperatureRecord.getTemperature())
							   .append("\n");
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
