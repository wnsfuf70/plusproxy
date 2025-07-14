package kr.api.link.cmmn.v2.service.flow.isp.combine;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import kr.api.link.cmmn.v2.configurable.model.ConfigurableContext;
import kr.api.link.cmmn.v2.service.flow.isp.Convertable;

public final class LockingConverterChain<I, O> implements Convertable<I, O> {

    private Convertable<I, ?> startConverter;
    private Convertable<?, O> endConverter;
    private final List<Convertable<?, ?>> middleConverters = new CopyOnWriteArrayList<>();

    private volatile Convertable<?, ?>[] cachedArray = new Convertable[0];
    private volatile boolean locked = false;
    private volatile boolean autoCompleted = false;

    public <T, R> LockingConverterChain<I, O> setNextConverter(Convertable<T, R> converter) {
        assertUnlocked();
        assertConcrete(converter);
        middleConverters.add(converter);
        return this;
    }

    public <T> LockingConverterChain<I, O> overrideStartConverter(Convertable<I, T> customStart) {
        assertUnlocked();
        assertConcrete(customStart);
        this.startConverter = customStart;
        return this;
    }

    public <T> LockingConverterChain<I, O> overrideEndConverter(Convertable<T, O> customEnd) {
        assertUnlocked();
        assertConcrete(customEnd);
        this.endConverter = customEnd;
        return this;
    }

    public void setDefaultStartAndEnd(Convertable<I, ?> start, Convertable<?, O> end) {
        assertConcrete(start);
        assertConcrete(end);
        this.startConverter = start;
        this.endConverter = end;
    }

    public LockingConverterChain<I, O> complete() {
        refreshCacheAndLock();
        autoCompleted = true;
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public O transform(ConfigurableContext ctx, I input) throws Exception {
        if (!autoCompleted && !locked) {
            synchronized (this) {
                if (!autoCompleted && !locked) {
                    refreshCacheAndLock();
                    autoCompleted = true;
                }
            }
        }

        Object current = input;
        for (Convertable<?, ?> converter : cachedArray) {
            current = invokeConverter(converter, ctx, current);
        }
        return (O) current;
    }

    @SuppressWarnings("unchecked")
    private Object invokeConverter(Convertable<?, ?> converter, ConfigurableContext ctx, Object input) throws Exception {
        return ((Convertable<Object, Object>) converter).transform(ctx, input);
    }

    private synchronized void refreshCacheAndLock() {
        if (startConverter == null || endConverter == null) {
            throw new IllegalStateException("Start and End converters must be set before locking.");
        }
        int size = 2 + middleConverters.size();
        Convertable<?, ?>[] newArray = new Convertable[size];
        newArray[0] = startConverter;
        for (int i = 0; i < middleConverters.size(); i++) {
            newArray[i + 1] = middleConverters.get(i);
        }
        newArray[size - 1] = endConverter;

        cachedArray = newArray;
        locked = true;
    }

    private void assertUnlocked() {
        if (locked) {
            throw new IllegalStateException("Chain is locked. Unlock before modifying.");
        }
    }

    private void assertConcrete(Convertable<?, ?> converter) {
        if (converter.getClass().isInterface()) {
            throw new IllegalArgumentException(
                "Converter must be a concrete implementation, but interface found: " 
                + converter.getClass().getName()
            );
        }
    }
}