package com.thesisproject.ct.contacttracingservice.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.thesisproject.ct.contacttracingservice.model.SmsObject;
import com.thesisproject.ct.contacttracingservice.model.SmsResponse;
import com.thesisproject.ct.contacttracingservice.model.UserProfile;

@Service
public class SmsService {
	
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
	
	public void sendDailyRecordSms() {
		
	}
	
	public void sendDetectionSms(UserProfile userProfile) {
//		SmsObject sms = new SmsObject();
//		sms.setApikey(this.semaphoreApiKey);
//		//sms.setSendername(this.senderName);
//		sms.setNumber("09208252263,09208252263,09208252263");
//		sms.setMessage("Fever detected, please coordinate with the clinic for further instructions.");
//		RestTemplate template = new RestTemplate();
//		HttpEntity<SmsObject> httpEntity = new HttpEntity<>(sms);
//		String response = template.postForObject(semaphoreMessagesUrl, httpEntity, String.class);
//		System.out.println(response);
	}
}
