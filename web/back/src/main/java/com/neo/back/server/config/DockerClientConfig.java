package com.neo.back.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class DockerClientConfig {

    @Bean
    public WebClient dockerWebClient(){
        return WebClient.builder()
                .baseUrl("http://ip주소/포트번호")
                .build();
    }
}
