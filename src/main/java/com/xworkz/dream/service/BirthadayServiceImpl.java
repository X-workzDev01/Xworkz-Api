package com.xworkz.dream.service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.xworkz.dream.repository.DreamRepository;

@Service
public class BirthadayServiceImpl implements BirthadayService{
	
	@Autowired
	DreamRepository repository;
	@Value("${login.sheetId}")
	private String spreadsheetId;
	
	@Override
	public void sendBirthdayEmails() throws IOException {
		
        List<List<Object>> birthdayDetails = repository.getBirthadayDetails(spreadsheetId);
        LocalDate currentDate = LocalDate.now();
        List<String> emailsToSend = birthdayDetails.stream()
                .filter(row -> {
                    LocalDate dob = LocalDate.parse((String) row.get(4));
                    return dob.getMonth() == currentDate.getMonth() && dob.getDayOfMonth() == currentDate.getDayOfMonth();
                })
                .map(row -> (String) row.get(2))
                .collect(Collectors.toList());
        for (String email : emailsToSend) {
            // SendEmail.send(email, "Birthday Greetings", "Happy Birthday!");
            // Add logic to send the email
        }
    }
	
	 @Scheduled(cron = "0 0 0 * * *") // Run at midnight every day
	    public void sendBirthdayEmailsScheduled() {
	        try {
	            sendBirthdayEmails();
	        } catch (IOException e) {
	            // Handle exception if needed
	            e.printStackTrace();
	        }
	    }

}
