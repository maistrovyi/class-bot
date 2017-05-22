package com.maystrovyy.configs;

import com.maystrovyy.bot.ClassBotClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.telegram.telegrambots.TelegramBotsApi;

@Configuration
@ComponentScan
@PropertySource(value = "/config/secret.properties")
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
    public TelegramBotsApi telegramBotsApi() {
        return new TelegramBotsApi();
    }

}