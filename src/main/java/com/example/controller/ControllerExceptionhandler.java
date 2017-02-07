package com.example.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class ControllerExceptionhandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(ControllerExceptionhandler.class);

	 @ExceptionHandler(value = {ConstraintViolationException.class})
	 @ResponseBody public ResponseEntity<List<String>> handleContraintViolationException(HttpServletRequest request, ConstraintViolationException ex) {
	   LOGGER.info("handleContraintViolationException", ex);
	   return new ResponseEntity<>(getErrorResponseFromConstraintViolationException(ex), HttpStatus.BAD_REQUEST);
	 }
	 
	 public List<String> getErrorResponseFromConstraintViolationException(ConstraintViolationException ex) {
		   List<String> errorItems = ex.getConstraintViolations()
		       .stream()
		       .map(cv ->cv.getMessage() )
		       .collect(Collectors.toList());
		   return errorItems;
		 }


}
