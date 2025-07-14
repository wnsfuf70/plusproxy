package kr.api.link.cmmn.v2.service.flow.isp.model;

import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.databind.JsonNode;

import kr.api.link.cmmn.v2.configurable.model.HttpResponseContext;

public class HttpFacadeInterfaceHolder extends FacadeInterfaceHolder<RequestEntity<JsonNode>, RequestEntity<String>, ResponseEntity<String>, RestClientException, HttpResponseContext<JsonNode>> {}