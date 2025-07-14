package kr.api.link.cmmn.v2.configurable.init;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.AbstractResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import kr.api.link.cmmn.v2.app.ApplicationInfo;
import kr.api.link.cmmn.v2.app.ResourceLocation;
import kr.api.link.cmmn.v2.configurable.ConfigurableInvoke;
import kr.api.link.cmmn.v2.configurable.model.ServiceConfig;
import kr.api.link.cmmn.v2.configurable.model.ServiceConfig.Convert;
import kr.api.link.cmmn.v2.configurable.model.ServiceConfig.Convert.Templete;
import kr.api.link.cmmn.v2.configurable.model.ServiceConfig.Logic;
import kr.api.link.cmmn.v2.configurable.model.ServiceDataRegistry;
import kr.api.link.cmmn.v2.configurable.model.ServiceRunType;
import kr.api.link.cmmn.v2.service.support.util.Utils;
import kr.api.link.cmmn.v2.service.support.util.test.ServiceConfigTracer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("PropertiesApiLoader")
public class LoadPropertiesTransistor implements LoadTransistor {
	
	@Autowired
	ObjectMapper mapper;
	
	@Autowired
	VelocityEngine engine;
	
	@SuppressWarnings("unchecked")
	public void loadServiceConfig(ConfigurableEnvironment configurableEnvironment, ApplicationInfo applicationInfo) throws Exception {
		
		String[] activeProfiles = configurableEnvironment.getActiveProfiles();
		
		String type = configurableEnvironment.getProperty("service.external.mount.path.type");
		String mountPath = configurableEnvironment.getProperty("service.external.mount.path.value");
		
		AbstractResource resource = null;
		for(String activeProfile : activeProfiles) {
			
			Yaml yaml = new Yaml();
			File file = null;
			
			try {
				if("classpath".equalsIgnoreCase(type)) {
					resource = new ClassPathResource(mountPath+"/"+activeProfile+".yml");
					file = resource.getFile();
				}
				else {
					file = new File(mountPath,activeProfile+".yml");
				}
				
				log.debug("load file : {}",file.getAbsolutePath());
			} 
			catch (IOException e) {
				throw e;
			}
			
			try {
				
				Map<String,Object> serviceYml = new ConcurrentHashMap<String, Object>();
				Object yamlOb = yaml.load(new FileReader(file));
				
				ServiceConfig serviceConfig = null;
				
				if(yamlOb!=null) {
					serviceYml = (Map<String,Object>)yamlOb;
					//propertiesServiceConfig = mapper.convertValue(serviceYml,PropertiesServiceConfig.class);
					// update...
					{
						serviceConfig = mapper.convertValue(serviceYml,ServiceConfig.class);
						serviceConfig.setServiceId(activeProfile);
					}
					
				}
				
				ServiceConfigTracer.trace(serviceConfig, log);
				
				String path = serviceConfig.getPath();
				applicationInfo.getPathAndServiceMapping().put(path,activeProfile);
				String mockPath = path.endsWith("/") ? path+"mock" : path+"/mock" ;
				String mockPath1 = path.endsWith("/") ? path+"mock" : path+"/mock1" ;
				
				applicationInfo.getPathAndServiceMapping().put(mockPath,activeProfile);
				applicationInfo.getPathAndServiceMapping().put(mockPath1,activeProfile);
				
				applicationInfo.getServiceConfigMap().put(activeProfile,serviceConfig);

			}
			catch (FileNotFoundException e) {
				throw new IllegalStateException(e);
			}
			
		}
		
	}
	
