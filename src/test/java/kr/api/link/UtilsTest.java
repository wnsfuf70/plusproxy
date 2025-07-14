package kr.api.link;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import kr.api.link.cmmn.v2.service.support.util.Utils;

public class UtilsTest {
    @Test
    void byteBufferRoundTrip() {
        byte[] data = "hello".getBytes(StandardCharsets.UTF_8);
        ByteBuffer buffer = Utils.newByteBuffer(data);
        byte[] read = Utils.getByteBufferAsReadOnly(buffer);
        assertArrayEquals(data, read);
    }
}
