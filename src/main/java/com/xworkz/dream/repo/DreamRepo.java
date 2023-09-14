package com.xworkz.dream.repo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;
import org.springframework.util.SystemPropertyUtils;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest;
import com.google.api.services.sheets.v4.model.CellData;
import com.google.api.services.sheets.v4.model.DimensionRange;
import com.google.api.services.sheets.v4.model.ExtendedValue;
import com.google.api.services.sheets.v4.model.GridCoordinate;
import com.google.api.services.sheets.v4.model.InsertDimensionRequest;
import com.google.api.services.sheets.v4.model.Request;
import com.google.api.services.sheets.v4.model.RowData;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.SheetProperties;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.UpdateCellsRequest;
import com.google.api.services.sheets.v4.model.UpdateSheetPropertiesRequest;
import com.google.api.services.sheets.v4.model.UpdateSpreadsheetPropertiesRequest;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.xworkz.dream.dto.StatusDto;
import com.xworkz.dream.dto.TraineeDto;
import com.xworkz.dream.wrapper.DreamWrapper;

@Repository
public class DreamRepo {

	@Value("${sheets.appName}")
	private String applicationName;
	@Value("${sheets.credentialsPath}")
	private String credentialsPath;
	private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
	private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);
	private Sheets sheetsService;
	@Value("${sheets.range}")
	private String range;
	@Value("${sheets.emailRange}")
	private String emailRange;
	@Value("${sheets.contactNumberRange}")
	private String contactNumberRange;
	@Value("${sheets.dropdownRange}")
	private String dropdownRange;
	@Value("${sheets.idRange}")
	private String idRange;
	@Value("${sheets.loginInfoRange}")
	private String loginInfoRange;
	@Value("${sheets.followUpRange}")
	private String followUpRange;
	@Value("${sheets.followUpStatus}")
	private String followUpStatus;
	@Value("${sheets.emailAndNameRange}")
	private String emailAndNameRange;
	@Value("${sheets.batchDetails}")
	private String batchDetails;
	@Value("${sheets.batchDetailsRange}")
	private String batchDetailsRange;
	@Value("${sheets.batchIdRange}")
	private String batchIdRange;
	@Value("${sheets.dateOfBirthDetailsRange}")
	private String dateOfBirthDetailsRange;
	@Value("${sheets.birthdayRange}")
	private String birthdayRange;
	@Value("${sheets.followUpEmailRange}")
	private String followUpEmailRange;
	@Value("${sheets.followUpStatusIdRange}")
	private String followUpStatusIdRange;
	@Value("${sheets.attendanceInfoRange}")
	private String attendanceInfoRange;
	@Value("${sheets.attendanceInfoIDRange}")
	private String attendanceInfoIDRange;

	@Autowired
	private ResourceLoader resourceLoader;

	@PostConstruct
	private void setSheetsService() throws IOException, FileNotFoundException, GeneralSecurityException {

		Resource resource = resourceLoader.getResource(credentialsPath);
		File file = resource.getFile();

		GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(file)).createScoped(SCOPES);

		HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);
		sheetsService = new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY,
				requestInitializer).setApplicationName(applicationName).build();
	}

	public boolean writeData(String spreadsheetId, List<Object> row) throws IOException {
		List<List<Object>> values = new ArrayList<>();
		values.add(row);
		ValueRange body = new ValueRange().setValues(values);
		sheetsService.spreadsheets().values().append(spreadsheetId, range, body).setValueInputOption("USER_ENTERED")
				.execute();
		return true;
	}

	@Cacheable(value = "emailData", key = "#spreadsheetId", unless = "#result == null")
	public ValueRange getEmails(String spreadsheetId) throws IOException {
		ValueRange response = sheetsService.spreadsheets().values().get(spreadsheetId, emailRange).execute();
		return response;
	}

	@Cacheable(value = "contactData", key = "#spreadsheetId", unless = "#result == null")
	public ValueRange getContactNumbers(String spreadsheetId) throws IOException {
		ValueRange response = sheetsService.spreadsheets().values().get(spreadsheetId, contactNumberRange).execute();
		return response;
	}

	public ValueRange getIds(String spreadsheetId) throws IOException {
		ValueRange response = sheetsService.spreadsheets().values().get(spreadsheetId, idRange).execute();

		return response;
	}

	@Cacheable(value = "getDropdowns", key = "#spreadsheetId", unless = "#result == null")
	public List<List<Object>> getDropdown(String spreadsheetId) throws IOException {
		ValueRange response = sheetsService.spreadsheets().values().get(spreadsheetId, dropdownRange).execute();

		return response.getValues();
	}

	public boolean updateLoginInfo(String spreadsheetId, List<Object> row) throws IOException {
		List<List<Object>> values = new ArrayList<>();
		values.add(row);
		ValueRange body = new ValueRange().setValues(values);

		sheetsService.spreadsheets().values().append(spreadsheetId, loginInfoRange, body)
				.setValueInputOption("USER_ENTERED").execute();
		return true;

	}

	@Cacheable(value = "sheetsData", key = "#spreadsheetId", unless = "#result == null")
	public List<List<Object>> readData(String spreadsheetId) throws IOException {

		ValueRange response = sheetsService.spreadsheets().values().get(spreadsheetId, range).execute();
		return response.getValues();
	}

	public UpdateValuesResponse update(String spreadsheetId, String range2, ValueRange valueRange) throws IOException {
		return sheetsService.spreadsheets().values().update(spreadsheetId, range2, valueRange)
				.setValueInputOption("RAW").execute();
	}

	public boolean saveToFollowUp(String spreadsheetId, List<Object> row) throws IOException {
		List<List<Object>> list = new ArrayList<List<Object>>();
		list.add(row);
		ValueRange body = new ValueRange().setValues(list);
		sheetsService.spreadsheets().values().append(spreadsheetId, followUpRange, body)
				.setValueInputOption("USER_ENTERED").execute();
		return true;
	}

	public boolean updateFollowUpStatus(String spreadsheetId, List<Object> statusData) throws IOException {
		List<List<Object>> list = new ArrayList<List<Object>>();
		list.add(statusData);
		ValueRange body = new ValueRange().setValues(list);
		sheetsService.spreadsheets().values().append(spreadsheetId, followUpStatus, body)
				.setValueInputOption("USER_ENTERED").execute();
		return true;
	}

	@Cacheable(value = "followUpDetails", key = "#spreadsheetId", unless = "#result == null")
	public List<List<Object>> getFollowUpDetails(String spreadsheetId) throws IOException {
		ValueRange response = sheetsService.spreadsheets().values().get(spreadsheetId, followUpRange).execute();
		return response.getValues();
	}

	public boolean updateCurrentFollowUpStatus(String spreadsheetId, String currentFollowRange, List<Object> data)
			throws IOException {
		List<List<Object>> list = new ArrayList<List<Object>>();
		list.add(data);
		ValueRange body = new ValueRange().setValues(list);
		sheetsService.spreadsheets().values().update(spreadsheetId, currentFollowRange, body)
				.setValueInputOption("USER_ENTERED").execute();
		return true;

	}

	public ValueRange getStatusId(String spreadsheetId) throws IOException {
		ValueRange response = sheetsService.spreadsheets().values().get(spreadsheetId, followUpStatusIdRange).execute();
		return response;
	}

	public List<List<Object>> getEmailsAndNames(String spreadsheetId, String value) throws IOException {
		ValueRange response = sheetsService.spreadsheets().values().get(spreadsheetId, emailAndNameRange).execute();

		return response.getValues();
	}

	@Cacheable(value = "followUpStatusDetails", key = "#spreadsheetId", unless = "#result == null")
	public List<List<Object>> getFollowUpStatusDetails(String spreadsheetId) throws IOException {

		ValueRange response = sheetsService.spreadsheets().values().get(spreadsheetId, followUpStatus).execute();
		return response.getValues();
	}

	public ValueRange getBatchId(String spreadsheetId) throws IOException {
		ValueRange response = sheetsService.spreadsheets().values().get(spreadsheetId, batchIdRange).execute();
		return response;
	}

	public boolean saveBatchDetails(String spreadsheetId, List<Object> row) throws IOException {
		List<List<Object>> values = new ArrayList<>();
		values.add(row);
		ValueRange body = new ValueRange().setValues(values);
		sheetsService.spreadsheets().values().append(spreadsheetId, batchDetailsRange, body)
				.setValueInputOption("USER_ENTERED").execute();
		return true;
	}

	public ValueRange getBirthDayId(String spreadsheetId) throws IOException {
		ValueRange response = sheetsService.spreadsheets().values().get(spreadsheetId, birthdayRange).execute();
		return response;
	}

	public boolean saveBirthDayDetails(String spreadsheetId, List<Object> row) throws IOException {
		List<List<Object>> values = new ArrayList<>();
		values.add(row);
		ValueRange body = new ValueRange().setValues(values);
		sheetsService.spreadsheets().values().append(spreadsheetId, dateOfBirthDetailsRange, body)
				.setValueInputOption("USER_ENTERED").execute();
		return true;
	}

	public UpdateValuesResponse updateFollow(String spreadsheetId, String range2, ValueRange valueRange)
			throws IOException {
		return sheetsService.spreadsheets().values().update(spreadsheetId, range2, valueRange)
				.setValueInputOption("RAW").execute();
	}

	public ValueRange getEmailList(String spreadsheetId) throws IOException {
		ValueRange response = sheetsService.spreadsheets().values().get(spreadsheetId, followUpEmailRange).execute();
		return response;
	}

	// suhas
	@Cacheable(value = "batchDetails", key = "#spreadsheetId", unless = "#result == null")

	public List<List<Object>> getCourseDetails(String spreadsheetId) throws IOException {
		ValueRange response = sheetsService.spreadsheets().values().get(spreadsheetId, batchDetailsRange).execute();

		return response.getValues();
	}

	public List<List<Object>> notification(String spreadsheetId) throws IOException {
		ValueRange response = sheetsService.spreadsheets().values().get(spreadsheetId, followUpStatus).execute();
		return response.getValues();
	}

	@CacheEvict(value = { "sheetsData", "emailData", "contactData", "followUpStatusDetails",
			"followUpDetails" }, allEntries = true)
	public void evictSheetsDataCaches() {
		// This method will be scheduled to run every 12 hours
		// and will evict all entries in the specified caches
	}

	@CacheEvict(value = { "sheetsData", "emailData", "contactData", "followUpStatusDetails",
			"followUpDetails" }, allEntries = true)
	public void evictAllCachesOnTraineeDetails() {
		// will evict all entries in the specified caches
		System.out.println("evictAllCachesOnTraineeDetails running");

	}

	@Cacheable(value = "followUpDetails", key = "#spreadsheetId", unless = "#result == null")
	public List<List<Object>> getFollowUpDetailsByid(String spreadsheetId) throws IOException {
		ValueRange response = sheetsService.spreadsheets().values().get(spreadsheetId, followUpRange).execute();
		return response.getValues();
	}

	public boolean writeAttendance(String spreadsheetId, List<Object> row) throws IOException {
		List<List<Object>> values = new ArrayList<>();
		values.add(row);
		ValueRange body = new ValueRange().setValues(values);
		sheetsService.spreadsheets().values().append(spreadsheetId, attendanceInfoRange, body)
				.setValueInputOption("USER_ENTERED").execute();
		return true;
	}

	public boolean addColumn(String spreadsheetId) throws Exception {
		int columnIndex = getColumnCount(spreadsheetId);
		List<Request> requests = new ArrayList<>();
		requests.add(new Request().setUpdateCells(new UpdateCellsRequest()
				.setStart(new GridCoordinate().setSheetId(1948817216).setRowIndex(0).setColumnIndex(columnIndex))
				.setRows(Collections.singletonList(new RowData().setValues(Collections.singletonList(new CellData()
						.setUserEnteredValue(new ExtendedValue().setStringValue(LocalDate.now().toString()))))))
				.setFields("userEnteredValue.stringValue")));
		BatchUpdateSpreadsheetRequest batchUpdateRequest = new BatchUpdateSpreadsheetRequest().setRequests(requests);
		sheetsService.spreadsheets().batchUpdate(spreadsheetId, batchUpdateRequest).execute();

		return true;
	}

	public int getColumnCount(String spreadsheetId) throws Exception {
		String range = "attendanceInfo" + "!1:1";
		ValueRange response = sheetsService.spreadsheets().values().get(spreadsheetId, range).execute();
		if (response.getValues() != null && !response.getValues().isEmpty()) {
			return response.getValues().get(0).size();
		} else {
			return 0;
		}
	}

	public void updateValueById(String id, String spreadsheetId, String newValue) throws IOException {

		// Define the range in which you want to search for the ID

		// Make the request to the Google Sheets API to get the row with the specified
		// ID
		ValueRange response = sheetsService.spreadsheets().values().get(spreadsheetId, attendanceInfoIDRange).execute();

		List<List<Object>> values = response.getValues();
		if (values != null && !values.isEmpty()) {

			for (int rowIndex = 0; rowIndex < values.size(); rowIndex++) {
				List<Object> row = values.get(rowIndex);
				String cellValue = row.get(0).toString(); // Assuming the ID is in the first column
				if (cellValue.equals(id)) {
					getSheetRangeByColumnLength(spreadsheetId);
					// Update the specific cell in the row
					String updateRange = "attendanceInfo!A3:K10" + (rowIndex + 1); // Assuming you want to update the
																					// value
					// in column B

					List<List<Object>> newValues = new ArrayList<>();
					newValues.add(Collections.singletonList(newValue));

					ValueRange body = new ValueRange().setValues(newValues);

					// Make the request to update the value in the specified cell
					UpdateValuesResponse result = sheetsService.spreadsheets().values()
							.update(spreadsheetId, updateRange, body).setValueInputOption("RAW").execute();

					// The value has been updated
					return;
				}
			}
		}

		// ID not found
	}

	public List<List<Object>> getSheetRangeByColumnLength(String sheetId) throws IOException {

		String columnRange = calculateColumnRange("A", 10);
		// Make the request to the Google Sheets API to retrieve values in the
		// calculated column range
//		ValueRange response = sheetsService.spreadsheets().values().get(sheetId, columnRange).execute();
		ValueRange response = sheetsService.spreadsheets().values().get(sheetId, "Hareesha").execute();

		List<List<Object>> values = response.getValues();
		return values;
	}

	public static String calculateColumnRange(String columnLetter, int columnLength) {
		// Calculate the range for the entire column (e.g., column A1:A10 if column
		// length is 10)
		return columnLetter + "1:" + columnLength;
	}

}
