package kr.api.link.cmmn.v2.service.component.convert.type;

import java.nio.ByteBuffer;

import kr.api.link.cmmn.v2.service.flow.isp.TypeConvertable;
import kr.api.link.cmmn.v2.service.support.util.Utils;

public class ByteBufferToByteArrayConverter implements TypeConvertable<ByteBuffer, byte[]> {
	
	@Override
	public byte[] convertType(ByteBuffer originSource) throws Exception {
		return Utils.getByteBufferAsReadOnly(originSource);
	}

}