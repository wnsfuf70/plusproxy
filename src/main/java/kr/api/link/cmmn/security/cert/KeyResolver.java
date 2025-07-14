package kr.api.link.cmmn.security.cert;

import kr.api.link.cmmn.security.core.context.CryptoContext;

public interface KeyResolver<T> {

    /**
     * 컨텍스트 기반으로 상대방 인증서를 가져온다.
     * LDAP 또는 로컬 방식이며 캐시를 활용한다.
     */
    T getCert(CryptoContext ctx) throws Exception;

    /**
     * (선택) 인증서 캐시 내보내기
     */
    void exportCache(); 
        // Optional - MemoryCertCache에서 구현 가능
}