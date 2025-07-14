package kr.api.link.cmmn.v2.service.support.util.test;

import org.slf4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import kr.api.link.cmmn.v2.configurable.model.ServiceConfig;

public class ServiceConfigTracer {

    private static final ObjectMapper YAML_MAPPER = new ObjectMapper(new YAMLFactory());

    /**
     * ServiceConfig를 YAML 포맷으로 출력합니다.
     * @param config ServiceConfig 인스턴스
     * @param log SLF4J Logger (보통 lombok.extern.slf4j.Slf4j 사용)
     */
    public static void trace(ServiceConfig config, Logger log) {
        if (!log.isDebugEnabled()) return;

        try {
            String yaml = YAML_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(config);
            log.debug("\n🧾 [ServiceConfig YAML Trace]\n{}", yaml);
        } catch (Exception e) {
            log.warn("Failed to trace ServiceConfig", e);
        }
    }
    
}