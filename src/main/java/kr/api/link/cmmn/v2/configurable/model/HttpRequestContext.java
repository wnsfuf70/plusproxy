package kr.api.link.cmmn.v2.configurable.model;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;

import kr.api.link.cmmn.v2.configurable.ConfigurableInvoke;
import lombok.Getter;
import lombok.Setter;

@Getter
public class HttpRequestContext<T> extends ConfigurableContext {
	
	@Setter
	RequestEntity<T> entity;
	
	@Setter
	Map<String,Object> queryStringMap;
	
	HttpHeaders readOnlyOriginHeader;
	
	@Setter
	Map<String,String> contentHeaderMap;

	@Setter
	Map<String,String> headers;
	
	@Setter
	T body;
	
	@Setter
	HttpMethod httpMethod;
	
	@Setter
	String path;
	
	@Setter
	String queryString;
	
	public static <T> HttpRequestContext<T> generate(ConfigurableInvoke invoke) {
	    return generate(invoke, null, null);
	}
	
	public static <T> HttpRequestContext<T> generate(ConfigurableInvoke invoke, RequestEntity<T> entity , Map<String,Object> queryStringMap) {
		return new HttpRequestContext<T> (invoke, entity, queryStringMap);
	}

	public HttpRequestContext(ConfigurableInvoke invoke, RequestEntity<T> entity , Map<String,Object> queryStringMap) {
		
		super(invoke);
		
		this.entity = entity;
		this.queryStringMap = queryStringMap;
		
		this.headers = new LinkedHashMap<String, String>();
		this.contentHeaderMap = new LinkedHashMap<String, String>();
		
		if(entity==null) return;
		if(entity.getHeaders()!=null) {
			this.readOnlyOriginHeader = entity.getHeaders();
			this.readOnlyOriginHeader.toSingleValueMap().forEach((k,v)->{
				if(
					k.equalsIgnoreCase(HttpHeaders.CONTENT_DISPOSITION) ||
					k.equalsIgnoreCase(HttpHeaders.CONTENT_ENCODING) ||
					k.equalsIgnoreCase(HttpHeaders.CONTENT_LANGUAGE) ||
					k.equalsIgnoreCase(HttpHeaders.CONTENT_LENGTH) ||
					k.equalsIgnoreCase(HttpHeaders.CONTENT_LOCATION) ||
					k.equalsIgnoreCase(HttpHeaders.CONTENT_RANGE) ||
					k.equalsIgnoreCase(HttpHeaders.CONTENT_TYPE)
				) {
					contentHeaderMap.put(k,v);
				}
				else {
					headers.put(k,v);
				}
			});
		}
		
		this.httpMethod = entity.getMethod();

		this.body = entity.getBody();
		
		this.path = entity.getUrl().getPath();

		this.queryString = entity.getUrl().getQuery();
		
	}

	@SuppressWarnings("unchecked")
	public static <T> HttpRequestContext<T> getRealType(ConfigurableContext ctx) {
		if(ctx instanceof HttpRequestContext) {
			return (HttpRequestContext<T>)ctx;
		}
	    throw new TypeMismatchException(ctx, HttpRequestContext.class);
	}
	
}