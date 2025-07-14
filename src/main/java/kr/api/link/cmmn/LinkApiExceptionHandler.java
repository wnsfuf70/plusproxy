package kr.api.link.cmmn;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import kr.api.link.cmmn.excp.DifferentDataKeyException;
import kr.api.link.cmmn.excp.InvalidRequestException;
import kr.api.link.cmmn.excp.InvalidResponseException;
import kr.api.link.cmmn.excp.RequestEncryptException;
import kr.api.link.cmmn.excp.RequestTransformException;
import kr.api.link.cmmn.excp.ResponseDecryptException;
import kr.api.link.cmmn.excp.ResponseTransformException;
import kr.api.link.cmmn.excp.TargetApiException;
import kr.api.link.cmmn.excp.TargetServiceException;
import kr.api.link.cmmn.excp.UndefinedServerException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class LinkApiExceptionHandler {
    
    // 요청 바디(json) 검증 예외
    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<Object> handleInvalidRequestException(InvalidRequestException e, WebRequest request) {
    	return new ResponseEntity<>(errorBody("Request Body(json) Invalid Error", e), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    // 응답 바디(json) 검증 예외
    @ExceptionHandler(InvalidResponseException.class)
    public ResponseEntity<Object> handleInvalidResponseException(InvalidResponseException e, WebRequest request) {
    	return new ResponseEntity<>(errorBody("Response Body(json) Invalid Error", e), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // 요청 변환 예외
    @ExceptionHandler(RequestTransformException.class)
    public ResponseEntity<Object> handleRequestTransformException(RequestTransformException e, WebRequest request) {
    	return new ResponseEntity<>(errorBody("Request Transform Error", e), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    // 응답 변환 예외
    @ExceptionHandler(ResponseTransformException.class)
    public ResponseEntity<Object> handleResponseTransformException(ResponseTransformException e, WebRequest request) {
    	return new ResponseEntity<>(errorBody("Response Transform Error", e), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    // 요청 암호화 예외
    @ExceptionHandler(RequestEncryptException.class)
    public ResponseEntity<Object> handleRequestEncryptException(RequestEncryptException e, WebRequest request) {
    	return new ResponseEntity<>(errorBody("Request Encrypt Error", e), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // 응답 복호화 예외
    @ExceptionHandler(ResponseDecryptException.class)
    public ResponseEntity<Object> handleResponseDecryptException(ResponseDecryptException e, WebRequest request) {
    	return new ResponseEntity<>(errorBody("Response Decrypt Error", e), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    // 제공기관 API 호출 예외
    @ExceptionHandler(TargetApiException.class)
    public ResponseEntity<Object> handleTargetApiException(TargetApiException e, WebRequest request) {
    	return new ResponseEntity<>(errorBody("Target API Error", e), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    // 정의된 제공기관 서비스 예외
    @ExceptionHandler(TargetServiceException.class)
    public ResponseEntity<Object> definedServiceException(TargetServiceException e, WebRequest request) {
    	return ResponseEntity.status(e.status).body(errorBody("Target Service Error", e));
    }
    
    // 데이터 키 값 불일치 예외
    @ExceptionHandler(DifferentDataKeyException.class)
    public ResponseEntity<Object> handleDifferentDataKeyException(DifferentDataKeyException e, WebRequest request) {
    	return new ResponseEntity<>(errorBody("Different Data Key Error", e), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Invalid JSON format",
                        "details", "MessageNotReadable",
                        "timestamp", LocalDateTime.now()
        ));
    }
    
    // 그 외 정의되지않은 예외
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleUndefinedServerException(Exception e, WebRequest request) {
        return new ResponseEntity<>(errorBody("Undefined Server Error", new UndefinedServerException(e)), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    private Map<String, Object> errorBody(String message, Exception e) {
    	log.error("========== " + message + " ==========", e);
    	Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("errorMsg", message);
        body.put("details", e.getLocalizedMessage());
        return body;
	}
}
