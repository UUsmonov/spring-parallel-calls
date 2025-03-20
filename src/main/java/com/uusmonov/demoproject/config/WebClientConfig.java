package com.uusmonov.demoproject.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;


@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(ClientProperties.class)
public class WebClientConfig {
    private final ClientProperties clientProperties;

    @Bean
    public HttpClient getHttpClient() {
        return HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, clientProperties.getTimeout().getConnect())
                .responseTimeout(Duration.ofMillis(clientProperties.getTimeout().getResponse()))
                .doOnConnected(conn ->
                        conn
                                .addHandlerLast(new ReadTimeoutHandler(clientProperties.getTimeout().getRead(), TimeUnit.MILLISECONDS))
                                .addHandlerLast(new WriteTimeoutHandler(clientProperties.getTimeout().getWrite(), TimeUnit.MILLISECONDS))
                );
    }

    @Bean("jokesWebClient")
    public WebClient jokesWebClient() {
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(getHttpClient()))
                .baseUrl(clientProperties.getBasePath())
                .build();
    }
}

