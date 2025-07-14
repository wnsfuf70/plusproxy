package kr.api.link.cmmn.v2.configurable.model;

import java.nio.ByteBuffer;

import org.springframework.http.MediaType;

import lombok.Data;

@Data
public class ServiceDataRegistry {
	
	Entry requestOrigin = new Entry();
	Entry requestVm = new Entry();
	Entry responseOrigin = new Entry();
	Entry responseVm = new Entry();
	
	/*
	ByteBuffer requestOrigin;
	ByteBuffer requestOriginSchema;
	
	ByteBuffer requestVm;
	ByteBuffer requestVmSchema;
	
	ByteBuffer responseOrigin;
	ByteBuffer responseOriginSchema;
	
	ByteBuffer responseVm;
	ByteBuffer responseVmSchema;
	*/
	
	@Data
	public static class Entry {
		ByteBuffer data;
		ByteBuffer schema;
		MediaType dataType;
		MediaType schemaType;
	}
	
}