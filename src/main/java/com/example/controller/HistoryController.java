package com.example.controller;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import com.example.constraint.TidConstraint;
import com.example.constraint.UserIdConstraint;
import com.example.domain.HistoryEntry;
import com.example.service.HistoryService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@Validated
public class HistoryController {
	private static final Long DEFERRED_TIMEOUT = 90000L;

	private static final Logger LOGGER = LoggerFactory.getLogger(HistoryController.class);
	private final HistoryService historyService;

	public HistoryController(HistoryService historyService) {
		this.historyService = historyService;
	}

	@ApiOperation(value = "GetHistory", notes = "Returns the history.")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "The history", response = HistoryEntry.class, responseContainer = "Set"),
			@ApiResponse(code = 404, message = "Not found"), @ApiResponse(code = 500, message = "Server side error") })
	@GetMapping("/history")
	public DeferredResult<Collection<HistoryEntry>> getHistory() {
		LOGGER.info("getting history");
		DeferredResult<Collection<HistoryEntry>> dr = new DeferredResult<>(DEFERRED_TIMEOUT);

		historyService.getHistory().subscribe(dr::setResult, dr::setErrorResult, () -> setNotFound(dr));
		return dr;
	}

	@ApiOperation(value = "Register a click", notes = "Register a click.")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "registered the click", response = String.class),
			@ApiResponse(code = 404, message = "Not found"), @ApiResponse(code = 500, message = "Server side error") })
	@GetMapping("/click")
	public DeferredResult<String> click(@RequestParam(value = "tid", required = true) @TidConstraint String tid,
			@RequestParam(value = "userid", required = true) @UserIdConstraint int userid

	) {
		LOGGER.info("registering click");
		DeferredResult<String> dr = new DeferredResult<>(DEFERRED_TIMEOUT);
		historyService.storeClick(userid, tid).subscribe(dr::setResult, dr::setErrorResult, () -> setNotFound(dr));
		return dr;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static void setNotFound(DeferredResult dr) {
		if (!dr.isSetOrExpired()) {
			dr.setResult(new ResponseEntity<>(HttpStatus.NOT_FOUND));
		}
	}
}
