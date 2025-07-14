package kr.api.link.cmmn.v2.service.flow.isp;

public interface TypeConvertable <I,O> {
	
	public abstract O convertType(I originSource) throws Exception;

}