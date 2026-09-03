package com.turismo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * Clientes HTTP salientes (RestClient / @HttpExchange) hacia los
 * servicios externos de PeruRail y SENAMHI. Las URLs base se simulan con
 * mocks desde el Sprint 0 (application.properties) y se reemplazan por
 * las APIs reales cuando esten disponibles.
 */
@Configuration
public class WebClientConfig {

    @Value("${integracion.perurail.base-url:http://localhost:9081/mock/perurail}")
    private String peruRailBaseUrl;

    @Value("${integracion.senamhi.base-url:http://localhost:9082/mock/senamhi}")
    private String senamhiBaseUrl;

    @Bean
    public RestClient peruRailRestClient() {
        return RestClient.builder().baseUrl(peruRailBaseUrl).build();
    }

    @Bean
    public RestClient senamhiRestClient() {
        return RestClient.builder().baseUrl(senamhiBaseUrl).build();
    }
}
