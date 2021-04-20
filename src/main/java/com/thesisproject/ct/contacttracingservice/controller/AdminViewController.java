package com.thesisproject.ct.contacttracingservice.controller;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.thesisproject.ct.contacttracingservice.model.Admin;
import com.thesisproject.ct.contacttracingservice.model.ApplicationVariable;
import com.thesisproject.ct.contacttracingservice.model.DetectionVariable;
import com.thesisproject.ct.contacttracingservice.model.SearchObject;
import com.thesisproject.ct.contacttracingservice.model.UserProfile;
import com.thesisproject.ct.contacttracingservice.service.ApplicationService;
import com.thesisproject.ct.contacttracingservice.service.UserService;
import com.thesisproject.ct.contacttracingservice.util.QRCodeUtility;

import springfox.documentation.annotations.ApiIgnore;

@Controller
@RequestMapping("")
@ApiIgnore
public class AdminViewController {
	
	@Autowired
	private ApplicationService applicationService;
	
	@Autowired
	private UserService userService;
	
	@Value("form.registration")
	private String registrationFormUrl;
	
	@GetMapping(path = "/")
	public String getHome(SearchObject searchObject,
						  ModelMap model) {
		
		model.addAttribute("qrCodeImage", "data:image/png;base64," + Base64.getEncoder().encodeToString(QRCodeUtility.generateQRCode(registrationFormUrl, 500, 500)));
		return "home";
	}
	
	@GetMapping(path = "/usermanagement")
	public String getUserManagement(SearchObject searchObject,
									UserProfile userProfile,
									ModelMap model) {
		
		model.addAttribute("validPositions", applicationService.getApplicationVariablesKeyValue("POSITION"));
		model.addAttribute("validDepartments", applicationService.getApplicationVariablesKeyValue("DEPARTMENT"));
		model.addAttribute("updateDisabled", true);
		return "user-management";
	}
	
	@PostMapping(path = "/usermanagement/search")
	public String postUserManagementSearch(SearchObject searchObject,
			                               UserProfile userProfile,
			                               BindingResult result,
			                               ModelMap model) {
		List<UserProfile> userProfileList = new ArrayList<>();
		
		Optional.ofNullable(searchObject.getFilter())
				.filter(filter -> !"".equals(filter))
				.map(userService::getUserProfiles)
				.ifPresent(userProfileList::addAll);
		
		
		model.addAttribute("validPositions", applicationService.getApplicationVariablesKeyValue("POSITION"));
		model.addAttribute("validDepartments", applicationService.getApplicationVariablesKeyValue("DEPARTMENT"));
		model.addAttribute("userProfiles", userProfileList);
		model.addAttribute("userProfile", userProfile);
		model.addAttribute("updateDisabled", true);
		
		return "user-management";
	}
	
	@PostMapping(path = "/usermanagement/edit")
	public String postUserManagementEdit(SearchObject searchObject,
			                         	   UserProfile userProfile,
			                         	   BindingResult result,
			                         	   ModelMap model) {
		List<UserProfile> userProfileList = new ArrayList<>();
		
		Optional.ofNullable(searchObject.getFilter())
				.filter(filter -> !"".equals(filter))
				.map(userService::getUserProfiles)
				.ifPresent(userProfileList::addAll);
		
		userProfile = userService.getUserProfile(userProfile.getUserProfileId());
		
		model.addAttribute("validPositions", applicationService.getApplicationVariablesKeyValue("POSITION"));
		model.addAttribute("validDepartments", applicationService.getApplicationVariablesKeyValue("DEPARTMENT"));
		model.addAttribute("userProfiles", userProfileList);
		model.addAttribute("userProfile", userProfile);
		model.addAttribute("updateDisabled", false);
		return "user-management";
	}
	
	@PostMapping(path = "/usermanagement/update")
	public String postUserManagementUpdate(SearchObject searchObject,
			                         	   @Valid UserProfile userProfile,
			                         	   BindingResult result,
			                         	   ModelMap model) {
		
		List<UserProfile> userProfileList = new ArrayList<>();
		
		Optional.ofNullable(searchObject.getFilter())
				.filter(filter -> !"".equals(filter))
				.map(userService::getUserProfiles)
				.ifPresent(userProfileList::addAll);
		
		model.addAttribute("validPositions", applicationService.getApplicationVariablesKeyValue("POSITION"));
		model.addAttribute("validDepartments", applicationService.getApplicationVariablesKeyValue("DEPARTMENT"));
		model.addAttribute("userProfiles", userProfileList);
		model.addAttribute("userProfile", userProfile);
		model.addAttribute("updateDisabled", false);
		
		if(result.hasErrors()) {
			return "user-management";
		}
		
		userProfile = Optional.ofNullable(userProfile)
								.map(up -> userService.putUserProfile(up.getUserProfileId(), up))
								.orElse(new UserProfile());
		return "user-management";
	}
	
