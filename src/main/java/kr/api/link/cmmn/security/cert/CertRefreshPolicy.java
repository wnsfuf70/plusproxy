package kr.api.link.cmmn.security.cert;

import java.util.Date;

import kr.api.link.cmmn.security.cert.MemoryCertCache.CachedCert;
import kr.api.link.cmmn.security.core.context.CryptoContext;


public class CertRefreshPolicy {

    /**
     * 캐시된 인증서가 갱신되어야 하는지 판단합니다.
     * - force-refresh: true 일 경우 즉시 갱신
     * - force-refresh-time-millis 초과 시 TTL 만료로 간주
     */
    public static boolean shouldRefresh(CachedCert cached, CryptoContext ctx) {
        if (CryptoContext.Util.isForceRefresh(ctx)) return true;

        long ttl = CryptoContext.Util.getForceRefreshTTL(ctx); // 기본: 600,000 (10분)
        long now = System.currentTimeMillis();
        long lastLoaded = cached.getLastLoadedAt(); // 캐시된 시점 (필드 필요)

        return (now - lastLoaded) > ttl;
    }

    /**
     * fallback: 인증서 자체의 만료 시점을 기준으로 7일 이내 만료 예정 시 true 반환
     * LDAP 재조회가 실패했을 때 보조 판단 기준으로 사용
     */
    public static boolean isNearExpiration(CachedCert cached) {
        try {
            Date notAfter = cached.getParsedCert().getNotAfter();
            long now = System.currentTimeMillis();
            long margin = 1000L * 60 * 60 * 24 * 7; // 7일
            return now > (notAfter.getTime() - margin);
        } catch (Exception e) {
            return true; // 파싱 실패 시 갱신 필요로 간주
        }
    }
}