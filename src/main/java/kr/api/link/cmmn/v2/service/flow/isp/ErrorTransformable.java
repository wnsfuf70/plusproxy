package kr.api.link.cmmn.v2.service.flow.isp;

@FunctionalInterface
public interface ErrorTransformable <I,O> {
	
	O errorHandle (Exception exception, I param ) throws Exception;
	
}