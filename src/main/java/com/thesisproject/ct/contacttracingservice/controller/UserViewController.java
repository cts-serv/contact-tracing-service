package com.thesisproject.ct.contacttracingservice.controller;

import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.thesisproject.ct.contacttracingservice.enums.FormStatus;
import com.thesisproject.ct.contacttracingservice.model.Form;
import com.thesisproject.ct.contacttracingservice.model.UserProfile;
import com.thesisproject.ct.contacttracingservice.model.UserRegistration;
import com.thesisproject.ct.contacttracingservice.service.ApplicationService;
import com.thesisproject.ct.contacttracingservice.service.EmailService;
import com.thesisproject.ct.contacttracingservice.service.FormService;
import com.thesisproject.ct.contacttracingservice.service.SmsService;
import com.thesisproject.ct.contacttracingservice.service.UserService;

import springfox.documentation.annotations.ApiIgnore;

@Controller
@RequestMapping("/cts")
@ApiIgnore
public class UserViewController {
	
	@Autowired
	private UserService userService;
	
	@Autowired 
	private FormService formService;
	
	@Autowired 
	private ApplicationService applicationService;
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private SmsService smsService;
	
	@GetMapping()
	public String getUserRegistration(UserRegistration userRegistration) {
		return "user-registration";
	}
	
	@PostMapping()
	public String submitUserRegistration(@Valid UserRegistration userRegistration,
										  BindingResult result,
										  ModelMap model) {
		
		if(result.hasErrors()) {
			return "user-registration";
		}
		
		userRegistration = userService.postUserRegistration(userRegistration);
		Form form = formService.createForm(userRegistration);		
		emailService.sendFormUrlEmail(form);
		
		model.addAttribute("userRegistration", userRegistration);
		return "user-registration-success";
	}
	
	@GetMapping("/forms/{formId}")
	public String getUserProfileForm(@PathVariable("formId") UUID formId,
								 Form form,
								 UserProfile userProfile,
								 ModelMap model) {
		form = formService.getForm(formId);
		
		if(form == null || FormStatus.SUBMITTED.name().equals(form.getStatus())) {
			return "user-profile-invalid";
		}
		
		userProfile = userService.getUserProfile(form.getUserRegistration().getUserProfileId());
		model.addAttribute("validPositions", applicationService.getApplicationVariablesKeyValue("POSITION"));
		model.addAttribute("validDepartments", applicationService.getApplicationVariablesKeyValue("DEPARTMENT"));
		model.addAttribute("userProfile", userProfile);
		return "user-profile";
	}
	
	@PostMapping("/forms/{formId}/otp")
	public String sendOTP(@PathVariable("formId") UUID formId,
			                      Form form,
			                      UserProfile userProfile,
			                      BindingResult result,
			                      ModelMap model) {
		userProfile.setOtp(smsService.sendOTP(userProfile).getCode());
		userService.postUserProfile(userProfile);
		
		userProfile.setOtp(null);
		model.addAttribute("validPositions", applicationService.getApplicationVariablesKeyValue("POSITION"));
		model.addAttribute("validDepartments", applicationService.getApplicationVariablesKeyValue("DEPARTMENT"));
		model.addAttribute("userProfile", userProfile);
		return "user-profile";
	}
	
	@PostMapping("/forms/{formId}/submit")
	public String submitUserProfileForm(@PathVariable("formId") UUID formId,
			                      Form form,
			                      @Valid UserProfile userProfile,
			                      BindingResult result,
			                      ModelMap model) {
		model.addAttribute("validPositions", applicationService.getApplicationVariablesKeyValue("POSITION"));
		model.addAttribute("validDepartments", applicationService.getApplicationVariablesKeyValue("DEPARTMENT"));
		model.addAttribute("userProfile", userProfile);
		if(!userService.getUserProfile(userProfile.getUserProfileId()).getOtp().equals(userProfile.getOtp()) && !result.hasFieldErrors("otp")) {
			result.addError(new FieldError("userProfile", "otp", "Invalid OTP"));
		}
		
		if(result.hasErrors()) {
			return "user-profile";
		}
		
		userService.postUserProfile(userProfile);
		formService.submitForm(form);
		emailService.sendPersonalQRCodeEmail(userProfile);
		smsService.sendRegistrationCompletionSms(userProfile);
		
		return "user-profile-success";
	}
}
