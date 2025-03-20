package com.uusmonov.demoproject.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(ClientProperties.class)
public class RestTemplateConfig {
    private final ClientProperties clientProperties;

    ClientHttpRequestFactory getClientHttpRequestFactory() {
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new
                HttpComponentsClientHttpRequestFactory();
        clientHttpRequestFactory.setConnectionRequestTimeout(clientProperties.getTimeout().getResponse());
        clientHttpRequestFactory.setConnectTimeout(clientProperties.getTimeout().getConnect());
        clientHttpRequestFactory.setReadTimeout(clientProperties.getTimeout().getRead());
        return clientHttpRequestFactory;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate(getClientHttpRequestFactory());
    }
}
