package kr.api.link.cmmn.security.cert;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.gpki.gpkiapi.cert.X509Certificate;

public class MemoryCertCache {

    private final Map<String, CachedCert> cache = new ConcurrentHashMap<>();

    /** 인증서 캐시 저장 */
    public void put(String certId, X509Certificate cert) {
        cache.put(certId, new CachedCert(cert, System.currentTimeMillis()));
    }

    /** 인증서 캐시 조회 */
    public CachedCert get(String certId) {
        return cache.get(certId);
    }

    public boolean contains(String certId) {
        return cache.containsKey(certId);
    }

    public Map<String, CachedCert> exportAll() {
        return cache;
    }

    public void clear() {
        cache.clear();
    }

    /** 내부 클래스: 캐시된 인증서 구조 */
    public static class CachedCert {
    	
    	private byte[] encodedCert;
        private X509Certificate parsedCert;
        private long lastLoadedAt;
        
        public CachedCert(X509Certificate parsedCert, long lastLoadedAt) {
        	this(parsedCert.getCert(),parsedCert,lastLoadedAt);
        }
        
        public CachedCert(byte[] encodedCert, X509Certificate parsedCert, long lastLoadedAt) {
			super();
			this.encodedCert = encodedCert;
			this.parsedCert = parsedCert;
			this.lastLoadedAt = lastLoadedAt;
		}

		public X509Certificate getParsedCert() {
            return parsedCert;
        }

        public long getLastLoadedAt() {
            return lastLoadedAt;
        }

		public byte[] getEncodedCert() {
			return encodedCert;
		}

		public void setEncodedCert(byte[] encodedCert) {
			this.encodedCert = encodedCert;
		}

		public void setParsedCert(X509Certificate parsedCert) {
			this.parsedCert = parsedCert;
		}
        
    }    
}