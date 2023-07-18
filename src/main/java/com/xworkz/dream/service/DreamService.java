package com.xworkz.dream.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.xworkz.dream.dto.BasicInfoDto;
import com.xworkz.dream.dto.CourseDto;
import com.xworkz.dream.dto.EducationInfoDto;
import com.xworkz.dream.dto.ReferalInfoDto;
import com.xworkz.dream.dto.SheetsDto;
import com.xworkz.dream.dto.TraineeDto;
import com.xworkz.dream.repo.DreamRepo;
import com.xworkz.dream.wrapper.DreamWrapper;

@Service
public class DreamService {

	@Autowired
	private DreamRepo repo;
	@Autowired 
	private DreamWrapper wrapper;
	@Autowired
	private CacheManager cacheManager;
	@Value("${sheets.rowStartRange}")
	private String rowStartRange;
	@Value("${sheets.rowEndRange}")
	private String rowEndRange;

	 private static final Logger logger = LoggerFactory.getLogger(DreamService.class);

	    // Rest of your code...
	 public ResponseEntity<String> writeData(String spreadsheetId, TraineeDto dto, HttpServletRequest request) {
		    try {
		    	if (true) {// isCookieValid(request)
		    		List<List<Object>> data =  repo.readData(spreadsheetId);
		            int size = data.size();
		            System.out.println(size);
		            
		            dto.setId(size+=1);
		            System.out.println(dto.getId());
		            List<Object> list = wrapper.dtoToList(dto);
		            
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
	        	if (true) {// isCookieValid(request)
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
	            if (true) {// isCookieValid(request)
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

		public ResponseEntity<SheetsDto> readData(String spreadsheetId, int startingIndex, int maxRows) {
		try {
			List<List<Object>> data = repo.readData(spreadsheetId);
			List<TraineeDto> dtos = getLimitedRows(data, startingIndex, maxRows);
			
			HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
					.getResponse();
			
			SheetsDto dto = new SheetsDto(dtos , dtos.size());
			return ResponseEntity.ok(dto);
		} catch (IOException e) {
			
			e.printStackTrace();
			
		}
		return null;
			
		}
		
		public List<TraineeDto> getLimitedRows(List<List<Object>> values, int startingIndex, int maxRows) {
		    List<TraineeDto> traineeDtos = new ArrayList<>();

		    int endIndex = startingIndex + maxRows;
		    int rowCount = values.size();

		    ListIterator<List<Object>> iterator = values.listIterator(startingIndex);

		    while (iterator.hasNext() && iterator.nextIndex() < endIndex) {
		        List<Object> row = iterator.next();
		    
		        if (row != null && !row.isEmpty()) {
		            TraineeDto traineeDto = wrapper.listToDto(row);
		            traineeDtos.add(traineeDto);
		        }
		    }

		    return traineeDtos;
		}

		public List<TraineeDto> filterData(String spreadsheetId , String searchValue) throws IOException {
			if(searchValue!=null && !searchValue.isEmpty()) {
				List<List<Object>> data = repo.readData(spreadsheetId);
				 List<List<Object>> filteredLists = data.stream()
			                .filter(list -> list.stream().anyMatch(value -> value.toString().toLowerCase().contains(searchValue)))
			                .collect(Collectors.toList());
				 List<TraineeDto> flist = new ArrayList<TraineeDto>();
			            for (List<Object> list2 : filteredLists) {
							TraineeDto dto = wrapper.listToDto(list2);
							flist.add(dto);
						}  
				return flist;
			}
			else {
				return null;
			}
		}

		public ResponseEntity<String> updateFollowUps(String spreadsheetId) {
			
			return null;
		}
		
		public ResponseEntity<String> update(String spreadsheetId, String email , TraineeDto dto){
			try {
				int rowIndex = findRowIndexByEmail(spreadsheetId, email);
				String range = "xworkzApi!" + rowStartRange + rowIndex + ":"+ rowEndRange + rowIndex;
				 List<List<Object>> values = Arrays.asList(wrapper.dtoToList(dto));

			        ValueRange valueRange = new ValueRange();
			        valueRange.setValues(values);
			        UpdateValuesResponse updated = repo.update(spreadsheetId , range , valueRange);
			       return ResponseEntity.ok("Updated Successfully");
			       
			} catch (IOException e) {
				e.printStackTrace();
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred ");
				
			}
			
		}
		
		 private int findRowIndexByEmail(String spreadsheetId, String email) throws IOException{
		     
				ValueRange data = repo.getEmails(spreadsheetId); 
		        List<List<Object>> values = data.getValues();
		        if (values != null) {
		            for (int i = 0; i < values.size(); i++) {
		                List<Object> row = values.get(i);
		                if (row.size() > 0 && row.get(0).toString().equalsIgnoreCase(email)) {
		                    return i + 3;
		                }
		            }
		        }
		        return -1;
		    }
		 
		
}

