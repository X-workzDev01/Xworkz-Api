package com.xworkz.dream.service;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.google.api.services.sheets.v4.model.ValueRange;
import com.xworkz.dream.dto.TraineeDto;
import com.xworkz.dream.repo.DreamRepo;
import com.xworkz.dream.wrapper.DreamWrapper;

@Service
public class DreamService {

	@Autowired
	private DreamRepo repo;
	

	 private static final Logger logger = LoggerFactory.getLogger(DreamService.class);

	    // Rest of your code...
	 public ResponseEntity<String> writeData(String spreadsheetId, TraineeDto dto, HttpServletRequest request) {
		    try {
		        if (isCookieValid(request)) {
		            List<Object> list = DreamWrapper.dtoToList(dto);
		            boolean writeStatus = repo.writeData(spreadsheetId, list);
		            if (writeStatus) {
		                logger.info("Data written successfully to spreadsheetId: {}", spreadsheetId);
		                return ResponseEntity.ok("Data written successfully");
		            } else {
		                logger.warn("Failed to write data to spreadsheetId: {}", spreadsheetId);
		                return ResponseEntity.badRequest().body("Failed to write data");
		            }
		        } else {
		            // Invalid cookie
		            logger.info("Invalid cookie in the request");
		            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid cookie");
		        }
		    } catch (IOException e) {
		        logger.error("Error occurred while writing data to spreadsheetId: {}", spreadsheetId, e);
		        e.printStackTrace();
		        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
		    }
		}

	    public ResponseEntity<String> emailCheck(String spreadsheetId, String email, HttpServletRequest request) {
	        try {
	            if (isCookieValid(request)) {
	                ValueRange values = repo.getEmails(spreadsheetId);
	                if (values.getValues() != null) {
	                    for (List<Object> row : values.getValues()) {
	                        if (row.get(0).toString().equalsIgnoreCase(email)) {
	                            logger.info("Email exists in spreadsheetId: {}", spreadsheetId);
	                            return ResponseEntity.status(HttpStatus.FOUND).body("Email exists");
	                        }
	                    }
	                }
	                logger.info("Email does not exist in spreadsheetId: {}", spreadsheetId);
	                return ResponseEntity.ok("Email does not exist");
	            } else {
	                // Invalid cookie
	                logger.info("Invalid cookie in the request");
	                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid cookie");
	            }
	        } catch (Exception e) {
	            logger.error("An error occurred while checking email in spreadsheetId: {}", spreadsheetId, e);
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
	        }
	    }

	    private boolean isCookieValid(HttpServletRequest request) {
	        Cookie[] cookies = request.getCookies();
	        if (cookies != null) {
	            for (Cookie cookie : cookies) {
	                if (cookie.getName().equals("Xworkz")) {
	                	System.out.println("Cookie Valid");
	                    return true;
	                }
	            }
	        }
	        return false;
	    }

	    
	    public ResponseEntity<String> contactNumberCheck(String spreadsheetId, Long contactNumber, HttpServletRequest request) {
	        try {
	            if (isCookieValid(request)) {
	                ValueRange values = repo.getContactNumbers(spreadsheetId);
	                if (values.getValues() != null) {
	                    for (List<Object> row : values.getValues()) {
	                        if (row.get(0).toString().equals(String.valueOf(contactNumber))) {
	                            logger.info("Contact Number exists in spreadsheetId: {}", spreadsheetId);
	                            return ResponseEntity.status(HttpStatus.FOUND).body("Contact Number exists");
	                        }
	                    }
	                }
	                logger.info("Contact Number does not exist in spreadsheetId: {}", spreadsheetId);
	                return ResponseEntity.ok("Contact Number does not exist");
	            } else {
	                // Invalid cookie
	                logger.info("Invalid cookie in the request");
	                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid cookie");
	            }
	        } catch (Exception e) {
	            logger.error("An error occurred while checking Contact Number in spreadsheetId: {}", spreadsheetId, e);
	            e.printStackTrace();
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
	        }
	    }
	    

	    @CacheEvict(value = {"sheetsData", "emailData" , "contactData" , "getDropdowns"}, allEntries = true)
	    @Scheduled(fixedDelay = 43200000) // 12 hours in milliseconds
	    public void evictAllCaches() {
	        // This method will be scheduled to run every 12 hours
	        // and will evict all entries in the specified caches
	    }
}

