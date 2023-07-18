package com.xworkz.dream.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
import com.xworkz.dream.repo.DreamRepo;
import com.xworkz.dream.util.DreamUtil;
import com.xworkz.dream.util.UtilLocal;
import com.xworkz.dream.wrapper.DreamWrapper;

@Service
public class LoginService {
	@Value("${login.userFile}")
	private String userFile;
	@Value("${login.sheetId}")
	private String sheetId;
	@Value("${login.cookieDomain}")
	private String cookieDomain;

	private static final int otpLenth = 6;
	private static final int otpExpiration = 10;
	List<User> users = new ArrayList<User>();

	@Autowired
	private DreamUtil util;
	@Autowired
	private ResourceLoader resourceLoader;
	@Autowired
	private DreamRepo repo;

	private static final Logger logger = LoggerFactory.getLogger(LoginService.class);

	private List<User> getUsers() throws IOException {

		Yaml yaml = new Yaml();
		Resource resource = resourceLoader.getResource(userFile);
		File file = resource.getFile();
		FileInputStream inputStream = new FileInputStream(file);
		Map<String, Map<Object, Object>> yamlData = (Map<String, Map<Object, Object>>) yaml.load(inputStream);
		List<Object> list = (List<Object>) yamlData.get("user");
		ObjectMapper objectMapper = new ObjectMapper();

		for (Object object : list) {
			User user = objectMapper.convertValue(object, User.class);
			users.add(user);
		}
		return users;
	}

	public ResponseEntity<String> validateLogin(String email) throws IOException {
		logger.info("Validating login for email: {}", email);

		User user = findUserByEmail(email);

		if (user == null) {
			logger.warn("User not found for email: {}", email);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User Not Found");
		}

		int otp = util.generateOTP();

		logger.debug("Generated OTP: {}", otp);
		System.out.println(cookieDomain);

		boolean otpSent = util.sendOtptoEmail(user.getEmail(), otp);
		if (otpSent) {
			logger.info("OTP sent successfully for email: {}", email);
			user.setOtp(otp);
			user.setOtpExpiration(LocalDateTime.now().plusMinutes(10));

			return ResponseEntity.ok("OTP sent");
		} else {
			logger.error("Failed to send OTP for email: {}", email);
			return ResponseEntity.status(HttpStatus.FOUND).body("User Found, OTP Not Sent");
		}
	}

	public ResponseEntity<String> validateOTP(String email, int otp) throws FileNotFoundException {
		logger.info("Validating OTP for email: {}, OTP: {}", email, otp);

		HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getResponse();
		Cookie cookie = new Cookie("Xworkz", util.generateToken());
		cookie.setMaxAge(0);
//		cookie.setSecure(false);
//		cookie.setDomain(cookieDomain);
	    cookie.setPath("/");
		
		response.addCookie(cookie);

		User user = findUser(email);

		logger.debug("Found user: {}", user);

		LocalDateTime currentDateTime = LocalDateTime.now();
		if (user == null) {
			logger.warn("User not found for email: {}", email);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not Found, Login First");
		}

		if (user.getOtpExpiration() != null && user.getOtp() != 0) {
			LocalDateTime expirationTime = user.getOtpExpiration();
			if (expirationTime.isAfter(currentDateTime)) {
				if (user.getOtp() == otp) {
					String token = util.generateToken();

					cookie = new Cookie("Xworkz", token);
					cookie.setHttpOnly(true);
					cookie.setMaxAge(60 * 30); // 1 day in seconds

					response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
					response.addCookie(cookie);
					boolean updateStatus = updateLoginInfo(user, sheetId);
					if (updateStatus) {
						logger.info("Login successful for email: {}", email);
						return ResponseEntity.ok("Login successful");
					} else {
						logger.error("Failed to update login info for email: {}", email);
						return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
								.body("Failed to update login info");
					}
				} else {
					logger.warn("Wrong OTP for email: {}", email);
					return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("OTP Wrong");
				}
			} else {
				logger.warn("OTP expired for email: {}", email);
				return ResponseEntity.status(HttpStatus.GONE).body("OTP EXPIRED");
			}
		} else {
			logger.error("OTP not saved for email: {}", email);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("OTP IS NOT SAVED & GENERATE AGAIN");
		}
	}

	private boolean updateLoginInfo(User user, String spreadsheetId) {
		logger.info("Updating login info for user: {}", user.getEmail());

		user.setLoginTime(LocalDateTime.now().toString());
		List<Object> list = DreamWrapper.userToList(user);

		try {
			boolean status = repo.updateLoginInfo(spreadsheetId, list);
			return status;
		} catch (IOException e) {
			logger.error("Failed to update login info for user: {}", user.getEmail(), e);
			e.printStackTrace();
		}
		return false;
	}

	private User findUserByEmail(String email) throws IOException {
		logger.debug("Finding user by email: {}", email);

		List<User> users = getUsers();
		User gotUser = users.stream().filter(user -> user.getEmail().equalsIgnoreCase(email)).findFirst().orElse(null);

		logger.debug("Found user by email: {}", email);

		return gotUser;
	}

	private User findUser(String email) throws FileNotFoundException {
		logger.debug("Finding user by email: {}", email);

		User gotUser = users.stream().filter(user -> user.getEmail().equalsIgnoreCase(email)).findFirst().orElse(null);

		logger.debug("Found user by email: {}", email);

		return gotUser;
	}
}
