package kr.api.link.cmmn.v2.service.component.convert.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class VelocityContextToVmRender {
	
	@Autowired
	VelocityEngine engine;
	
	public String toRender (VelocityContext context , byte[] templete) throws IOException {
		
		StringWriter writer = new StringWriter();
		
		try (
			ByteArrayInputStream bais = new ByteArrayInputStream(templete);
			InputStreamReader isr = new InputStreamReader(bais)){
			log.debug("{}",ToStringBuilder.reflectionToString(context));
			engine.evaluate(context, writer,"[render]",isr);
		}
		catch (IOException e) {
			throw e;
		}
		
		return writer.toString();
	}
	
}