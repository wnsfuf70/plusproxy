package kr.api.link.cmmn.security.encoding;

import kr.api.link.cmmn.security.constant.EncodingType;

public class CodecFactory {

    private final Base64Codec base64 = new Base64Codec();
    private final HexCodec hex = new HexCodec();

    public Codec get(EncodingType encodingType) {
        if (encodingType == null) return base64; // fallback

        switch (encodingType) {
            case HEX: return hex;
            case NONE: return new NoOpCodec();
            case BASE64:
            default: return base64;
        }
    }

    public static class NoOpCodec implements Codec {
        @Override
        public byte[] encode(byte[] data) { return data; }
        @Override
        public byte[] decode(byte[] data) { return data; }
    }
}