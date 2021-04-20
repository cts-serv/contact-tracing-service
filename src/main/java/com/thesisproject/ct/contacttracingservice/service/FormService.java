package com.thesisproject.ct.contacttracingservice.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.thesisproject.ct.contacttracingservice.entity.FormEntity;
import com.thesisproject.ct.contacttracingservice.enums.FormStatus;
import com.thesisproject.ct.contacttracingservice.model.Form;
import com.thesisproject.ct.contacttracingservice.model.UserRegistration;
import com.thesisproject.ct.contacttracingservice.repository.FormRepository;

@Service
public class FormService {
	
	@Value("${form.basepath}")
	private String baseUrl;
	
	@Autowired
	private FormRepository formRepository;
	
	public String sendFormUrlToEmail(String email) {
		return "OK";
	}
	
	public Form getForm(UUID formId) {
		return formRepository.findById(formId)
					   		 .map(Form::new)
					   		 .orElse(null);
	}
	
	public Form createForm(UserRegistration userRegistration) {
		Form form = new Form(userRegistration);
		return Optional.of(form)
					   .map(FormEntity::new)
					   .map(formRepository::saveAndFlush)
					   .map(Form::new)
					   .map(frm -> {
						   frm.setUserRegistration(userRegistration);
						   return frm;
					   })
					   .orElse(null);
	}
	
	public Form submitForm(Form form) {
		FormEntity formEntity = formRepository.getOne(form.getFormId());
		formEntity.setStatus(FormStatus.SUBMITTED.name());
		formEntity.setSubmittedDate(LocalDateTime.now());
		
		formRepository.saveAndFlush(formEntity);
		return null;
	}
}
