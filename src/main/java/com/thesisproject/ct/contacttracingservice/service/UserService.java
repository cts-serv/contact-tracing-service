package com.thesisproject.ct.contacttracingservice.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.thesisproject.ct.contacttracingservice.entity.FormEntity;
import com.thesisproject.ct.contacttracingservice.entity.TemperatureRecordEntity;
import com.thesisproject.ct.contacttracingservice.entity.UserProfileEntity;
import com.thesisproject.ct.contacttracingservice.error.BadRequestException;
import com.thesisproject.ct.contacttracingservice.error.NotFoundException;
import com.thesisproject.ct.contacttracingservice.model.Detection;
import com.thesisproject.ct.contacttracingservice.model.TemperatureRecord;
import com.thesisproject.ct.contacttracingservice.model.UserProfile;
import com.thesisproject.ct.contacttracingservice.model.UserRegistration;
import com.thesisproject.ct.contacttracingservice.repository.FormRepository;
import com.thesisproject.ct.contacttracingservice.repository.TemperatureRecordRepository;
import com.thesisproject.ct.contacttracingservice.repository.UserProfileRepository;

@Service
public class UserService {
	
	@Autowired
	private UserProfileRepository userProfileRepository;
	
	@Autowired
	private TemperatureRecordRepository temperatureRecordRepository;
	
	@Autowired 
	private FormRepository formRepository;
	
	@Autowired
	private ApplicationService applicationService;
	
	@Autowired
	private SmsService smsService;
	
	@Autowired
	private EmailService emailService;
	
	@Value("${cts.detection.temperature}")
	private String detectionTemperature;
	
	public List<UserProfile> getUserProfiles(String filter) {
		List<UserProfileEntity> entities = Optional.ofNullable(filter)
													.map(fltr -> fltr + "%")
													.map(StringUtils::capitalize)
													.map(fltr -> userProfileRepository.findAllByIdNumberLikeOrFirstNameLikeOrMiddleNameLikeOrLastNameLike(fltr, fltr, fltr, fltr))
													.orElse(userProfileRepository.findAll());
		return entities.stream()
						.map(UserProfile::new)
						.map(this::populateUserProfileTemperatureRecords)
						.map(this::populateDepartmentAndPosition)
						.collect(Collectors.toList());
	}
	
	public UserProfile getUserProfile(UUID userProfileId)  {
		return userProfileRepository.findById(userProfileId)
								.map(UserProfile::new)
								.map(this::populateUserProfileTemperatureRecords)
								.map(this::populateDepartmentAndPosition)
					   			.orElse(new UserProfile());
	}
	
	private UserProfile populateUserProfileTemperatureRecords(UserProfile userProfile) {
		userProfile.getTemperatureRecords().addAll(this.getTemperatureRecords(userProfile.getUserProfileId()));
		Comparator<TemperatureRecord> tempRecordDateReverseComparator = Comparator.comparing(TemperatureRecord::getRecordDate).reversed();
		String lastTemperatureRecord = userProfile.getTemperatureRecords().stream().sorted(tempRecordDateReverseComparator).findFirst().map(TemperatureRecord::getTemperature).map(String::valueOf).orElse("--");
		userProfile.setLastTemperatureRecord(lastTemperatureRecord);
		return userProfile;
	}
	
	private UserProfile populateDepartmentAndPosition(UserProfile userProfile) {
		Optional.ofNullable(userProfile.getDepartment())
				.map(applicationService.getApplicationVariablesKeyValue("DEPARTMENT")::get)
				.ifPresent(userProfile::setDepartment);
		
		Optional.ofNullable(userProfile.getPosition())
				.map(applicationService.getApplicationVariablesKeyValue("POSITION")::get)
				.ifPresent(userProfile::setPosition);
		
		return userProfile;
	}
	
	public List<TemperatureRecord> getTemperatureRecords(UUID userProfileId) {
		return temperatureRecordRepository.findAllByUserProfileId(userProfileId)
										   .stream()
										   .map(TemperatureRecord::new)
										   .collect(Collectors.toList());
	}
	
	public UserProfile postUserProfile(UserProfile userProfile) throws BadRequestException {
		return Optional.ofNullable(userProfile)
					   .map(UserProfileEntity::new)
					   .map(userProfileRepository::saveAndFlush)
					   .map(UserProfile::new)
					   .orElseThrow(BadRequestException::new);
	}
	
	public UserProfile putUserProfile(UUID userProfileId, UserProfile userProfile) throws NotFoundException {
		if(userProfileRepository.existsById(userProfileId)) {
			userProfile.setUserProfileId(userProfileId);
			return Optional.ofNullable(userProfile)
						   .map(UserProfileEntity::new)
						   .map(userProfileRepository::saveAndFlush)
						   .map(UserProfile::new)
						   .orElseThrow(BadRequestException::new);
		} else {
			throw new NotFoundException();
		}
	}
	
	public void deleteUserProfile(UUID userProfileId) {
		userProfileRepository.deleteById(userProfileId);
	}
	
	public UserProfile getFormUser(UUID formId) {
		return formRepository.findById(formId)
					   		 .map(FormEntity::getUserProfileId)
					   		 .map(this::getUserProfile)
					   		 .orElse(new UserProfile());
	}
	
	public TemperatureRecord postTemperatureRecord(UUID userProfileId, TemperatureRecord temperatureRecord, MultipartFile imageFile) {
		return Optional.ofNullable(temperatureRecord)
					   .map(temp -> {
						   temp.setUserProfileId(userProfileId);
						   return temp;
					   })
					   .map(tempRec -> this.verifyDetection(tempRec, imageFile))
					   .map(TemperatureRecordEntity::new)
					   .map(temperatureRecordRepository::saveAndFlush)
					   .map(TemperatureRecord::new)
					   .orElse(new TemperatureRecord());
	}
	
	public UserRegistration postUserRegistration(UserRegistration userRegistration) throws BadRequestException {
		return Optional.ofNullable(userRegistration)
					   .map(UserProfileEntity::new)
					   .map(userProfileRepository::saveAndFlush)
					   .map(UserRegistration::new)
					   .orElseThrow(BadRequestException::new);
	}
	
	public TemperatureRecord verifyDetection(TemperatureRecord temperatureRecord, MultipartFile imageFile) {
		Optional.ofNullable(temperatureRecord)
				.filter(temp -> Double.parseDouble(this.detectionTemperature) <= temp.getTemperature())
				.ifPresent(temp -> {
					UserProfile userProfile = this.getUserProfile(temp.getUserProfileId());
					smsService.sendDetectionSms(userProfile);
					temperatureRecord.setDetection(true);
					List<TemperatureRecord> temperatureRecords = Arrays.asList(temperatureRecord);
					userProfile.setTemperatureRecords(temperatureRecords);
					userProfile.setImageFile(imageFile);
					emailService.sendDetectionEmail(userProfile);
				});
		return temperatureRecord;
	}
	
	public List<Detection> getDetections() {
		List<Detection> detections = new ArrayList<>();
		temperatureRecordRepository.findAllByDetection(true)
								   .stream()
								   .map(TemperatureRecord::new)
								   .map(temp -> {
									   Detection detection = new Detection();
									   detection.setTemperatureRecord(temp);
									   detection.setUserProfile(this.getUserProfile(temp.getUserProfileId()));
									   return detection;
								   })
								   .forEach(detections::add);
		return detections;
	}
}