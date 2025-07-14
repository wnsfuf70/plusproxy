package kr.api.link.cmmn.v2.service.support.util.xml;

import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;

public class SaxSeparatedHandler {

    private final Map<String, Object> rootMap = new LinkedHashMap<>();

    public Map<String, Object> getResult() {
        return rootMap;
    }

    public void parse(Reader xmlStream) throws Exception {
    	
        Builder builder = new Builder();
        Document doc = builder.build(xmlStream);
        Element root = doc.getRootElement();

        rootMap.put(root.getQualifiedName(), elementToMap(root));
    }

    private Object elementToMap(Element element) {
        Map<String, Object> map = new LinkedHashMap<>();

        // 속성 처리
        if (element.getAttributeCount() > 0) {
            Map<String, String> attrMap = new LinkedHashMap<>();
            for (int i = 0; i < element.getAttributeCount(); i++) {
                Attribute attr = element.getAttribute(i);
                attrMap.put(attr.getQualifiedName(), attr.getValue());
            }
            map.put("attr", attrMap);
        }

        // 자식 요소 처리
        Elements children = element.getChildElements();
        if (children.size() == 0) {
            String text = element.getValue().trim();
            if (!text.isEmpty()) {
                return text; // 단순 텍스트 노드
            }
        } else {
            for (int i = 0; i < children.size(); i++) {
                Element child = children.get(i);
                String childName = child.getQualifiedName();
                Object childMap = elementToMap(child);

                if (map.containsKey(childName)) {
                    Object existing = map.get(childName);

                    if (existing instanceof List) {
                        List<?> rawList = (List<?>) existing;
                        @SuppressWarnings("unchecked")
						List<Object> safeList = Collections.checkedList((List<Object>) rawList, Object.class);
                        safeList.add(childMap);
                    } else {
                        List<Object> list = Collections.checkedList(new ArrayList<>(), Object.class);
                        list.add(existing);
                        list.add(childMap);
                        map.put(childName, list);
                    }
                } else {
                    map.put(childName, childMap);
                }
                
            }
        }

        return Collections.unmodifiableMap(map);
    }
}
