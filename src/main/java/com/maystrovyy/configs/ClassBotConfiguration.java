package com.maystrovyy.configs;

import com.maystrovyy.bot.ClassBotClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.TelegramBotsApi;

@Configuration
@ComponentScan
@PropertySource(value = "classpath:/config/secret.properties")
public class ClassBotConfiguration {

    private static final String USERNAME = "ClassBot";

    @Value(value = "${token}")
    public String token;

    @Bean
    public ClassBotClient botClassInstance() {
        return ClassBotClient.of()
                .token(token)
                .username(USERNAME)
                .create();
    }

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
//        restTemplate.getMessageConverters().add(0 ,mappingJackson2HttpMessageConverter());
        return restTemplate;
    }

    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        return new MappingJackson2HttpMessageConverter();
    }

    @Bean
    public TelegramBotsApi telegramBotsApi() {
        return new TelegramBotsApi();
    }

}