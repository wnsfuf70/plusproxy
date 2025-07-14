package kr.api.link.cmmn.v2.service.component.convert.security;

import kr.api.link.cmmn.crpt_back.GPKIModule;

public class Test {

	public static void main(String[] args) throws Exception {
		
		String certId = "SVR1741845003";
		String envKeyPassword = "godakd03!";
		String sigKeyPassword = "godakd03!";
		String mountPath = "C:\\gpki\\";
		String copyPath = "C:\\gpki\\";
		
		GPKIModule module = GPKIModule.INSTANCE;
		module.initialize(mountPath,copyPath,certId,envKeyPassword,sigKeyPassword,true);
		
	}

}
