package kr.api.link.cmmn.security.encoding;

public interface Codec {
    byte[] encode(byte[] data);
    byte[] decode(byte[] data);
}
