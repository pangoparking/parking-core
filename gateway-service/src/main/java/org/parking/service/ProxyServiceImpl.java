package org.parking.service;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.mvc.ProxyExchange;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProxyServiceImpl implements ProxyService {

	private static final String ALL_URLS_MSG = "configuration with all URL's (name -> service connections) {}";
	private static final String ROUTED_URI_MSG = "routedURI: {}";
	private static final String FIRST_ITEM_MSG = "first item of URN is '{}'";
	private static final String SERVICE_URL_NOT_EXIST_MSG = "serviceURL does not exist for URN: %s";
	private static final String SERVICE_URL_MSG = "serviceURL for URN: {} is '{}'";
	private static final String URN_MSG = "URN is '{}'";
	private static final String NOT_SUPPORT_METHOD_MSG = "given proxy implementation does not support HTTP method %s";

	@Value("${app.routed.urls}")
	List<String> urls;

	Map<String, String> mapOfUrls; // key - first item of URN, value - routed service URL

	@Override
	public ResponseEntity<byte[]> proxyRouting(ProxyExchange<byte[]> proxy, HttpServletRequest request,
			HttpMethod method) {
		String receivedURN = getURN(request);
		String serviceUrl = getServiceURL(receivedURN);
		String routedURI = serviceUrl + receivedURN;
		String name = method.name();
		return switch (name) {
			case "GET" -> getRouting(proxy, request, routedURI);
			case "PUT" -> putRouting(proxy, request, routedURI);
			default -> ResponseEntity.status(404)
					.body(String.format(NOT_SUPPORT_METHOD_MSG, name).getBytes());
			};
	}

	private String getURN(HttpServletRequest request) {
		String receivedURN = request.getRequestURI();
		log.debug(URN_MSG, receivedURN);
		return receivedURN;
	}

	private String getServiceURL(String receivedURN) {
		String firstURNItem = getFirstURNItem(receivedURN);
		String serviceURL = mapOfUrls.get(firstURNItem);
		log.debug(SERVICE_URL_MSG, receivedURN, serviceURL);
		if (serviceURL == null) {
			String str = String.format(SERVICE_URL_NOT_EXIST_MSG, receivedURN);
			log.error(str);
			throw new NoSuchElementException(str);
		}
		return serviceURL;
	}

	private String getFirstURNItem(String receivedURI) {
		String firstUrnItem = receivedURI.split("/+")[1];
		log.debug(FIRST_ITEM_MSG, firstUrnItem);
		return firstUrnItem;
	}

	private ResponseEntity<byte[]> getRouting(ProxyExchange<byte[]> proxy, HttpServletRequest request, String routedURI) {
		String queryString = request.getQueryString();
		routedURI = String.format("%s%s", routedURI,
				queryString != null ? "?" + queryString : "");
		log.debug(ROUTED_URI_MSG, routedURI);
		return proxy.uri(routedURI).get();
	}

	private ResponseEntity<byte[]> putRouting(ProxyExchange<byte[]> proxy, HttpServletRequest request, String routedURI) {
		log.debug(ROUTED_URI_MSG, routedURI);
		return proxy.uri(routedURI).put();
	}

	@PostConstruct
	void createMapOfUrls() {
		log.info(ALL_URLS_MSG, urls);
		mapOfUrls = urls.stream().collect(Collectors.toMap(url -> url.split("->")[0], url -> url.split("->")[1]));
	}


}
