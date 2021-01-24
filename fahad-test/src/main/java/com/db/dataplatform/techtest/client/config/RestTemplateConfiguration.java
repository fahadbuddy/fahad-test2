package com.db.dataplatform.techtest.client.config;

import lombok.RequiredArgsConstructor;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@Configuration
@RequiredArgsConstructor
public class RestTemplateConfiguration {


    @Bean
    public RestTemplate createRestTemplate(MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter,
                                           StringHttpMessageConverter stringHttpMessageConverter,
                                           final RestTemplateBuilder restTemplateBuilder) {

        RestTemplate restTemplate = restTemplateBuilder.build();

        CloseableHttpClient client = HttpClients.createDefault();
        RestTemplate template= new RestTemplate();
        template.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
        template.setMessageConverters(Arrays.asList(mappingJackson2HttpMessageConverter, stringHttpMessageConverter));

        return restTemplate;
    }
    
    @Bean
    public RestTemplateBuilder restTemplateBuilder() {
        return new RestTemplateBuilder();
    }

    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        return new MappingJackson2HttpMessageConverter();
    }

    @Bean
    public StringHttpMessageConverter stringHttpMessageConverter() {
        return new StringHttpMessageConverter();
    }

}
