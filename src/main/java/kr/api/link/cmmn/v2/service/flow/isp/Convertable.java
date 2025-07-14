package kr.api.link.cmmn.v2.service.flow.isp;

import kr.api.link.cmmn.v2.configurable.model.ConfigurableContext;

public interface Convertable <I,O> {
	
	public abstract O transform(ConfigurableContext ctx, I originSource) throws Exception;

}