	@Override
	public void loadServiceDataResitry(ConfigurableInvoke configurableInvoke) throws Exception {
		
		ServiceConfig serviceConfig = configurableInvoke.getServiceConfig();
		
		Logic logic = serviceConfig.getLogic();
		ServiceRunType serviceRunType = logic.getServiceRunType();
		
		Convert convert = serviceConfig.getConvert();
		
		ResourceLocation loc = convert.getResourceLocation();
		
		Templete reqOrigin = convert.getReqOrigin();
		MediaType reqOriginType = reqOrigin.getMediaType();
		
		Templete reqVm = convert.getReqVm();
		MediaType reqVmType = reqVm.getMediaType();
		
		Templete resOrigin = convert.getResOrigin();
		MediaType resOriginType = resOrigin.getMediaType();
		
		Templete resVm = convert.getResVm();
		MediaType resVmType = resVm.getMediaType();
		
		ServiceDataRegistry registry = new ServiceDataRegistry();
		
		{
			ByteBuffer reqOriginBuffer = Utils.getTempleByteBuffer(reqOrigin.getTempletePath(),loc);
			ByteBuffer schemaReqByteBuffer = getSchemaByteBuffer(reqOriginBuffer, reqOriginType);
			registry.getRequestOrigin().setData(reqOriginBuffer);
			registry.getRequestOrigin().setSchema(schemaReqByteBuffer);
			registry.getRequestOrigin().setDataType(reqOriginType);
			registry.getRequestOrigin().setSchemaType(reqOriginType);
			
			if(ServiceRunType.BYPASS_PROXY.compareTo(serviceRunType)==0){
				registry.getRequestVm().setData(reqOriginBuffer);
				registry.getRequestVm().setSchema(schemaReqByteBuffer);
				registry.getRequestVm().setDataType(reqOriginType);
				registry.getRequestVm().setSchemaType(reqOriginType);
			}
			else {
				ByteBuffer reqVmBuffer = Utils.getTempleByteBuffer(reqVm.getTempletePath(),loc);
				ByteBuffer schemaReqVmByteBuffer = getSchemaByteBuffer(reqVmBuffer, reqVmType);
				{
				registry.getRequestVm().setData(reqVmBuffer);
				registry.getRequestVm().setSchema(schemaReqVmByteBuffer);
				registry.getRequestVm().setDataType(reqVmType);
				registry.getRequestVm().setSchemaType(reqVmType);
				}
			}
		}
		
		{
			ByteBuffer resVmBuffer = Utils.getTempleByteBuffer(resVm.getTempletePath(),loc);
			ByteBuffer schemaResVmByteBuffer = getSchemaByteBuffer(resVmBuffer, resVmType);
			registry.getResponseVm().setData(resVmBuffer);
			registry.getResponseVm().setSchema(schemaResVmByteBuffer);
			registry.getResponseVm().setDataType(resVmType);
			registry.getResponseVm().setSchemaType(resVmType);
			
			if(ServiceRunType.BYPASS_PROXY.compareTo(serviceRunType)==0){
				registry.getResponseOrigin().setData(resVmBuffer);
				registry.getResponseOrigin().setSchema(schemaResVmByteBuffer);
				registry.getResponseOrigin().setDataType(resVmType);
				registry.getResponseOrigin().setSchemaType(resVmType);
			}
			else {
				ByteBuffer resOriginBuffer = Utils.getTempleByteBuffer(resOrigin.getTempletePath(),loc);
				ByteBuffer schemaResByteBuffer = getSchemaByteBuffer(resOriginBuffer, resOriginType);
				registry.getResponseOrigin().setData(resOriginBuffer);
				registry.getResponseOrigin().setSchema(schemaResByteBuffer);
				registry.getResponseOrigin().setDataType(resOriginType);
				registry.getResponseOrigin().setSchemaType(resOriginType);
			}
		}
		
		configurableInvoke.setServiceDataRegistry(registry);
	}
	
	public ByteBuffer getSchemaByteBuffer(ByteBuffer source , MediaType type) throws IOException {
		
		byte[] byteBufferAsReadOnly = Utils.getByteBufferAsReadOnly(source);
		ByteBuffer schemaBuffer = null;
		if(MediaType.APPLICATION_XML.isCompatibleWith(type)) {
			byte[] schemaValue = Utils.Xml.schemaProfile(new InputStreamReader(new ByteArrayInputStream(byteBufferAsReadOnly)));
			schemaBuffer = Utils.newByteBuffer(schemaValue);
		}
		else if(MediaType.APPLICATION_JSON.isCompatibleWith(type)) {
			JsonNode tree = mapper.readTree(byteBufferAsReadOnly,0,byteBufferAsReadOnly.length);
			Map<String, Object> schemaProfile = Utils.Json.schemaProfile(tree);
			byte[] schemaValue = mapper.writeValueAsBytes(schemaProfile);
			schemaBuffer = Utils.newByteBuffer(schemaValue);
		}
		
		return schemaBuffer;
	}
	
}