package com.maystrovyy.bot;

import com.maystrovyy.bot.core.ClassBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.generics.BotSession;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component
public final class ClassBotInitializer {

    @Autowired
    private ClassBot classBot;

    @Autowired
    private TelegramBotsApi telegramBotsApi;

    private BotSession botSession;

    static {
		ApiContextInitializer.init();
	}

    @PostConstruct
    private void initialize() {
        try {
            botSession = telegramBotsApi.registerBot(classBot);
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }
    }

    @PreDestroy
    private void tearDown() {
        botSession.stop();
    }

}