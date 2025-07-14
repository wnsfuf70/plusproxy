package kr.api.link.cmmn.v2.service.component.convert;

import java.nio.ByteBuffer;

import org.apache.velocity.VelocityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import kr.api.link.cmmn.v2.configurable.ConfigurableInvoke;
import kr.api.link.cmmn.v2.configurable.model.ConfigurableContext;
import kr.api.link.cmmn.v2.configurable.model.HttpRequestContext;
import kr.api.link.cmmn.v2.configurable.model.ServiceDataRegistry;
import kr.api.link.cmmn.v2.service.component.convert.impl.VelocityContextToVmRender;
import kr.api.link.cmmn.v2.service.component.convert.type.RequestContextToVelocityContext;
import kr.api.link.cmmn.v2.service.support.RequestConvertableChain;
import kr.api.link.cmmn.v2.service.support.util.Utils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class VelocityRequestConverter implements RequestConvertableChain {

	@Autowired
	VelocityContextToVmRender render;
	
	@Autowired
	RequestContextToVelocityContext contextGen;

	@Override
	public HttpRequestContext<String> transform(ConfigurableContext ctx, HttpRequestContext<String> originSource)
			throws Exception {
		
		ConfigurableInvoke invoke = originSource.getInvoke();
		
		// class path 추가해야함
		ServiceDataRegistry registry = invoke.getServiceDataRegistry();
		
		ByteBuffer requestVm = registry.getRequestVm().getData();
		byte[] byteBufferAsReadOnly = Utils.getByteBufferAsReadOnly(requestVm);
		
		VelocityContext velocityContext = contextGen.convertType(originSource);
		
		String renderRequest = render.toRender(velocityContext,byteBufferAsReadOnly);
		log.debug("renderRequest : {}" , renderRequest);
		originSource.setBody(renderRequest);
		
		return originSource;
	
	}
	
}