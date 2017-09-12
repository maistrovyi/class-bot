package com.maystrovyy.configs;

import com.maystrovyy.bot.core.ClassBot;
import com.maystrovyy.bot.processors.DefaultClassBotProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.telegram.telegrambots.ApiContextInitializer;

@SpringBootConfiguration
@PropertySource(value = "classpath:/config/secret.properties")
public class ClassBotConfiguration {

    @Autowired
    private ApplicationContext context;

    @Value(value = "${token}")
    public String token;

    @Value(value = "${username}")
    public String username;

    static {
        ApiContextInitializer.init();
    }

    @Bean
    public ClassBot classBot() {
        return ClassBot.of()
                .botToken(token)
                .botUsername(username)
                .processor(context.getBean(DefaultClassBotProcessor.class))
                .build();
    }

}