package kr.api.link.cmmn.v2.service.support.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.core.io.AbstractResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.RequestEntity.BodyBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.fasterxml.jackson.databind.node.ValueNode;

import kr.api.link.cmmn.v2.app.ResourceLocation;
import kr.api.link.cmmn.v2.service.support.util.xml.SaxSeparatedHandler;
import kr.api.link.cmmn.v2.service.support.util.xml.XsdConfig;
import kr.api.link.cmmn.v2.service.support.util.xml.XsdGen;
import nu.xom.ParsingException;

public class Utils {
	
	public static final class Http {
		
		public static <T> RequestEntity<T> makeRequestEntity(
				URI uri, HttpMethod method, 
				Map<String, String> headerMap,
				T body, MediaType contentType) {
			
			BodyBuilder bodyBuilder = RequestEntity.method(method, uri);
			
			headerMap.forEach((k, v) -> {
				if( !(
						k.equalsIgnoreCase(HttpHeaders.CONTENT_DISPOSITION) ||
						k.equalsIgnoreCase(HttpHeaders.CONTENT_ENCODING) ||
						k.equalsIgnoreCase(HttpHeaders.CONTENT_LANGUAGE) ||
						k.equalsIgnoreCase(HttpHeaders.CONTENT_LENGTH) ||
						k.equalsIgnoreCase(HttpHeaders.CONTENT_LOCATION) ||
						k.equalsIgnoreCase(HttpHeaders.CONTENT_RANGE) ||
						k.equalsIgnoreCase(HttpHeaders.CONTENT_TYPE)
					)) {
						bodyBuilder.header(k, v);
					}
			});

			bodyBuilder.contentType(contentType);

			RequestEntity<T> remoteEntity = bodyBuilder.body(body);

			return remoteEntity;
		}
		
		public static <T> RequestEntity<T> makeRequestEntity(
				String url, HttpMethod method, 
				Map<String, String> headerMap,
				T body, MediaType contentType) {
			URI uri = URI.create(url);
			return makeRequestEntity(uri, method, headerMap,body, contentType);
		}
		
		public static void clearContentHeaders(Map<String, String> headers) {
			
			headers.forEach((k,v)->{
				if( 
					k.equalsIgnoreCase(HttpHeaders.CONTENT_DISPOSITION) ||
					k.equalsIgnoreCase(HttpHeaders.CONTENT_ENCODING) ||
					k.equalsIgnoreCase(HttpHeaders.CONTENT_LANGUAGE) ||
					k.equalsIgnoreCase(HttpHeaders.CONTENT_LENGTH) ||
					k.equalsIgnoreCase(HttpHeaders.CONTENT_LOCATION) ||
					k.equalsIgnoreCase(HttpHeaders.CONTENT_RANGE) ||
					k.equalsIgnoreCase(HttpHeaders.CONTENT_TYPE)
				) {
					headers.remove(k);
				}
			});
			
		}
		
		public static boolean isContentHeadersKey(String key) {
			if( 
				key.equalsIgnoreCase(HttpHeaders.CONTENT_DISPOSITION) ||
				key.equalsIgnoreCase(HttpHeaders.CONTENT_ENCODING) ||
				key.equalsIgnoreCase(HttpHeaders.CONTENT_LANGUAGE) ||
				key.equalsIgnoreCase(HttpHeaders.CONTENT_LENGTH) ||
				key.equalsIgnoreCase(HttpHeaders.CONTENT_LOCATION) ||
				key.equalsIgnoreCase(HttpHeaders.CONTENT_RANGE) ||
				key.equalsIgnoreCase(HttpHeaders.CONTENT_TYPE)
			) {
				return true;
			}
			return false;
		}
	}
	
	public static final class Json {

		public static Map<String,Object> schemaProfile(JsonNode node){
			if(node==null) return null;
			return schemaProfile(new LinkedHashMap<String,Object>(),node);
		}
		
