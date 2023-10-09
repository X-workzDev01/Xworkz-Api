package com.xworkz.dream.util;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EncryptionHelper {
	static Cipher cipher;
	final static String secretKeyString = "aeroplaneaeropla";
	static Logger logger = LoggerFactory.getLogger(EncryptionHelper.class);

	public EncryptionHelper() {
		logger.info("{} is Created...........");
	}

	public String encrypt(String plainText) {
		SecretKey secretKey;
		try {
			secretKey = new SecretKeySpec(secretKeyString.getBytes("UTF-8"), "AES");
			cipher = Cipher.getInstance("AES");
			byte[] plainTextByte = plainText.getBytes();
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			byte[] encryptedByte = cipher.doFinal(plainTextByte);
			Base64.Encoder encoder = Base64.getEncoder();
			String encryptedText = encoder.encodeToString(encryptedByte);
			return encryptedText;
		} catch (UnsupportedEncodingException e) {
			logger.error("Encoding is unsupported exception is {} and message is {}", e, e.getMessage());
		} catch (NoSuchAlgorithmException e) {
			logger.error("Algorithm is not found exception is {} and message is {}", e, e.getMessage());
		} catch (NoSuchPaddingException e) {
			logger.error("padding is not found exception is {} and message is {}", e, e.getMessage());
		} catch (InvalidKeyException e) {
			logger.error("Key is invalid exception is {} and message is {}", e, e.getMessage());
		} catch (IllegalBlockSizeException e) {
			logger.error("exception is {} and message is {}", e, e.getMessage());
		} catch (BadPaddingException e) {
			logger.error("exception is {} and message is {}", e, e.getMessage());
		}
		return null;
	}

	public String decrypt(String encryptedText) {
		SecretKey secretKey;
		try {
			secretKey = new SecretKeySpec(secretKeyString.getBytes("UTF-8"), "AES");
			cipher = Cipher.getInstance("AES");
			Base64.Decoder decoder = Base64.getDecoder();
			byte[] encryptedTextByte = decoder.decode(encryptedText);
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			byte[] decryptedByte = cipher.doFinal(encryptedTextByte);
			String decryptedText = new String(decryptedByte);
			return decryptedText;
		} catch (UnsupportedEncodingException e) {
			logger.error("Encoding is unsupported exception is {} and message is {}", e, e.getMessage());
		} catch (NoSuchAlgorithmException e) {
			logger.error("Algorithm is not found exception is {} and message is {}", e, e.getMessage());
		} catch (NoSuchPaddingException e) {
			logger.error("padding is not found exception is {} and message is {}", e, e.getMessage());
		} catch (InvalidKeyException e) {
			logger.error("Key is invalid exception is {} and message is {}", e, e.getMessage());
		} catch (IllegalBlockSizeException e) {
			logger.error("exception is {} and message is {}", e, e.getMessage());
		} catch (BadPaddingException e) {
			logger.error("exception is {} and message is {}", e, e.getMessage());
		}
		return null;
	}

}
