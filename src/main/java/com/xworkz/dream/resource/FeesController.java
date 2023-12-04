package com.xworkz.dream.resource;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
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
import com.xworkz.dream.feesDtos.FeesUiDto;
import com.xworkz.dream.feesDtos.SheetFeesDetiles;
import com.xworkz.dream.service.FeesService;

import io.swagger.annotations.ApiOperation;

@RequestMapping("/api")
@RestController
public class FeesController {

	@Autowired
	private FeesService feesService;
	private Logger log = LoggerFactory.getLogger(FeesController.class);
	@Value("${sheets.getFeesDetiles}")
	private String getFeesDetilesRange;
	private String range;
	@Value("${sheets.getFeesDetilesfollowupRange}")
	private String getFeesDetilesfollowupRange;


	@ApiOperation("Saving feesDetiles")
	@PostMapping("/saveFees")
	public ResponseEntity<String> writeFeesSaveOpration(@RequestBody FeesUiDto dto)
			throws IOException, IllegalAccessException {
		log.info("Running save Fees detiles controller ");
		return ResponseEntity.ok(feesService.writeFeesDetiles(dto));


	}

	@ApiOperation("Update feesDetiles")
	@PutMapping("/updateFeesDeties")
	public ResponseEntity<FeesDto> updateFeesFollowUp(@RequestBody FeesDto dto) throws IOException {
		return ResponseEntity.ok(feesService.updateFeesFollowUp(dto, getFeesDetilesRange, range));
	}

	@ApiOperation("get FeesDetiles by Email")
	@GetMapping("/getFeesDetilesByEmail/{email}")
	public ResponseEntity<FeesWithHistoryDto> getDetilesByEmail(@PathVariable String email) throws IOException {
		return ResponseEntity
				.ok(feesService.getDetilesByEmail(email, getFeesDetilesRange, getFeesDetilesfollowupRange));
	}

	@ApiOperation("Get All feesDetiles By Selected Option")
	@GetMapping("/getFeesDetilesBySelectedOption/{minIndex}/{maxIndex}/{date}/{batch}/{paymentMode}")
	public ResponseEntity<SheetFeesDetiles> getFeesDetilesBySelectedOption(@PathVariable String minIndex,
			@PathVariable String maxIndex, @PathVariable String date, @PathVariable String batch,
			@PathVariable String paymentMode) throws IOException {
		return ResponseEntity
				.ok(feesService.getAllFeesDetiles(getFeesDetilesRange, minIndex, maxIndex, date, batch, paymentMode));
	}

}
