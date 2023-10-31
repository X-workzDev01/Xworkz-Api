package com.xworkz.dream.service;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.xworkz.dream.repository.DreamRepository;
import com.xworkz.dream.util.DreamUtil;

@Service
public class BirthadayServiceImpl implements BirthadayService{
	
	@Autowired
	private DreamRepository repository;
	@Autowired
	private DreamUtil util;
	@Value("${login.sheetId}")
	private String spreadsheetId;
	
	private static final Logger logger = LoggerFactory.getLogger(BirthadayServiceImpl.class);
	
	
	private String findNameByEmail(String email) throws IOException {
		List<List<Object>> birthdayDetails = repository.getBirthadayDetails(spreadsheetId);
		 Optional<String> optionalName = birthdayDetails.stream()
		            .filter(row -> email.equals(row.get(2)))
		            .map(row -> (String) row.get(1))
		            .findFirst();

		    return optionalName.orElse("Unknown"); 
		
	}
	
	@Override
	public void sendBirthdayEmails() throws IOException {
		String subject="Birthday Wishes : X-workZ";
        List<List<Object>> birthdayDetails = repository.getBirthadayDetails(spreadsheetId);
        System.out.println("birthdayDetails : "+birthdayDetails);
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        List<String> emailsToSend = birthdayDetails.stream()
                .filter(row -> {
                    LocalDate dob = LocalDate.parse((String) row.get(4),dateFormatter);
                    return dob.getMonth() == currentDate.getMonth() && dob.getDayOfMonth() == currentDate.getDayOfMonth();
                })
                .map(row -> (String) row.get(2))
                .collect(Collectors.toList());
        for (String email : emailsToSend) {
        	String nameByEmail = findNameByEmail(email);
        	util.sendBirthadyEmail(email, subject, nameByEmail);
        }
    }
	
	 @Scheduled(cron = "0 0 0 * * *")
	 public void sendBirthdayEmailsScheduled() {
	        try {
	            sendBirthdayEmails();
	        } catch (IOException e) {
	        logger.info("Birthday Mail is not working : {} ",e.getMessage());
	        }
	    }

}
