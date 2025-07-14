package kr.api.link.cmmn.security.cert;

import com.gpki.gpkiapi_jni;
import com.gpki.gpkiapi.cert.X509Certificate;
import com.gpki.gpkiapi.exception.GpkiApiException;

import kr.api.link.cmmn.security.core.context.CryptoContext;
import kr.api.link.cmmn.security.core.context.CryptoKeys;

public class LdapKeyResolver implements KeyResolver<X509Certificate> {

    private final gpkiapi_jni jni;
    private final MemoryCertCache certCache = new MemoryCertCache();

    public LdapKeyResolver(gpkiapi_jni jni) {
        this.jni = jni;
    }

    @Override
    public X509Certificate getCert(CryptoContext ctx) throws Exception {
        String certId = ctx.getOrDefault(CryptoKeys.TARGET_GPKI_CERT_ID, null);
        if (certId == null) throw new IllegalArgumentException("target-cert-id must be provided");

        boolean useLdap = ctx.getOrDefault(CryptoKeys.USE_LDAP, true);
        MemoryCertCache.CachedCert cached = certCache.get(certId);

        boolean needRefresh = (cached == null || CertRefreshPolicy.shouldRefresh(cached, ctx));

        if (!useLdap || !needRefresh) {
            return cached != null ? cached.getParsedCert() : null;
        }

        // LDAP 재조회
        String baseUrl = ctx.getOrDefault(CryptoKeys.LDAP_URL, "ldap://152.99.57.127:389/cn=");
        String dn = (certId.charAt(3) > '9')
                ? ",ou=Group of Server,o=Public of Korea,c=KR"
                : ",ou=Group of Server,o=Government of Korea,c=KR";

        int result = jni.LDAP_GetAnyDataByURL("userCertificate;binary", baseUrl + certId + dn);
        if (result != 0) throw new GpkiApiException(jni.sDetailErrorString);

        byte[] der = jni.baReturnArray;
        X509Certificate javaCert = new X509Certificate(der) ;
        certCache.put(certId, javaCert);

        return javaCert;
    }

    @Override
    public void exportCache() {
        certCache.exportAll().forEach((k, v) -> {
        	X509Certificate parsedCert = v.getParsedCert();
        	int remainDays;
			try {
				remainDays = parsedCert.getRemainDays();
				System.out.println("CertID: " + k + ", expires: " + remainDays);
			} catch (GpkiApiException e) {
				e.printStackTrace();
			}
        });
    }
}