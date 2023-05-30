package com.xworkz.dream.service;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.google.api.services.sheets.v4.model.ValueRange;
import com.xworkz.dream.dto.TraineeDto;
import com.xworkz.dream.repo.DreamRepo;

@Service
public class DreamService {
	
	
	@Autowired
	private DreamRepo repo;
	
	
	
	
	public boolean writeData(String spreadsheetId,TraineeDto dto) {
		try {
			return repo.writeData(spreadsheetId, dto);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}




	public ResponseEntity<String> emailCheck( String spreadsheetId , String email) {
		// TODO Auto-generated method stub
		try {
			ValueRange values = repo.getEmails(spreadsheetId);
		    if (values.getValues() != null) {
		    	System.out.println(values);
                for (List<Object> row : values.getValues()) {
                    if (row.get(0).toString().equalsIgnoreCase(email)) {
                        return ResponseEntity.ok("Email exists");
                    }
                }
            }
		    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Email does not exist");
			
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");			
		}
		
	}
	
	public ResponseEntity<String> contactNumberCheck( String spreadsheetId , Long contactNumber) {
		// TODO Auto-generated method stub
		try {
			ValueRange values = repo.getContactNumbers(spreadsheetId);
		    if (values.getValues() != null) {
		    	System.out.println(values);
                for (List<Object> row : values.getValues()) {
                    if (row.get(0).toString().equals(String.valueOf(contactNumber))){
                        return ResponseEntity.ok("Contact Number exists");
                    }
                }
            }
		    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Contact Number Does not exist");
			
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");			
		}
		
	}

}
