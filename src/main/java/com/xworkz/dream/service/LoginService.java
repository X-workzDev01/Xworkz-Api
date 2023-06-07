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
import com.xworkz.dream.util.DreamUtil;
import com.xworkz.dream.util.MailSender;

@Service
public class LoginService {
	@Value("${login.userFile}")
	private String userFile;
	private static final int otpLenth = 6;
	private static final int otpExpiration = 10;
	List<User> users = new ArrayList<User>();

	@Autowired
	private MailSender mailSender;
	@Autowired
	private ResourceLoader resourceLoader;

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

		User user = findUserByEmail(email);
		if (user == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User Not Found");
		}

		int otp = DreamUtil.generateOTP();
//		System.out.println(otp);
		boolean otpSent = mailSender.sendOtptoEmail(user.getEmail(), otp);

		if (otpSent) {

			user.setOtp(otp);
			user.setOtpExpiration(LocalDateTime.now().plusMinutes(10));

			return ResponseEntity.ok("OTP sent");
		}
		return ResponseEntity.status(HttpStatus.FOUND).body("User Found , OTP Not Sent");

	}

	public ResponseEntity<String> validateOTP(String email, int otp) throws FileNotFoundException {
		HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getResponse();
		Cookie cookie = new Cookie("Xworkz", null);
		cookie.setMaxAge(0);
		response.addCookie(cookie);
		User user = findUser(email);

		LocalDateTime currentDateTime = LocalDateTime.now();
		if (user == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not Found , Login First");
		}

		if (user.getOtpExpiration() != null && user.getOtp() != 0) {
			LocalDateTime expirationTime = user.getOtpExpiration();
			if (expirationTime.isAfter(currentDateTime)) {
				if (user.getOtp() == otp) {
					String token = DreamUtil.generateToken();

					cookie = new Cookie("Xworkz", token);
					cookie.setHttpOnly(true);
					cookie.setSecure(false);
					cookie.setMaxAge(60 * 30); // 1 day in seconds

					response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
					response.addCookie(cookie);
					return ResponseEntity.ok("Login sucessfull");

				}

				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("OTP Wrong");

			}
			return ResponseEntity.status(HttpStatus.GONE).body("OTP EXPIRED");
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("OTP IS NOT SAVED & GENERATE AGAIN");
	}

	private User findUserByEmail(String email) throws IOException {
		List<User> users = getUsers();
		User gotUser = users.stream().filter(user -> user.getEmail().equalsIgnoreCase(email)).findFirst().orElse(null);
		return gotUser;
	}

	private User findUser(String email) throws FileNotFoundException {

		User gotUser = users.stream().filter(user -> user.getEmail().equalsIgnoreCase(email)).findFirst().orElse(null);
		return gotUser;
	}

}
