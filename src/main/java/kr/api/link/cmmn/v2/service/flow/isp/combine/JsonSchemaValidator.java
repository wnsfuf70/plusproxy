package kr.api.link.cmmn.v2.service.flow.isp.combine;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion.VersionFlag;
import com.networknt.schema.ValidationMessage;

import kr.api.link.cmmn.excp.InvalidRequestException;
import kr.api.link.cmmn.excp.InvalidResponseException;
import kr.api.link.cmmn.v2.configurable.model.ConfigurableContext;
import kr.api.link.cmmn.v2.service.component.schema.JsonRequestSchemaValidator;
import kr.api.link.cmmn.v2.service.flow.isp.Validatable;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public abstract class JsonSchemaValidator<T extends ConfigurableContext> implements Validatable<T> {
	
	@Autowired
	ObjectMapper mapper;
	
	@Override
	public boolean validate(T ctx) throws Exception {
		
		JsonSchemaFactory factory = JsonSchemaFactory.getInstance(VersionFlag.V4);
		
		JsonNode validateParam = getBody(ctx);
		byte[] schemaByte = schemaByte(ctx);
		
		try(ByteArrayInputStream inputStream = new ByteArrayInputStream(schemaByte)){
			JsonSchema jsonSchema = factory.getSchema(inputStream);
			Set<ValidationMessage> error = jsonSchema.validate(validateParam);
			if (!error.isEmpty()) {
                List<ValidationErrorResponse> errorList = error.stream()
                        .map(ValidationErrorResponse::fromValidationMessage)
                        .collect(Collectors.toList());
                
                List<String> errorMessageList = new ArrayList<String>();
                errorList.forEach(err -> {
                		log.error("❌ Validation Error [Field: {}, Message: {}]", err.getField(), err.getMessage());
                		errorMessageList.add(err.getMessage());
                	}
                );
                
               // String errorJson = mapper.writeValueAsString(errorMessageList);
                JsonNode convertValue = mapper.convertValue(errorMessageList,JsonNode.class);
                String errorJson = convertValue.toPrettyString();
                if(this instanceof JsonRequestSchemaValidator) {
                	throw new InvalidRequestException(errorJson);
                }
                else {
                	throw new InvalidResponseException(errorJson);
                }
            }
		}
	    
	    return true;
	}
	
	public abstract JsonNode getBody(T context);
	public abstract byte[] schemaByte(T context) ;
	
	public static class ValidationErrorResponse {
	    private String field;
	    private String message;

	    public ValidationErrorResponse(String field, String message) {
	        this.field = field;
	        this.message = message;
	    }

	    public String getField() {
	        return field;
	    }

	    public String getMessage() {
	        return message;
	    }

	    public static ValidationErrorResponse fromValidationMessage(ValidationMessage msg) {
	        String[] parts = msg.getMessage().split(":");
	        String field = parts[0].trim();
	        String message = parts.length > 1 ? parts[1].trim() : "Validation error";
	        return new ValidationErrorResponse(field, message);
	    }
	}
	
}