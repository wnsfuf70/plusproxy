/*
package kr.api.link.cmmn.v2.service.support.util.xml;


import java.io.Reader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

import com.fasterxml.aalto.stax.InputFactoryImpl;

public class AaltoSaxHandler {

    private final Map<String, Object> rootMap = new LinkedHashMap<>();
    private final Deque<Map<String, Object>> stack = new ArrayDeque<>();
    private final Deque<String> elementStack = new ArrayDeque<>();
    private final StringBuilder textBuffer = new StringBuilder();

    public Map<String, Object> getResult() {
        return rootMap;
    }

    public void parse(Reader xmlStream) throws Exception {
        XMLInputFactory factory = new InputFactoryImpl();
        XMLStreamReader reader = factory.createXMLStreamReader(xmlStream);

        while (reader.hasNext()) {
            int event = reader.next();
            switch (event) {
                case XMLStreamConstants.START_ELEMENT:
                    startElement(reader);
                    break;
                case XMLStreamConstants.CHARACTERS:
                    characters(reader);
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    endElement();
                    break;
            }
        }
    }

    private void startElement(XMLStreamReader reader) {
        flushText();

        String elementName = reader.getLocalName();
        Map<String, Object> elementMap = new LinkedHashMap<>();

        // 속성 처리
        for (int i = 0; i < reader.getAttributeCount(); i++) {
            String attrName = reader.getAttributeLocalName(i);
            String attrValue = reader.getAttributeValue(i);
            Map<String, String> attrMap = (Map<String, String>) elementMap.get("attr");
            if (attrMap == null) {
                attrMap = new LinkedHashMap<>();
                elementMap.put("attr", attrMap);
            }
            attrMap.put(attrName, attrValue);
        }

        if (stack.isEmpty()) {
            rootMap.put(elementName, elementMap);
        } else {
            Map<String, Object> parent = stack.peek();
            Object existing = parent.get(elementName);
            if (existing == null) {
                parent.put(elementName, elementMap);
            } else if (existing instanceof List<?>) {
                ((List<Object>) existing).add(elementMap);
            } else {
                List<Object> list = new ArrayList<>();
                list.add(existing);
                list.add(elementMap);
                parent.put(elementName, list);
            }
        }

        stack.push(elementMap);
        elementStack.push(elementName);
    }

    private void endElement() {
        flushText();
        stack.pop();
        elementStack.pop();
    }

    private void characters(XMLStreamReader reader) {
        textBuffer.append(reader.getText());
    }

    private void flushText() {
        String text = textBuffer.toString().trim();
        if (!text.isEmpty() && !stack.isEmpty()) {
            Map<String, Object> current = stack.peek();
            Object existing = current.get("value");
            if (existing == null) {
                current.put("value", text);
            } else if (existing instanceof List) {
                @SuppressWarnings("unchecked")
                List<Object> list = (List<Object>) existing;
                list.add(text);
            } else {
                List<Object> list = new ArrayList<>();
                list.add(existing);
                list.add(text);
                current.put("value", list);
            }
        }
        textBuffer.setLength(0);
    }
    
   
}
 */