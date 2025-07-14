package kr.api.link.cmmn.v2.service.support.util.test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import org.springframework.core.io.FileSystemResource;

public class ByteBufferTest {
	
	public static void main2(String[] args) {
		
        // 문자열
        String input = "Hello, ByteBuffer";

        // 문자열을 바이트 배열로 변환
        byte[] inputData = input.getBytes(StandardCharsets.UTF_8);

        // ByteBuffer를 생성
        ByteBuffer buffer = ByteBuffer.allocateDirect(inputData.length);

        // 데이터를 ByteBuffer에 쓰기
        buffer.put(inputData);

        // 버퍼를 flip하여 읽기 모드로 전환
        buffer.flip();
        
        // ByteBuffer에서 데이터 읽기
        byte[] outputData = new byte[buffer.remaining()];
        buffer.asReadOnlyBuffer().get(outputData);
        
        // 읽은 데이터를 문자열로 변환
        String output = new String(outputData, StandardCharsets.UTF_8);

        System.out.println("Input: " + input);
        System.out.println("Output: " + output);
        
        int remaining = buffer.remaining();
        System.out.println(remaining);
        
        // ByteBuffer에서 데이터 읽기
        byte[] outputData2 = new byte[buffer.remaining()];
        buffer.asReadOnlyBuffer().get(outputData2);
        
     // 읽은 데이터를 문자열로 변환
        String output2 = new String(outputData2, StandardCharsets.UTF_8);
        System.out.println("Output: " + output2);
        
    }
	
	public static void main(String[] args) {
	
		FileSystemResource resource = new FileSystemResource("C:\\eclipse\\workspace\\demo\\src\\main\\resources\\templates\\service001-req.origin");
		try {
			
			ByteBuffer initByteBuffer = newByteBuffer(resource.getContentAsByteArray());
			
			byte[] byteBufferAsReadOnly = getByteBufferAsReadOnly(initByteBuffer);
			String output2 = new String(byteBufferAsReadOnly, StandardCharsets.UTF_8);
			
			byte[] byteBufferAsReadOnly2 = getByteBufferAsReadOnly(initByteBuffer);
			String output3 = new String(byteBufferAsReadOnly2, StandardCharsets.UTF_8);
			
			System.out.println(output2);
			System.out.println(output3);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	
	}
	
	public static ByteBuffer newByteBuffer(byte[] resource) {
        ByteBuffer buffer = ByteBuffer.allocateDirect(resource.length);
        buffer.put(resource);
        buffer.flip();
        return buffer.asReadOnlyBuffer();
    }
	
	public static byte[] getByteBufferAsReadOnly(ByteBuffer buffer) {
		byte[] outputData = new byte[buffer.remaining()];
		buffer.asReadOnlyBuffer().get(outputData);
		return outputData;
	}
	
}
