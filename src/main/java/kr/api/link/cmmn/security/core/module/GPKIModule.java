package kr.api.link.cmmn.security.core.module;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.io.FileUtils;

import com.gpki.gpkiapi_jni;
import com.gpki.gpkiapi.GpkiApi;
import com.gpki.gpkiapi.cert.X509Certificate;
import com.gpki.gpkiapi.exception.GpkiApiException;
import com.gpki.gpkiapi.storage.Disk;

import kr.api.link.cmmn.security.cert.CertRefreshPolicy;
import kr.api.link.cmmn.security.cert.MemoryCertCache.CachedCert;
import kr.api.link.cmmn.security.core.context.CryptoContext;
import kr.api.link.cmmn.security.core.context.CryptoKeys;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum GPKIModule {
	
	INSTANCE;
	
	private byte[] envCert, envKey, sigCert, sigKey;
	private gpkiapi_jni jni;
	private Path gpkiPath;
	public boolean init = false;
	public String copyPath;
	public String ldapUrl;
	
	
	public void initialize(String mountPath, String copyPath, String certId, String envKeyPasswd, String sigKeyPasswd, boolean testGpki ,String ldapUrl) throws GpkiApiException, IOException {
		
		if (Files.notExists(Paths.get(mountPath)))
			throw new GpkiApiException("gpki mount directory<" + mountPath + "> not exists.");
		
		Files.list(Paths.get(mountPath))
		.forEach(path -> {
			
			if (Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS) && (this.gpkiPath == null || !path.getFileName().equals(this.gpkiPath.getFileName()))) {
				try {
					FileUtils.copyDirectory(new File(path.toString()) ,new File(copyPath), null, false, StandardCopyOption.REPLACE_EXISTING);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				log.info("copy from <{}> to <{}> has been completed.", path, copyPath);
				this.gpkiPath = path;
				this.copyPath = copyPath;
			}
		});
		
		// initialize api
		GpkiApi.init(copyPath);
		
		// initialize jni
		this.jni = new gpkiapi_jni();
		if(jni.API_Init(copyPath) != 0)
			throw new GpkiApiException(jni.sDetailErrorString);
		
		// load source Certificates
		String envCertPath = copyPath + File.separator + certId + "_env.cer";
		String envKeyPath = copyPath + File.separator + certId + "_env.key";
		String sigenvCertPath = copyPath + File.separator + certId + "_sig.cer";
		String sigKeyPath = copyPath + File.separator + certId + "_sig.key";
		log.info("envCertPath : {}", envCertPath);
		log.info("envKeyPath : {}", envKeyPath);
		log.info("sigenvCertPath : {}", sigenvCertPath);
		log.info("sigKeyPath : {}", sigKeyPath);
		
		envCert = Disk.readCert(envCertPath).getCert();
		envKey = Disk.readPriKey(envKeyPath, envKeyPasswd).getKey();
		sigCert = Disk.readCert(sigenvCertPath).getCert();
		sigKey = Disk.readPriKey(sigKeyPath, sigKeyPasswd).getKey();
		
		this.ldapUrl = ldapUrl;
		log.info("=======================================================");
		log.info("================ default ldap : " + ldapUrl + "======================");
		log.info("=======================================================");
		// test gpki
		if (testGpki) testGpki();
		this.init = true;
		
	}
	
	public void testGpki() throws GpkiApiException {
		log.info("=======================================================");
		log.info("================ TEST GPKI START ======================");
		log.info("=======================================================");
		
		String original_Eng = "abc";
		log.info("=== TEST ENG STRING: "+ original_Eng);
		try {
			byte[] encrypted = encrypt(original_Eng.getBytes(), envCert);
			log.info("=== TEST ENG ENCRYPT STRING: "+ Base64.getEncoder().encode(encrypted));
			String decrypted = new String(decrypt(encrypted));
			log.info("=== TEST ENG DECRYPT STRING: "+decrypted);
			
			if (!original_Eng.equals(decrypted)) {
				throw new GpkiApiException("GpkiModule not initialized properly(english)");
			}
			log.info("=== TEST ENG: OK");
		} catch (GpkiApiException e) {
			throw e;
		}

		String original = "한글테스트";
		log.info("=== TEST KOR STRING: "+ original);
		try {
			byte[] encrypted = encrypt(original.getBytes(), envCert);
			log.info("=== TEST KOR ENCRYPT STRING: "+ Base64.getEncoder().encode(encrypted));
			byte[] encode = Base64.getEncoder().encode(encrypted);
			log.info("=== TEST KOR ENCRYPT STRING: "+ new String(encode));
			String decrypted = new String(decrypt(encrypted));
			log.info("=== TEST KOR DECRYPT STRING: "+decrypted);
			if (!original.equals(decrypted)) {
				throw new GpkiApiException("GpkiModule not initialized properly(korean)");
			}
			log.info("=== TEST KOR: OK");
		} catch (GpkiApiException e) {
			throw e;
		}
		
		log.info("=======================================================");
		log.info("================ TEST GPKI END ========================");
		log.info("=======================================================");
	}
	
	private static final Map<String, CachedCert> certCache = new ConcurrentHashMap<>();
	
	public byte[] getCert(CryptoContext ctx) throws GpkiApiException {
		
	    String certId = ctx.get(CryptoKeys.TARGET_GPKI_CERT_ID);
	    boolean useLdap = ctx.getOrDefault(CryptoKeys.USE_LDAP, true);
	    String ctxLdapUrl = ctx.getOrDefault(CryptoKeys.LDAP_URL,ldapUrl);
	    
	    log.debug("ctxLdapUrl : " + ctxLdapUrl);
	    
	    if (!useLdap) {
	        return getCertFromFile(ctx.get(CryptoKeys.TARGET_GPKI_CERT_ID));
	    }

	    CachedCert cached = certCache.get(certId);
	    if (cached != null && !CertRefreshPolicy.shouldRefresh(cached, ctx)) {
	        log.debug("certCache hit: {}", certId);
	        return cached.getEncodedCert();
	    }
	    
	    
	    byte[] certBytes = getCertFromLdap(certId, ctxLdapUrl);
	    X509Certificate parsed = parse(certBytes);
	    certCache.put(certId, new CachedCert(certBytes, parsed, System.currentTimeMillis()));
	    
	    return certBytes;
	}

    public byte[] getCertFromLdap(String certId, String ldapUrl) throws GpkiApiException {
    	
        if (certId == null)
        	throw new IllegalArgumentException("target certId is null");
        if (ldapUrl == null)
            throw new IllegalArgumentException("ldapUrl is null");

        String dn = (certId.charAt(3) > '9')
            ? ",ou=Group of Server,o=Public of Korea,c=KR"
            : ",ou=Group of Server,o=Government of Korea,c=KR";
        
        String requestQuery = ldapUrl + certId + dn;
        log.debug("request ldap query : {} ",requestQuery);
        
        int result = jni.LDAP_GetAnyDataByURL("userCertificate;binary", requestQuery);
        checkResult(result, "LDAP cert load failed", jni);

        return new X509Certificate(jni.baReturnArray).getCert();
        
    }

    public byte[] getCertFromFile(String certId) throws GpkiApiException {
    	String path = copyPath + File.separator + certId + "_env.cer";
        if (path == null || !new File(path).exists()) {
            throw new GpkiApiException("Certificate file not found: " + path);
        }
        return Disk.readCert(path).getCert();
    }
	
	public byte[] encrypt(byte[] plain, byte[] cert) throws GpkiApiException {
		int result = jni.CMS_MakeEnvelopedData(cert, plain, gpkiapi_jni.SYM_ALG_NEAT_CBC);
		checkResult(result, "Fail to encrypt message", jni);
		return jni.baReturnArray;
	}
	
	public byte[] decrypt(byte[] encrypted) throws GpkiApiException {
		int result = jni.CMS_ProcessEnvelopedData(envCert, envKey,	encrypted);
		checkResult(result, "Fail to decrpyt message", jni);
		return jni.baReturnArray;
	}

	public byte[] sign(byte[] plain) throws GpkiApiException {
		int result = jni.CMS_MakeSignedData(sigCert, sigKey, plain, null);
		checkResult(result, "Fail to sign message", jni);
		return jni.baReturnArray;
	}

	public byte[] validate(byte[] signed) throws GpkiApiException {
		int result = jni.CMS_ProcessSignedData(signed);
		checkResult(result, "Fail to validate signed message", jni);
		return jni.baData;
	}
	
	private void checkResult(int result ,String message,  gpkiapi_jni jni) throws GpkiApiException {
		if( 0 != result){
			if(null != jni){
				throw new GpkiApiException(message + " : gpkiErrorMessage=" + jni.sDetailErrorString);
			}else{
				throw new GpkiApiException(message + " : gpkiErrorCode=" + result);
			}
		}
	}

	private static boolean isExpired(CachedCert cached) {
	    try {
	        Date notAfter = cached.getParsedCert().getNotAfter();
	        long now = System.currentTimeMillis();
	        long margin = 1000L * 60 * 60 * 24 * 7; // 7일 전부터 재조회
	        return now > (notAfter.getTime() - margin);
	    } catch (Exception e) {
	        return true;
	    }
	}

	private X509Certificate parse(byte[] certBytes) throws GpkiApiException {
	    return new X509Certificate(certBytes);
	}

}