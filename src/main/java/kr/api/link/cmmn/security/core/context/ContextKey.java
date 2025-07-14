package kr.api.link.cmmn.security.core.context;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(of = "name")
@ToString
public final class ContextKey<T> {

    private final String name;

    public ContextKey(String name) {
        this.name = name;
    }
    
    public String name() {
        return name;
    }

}