package com.example.controller;

import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.validator.constraints.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import com.example.constraint.DimensionConstraint;
import com.example.constraint.UserIdConstraint;
import com.example.domain.AdRequest;
import com.example.domain.AdResponse;
import com.example.service.AdService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@Validated
public class AdController {
	private static final Long DEFERRED_TIMEOUT = 90000L;

	private static final Logger LOGGER = LoggerFactory.getLogger(AdController.class);
	private final AdService adService;

	public AdController(AdService adService) {
		this.adService = adService;
	}

	
	//TODO: unclean on what a domain means
	public static String getDomainName(String uri) {
		try {
			String host =  new URI(uri).getHost();
			return host.startsWith("www.") ? host.substring(4) : host;
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			LOGGER.error("bad uri, returning empty ");
		}
		return "";
	}
	
	// Probably not totally correct
	private static String getClientIp(HttpServletRequest httprequest) {
		String ipAddress = httprequest.getHeader("X-FORWARDED-FOR");
		if (ipAddress == null) {
			   ipAddress = httprequest.getRemoteAddr();
		}
		return ipAddress;
	}

	@ApiOperation(value = "get an ad", notes = "Gets an ad.")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "A deviceId", response = AdResponse.class),
			@ApiResponse(code = 404, message = "Not found"), @ApiResponse(code = 500, message = "Server side error") })
	@GetMapping("/ad")
	public DeferredResult<AdResponse> getAd(
			@RequestParam(value = "width", required = true) @DimensionConstraint int width,
			@RequestParam(value = "height", required = true) @DimensionConstraint int height,
			@RequestParam(value = "userid", required = true) @UserIdConstraint int userid,
			@RequestParam(value = "url", required = true) @URL String url,
			HttpServletRequest servletRequest) {
		LOGGER.info("getting ad");
		DeferredResult<AdResponse> dr = new DeferredResult<>(DEFERRED_TIMEOUT);
		// Going to need the useragent/ip
		String userAgent = servletRequest.getHeader(HttpHeaders.USER_AGENT);
        String ipAddr = getClientIp(servletRequest);



		AdRequest request = AdRequest.builder().width(width).height(height).userid(userid).domain(getDomainName(url)).userip(ipAddr).userAgent(userAgent).build();

		adService.getAd(request).subscribe(dr::setResult, dr::setErrorResult, () -> setNotFound(dr));
		return dr;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static void setNotFound(DeferredResult dr) {
		if (!dr.isSetOrExpired()) {
			dr.setResult(new ResponseEntity<>(HttpStatus.NOT_FOUND));
		}
	}
}
