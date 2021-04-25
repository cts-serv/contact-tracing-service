package com.thesisproject.ct.contacttracingservice.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Detection {
	private TemperatureRecord temperatureRecord;
	private UserProfile userProfile;
	private List<UserProfile> contactedUsers;
	
	public Detection(TemperatureRecord temperatureRecord) {
		this.temperatureRecord = temperatureRecord;
		this.contactedUsers = new ArrayList<>();
	}
}
