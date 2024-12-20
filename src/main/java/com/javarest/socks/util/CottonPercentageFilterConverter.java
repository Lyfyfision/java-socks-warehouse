package com.javarest.socks.util;

import com.javarest.socks.dto.CottonPercentageFilter;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class CottonPercentageFilterConverter implements Converter<String, CottonPercentageFilter> {
    @Override
    public CottonPercentageFilter convert(String source) {
        return new CottonPercentageFilter(source);
    }
}
