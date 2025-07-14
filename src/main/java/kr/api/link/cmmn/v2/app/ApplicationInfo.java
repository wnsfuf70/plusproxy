package kr.api.link.cmmn.v2.app;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import kr.api.link.cmmn.v2.configurable.model.ServiceConfig;
import kr.api.link.cmmn.v2.configurable.model.ServiceDataRegistry;
import kr.api.link.cmmn.v2.service.CommonService;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public final class ApplicationInfo {
	
	public volatile String applicationName;
	
	//경로(path) : 서비스아이디 (active profile name)
	public final Map<String,String> pathAndServiceMapping =  new ConcurrentHashMap<String,String>();
	
	//서비스아이디 (active profile name) : CommonService..  implements class....
	public final Map<String,? super CommonService> serviceImplementsMap = new ConcurrentHashMap<String,CommonService>();
	
	// 서비스아이디 (active profile name) : 서비스설정 (ServiceConfig)
	public final Map<String,ServiceConfig> serviceConfigMap = new ConcurrentHashMap<String, ServiceConfig>();
	
	// 서비스아이디 (active profile name) : 서비스스키마등 vm 파일 (ServiceDataResitry)
	public final Map<String,ServiceDataRegistry> serviceDataResitryMap= new ConcurrentHashMap<String, ServiceDataRegistry>();

}