package com.thesisproject.ct.contacttracingservice.util;

import java.io.ByteArrayOutputStream;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import lombok.experimental.UtilityClass;

@UtilityClass
public class QRCodeUtility {
	public byte[] generateQRCode(String content, int width, int height) {
		try(ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			QRCodeWriter qrCodeWriter = new QRCodeWriter();
			BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height);
			ImageIO.write(MatrixToImageWriter.toBufferedImage(bitMatrix), "png", baos);
			return baos.toByteArray();
		} catch (Exception e) {
			return null;
		}
	}
}
