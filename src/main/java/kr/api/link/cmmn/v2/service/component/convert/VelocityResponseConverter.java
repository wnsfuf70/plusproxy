package kr.api.link.cmmn.v2.service.component.convert;

import java.nio.ByteBuffer;

import org.apache.velocity.VelocityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import kr.api.link.cmmn.v2.configurable.ConfigurableInvoke;
import kr.api.link.cmmn.v2.configurable.model.ConfigurableContext;
import kr.api.link.cmmn.v2.configurable.model.HttpResponseContext;
import kr.api.link.cmmn.v2.configurable.model.ServiceDataRegistry;
import kr.api.link.cmmn.v2.service.component.convert.impl.VelocityContextToVmRender;
import kr.api.link.cmmn.v2.service.component.convert.type.ResponseEntityToVelocityContext;
import kr.api.link.cmmn.v2.service.support.ResponseConvertableChain;
import kr.api.link.cmmn.v2.service.support.util.Utils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class VelocityResponseConverter implements ResponseConvertableChain {
	
	@Autowired
	VelocityContextToVmRender render;
	
	@Autowired
	ResponseEntityToVelocityContext contextGen;
	
	@Override
	public HttpResponseContext<String> transform(ConfigurableContext ctx, HttpResponseContext<String> remoteResponse) throws Exception {

		log.debug("### transformResponseBody ");
		
		ConfigurableInvoke invoke = remoteResponse.getInvoke();

		ServiceDataRegistry registry = invoke.getServiceDataRegistry();
		
		ByteBuffer requestVm = registry.getResponseVm().getData();
		byte[] byteBufferAsReadOnly = Utils.getByteBufferAsReadOnly(requestVm);
		
		VelocityContext velocityContext = contextGen.convertType(remoteResponse);
		
		String renderResponse = render.toRender(velocityContext, byteBufferAsReadOnly);
		remoteResponse.setBody(renderResponse);
		
		log.debug("remoteResponse : {}", remoteResponse.getBody());
		
		return remoteResponse;
	}

}