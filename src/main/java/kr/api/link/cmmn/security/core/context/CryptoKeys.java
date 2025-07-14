package kr.api.link.cmmn.security.core.context;

public final class CryptoKeys {

	public static final ContextKey<Boolean> USE_CRYPTO = new ContextKey<>("crypto.use");

	public static final ContextKey<Boolean> USE_CRYPTO_REQUEST = new ContextKey<>("crypto.use.request");
	public static final ContextKey<Boolean> USE_CRYPTO_RESPONSE = new ContextKey<>("crypto.use.response");
	
	public static final ContextKey<String> CRYPTO_TYPE = new ContextKey<>("crypto.type");
	
    public static final ContextKey<Boolean> USE_GPKI_SIGN = new ContextKey<>("crypto.sign.use");
    //public static final ContextKey<Boolean> USE_ENCODING = new ContextKey<>("crypto.encoding.use");
    public static final ContextKey<String> ENCODING_TYPE = new ContextKey<>("crypto.encoding.type");

    public static final ContextKey<String> LDAP_URL = new ContextKey<>("crypto.gpki.ldap.url");
    public static final ContextKey<Boolean> USE_LDAP = new ContextKey<>("crypto.gpki.ldap.use");

    public static final ContextKey<String> TARGET_GPKI_CERT_ID = new ContextKey<>("crypto.gpki.cert.target.id");

    public static final ContextKey<Boolean> FORCE_REFRESH = new ContextKey<>("crypto.gpki.cert.force-refresh");
    public static final ContextKey<Long> FORCE_REFRESH_TIME_MILLIS = new ContextKey<>("crypto.gpki.cert.force-refresh-time-millis");

    public static final ContextKey<Integer> ARIA_KEY_SIZE = new ContextKey<>("crypto.aria.key-size");
    public static final ContextKey<String> ARIA_KEY_ENC = new ContextKey<>("crypto.aria.key.enc");
    public static final ContextKey<String> ARIA_KEY_MAC = new ContextKey<>("crypto.aria.key.mac");
    
    private CryptoKeys() {}
    
    /**
    ARIA 암호화 키	"crypto.aria.key.enc"
    ARIA MAC 키	"crypto.aria.key.mac"
    ARIA 키사이즈	"crypto.aria.key.size"
    GPKI 대상 서버 ID	"crypto.gpki.cert.target.id"
    GPKI 인증서 로컬 경로	"crypto.gpki.cert.target.path"
    GPKI LDAP URL	"crypto.gpki.ldap.url"
    암호화 수행 여부	"crypto.use"
    암복호화 종류 "crypto.type"
    인코딩 수행 여부	"crypto.encoding.use"
    인코딩 타입	"crypto.encoding.type"
    전자서명 수행 여부	"crypto.sign.use"
    **/
    
    /**
	
crypto:
  use: true             # 암복호화 수행 여부 (기본값: true)

  type: GPKI            # 암호화 방식: GPKI | ARIA

  encoding:
    use: true           # 인코딩 수행 여부 (기본값: true)
    type: BASE64        # BASE64 | HEX | NONE

  sign:
    use: true           # 전자서명 수행 여부 (기본값: true) - GPKI or MAC 기반 사용

  gpki:
    cert:
      target:
        id: partner001                  # 암호화 대상 서버 ID
        path: /app/secrets/partner001.cer  # (선택) 로컬 인증서 파일 경로

      force-refresh: true               # LDAP에서 무조건 재조회할지 여부
      force-refresh-time-millis: 600000 # 재조회 TTL (단위: ms)

    ldap:
      use: true                         # LDAP 사용 여부
      url: ldap://root.ca.go.kr        # LDAP 기본 접속 URL

  aria:
    key:
      enc: thisIs16ByteKey              # 암호화용 마스터 키 (필수)
      mac: thisIsMacKeyUsedForHmac      # MAC용 키 (옵션, HmacSigner 사용 시)
    key-size: 128                       # 128, 192, 256
	
     **/
    
}