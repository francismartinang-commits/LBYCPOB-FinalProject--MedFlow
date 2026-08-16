package com.dlsu.medflow.config;

import com.dlsu.medflow.service.HospitalDataStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * {@link HospitalDataStore} already had its own static factory method,
 * {@code loadOrCreate()}, that loads the saved file from disk or seeds fresh
 * demo data. Rather than rewrite that logic to fit Spring's usual
 * no-arg-constructor bean style, this single {@code @Bean} method just calls
 * it - letting the entire service class survive the JavaFX-to-Spring-Boot
 * conversion completely unchanged.
 */
@Configuration
public class AppConfig {

    @Bean
    public HospitalDataStore hospitalDataStore() {
        return HospitalDataStore.loadOrCreate();
    }
}
