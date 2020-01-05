package com.pingidentity.moderno.qrcode;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.pingidentity.moderno.model.api.AuthenticationTokenResponse;
import com.pingidentity.pingidsdk.PingIDSdkException;
import com.sun.jersey.core.util.Base64;

/**
 * 
 * QR Code utilities
 * Created by Ping Identity on 3/23/17.
 * Copyright © 2017 Ping Identity. All rights reserved.
 *
 */
public class QRCodeUtils {

	private static final Logger logger = LoggerFactory.getLogger(QRCodeUtils.class);
	
	/**
	 * QR Code default width
	 */
	private static final int QR_CODE_DEFAULT_WIDTH = 450;
	
	/**
	 * QR Code default height
	 */
	private static final int QR_CODE_DEFAULT_HEIGHT = 450;
	
	/**
	 * Hints to be used when creating the QR code
	 */
	private static Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
	static {
    	hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
    	hints.put(EncodeHintType.MARGIN, 0);
	}
	
	/**
	 * Creates an authentication token (this token will be embedded in the QR code)
	 * @return an authentication token (this token will be embedded in the QR code)
	 */
	public static AuthenticationTokenResponse createAuthenticationToken() {
		AuthenticationTokenGenerator qrCodeHandler = new AuthenticationTokenGenerator();
		try {
			AuthenticationTokenResponse response = qrCodeHandler.createAuthenticationToken();
			return response;
		} catch (PingIDSdkException e) {
			logger.error("failed to create an authentication token",e);
			return null;
		}
	}
	
	/**
	 * create QR code image from the given data
	 * @param data the data to be embedded in the QR code image
	 * @return QR code image
	 */
	public static String createQRCodeImage(String data){
		try {
			BufferedImage bi = createImage(data);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ImageIO.write(bi, "PNG", out);
			byte[] bytes = out.toByteArray();
			String base64bytes = new String(Base64.encode(bytes));
			String src = "data:image/png;base64," + base64bytes;
			return src;
		} catch (Exception e) {
			logger.error("failed to create a QR code image",e);
		}
		return "";
	}

	/**
	 * Creates a buffered image from the data
	 * @param data the data
	 * @return buffered image
	 */
	private static BufferedImage createImage(String data) {
		BitMatrix bitMatrix = null;
		try {

			bitMatrix = new com.google.zxing.qrcode.QRCodeWriter().encode(data, BarcodeFormat.QR_CODE, QR_CODE_DEFAULT_WIDTH,
						QR_CODE_DEFAULT_HEIGHT, hints);

			return MatrixToImageWriter.toBufferedImage(bitMatrix);
		} catch (Exception e) {
			logger.error("failed to create a buffered image",e);
			return null;
		}
	}
}
