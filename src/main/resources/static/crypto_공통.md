# 🔐 crypto.gpki 설정

GPKI 암복호화 처리를 위한 기본 인증서 및 키 로딩 경로 설정입니다.  
보통 서버 기동 시 초기화되며, 이후 GPKIModule 내부에서 재사용됩니다.

## ✅ 기본 구조

application.yaml

crypto:
  gpki:
    mount-path: C:/gpki
    copy-path: C:/gpki
    cert-id: SVR1741845003
    env-key-password: godakd03!
    sig-key-password: godakd03!
    test-gpki: true
    ldap-url: ldap://152.99.57.127:389/cn=
    
    
