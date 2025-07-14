package kr.api.link.cmmn.crpt_back;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Base64;

import org.apache.commons.io.FileUtils;
import org.springframework.util.ObjectUtils;

import com.gpki.gpkiapi_jni;
import com.gpki.gpkiapi.GpkiApi;
import com.gpki.gpkiapi.cert.X509Certificate;
import com.gpki.gpkiapi.exception.GpkiApiException;
import com.gpki.gpkiapi.storage.Disk;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum GPKIModule implements Crypto {

	INSTANCE;
	
	private gpkiapi_jni jni;
	private byte[] envCert, envKey, sigCert, sigKey;
	private Path gpkiPath;
	
	public boolean init = false;
	public String copyPath;
	
	/*
	private GPKIModule(String targetCertId, boolean useLdap, String ldapUrl) throws InitializeGPKIException {
		
		log.info("crypto.target-cert-id : {}", targetCertId);
		log.info("crypto.use-ldap : {}", useLdap);
		log.info("crypto.ldap-url : {}", ldapUrl);
		if (ObjectUtils.isEmpty(targetCertId)) {
			throw new InitializeGPKIException("<crypto.target-cert-id> is empty.");
		}
		if (!GPKIModule.INSTANCE.init) {
			throw new InitializeGPKIException("GPKI Module not initialized.");
		}
		
		try {
			if (useLdap) {
				targetCert = GPKIModule.INSTANCE.getCert(targetCertId, useLdap, ldapUrl);
				log.info("get <{}> Cert from LDAP<{}>.", targetCertId, useLdap);
			} else {
				String targetCertPath = GPKIModule.INSTANCE.copyPath + File.separator + targetCertId + ".cer";
				targetCert = GPKIModule.INSTANCE.getCert(targetCertPath, useLdap, ldapUrl);
				log.info("get <{}> Cert from File<{}>.", targetCertId, targetCertPath);
			}
		} catch (GpkiApiException e) {
			throw new InitializeGPKIException(e);
		}
		
	}
	*/
	
	public void initialize(String mountPath, String copyPath, String certId, String envKeyPasswd, String sigKeyPasswd, boolean testGpki) throws GpkiApiException, IOException {
		
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
		
		// test gpki
		if (testGpki) testGpki();
		
		this.init = true;
	}
	
	public byte[] getCert(String certInfo, boolean useLdap, String ldapUrl) throws GpkiApiException {
		byte[] targetCert;
		if (useLdap && !ObjectUtils.isEmpty(ldapUrl)) {
			String dn;
			if (certInfo.charAt(3) > '9') { 
				dn = ",ou=Group of Server,o=Public of Korea,c=KR";
			} else {
				dn = ",ou=Group of Server,o=Government of Korea,c=KR";
			}
			int ret = jni.LDAP_GetAnyDataByURL("userCertificate;binary", ldapUrl + certInfo + dn);
			checkResult(ret, jni);
			targetCert = new X509Certificate(jni.baReturnArray).getCert();
		} else {
			if (certInfo==null || !new File(certInfo).exists()) throw new GpkiApiException("<" + certInfo + "> not exist." );
			targetCert = Disk.readCert(certInfo).getCert();
		}
		return targetCert;
	}
	

	private void checkResult(int result, gpkiapi_jni gpki)throws GpkiApiException {
		this.checkResult(result, null, gpki);
	}
	
	private void checkResult(int result ,String message,  gpkiapi_jni gpki)throws GpkiApiException {
		if( 0 != result){
			if(null != gpki){
				throw new GpkiApiException(message + " : gpkiErrorMessage=" + gpki.sDetailErrorString);
			}else{
				throw new GpkiApiException(message + " : gpkiErrorCode=" + result);
			}
		}
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

}
