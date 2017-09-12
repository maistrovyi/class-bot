/*
package com.maystrovyy.configs;

import com.maystrovyy.bot.ClassBotClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;

@SpringBootConfiguration
@PropertySource(value = "classpath:/config/secret.properties")
public class ClassBotConfiguration {

    @Value(value = "${token}")
    public String token;

    @Value(value = "${username}")
    public String username;

    @Bean
    public ClassBotClient classBotClient() {
        return ClassBotClient.of()
                .token(token)
                .username(username)
                .create();
    }

}
*/
