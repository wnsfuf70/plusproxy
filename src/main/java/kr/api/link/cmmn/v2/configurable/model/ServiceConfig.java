package kr.api.link.cmmn.v2.configurable.model;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import kr.api.link.cmmn.v2.app.ResourceLocation;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class ServiceConfig {
	
	public String serviceId;
	
	public String path;
	
	public Logic logic;
	
	public Convert convert;
	
	public Backend backend;
	
	public Crypto crypto;
	
	public static class Logic {
		
		@Getter @Setter
		String version;
		
		@Getter
		String type;
		
		@Getter
		@JsonProperty("component")
		ComponentEntry componentEntry = new ComponentEntry();
		
		@Getter
		@JsonIgnoreProperties
		ServiceRunType serviceRunType;

		public void setType(String type) {
			this.type = type;
			this.serviceRunType = ServiceRunType.getCompetitionType(type);
		}
		
		@Data
		public static class ComponentEntry {
			
			@JsonProperty("name")
			List<String> name;
			
			@JsonProperty("validator")
			MessageTypeHolder validator = new MessageTypeHolder();
			@JsonProperty("convertor")
			MessageTypeHolder convertor = new MessageTypeHolder();
			@JsonProperty("apicaller")
			CallerTypeHolder apicaller = new CallerTypeHolder();
			
			@Data
			public static class MessageTypeHolder {
				String req;
				String res;
			}
			
			@Data
			public static class CallerTypeHolder {
				String call;
				String error;
			}
		}
		
	}
	
	@Data
	public static class Convert {
		
		@JsonProperty("resource-location")
		ResourceLocation resourceLocation = ResourceLocation.FILE;
		
		@JsonProperty("req-origin")
		Templete reqOrigin;
		@JsonProperty("req-vm")
		Templete reqVm;
		@JsonProperty("res-origin")
		Templete resOrigin;
		@JsonProperty("res-vm")
		Templete resVm;
		
		public static class Templete {
			
			@Getter
			String type;
			
			@JsonProperty("templete-path")
			@Getter @Setter
			String templetePath;
			
			@JsonIgnoreProperties @Getter
			MediaType mediaType;
			
			public void setType(String type) {
				this.type = type;
				this.mediaType = Templete.transMediaType(type);
			}
			
			@JsonIgnoreProperties
			private static MediaType transMediaType(String typeString) {
				
				MediaType type = null;
				if("json".equalsIgnoreCase(typeString)) {
					type = MediaType.APPLICATION_JSON;
				}
				else if("xml".equalsIgnoreCase(typeString)) {
					type = MediaType.APPLICATION_XML;
				}
				else {
					type = MediaType.valueOf(typeString);
				}
				
				return type;
			}
		}
		
	}
	
	@Data
	public static class Backend {
		
		String address;
		
		@JsonProperty("method")
		@JsonSerialize(using = ToStringSerializer.class)
		HttpMethod mehtod;
		
		Header header;
		
		@Data
		public static class Header {
			
			@JsonProperty("static-prop")
			Map<String,String> staticProp;
			
		}
		
	}
	
	@Data
	public static class Crypto {
		
		private boolean use;
		@JsonProperty("use-request")
		private Boolean useRequest = null;
		@JsonProperty("use-response")
		private Boolean useResponse = null;
		
	    private String type = "GPKI";
		
	    private Encoding encoding = new Encoding();
	    private Sign sign = new Sign();
	    private Gpki gpki = new Gpki();
	    private Aria aria = new Aria();

	    @Getter @Setter
	    public static class Encoding {
	        private boolean use = true;
	        private String type = "BASE64";
	    }

	    @Getter @Setter
	    public static class Sign {
	        private boolean use = true;
	    }

	    @Getter @Setter
	    public static class Gpki {
	    	
	    	@JsonProperty("target-cert-id")
	        private String targetServerId;
	    	
	    	@JsonProperty("force-refresh")
	        private Boolean forceRefresh;
	    	@JsonProperty("force-refresh-time-millis")
	        private long forceRefreshTimeMillis = 0;
	        
	    	@JsonProperty("ldap-url")
	        private String ldapUrl;
	    	
	        @JsonProperty("ldap")
	        private Ldap ldap = new Ldap();
	        
	        @Getter @Setter
	        public static class Ldap {
	            private Boolean use = true;
	            @JsonProperty("ldap-url")
	            private String ldapUrl;
	        }
	        
	    }

	    @Getter @Setter
	    public static class Aria {
	    	@JsonProperty("key-enc")
	        private String keyEnc;
	    	@JsonProperty("key-mac")
	        private String keyMac;
	    	@JsonProperty("key-size")
	        private int keySize = 128;
	    }
		
	}
	
}