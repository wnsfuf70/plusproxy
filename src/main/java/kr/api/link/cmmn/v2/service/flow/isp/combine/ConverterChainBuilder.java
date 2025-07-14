package kr.api.link.cmmn.v2.service.flow.isp.combine;

import kr.api.link.cmmn.v2.configurable.model.ConfigurableContext;
import kr.api.link.cmmn.v2.service.flow.isp.Convertable;

public class ConverterChainBuilder<I,O> implements Convertable<I, O> {

    private final Convertable<I, O> converter;

    protected ConverterChainBuilder(Convertable<I, O> converter) {
        this.converter = converter;
    }

    public static <I, O> ConverterChainBuilder<I, O> from(Convertable<I, O> converter) {
        return new ConverterChainBuilder<>(converter);
    }

    public <R> ConverterChainBuilder<I, R> chain(Convertable<O, R> next) {
        final Convertable<I, O> currentConverter = converter;
        Convertable<I, R> chained = new Convertable<I,R>() {
            @Override
            public R transform(ConfigurableContext ctx,I param) throws Exception {
                O inter = currentConverter.transform(ctx,param);
                if (inter == null) {
                    throw new Exception("Intermediate null ... result");
                }
                return next.transform(ctx,inter);
            }
        };
        return new ConverterChainBuilder<>(chained);
    }

    @Override
    public O transform(ConfigurableContext ctx,I param) throws Exception {
        return converter.transform(ctx,param);
    }
    
}