	@PostMapping(path = "/usermanagement/delete")
	public String postUserManagementDelete(SearchObject searchObject,
			                         	   UserProfile userProfile,
			                         	   BindingResult result,
			                         	   ModelMap model) {
		
		List<UserProfile> userProfileList = new ArrayList<>();
		
		Optional.ofNullable(searchObject.getFilter())
				.filter(filter -> !"".equals(filter))
				.map(userService::getUserProfiles)
				.ifPresent(userProfileList::addAll);
		
		model.addAttribute("validPositions", applicationService.getApplicationVariablesKeyValue("POSITION"));
		model.addAttribute("validDepartments", applicationService.getApplicationVariablesKeyValue("DEPARTMENT"));
		model.addAttribute("userProfiles", userProfileList);
		model.addAttribute("userProfile", userProfile);
		model.addAttribute("updateDisabled", false);
		
		if(result.hasErrors()) {
			return "user-management";
		}
		
		userService.deleteUserProfile(userProfile.getUserProfileId());
		return "user-management";
	}
	
	@GetMapping(path = "/applicationsettings")
	public String getApplicationSettings(SearchObject searchObject,
										 ApplicationVariable applicationVariable,
										 DetectionVariable detectionVariable,
										 BindingResult result,
										 ModelMap model) {
		
		model.addAttribute("validPositions", applicationService.getApplicationVariables("POSITION"));
		model.addAttribute("validDepartments", applicationService.getApplicationVariables("DEPARTMENT"));
		model.addAttribute("detectionVariable", new DetectionVariable(applicationService.getApplicationVariables("DETECTION VARIABLE")));
		return "application-settings";
	}
	
	@PostMapping(path = "/applicationsettings/add")
	public String postApplicationSettingsAdd(SearchObject searchObject,
											 @Valid ApplicationVariable applicationVariable,
											 DetectionVariable detectionVariable,
											 BindingResult result,
											 ModelMap model) {
		if(result.hasErrors()) {
			model.addAttribute("validPositions", applicationService.getApplicationVariables("POSITION"));
			model.addAttribute("validDepartments", applicationService.getApplicationVariables("DEPARTMENT"));
			model.addAttribute("detectionVariable", new DetectionVariable(applicationService.getApplicationVariables("DETECTION VARIABLE")));
			return "application-settings";
		}
		
		applicationService.addApplicationVariable(applicationVariable);
		
		model.addAttribute("validPositions", applicationService.getApplicationVariables("POSITION"));
		model.addAttribute("validDepartments", applicationService.getApplicationVariables("DEPARTMENT"));
		model.addAttribute("detectionVariable", new DetectionVariable(applicationService.getApplicationVariables("DETECTION VARIABLE")));
		return "application-settings";
	}
	
	@PostMapping(path = "/applicationsettings/save")
	public String postApplicationSettingsSave(SearchObject searchObject,
												ApplicationVariable applicationVariable,
												@Valid DetectionVariable detectionVariable,
												BindingResult result,
												ModelMap model) {
		if(result.hasErrors()) {
			model.addAttribute("validPositions", applicationService.getApplicationVariables("POSITION"));
			model.addAttribute("validDepartments", applicationService.getApplicationVariables("DEPARTMENT"));
			model.addAttribute("detectionVariable", new DetectionVariable(applicationService.getApplicationVariables("DETECTION VARIABLE")));
			return "application-settings";
		}
		
		applicationService.updateDetectionVariables(detectionVariable);
		
		model.addAttribute("validPositions", applicationService.getApplicationVariables("POSITION"));
		model.addAttribute("validDepartments", applicationService.getApplicationVariables("DEPARTMENT"));
		model.addAttribute("detectionVariable", new DetectionVariable(applicationService.getApplicationVariables("DETECTION VARIABLE")));
		return "application-settings";
	}
	
	@PostMapping(path = "/applicationsettings/remove")
	public String postApplicationSettingsRemove(SearchObject searchObject,
												@Valid ApplicationVariable applicationVariable,
												DetectionVariable detectionVariable,
												BindingResult result,
												ModelMap model) {
		if(result.hasErrors()) {
			model.addAttribute("validPositions", applicationService.getApplicationVariables("POSITION"));
			model.addAttribute("validDepartments", applicationService.getApplicationVariables("DEPARTMENT"));
			model.addAttribute("detectionVariable", new DetectionVariable(applicationService.getApplicationVariables("DETECTION VARIABLE")));
			return "application-settings";
		}
		
		applicationService.deleteApplicationVariable(applicationVariable);
		
		model.addAttribute("validPositions", applicationService.getApplicationVariables("POSITION"));
		model.addAttribute("validDepartments", applicationService.getApplicationVariables("DEPARTMENT"));
		model.addAttribute("detectionVariable", new DetectionVariable(applicationService.getApplicationVariables("DETECTION VARIABLE")));
		return "application-settings";
	}
	
	@GetMapping(path = "/detectionhistory")
	public String getDetectionHistory(SearchObject searchObject,
									  ApplicationVariable applicationVariable,
									  DetectionVariable detectionVariable,
									  BindingResult result,
									  ModelMap model) {
		
		model.addAttribute("detections", userService.getDetections());
		model.addAttribute("validPositions", applicationService.getApplicationVariables("POSITION"));
		model.addAttribute("validDepartments", applicationService.getApplicationVariables("DEPARTMENT"));
		model.addAttribute("detectionVariable", new DetectionVariable(applicationService.getApplicationVariables("DETECTION VARIABLE")));
		return "detection-history";
	}
	
	@GetMapping(path = "/login")
	public ModelAndView getLogin(ModelMap model) {
		return new ModelAndView("login", "admin", new Admin());
	}
	
	@GetMapping(path = "/logout")
	public ModelAndView logout(ModelMap model) {
		return new ModelAndView("login", "admin", new Admin());
	}
}
