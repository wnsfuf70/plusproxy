package kr.api.link.cmmn.v2.configurable.model;

import kr.api.link.cmmn.v2.configurable.ConfigurableInvoke;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public abstract class ConfigurableContext {

	public ConfigurableInvoke invoke;
	
	public ConfigurableContext(ConfigurableInvoke invoke) {
		this.invoke = invoke;
	}

}