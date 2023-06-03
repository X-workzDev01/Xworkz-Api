package com.xworkz.dream.service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.snakeyaml.Yaml;
import com.xworkz.dream.util.DreamUtil;
import com.xworkz.dream.util.MailSender;
import com.xworkz.dream.util.User;

@Service
public class LoginService {
	@Value("${login.userFile}")
	private String userFile;
	private static final int otpLenth = 6;
	private static final int otpExpiration = 10;
	List<User> users = new ArrayList<User>();

	@Autowired
	private MailSender mailSender;

	private List<User> getUsers() throws FileNotFoundException {

		Yaml yaml = new Yaml();
		FileInputStream inputStream = new FileInputStream(userFile);
		Map<String, Map<Object, Object>> yamlData = (Map<String, Map<Object, Object>>) yaml.load(inputStream);
		List<Object> list = (List<Object>) yamlData.get("user");
		ObjectMapper objectMapper = new ObjectMapper();

		for (Object object : list) {
			User user = objectMapper.convertValue(object, User.class);
			users.add(user);
		}
		return users;
	}

	public ResponseEntity<String> validateLogin(String email) throws FileNotFoundException {

		User user = findUserByEmail(email);
		System.out.println(user);
		if (user == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User Not Found");
		}

		int otp = DreamUtil.generateOTP();
		System.out.println(otp);
		boolean otpSent = mailSender.sendOtptoEmail(user.getEmail(), otp);
		System.out.println(otpSent);
		if (otpSent) {
			System.out.println("setting");
			user.setOtp(otp);
			user.setOtpExpiration(LocalDateTime.now().plusMinutes(10));
			System.out.println(user);
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
		System.out.println(user);
		LocalDateTime currentDateTime = LocalDateTime.now();
		if (user == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("OTP not Found , Login First");
		}
		
		if (user.getOtpExpiration() != null && user.getOtp() != 0) {
			LocalDateTime expirationTime = user.getOtpExpiration();
			if (expirationTime.isAfter(currentDateTime)) {
				if (user.getOtp() == otp) {
					String token = DreamUtil.generateToken();
					System.out.println(token);

					cookie = new Cookie("Xworkz", token);
					cookie.setHttpOnly(true);
					cookie.setMaxAge(60 * 30); // 1 day in seconds

					response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
					response.addCookie(cookie);
					return ResponseEntity.ok("Login sucessfull");

				}

				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("OTP Wrong");

			}
			return ResponseEntity.status(HttpStatus.GONE).body("OTP EXPIRED");
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("OTP IS SAVED & GENERATE AGAIN");
	}

	private User findUserByEmail(String email) throws FileNotFoundException {
		List<User> users = getUsers();

		System.out.println(users);
		User gotUser = users.stream().filter(user -> user.getEmail().equalsIgnoreCase(email)).findFirst().orElse(null);
		System.out.println(gotUser);
		return gotUser;
	}

	private User findUser(String email) throws FileNotFoundException {
		System.out.println("-------------------");
		System.out.println(users);
		User gotUser = users.stream().filter(user -> user.getEmail().equalsIgnoreCase(email)).findFirst().orElse(null);
		System.out.println(gotUser);
		return gotUser;
	}

}
