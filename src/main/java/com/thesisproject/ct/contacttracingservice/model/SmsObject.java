package com.thesisproject.ct.contacttracingservice.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SmsObject {
	private String apikey;
	private String number;
	private String message;
	private String sendername;
}
