package org.parking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.mvc.ProxyExchange;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.parking.service.ProxyService;

@RestController
@Slf4j
public class GatewayController {

	private static final String RECEIVED_REQUEST_MSG = "received {} request: {}?{}";
	
	@Autowired
	ProxyService proxyService;
	
	@GetMapping("/**")
	ResponseEntity<byte[]> getRequests(ProxyExchange<byte[]> proxy, HttpServletRequest request) {
		log.debug(RECEIVED_REQUEST_MSG, request.getMethod(), request.getRequestURL(), request.getQueryString());
		return proxyService.proxyRouting(proxy, request, HttpMethod.GET);
	}
	
	@PutMapping("/**")
	ResponseEntity<byte[]> putRequests(ProxyExchange<byte[]> proxy, HttpServletRequest request) {
		log.debug(RECEIVED_REQUEST_MSG, request.getMethod(), request.getRequestURL(), request.getQueryString());
		return proxyService.proxyRouting(proxy, request, HttpMethod.PUT);
	}
	
}
