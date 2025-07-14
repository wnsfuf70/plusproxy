package kr.api.link.cmmn.security.encoding;

public class HexCodec implements Codec {
    private static final char[] HEX = "0123456789ABCDEF".toCharArray();
    @Override
    public byte[] encode(byte[] data){
        char[] out = new char[data.length*2];
        for(int i=0;i<data.length;i++){
            int v = data[i] & 0xFF;
            out[i*2]=HEX[v>>>4];
            out[i*2+1]=HEX[v&0x0F];
        }
        return new String(out).getBytes();
    }
    @Override
    public byte[] decode(byte[] data){
        String str = new String(data);
        int len = str.length();
        if(len%2!=0) throw new IllegalArgumentException("Invalid HEX");
        byte[] out = new byte[len/2];
        for(int i=0;i<len;i+=2){
            out[i/2]=(byte)((Character.digit(str.charAt(i),16)<<4)+Character.digit(str.charAt(i+1),16));
        }
        return out;
    }
}
