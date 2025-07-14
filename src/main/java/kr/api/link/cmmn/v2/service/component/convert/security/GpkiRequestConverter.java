package kr.api.link.cmmn.v2.service.component.convert.security;

import org.springframework.beans.factory.annotation.Autowired;

import kr.api.link.cmmn.crpt_back.SecurityCrypto;
import kr.api.link.cmmn.v2.configurable.model.ConfigurableContext;
import kr.api.link.cmmn.v2.configurable.model.HttpRequestContext;
import kr.api.link.cmmn.v2.service.support.RequestConvertableChain;

public class GpkiRequestConverter implements RequestConvertableChain {

	@Autowired
	SecurityCrypto crypto;
	
	@Override
	public HttpRequestContext<String> transform(ConfigurableContext ctx, HttpRequestContext<String> originSource)
			throws Exception {
		
		
		
		return null;
	}
	
}
