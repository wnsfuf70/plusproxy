package kr.api.link.cmmn.v2.service.support;

import kr.api.link.cmmn.v2.configurable.model.HttpRequestContext;
import kr.api.link.cmmn.v2.service.flow.isp.Convertable;

public interface RequestConvertableChain extends Convertable <HttpRequestContext<String>,HttpRequestContext<String>> {}
