package kr.api.link.cmmn.v2.service.support.util.xml;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class XsdConfig {
    private String xsdPrefix = "xsd";
    private boolean enableMaxOccursOnce = false;

    public String getXsdPrefix() {
        return xsdPrefix;
    }

    public void setXsdPrefix(String xsdPrefix) {
        this.xsdPrefix = xsdPrefix;
    }

    public boolean isEnableMaxOccursOnce() {
        return enableMaxOccursOnce;
    }

    public void setEnableMaxOccursOnce(boolean enableMaxOccursOnce) {
        this.enableMaxOccursOnce = enableMaxOccursOnce;
    }

}
