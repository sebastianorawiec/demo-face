package com.azure.face.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class RestTemplateConfig {


    @Value("${azure.key}")
    private String key;


    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        RestTemplate restTemplate = builder.
                setConnectTimeout(Duration.ofMillis(3000)).
                setReadTimeout(Duration.ofMillis(3000)).build();
        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<ClientHttpRequestInterceptor>();
        // add to every azure azure key
        interceptors.add(new HeaderRequestInterceptor("Ocp-Apim-Subscription-Key",key ));
        restTemplate.setInterceptors(interceptors);
        return restTemplate;
    }

}
