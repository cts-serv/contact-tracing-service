package com.thesisproject.ct.contacttracingservice.error;

import org.springframework.stereotype.Component;

import lombok.NoArgsConstructor;

@Component
@NoArgsConstructor
public class NotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;

}
