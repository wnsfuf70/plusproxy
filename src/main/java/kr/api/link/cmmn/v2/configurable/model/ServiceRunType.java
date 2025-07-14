package kr.api.link.cmmn.v2.configurable.model;

public enum ServiceRunType {
	
	//들어온 요청 그대로 넘김
	BYPASS_PROXY("bypass"),
	
	//모든 연계 http 설정 자동화
	HTTP_ALL_CONFIG("config"),
	
	//메세지 변환만 커스텀
	HTTP_MESSAGE_CUSTOM("message_custom"),
	
	//전체 커스텀 (기존 서비스별 서비스구현 방식)
	ENDPOINT("endpoint");
	
	String name;
	
	private ServiceRunType(String name) {
		this.name = name;
	}
	
	public static ServiceRunType getCompetitionType(String type) {
		
		if("bypass".equalsIgnoreCase(type) || "proxy".equalsIgnoreCase(type)) {
			return BYPASS_PROXY;
		}
		else if("config".equalsIgnoreCase(type) || "httpAllConfig".equalsIgnoreCase(type)) {
			return HTTP_ALL_CONFIG;
		}
		else if("message_custom".equalsIgnoreCase(type) || "httpMessageCustom".equalsIgnoreCase(type)) {
			return HTTP_MESSAGE_CUSTOM;
		}
		else if("endpoint".equalsIgnoreCase(type) || "source".equalsIgnoreCase(type)) {
			return ENDPOINT;
		}
		else {
			return defaultType();
		}
		
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public static ServiceRunType defaultType() {
		return ServiceRunType.HTTP_ALL_CONFIG;
	}
	
}