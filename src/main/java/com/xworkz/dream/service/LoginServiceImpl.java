package com.xworkz.dream.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.snakeyaml.Yaml;
import com.xworkz.dream.dto.utils.User;
import com.xworkz.dream.repository.DreamRepository;
import com.xworkz.dream.util.DreamUtil;
import com.xworkz.dream.wrapper.DreamWrapper;

@Service
public class LoginServiceImpl implements LoginService {
	@Value("${login.userFile}")
	private String userFile;
	@Value("${login.sheetId}")
	private String sheetId;
	@Value("${login.cookieDomain}")
	private String cookieDomain;

	List<User> users = new ArrayList<User>();

	@Autowired
	private DreamUtil util;
	@Autowired
	private ResourceLoader resourceLoader;
	@Autowired
	private DreamRepository repo;

	private static final Logger log = LoggerFactory.getLogger(LoginServiceImpl.class);

	private List<User> getUsers() throws IOException {
	    Yaml yaml = new Yaml();
	    Resource resource = resourceLoader.getResource(userFile);
	    File file = resource.getFile();
	    FileInputStream inputStream = new FileInputStream(file);
	    @SuppressWarnings("unchecked")
		Map<String, Map<Object, Object>> yamlData = (Map<String, Map<Object, Object>>) yaml.load(inputStream);
	    @SuppressWarnings("unchecked")
		List<Object> list = (List<Object>) yamlData.get("user");
	    ObjectMapper objectMapper = new ObjectMapper();

	    if (list == null) {
	        return Collections.emptyList(); 
	    }

	    for (Object object : list) {
	        User user = objectMapper.convertValue(object, User.class);
	        users.add(user);
	    }
	    return users;
	}

	@Override
	public ResponseEntity<String> validateLogin(String email) throws IOException {
	    log.info("Validating login for email: {}", email);

	    User user = findUserByEmail(email);

	    if (user == null) {
	        log.warn("User not found for email: {}", email);
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User Not Found");
	    }

	    int otp = util.generateOTP();

	    log.debug("Generated OTP: {}", otp);
	   

	    boolean otpSent = util.sendOtptoEmail(user.getEmail(), otp);
	    if (otpSent) {
	        log.info("OTP sent successfully for email: {}", email);
	        user.setOtp(otp);
	        user.setOtpExpiration(LocalDateTime.now().plusMinutes(10));

	        return ResponseEntity.ok("OTP sent");
	    } else {
	        log.error("Failed to send OTP for email: {}", email);
	        return ResponseEntity.status(HttpStatus.FOUND).body("User Found, OTP Not Sent");
	    }
	}

	@Override
	public ResponseEntity<String> validateOTP(String email, int otp) throws FileNotFoundException {
	    log.info("Validating OTP for email: {}, OTP: {}", email, otp);

	    HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
	            .getResponse();

	    if (response == null) {
	        log.error("Failed to get HttpServletResponse");
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
	    }

	    Cookie cookie = new Cookie("Xworkz", util.generateToken());
	    cookie.setMaxAge(0);
	    cookie.setPath("/");

	    response.addCookie(cookie);

	    User user = findUser(email);

	    log.debug("Found user: {}", user);

	    LocalDateTime currentDateTime = LocalDateTime.now();

	    if (user == null) {
	        log.warn("User not found for email: {}", email);
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not Found, Login First");
	    }

	    LocalDateTime otpExpiration = user.getOtpExpiration();
	    int userOtp = user.getOtp();

	    if (otpExpiration != null && userOtp != 0) {
	        if (otpExpiration.isAfter(currentDateTime)) {
	            if (userOtp == otp) {
	                String token = util.generateToken();

	                cookie = new Cookie("Xworkz", token);
	                cookie.setHttpOnly(true);
	                cookie.setMaxAge(60 * 30); // 1 day in seconds

	                response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
	                response.addCookie(cookie);
	                boolean updateStatus = updateLoginInfo(user, sheetId);
	                if (updateStatus) {
	                    log.info("Login successful for email: {}", email);
	                    return ResponseEntity.ok("Login successful");
	                } else {
	                    log.error("Failed to update login info for email: {}", email);
	                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                            .body("Failed to update login info");
	                }
	            } else {
	                log.warn("Wrong OTP for email: {}", email);
	                return ResponseEntity.status(HttpStatus.OK).body("OTP Wrong");
	            }
	        } else {
	            log.warn("OTP expired for email: {}", email);
	            return ResponseEntity.status(HttpStatus.GONE).body("OTP EXPIRED");
	        }
	    } else {
	        log.error("OTP not saved for email: {}", email);
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("OTP IS NOT SAVED & GENERATE AGAIN");
	    }
	}
	
	

	private boolean updateLoginInfo(User user, String spreadsheetId) {
	    if (user == null || user.getEmail() == null) {
	        log.warn("User or user email is null");
	        return false;
	    }

	    log.info("Updating login info for user: {}", user.getEmail());

	    user.setLoginTime(LocalDateTime.now().toString());
	    List<Object> list = DreamWrapper.userToList(user);

	    try {
	        boolean status = repo.updateLoginInfo(spreadsheetId, list);
	        return status;
	    } catch (IOException e) {
	        log.error("Failed to update login info for user: {}", user.getEmail(), e);
	        e.printStackTrace();
	    }
	    return false;
	}


	private User findUserByEmail(String email) throws IOException {
		log.debug("Finding user by email: {}", email);

		if (email == null) {
			log.warn("Email is null");
			return null;
		}

		List<User> users = getUsers();

		if (users == null) {
			log.error("Failed to retrieve users");
			return null;
		}

		User gotUser = users.stream().filter(user -> {
			String userEmail = user.getEmail();
			return userEmail != null && userEmail.equalsIgnoreCase(email);
		}).findFirst().orElse(null);

		log.debug("Found user by email: {}", email);

		return gotUser;
	}

	private User findUser(String email) throws FileNotFoundException {
		log.debug("Finding user by email: {}", email);

		User gotUser = users.stream().filter(user -> user.getEmail() != null && user.getEmail().equalsIgnoreCase(email))
				.findFirst().orElse(null);

		log.debug("Found user by email: {}", email);

		return gotUser;
	}

}
