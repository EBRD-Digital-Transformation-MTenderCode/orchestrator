package com.procurement.orchestrator.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ConversionServiceFactoryBean;
import org.springframework.core.convert.converter.Converter;

import java.util.HashSet;
import java.util.Set;

@Configuration
public class ConverterConfig {
    @Bean
    public ConversionServiceFactoryBean conversionService() {
        final Set<Converter> converters = new HashSet<>();
        final ConversionServiceFactoryBean bean = new ConversionServiceFactoryBean();
        bean.setConverters(converters);
        return bean;
    }
}
