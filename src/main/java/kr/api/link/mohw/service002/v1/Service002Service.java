package kr.api.link.mohw.service002.v1;

import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import kr.api.link.cmmn.v2.configurable.model.HttpRequestContext;
import kr.api.link.cmmn.v2.service.CommonService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Profile(Service002Configuration.SERVICE)
@Service(Service002Configuration.SERVICE + Service002Configuration.VERSION + "Service")
public class Service002Service implements CommonService {
	
	@Getter
	String name;
	
	public Service002Service() {
		this.name = "im service002 / service endpoint";
	}
	
	@Override
	public ResponseEntity<JsonNode> service(HttpRequestContext<JsonNode> requestEntity) throws Exception {
		ObjectNode objectNode = JsonNodeFactory.instance.objectNode();
		objectNode.put("data","service2");
		return ResponseEntity.ok(objectNode);
	}

}
