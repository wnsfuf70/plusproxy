package kr.api.link.cmmn.security.signature;

import java.nio.charset.StandardCharsets;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import kr.api.link.cmmn.security.core.context.CryptoContext;
import kr.api.link.cmmn.security.core.context.CryptoKeys;

public class HmacSigner implements Signer, Verifier {

    private static final String HMAC_ALGORITHM = "HmacSHA256";

    @Override
    public byte[] sign(byte[] data, CryptoContext ctx) throws Exception {
        String key = ctx.get(CryptoKeys.ARIA_KEY_MAC);
        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("Missing ARIA MAC key (ARIA_KEY_MAC)");
        }
        Mac mac = Mac.getInstance(HMAC_ALGORITHM);
        mac.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM));
        return mac.doFinal(data);
    }

    @Override
    public byte[] verify(byte[] signed, CryptoContext ctx) throws Exception {
        int macLen = 32; // 256 bits

        if (signed.length <= macLen) {
            throw new IllegalArgumentException("Invalid signed data format (too short)");
        }

        byte[] data = new byte[signed.length - macLen];
        byte[] mac = new byte[macLen];

        System.arraycopy(signed, 0, data, 0, data.length);
        System.arraycopy(signed, data.length, mac, 0, macLen);

        byte[] expected = sign(data, ctx);

        for (int i = 0; i < macLen; i++) {
            if (mac[i] != expected[i]) {
                throw new SecurityException("MAC validation failed");
            }
        }

        return data;
    }
}
