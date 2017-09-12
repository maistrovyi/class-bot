/*
package com.maystrovyy.bot;

import com.maystrovyy.bot.core.ClassBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;

import javax.annotation.PostConstruct;

@Component
public final class ClassBotInitializer {

    @Autowired
    private ClassBot classBot;

    // TODO: 9/11/17 add Vavr exception wrapping
    @PostConstruct
    private void initialize() {
        try {
            ApiContextInitializer.init();
            new TelegramBotsApi().registerBot(classBot);
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }
    }

}
*/
