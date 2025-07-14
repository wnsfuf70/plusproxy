package kr.api.link.cmmn.v2.configurable.model;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import kr.api.link.cmmn.v2.configurable.ConfigurableInvoke;
import kr.api.link.cmmn.v2.service.support.util.Utils;
import lombok.Getter;
import lombok.Setter;

@Getter
public class HttpResponseContext<T> extends ConfigurableContext {
	
	@Setter
	ResponseEntity<T> entity;

	HttpHeaders readOnlyOriginHeader;
	
	@Setter
	Map<String,String> contentHeaderMap;
	
	@Setter
	Map<String,String> responseHeaders;
	
	@Setter
	T body;
	
	public static <T> HttpResponseContext<T> generate(ConfigurableInvoke invoke) {
		return generate(invoke,null);
	}
	
	public static <T> HttpResponseContext<T> generate(ConfigurableInvoke invoke, ResponseEntity<T> entity) {
		return new HttpResponseContext<T> (invoke,entity);
	}

	public HttpResponseContext(ConfigurableInvoke invoke, ResponseEntity<T> entity) {
		
		super(invoke);
		
		this.invoke = invoke;
		
		this.entity = entity;
		
		this.contentHeaderMap = new LinkedHashMap<String, String>();
		
		this.responseHeaders = new LinkedHashMap<String, String>();
		
		if(entity!=null) {
			
			if(entity.getHeaders()!=null) {
				this.readOnlyOriginHeader = entity.getHeaders();
				
				this.readOnlyOriginHeader.toSingleValueMap().forEach((k,v)->{
					if(!Utils.Http.isContentHeadersKey(k)) {
						responseHeaders.put(k,v);
					}
					else {
						contentHeaderMap.put(k,v);
					}
				});
			}
			
			this.body = entity.getBody();
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public static <T> HttpResponseContext<T> getRealType(ConfigurableContext ctx) {
		if(ctx instanceof HttpResponseContext) {
			return (HttpResponseContext<T>)ctx;
		}
	    throw new TypeMismatchException(ctx, HttpResponseContext.class);
	}
	
}