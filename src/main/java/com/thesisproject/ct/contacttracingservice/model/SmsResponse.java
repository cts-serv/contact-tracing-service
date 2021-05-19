package com.thesisproject.ct.contacttracingservice.model;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SmsResponse {
	private String code;
	private List<String> number;
}