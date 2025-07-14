package kr.api.link.cmmn.v2.service.support.util.xml;

import java.io.File;
import java.io.IOException;

import nu.xom.ParsingException;

public class Test {
	
	public static void test(String[] args) {
		
		XsdGen xsd = new XsdGen();
		
		try {
			xsd.parse(new File("C:\\\\eclipse\\\\workspace\\\\demo\\\\src\\\\main\\\\resources\\\\templates\\\\service001-req.vm"));
			xsd.write(System.out);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParsingException e) {
			e.printStackTrace();
		}
		
	}
	
	
	public static void main(String[] args) {
	}
	
}
