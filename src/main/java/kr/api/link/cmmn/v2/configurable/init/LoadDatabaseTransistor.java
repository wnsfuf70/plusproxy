package kr.api.link.cmmn.v2.configurable.init;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

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

@Component("LoadDatabaseTransistor")
public class LoadDatabaseTransistor implements LoadTransistor {

        @Autowired
        ObjectMapper mapper;

        @Override
        public void loadServiceConfig(ConfigurableEnvironment configurableEnvironment, ApplicationInfo applicationInfo)
                        throws Exception {

                String configJson = configurableEnvironment.getProperty("service.db.configs");
                ServiceConfig[] configs = null;

                if (configJson != null && !configJson.isBlank()) {
                        configs = mapper.readValue(configJson, ServiceConfig[].class);
                } else {
                        String[] activeProfiles = configurableEnvironment.getActiveProfiles();
                        configs = new ServiceConfig[activeProfiles.length];
                        for (int i = 0; i < activeProfiles.length; i++) {
                                ServiceConfig cfg = new ServiceConfig();
                                cfg.setServiceId(activeProfiles[i]);
                                cfg.setPath("/" + activeProfiles[i]);
                                cfg.setLogic(new ServiceConfig.Logic());
                                cfg.setConvert(new ServiceConfig.Convert());
                                cfg.setBackend(new ServiceConfig.Backend());
                                configs[i] = cfg;
                        }
                }

                for (ServiceConfig cfg : configs) {
                        if (cfg == null)
                                continue;
                        applicationInfo.getServiceConfigMap().put(cfg.getServiceId(), cfg);
                        applicationInfo.getPathAndServiceMapping().put(cfg.getPath(), cfg.getServiceId());
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
                        ByteBuffer reqOriginBuffer = Utils.getTempleByteBuffer(reqOrigin.getTempletePath(), loc);
                        ByteBuffer schemaReqByteBuffer = getSchemaByteBuffer(reqOriginBuffer, reqOriginType);
                        registry.getRequestOrigin().setData(reqOriginBuffer);
                        registry.getRequestOrigin().setSchema(schemaReqByteBuffer);
                        registry.getRequestOrigin().setDataType(reqOriginType);
                        registry.getRequestOrigin().setSchemaType(reqOriginType);

                        if (ServiceRunType.BYPASS_PROXY.compareTo(serviceRunType) == 0) {
                                registry.getRequestVm().setData(reqOriginBuffer);
                                registry.getRequestVm().setSchema(schemaReqByteBuffer);
                                registry.getRequestVm().setDataType(reqOriginType);
                                registry.getRequestVm().setSchemaType(reqOriginType);
                        } else {
                                ByteBuffer reqVmBuffer = Utils.getTempleByteBuffer(reqVm.getTempletePath(), loc);
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
                        ByteBuffer resVmBuffer = Utils.getTempleByteBuffer(resVm.getTempletePath(), loc);
                        ByteBuffer schemaResVmByteBuffer = getSchemaByteBuffer(resVmBuffer, resVmType);
                        registry.getResponseVm().setData(resVmBuffer);
                        registry.getResponseVm().setSchema(schemaResVmByteBuffer);
                        registry.getResponseVm().setDataType(resVmType);
                        registry.getResponseVm().setSchemaType(resVmType);

                        if (ServiceRunType.BYPASS_PROXY.compareTo(serviceRunType) == 0) {
                                registry.getResponseOrigin().setData(resVmBuffer);
                                registry.getResponseOrigin().setSchema(schemaResVmByteBuffer);
                                registry.getResponseOrigin().setDataType(resVmType);
                                registry.getResponseOrigin().setSchemaType(resVmType);
                        } else {
                                ByteBuffer resOriginBuffer = Utils.getTempleByteBuffer(resOrigin.getTempletePath(), loc);
                                ByteBuffer schemaResByteBuffer = getSchemaByteBuffer(resOriginBuffer, resOriginType);
                                registry.getResponseOrigin().setData(resOriginBuffer);
                                registry.getResponseOrigin().setSchema(schemaResByteBuffer);
                                registry.getResponseOrigin().setDataType(resOriginType);
                                registry.getResponseOrigin().setSchemaType(resOriginType);
                        }
                }

                configurableInvoke.setServiceDataRegistry(registry);
        }

        private ByteBuffer getSchemaByteBuffer(ByteBuffer source, MediaType type) throws IOException {

                byte[] byteBufferAsReadOnly = Utils.getByteBufferAsReadOnly(source);
                ByteBuffer schemaBuffer = null;
                if (MediaType.APPLICATION_XML.isCompatibleWith(type)) {
                        byte[] schemaValue = Utils.Xml.schemaProfile(new InputStreamReader(new ByteArrayInputStream(byteBufferAsReadOnly)));
                        schemaBuffer = Utils.newByteBuffer(schemaValue);
                } else if (MediaType.APPLICATION_JSON.isCompatibleWith(type)) {
                        JsonNode tree = mapper.readTree(byteBufferAsReadOnly, 0, byteBufferAsReadOnly.length);
                        Map<String, Object> schemaProfile = Utils.Json.schemaProfile(tree);
                        byte[] schemaValue = mapper.writeValueAsBytes(schemaProfile);
                        schemaBuffer = Utils.newByteBuffer(schemaValue);
                }

                return schemaBuffer;
        }

}
