package kr.api.link.cmmn.v2.service.component.schema;

import java.nio.ByteBuffer;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;

import kr.api.link.cmmn.v2.configurable.model.HttpResponseContext;
import kr.api.link.cmmn.v2.configurable.model.ServiceDataRegistry.Entry;
import kr.api.link.cmmn.v2.service.flow.isp.combine.JsonSchemaValidator;
import kr.api.link.cmmn.v2.service.support.util.Utils;

@Component
public class JsonResponseSchemaValidator extends JsonSchemaValidator<HttpResponseContext<JsonNode>> {

	@Override
	public JsonNode getBody(HttpResponseContext<JsonNode> context) {
		return context.getBody();
	}

	@Override
	public byte[] schemaByte(HttpResponseContext<JsonNode> context) {
		Entry requestOrigin = context.getInvoke().getServiceDataRegistry().getResponseVm();
		ByteBuffer schema = requestOrigin.getSchema();
		byte[] byteBufferAsReadOnly = Utils.getByteBufferAsReadOnly(schema);
		return byteBufferAsReadOnly;
	}
}