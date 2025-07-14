package kr.api.link.cmmn.v2.service.support;

import kr.api.link.cmmn.v2.configurable.model.HttpResponseContext;
import kr.api.link.cmmn.v2.service.flow.isp.Convertable;

public interface ResponseConvertableChain extends Convertable <HttpResponseContext<String>,HttpResponseContext<String>> {
	
}