		private static Map<String,Object> schemaProfile(Map<String,Object> schemaProfile , JsonNode node) {

			if(node instanceof ObjectNode) {
				schemaProfile.put("type","object");
				LinkedHashMap<String, Object> propertiesMap = new LinkedHashMap<String,Object>();
				ArrayList<String> requiredList = new ArrayList<String>();
				node.fieldNames().forEachRemaining((key)->{
					propertiesMap.put(key,schemaProfile(new LinkedHashMap<String, Object>(),node.get(key)));
					requiredList.add(key);
				});
				schemaProfile.put("properties",propertiesMap);
				schemaProfile.put("required",requiredList);
			}
			else if(node instanceof ArrayNode) {
				schemaProfile.put("type","array");
				List<Map<String,Object>> items = new ArrayList<Map<String,Object>>();
				node.forEach((child)->{
					items.add(schemaProfile(new LinkedHashMap<String, Object>(),child));
				});
				schemaProfile.put("items",items);
			}
			else if(node instanceof ValueNode) {
				schemaProfile.put("type",node.getNodeType().toString().toLowerCase());
				if(node instanceof TextNode) {
					if(Pattern.matches("^\\d{4}-\\d{2}-\\d{2}$",node.textValue())) {
						schemaProfile.put("format","date");
					}
					/*
					 * GPT 정규식 만들어달라고 한 다음 적용
					if(Pattern.matches("",node.textValue())) {
						schemaProfile.put("format","date-time");
					}
					if(Pattern.matches("",node.textValue())) {
						schemaProfile.put("format","time");
					}*/
				}
			}
			
			return schemaProfile;
		}
		
	}
	
	public static final class Xml {

		public static Map<String,Object> parseXmlToMap(InputStream xmlStream) {
			return parseXmlToMap(new InputStreamReader(xmlStream));
		}
		
		public static Map<String,Object> parseXmlToMap(String xmlStream) {
			return parseXmlToMap(new StringReader(xmlStream));
		}
		
		public static Map<String,Object> parseXmlToMap(byte[] xmlStream) {
			
			return parseXmlToMap(new InputStreamReader(new ByteArrayInputStream(xmlStream)));
		}
		
		public static Map<String,Object> parseXmlToMap(Reader reader) {

			SaxSeparatedHandler handler = new SaxSeparatedHandler();

			try (Reader sr  = reader){
				handler.parse(sr);
			} catch (Exception e) {
				e.printStackTrace();
			}
		
			return handler.getResult();
		}
		
		public static byte[] schemaProfile(Reader xml) {

			XsdGen xsd = new XsdGen(new XsdConfig());
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try (Reader xmlReader = xml){
				xsd.parse(xmlReader);
				xsd.write(baos);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ParsingException e) {
				e.printStackTrace();
			}
			return baos.toByteArray();
		}
		
	}
	
	public static boolean isEmpty(String str) {
		if(str==null) return true;
		if("".equals(str.trim())) return true;
		return false;
	}
	
	public static ByteBuffer newByteBuffer(byte[] resource) {
        return newByteBuffer(resource,true);
    }
	
	public static ByteBuffer newByteBuffer(byte[] resource , boolean direct) {
        ByteBuffer buffer = direct ? ByteBuffer.allocateDirect(resource.length) : ByteBuffer.allocate(resource.length);
        buffer.put(resource);
        buffer.flip();
        return buffer.asReadOnlyBuffer();
    }
	
	public static byte[] getByteBufferAsReadOnly(ByteBuffer buffer) {
		byte[] outputData = new byte[buffer.remaining()];
		buffer.asReadOnlyBuffer().get(outputData);
		return outputData;
	}
	
	public static ByteBuffer getTempleByteBuffer(String templetePath, ResourceLocation loc) throws IOException {
		
		AbstractResource resource = null;
		
		if(ResourceLocation.FILE.compareTo(loc)==0) {
			resource = new FileSystemResource(templetePath);
		}
		else {
			resource = new ClassPathResource(templetePath);
		}
		
		return Utils.newByteBuffer(resource.getContentAsByteArray());
	}
	
}