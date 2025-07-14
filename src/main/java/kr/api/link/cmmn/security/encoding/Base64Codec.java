package kr.api.link.cmmn.security.encoding;

import java.util.Base64;

public class Base64Codec implements Codec {
    @Override
    public byte[] encode(byte[] data){ return Base64.getEncoder().encode(data);}
    @Override
    public byte[] decode(byte[] data){ return Base64.getDecoder().decode(data);}
}


