package com.xworkz.dream.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xworkz.dream.dto.utils.FeesWithHistoryDto;
import com.xworkz.dream.feesDtos.FeesDto;
import com.xworkz.dream.feesDtos.FeesFinalDto;
import com.xworkz.dream.feesDtos.FeesUiDto;
import com.xworkz.dream.feesDtos.SheetFeesDetiles;
import com.xworkz.dream.service.FeesService;

import io.swagger.annotations.ApiOperation;

@RequestMapping("/api")
@RestController
public class FeesController {
	@Autowired
	private FeesService feesService;
	@Autowired
	private FeesFinalDto feesFinalDtoRanges;

	@ApiOperation("Saving feesDetiles")
	@PostMapping("/saveFees")
	public ResponseEntity<String> writeFeesSaveOpration(@RequestBody FeesUiDto dto) {
		return ResponseEntity.ok(feesService.writeFeesDetails(dto, feesFinalDtoRanges.getFeesEmailRange()));

	}

	@ApiOperation("Update feesDetiles")
	@PutMapping("/updateFeesDeties")
	public ResponseEntity<String> updateFeesFollowUp(@RequestBody FeesDto dto) {
		return ResponseEntity.ok(feesService.updateFeesFollowUp(dto, feesFinalDtoRanges.getGetFeesDetilesRange()));
	}

	@ApiOperation("get FeesDetiles by Email")
	@GetMapping("/getFeesDetilesByEmail/{email}")
	public ResponseEntity<FeesWithHistoryDto> getDetilesByEmail(@PathVariable String email) {
		return ResponseEntity.ok(feesService.getDetailsByEmail(email, feesFinalDtoRanges.getGetFeesDetilesRange(),
				feesFinalDtoRanges.getGetFeesDetilesfollowupRange()));
	}

	@ApiOperation("Get All feesDetiles By Selected Option")
	@GetMapping("/getFeesDetilesBySelectedOption/{minIndex}/{maxIndex}/{date}/{batch}/{paymentMode}/{status}")
	public ResponseEntity<SheetFeesDetiles> getFeesDetilesBySelectedOption(@PathVariable String minIndex,
			@PathVariable String maxIndex, @PathVariable String date, @PathVariable String batch,
			@PathVariable String paymentMode, @PathVariable String status) {
		return ResponseEntity.ok(feesService.getAllFeesDetails(feesFinalDtoRanges.getGetFeesDetilesRange(), minIndex,
				maxIndex, date, batch, paymentMode, status));
	}

	@ApiOperation("transforFeesDetilesExistingRecords")
	@PostMapping("/transforFeesDetilesExistingRecords/{courseName}")
	public String transforDataTraineeInto(@PathVariable String courseName) {
		return feesService.transForData(feesFinalDtoRanges.getId(), feesFinalDtoRanges.getFeesEmailRange(), courseName);
	}

	@PutMapping("/updateFeesDetailsChangeEmailAndFeeConcession/{feesConcession}/{traineeName}/{oldEmail}/{newEmail}/{updatedBy}")
	public String updatefeesEmailAndNameAndFeesConcession(@PathVariable Integer feesConcession,
			@PathVariable String traineeName, @PathVariable String oldEmail, @PathVariable String newEmail,
			@PathVariable String updatedBy) {
		feesService.updateNameAndEmail(feesConcession, traineeName, oldEmail, newEmail, updatedBy);
		return "Updated sucessfully";
	}

}
