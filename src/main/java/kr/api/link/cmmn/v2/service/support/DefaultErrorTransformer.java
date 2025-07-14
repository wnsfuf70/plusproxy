package kr.api.link.cmmn.v2.service.support;

import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import kr.api.link.cmmn.v2.service.flow.isp.ErrorTransformable;

public interface DefaultErrorTransformer extends ErrorTransformable<RequestEntity<String>, ResponseEntity<String>> {}