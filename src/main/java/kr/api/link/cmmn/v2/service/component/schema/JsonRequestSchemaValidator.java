package kr.api.link.cmmn.v2.service.component.schema;

import java.nio.ByteBuffer;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;

import kr.api.link.cmmn.v2.configurable.model.HttpRequestContext;
import kr.api.link.cmmn.v2.configurable.model.ServiceDataRegistry.Entry;
import kr.api.link.cmmn.v2.service.flow.isp.combine.JsonSchemaValidator;
import kr.api.link.cmmn.v2.service.support.util.Utils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JsonRequestSchemaValidator extends JsonSchemaValidator<HttpRequestContext<JsonNode>> {

	@Override
	public JsonNode getBody(HttpRequestContext<JsonNode> context) {
		return context.getBody();
	}

	@Override
	public byte[] schemaByte(HttpRequestContext<JsonNode> context) {
		
		Entry requestOrigin = context.getInvoke().getServiceDataRegistry().getRequestOrigin();
		ByteBuffer schema = requestOrigin.getSchema();
		byte[] byteBufferAsReadOnly = Utils.getByteBufferAsReadOnly(schema);
		
		String string = new String(byteBufferAsReadOnly);
		log.debug("### request valid : {}",string);
		
		return byteBufferAsReadOnly;
	}

}