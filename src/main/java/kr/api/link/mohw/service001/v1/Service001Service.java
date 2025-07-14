package kr.api.link.mohw.service001.v1;

import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import kr.api.link.cmmn.v2.configurable.model.HttpRequestContext;
import kr.api.link.cmmn.v2.configurable.model.ServiceConfig;
import kr.api.link.cmmn.v2.service.CommonService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Profile(Service001Configuration.SERVICE)
@Service(Service001Configuration.SERVICE + Service001Configuration.VERSION + "Service")
public class Service001Service implements CommonService {
	
	@Getter
	String name;
	
	public Service001Service() {
		this.name = "im service001 / service endpoint";
	}
	
	@Override
	public ResponseEntity<JsonNode> service(HttpRequestContext<JsonNode> requestEntity) throws Exception {
		
		ObjectNode objectNode = JsonNodeFactory.instance.objectNode();
		objectNode.put("data","3");
		
		ServiceConfig serviceConfig = requestEntity.getInvoke().getServiceConfig();
		log.debug("{}",serviceConfig);
		
		return ResponseEntity.ok(objectNode);
	}

}
