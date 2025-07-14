# 🧩 서비스별 암복호화 설정

해당 설정은 각 서비스가 자체적으로 사용하는 암복호화 방식, 서명 여부, 인코딩 처리 정책을 기술합니다.  
`GPKI`, `ARIA`, 기타 방식이 추가될 수 있으며, 모든 값은 `CryptoContext`로 매핑되어 처리됩니다.


service001.yaml

crypto:
  use: true
  use-request: true
  use-response: false
  type: GPKI
  encoding:
    use: true
    type: BASE64
  sign:
    use: true
  gpki:
    target-cert-id: SVRTARGET123123
    force-refresh: false
    force-refresh-time-millis: 600000
  aria:
    key-enc: thisIs16ByteKey
    key-mac: thisIsMacKeyUsedForHmac
    key-size: 128