package kr.api.link.cmmn.v2.app;

import java.util.Map;

import kr.api.link.cmmn.v2.configurable.model.ServiceConfig;
import kr.api.link.cmmn.v2.configurable.model.ServiceDataRegistry;
import kr.api.link.cmmn.v2.service.CommonService;

public enum GlobalPreference {

	SINGLETON;
	
	private static final ApplicationInfo applicationInfo = new ApplicationInfo();
	
	public static ApplicationInfo getApplicationInfo() {
		return GlobalPreference.applicationInfo;
	}
	
	public static String getServiceIdByPath(String path) {
		ApplicationInfo info = GlobalPreference.getApplicationInfo();
		Map<String, String> pathAndServiceMapping = info.getPathAndServiceMapping();
		String serviceId = pathAndServiceMapping.get(path);
		return serviceId;
	}

	public static String getMappingPathByServiceId(String serviceId) {
		ApplicationInfo info = GlobalPreference.getApplicationInfo();
		Map<String, ServiceConfig> serviceConfigMap = info.getServiceConfigMap();
		ServiceConfig serviceConfig = serviceConfigMap.get(serviceId);
		String path = serviceConfig.getPath();
		return path;
	}
	
	public static ServiceConfig getServiceConfigByServiceId(String serviceId) {
		ApplicationInfo info = GlobalPreference.getApplicationInfo();
		Map<String, ServiceConfig> serviceConfigMap = info.getServiceConfigMap();
		return serviceConfigMap.get(serviceId);
	}
	
	public static CommonService getCommonServiceByServiceId(String serviceId) {
		ApplicationInfo info = GlobalPreference.getApplicationInfo();
		Map<String, ? super CommonService> serviceImplementsMap = info.getServiceImplementsMap();
		CommonService commonService = (CommonService) serviceImplementsMap.get(serviceId);
		return commonService;
	}
	
	public static ServiceDataRegistry getServiceDataResitryByServiceId(String serviceId) {
		ApplicationInfo info = GlobalPreference.getApplicationInfo();
		Map<String, ServiceDataRegistry> serviceDataResitryMap = info.getServiceDataResitryMap();
		ServiceDataRegistry serviceDataResitry = (ServiceDataRegistry) serviceDataResitryMap.get(serviceId);
		return serviceDataResitry;
	}
}