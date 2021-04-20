package com.thesisproject.ct.contacttracingservice.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xhtmlrenderer.pdf.PDFEncryption;

import com.lowagie.text.DocumentException;
import com.thesisproject.ct.contacttracingservice.enums.SubjectTableHeaders;
import com.thesisproject.ct.contacttracingservice.model.Form;
import com.thesisproject.ct.contacttracingservice.model.TemperatureRecord;
import com.thesisproject.ct.contacttracingservice.model.UserProfile;
import com.thesisproject.ct.contacttracingservice.util.QRCodeUtility;

@Service
public class EmailService {
	
	@Autowired
	private SpringTemplateEngine templateEngine;

	@Autowired
	private JavaMailSender emailSender;

	@Autowired
	UserService userService;

	@Value("${form.basepath}")
	private String baseUrl;
	
	public void sendFormUrlEmail(Form form) {
		MimeMessage message = emailSender.createMimeMessage();
		try {
			MimeMessageHelper helper = new MimeMessageHelper(message);
			helper.setTo(form.getUserRegistration().getEmail());
			helper.setFrom("no-reply@contacttracingservice.com");
			helper.setSubject("Contact Tracing Form URL");
			
			Context context = new Context();
			context.setVariable("firstName", form.getUserRegistration().getFirstName());
			context.setVariable("formUrl", this.baseUrl+form.getFormId());
			helper.setText(templateEngine.process("form-url-email", context),  true);
			
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		
		emailSender.send(message);
	}

	public void sendPersonalQRCodeEmail(UserProfile userProfile) {
		MimeMessage message = emailSender.createMimeMessage();
		File attachmentFile = new File(userProfile.getUserProfileId() + "attachment.pdf");
		File attachmentImage = new File(userProfile.getUserProfileId() + "attachment.png");
		
		try(OutputStream outputStream = new FileOutputStream(attachmentFile)) {
			FileUtils.writeByteArrayToFile(attachmentImage, QRCodeUtility.generateQRCode(userProfile.getUserProfileId().toString(), 200, 200));
			FileSystemResource file = new FileSystemResource(attachmentFile);
			MimeMessageHelper helper = new MimeMessageHelper(message, true);
			helper.setFrom("no-reply-contact-tracing@gmail.com");
			helper.setTo(userProfile.getEmail());
			helper.setSubject("Contact Tracing Identification QR Code");
			
			Context context = new Context();
			context.setVariable("firstName", userProfile.getFirstName());
			context.setVariable("imageUrl","data:image/png;base64," + Base64.getEncoder().encodeToString(QRCodeUtility.generateQRCode(userProfile.getUserProfileId().toString(), 200, 200)));
			helper.setText(templateEngine.process("personal-qr-code-email", context),  true);
			
			ITextRenderer renderer = new ITextRenderer();
			String html = templateEngine.process("personal-qr-code-attachment", context);
			PDFEncryption encryption = new PDFEncryption(userProfile.getIdNumber().getBytes(), userProfile.getIdNumber().getBytes());
			renderer.setDocumentFromString(html);
			renderer.setPDFEncryption(encryption);
			renderer.layout();
			renderer.createPDF(outputStream);
			
			

			helper.addAttachment("attachment.pdf", file);
			emailSender.send(message);

		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		} finally {
			try {
				FileUtils.forceDelete(attachmentImage);
				FileUtils.forceDelete(attachmentFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void sendDetectionEmail(UserProfile userProfile) {
		MimeMessage message = emailSender.createMimeMessage();
		File attachmentFile = new File(userProfile.getUserProfileId() + "attachment.pdf");
		
		try(OutputStream outputStream = new FileOutputStream(attachmentFile)) {
			FileSystemResource file = new FileSystemResource(attachmentFile);
			MimeMessageHelper helper = new MimeMessageHelper(message, true);
			helper.setFrom("no-reply-contact-tracing@gmail.com");
			helper.setTo(userProfile.getEmail());
			helper.setSubject("Fever Detection Notification");
			
			Context context = new Context();
			context.setVariable("userProfile", userProfile);
			context.setVariable("temperatureRecord", userProfile.getTemperatureRecords().get(0));
			if(null != userProfile.getImageFile()) {
				context.setVariable("imageUrl","data:image/jpg;base64," + Base64.getEncoder().encodeToString(userProfile.getImageFile().getBytes()));
			}
			helper.setText(templateEngine.process("detection-email", context),  true);
			
			ITextRenderer renderer = new ITextRenderer();
			String html = templateEngine.process("detection-attachment", context);
			renderer.setDocumentFromString(html);
			renderer.layout();
			renderer.createPDF(outputStream);

			helper.addAttachment("attachment.pdf", file);
			emailSender.send(message);

		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		} finally {
			try {
				FileUtils.forceDelete(attachmentFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void sendUserProfilesReport() {
		MimeMessage message = emailSender.createMimeMessage();
		File file = new File("report.csv");
		try(FileOutputStream fos = new FileOutputStream(file); ByteArrayOutputStream out = new ByteArrayOutputStream(); CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out), CSVFormat.DEFAULT.withHeader(SubjectTableHeaders.class))) {
			
			MimeMessageHelper helper = new MimeMessageHelper(message, true);
			helper.setFrom("no-reply-contact-tracing@gmail.com");
			helper.setTo("cts.service.2021@gmail.com");
			helper.setSubject("Contact Tracing Daily Report");
			helper.setText("Attached is the updated report of the registered contact tracing users.");

			List<UserProfile> userProfileList = userService.getUserProfiles(null);
			for (UserProfile userProfile : userProfileList) {
				if(!userProfile.getTemperatureRecords().isEmpty()) {
					for(TemperatureRecord temperatureRecord : userProfile.getTemperatureRecords()) {
						List<String> fields = Arrays.asList(String.valueOf(userProfile.getUserProfileId()), 
								userProfile.getFirstName(),
								userProfile.getMiddleName(), 
								userProfile.getLastName(), 
								userProfile.getIdNumber(),
								userProfile.getContactNumber(), 
								userProfile.getEmail(), 
								userProfile.getPosition(), 
								userProfile.getDepartment(),
								String.valueOf(userProfile.isUserAgreementAccepted()),
								String.valueOf(temperatureRecord.getTemperature()),
								temperatureRecord.getAreaCode(),
								String.valueOf(temperatureRecord.getRecordDate()));
						csvPrinter.printRecord(fields);
					}
				} else {
					List<String> fields = Arrays.asList(String.valueOf(userProfile.getUserProfileId()), 
							userProfile.getFirstName(),
							userProfile.getMiddleName(), 
							userProfile.getLastName(), 
							userProfile.getIdNumber(),
							userProfile.getContactNumber(), 
							userProfile.getEmail(), 
							userProfile.getPosition(), 
							userProfile.getDepartment(),
							String.valueOf(userProfile.isUserAgreementAccepted()));
					csvPrinter.printRecord(fields);
				}
				
				
			}
			
			csvPrinter.flush();
			helper.addAttachment("attachment.csv", new ByteArrayDataSource(out.toByteArray(), "application/csv"));
			emailSender.send(message);
			
			fos.close();
			FileUtils.forceDelete(file);
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}