package com.maystrovyy.bot.core;

import com.maystrovyy.bot.processors.ClassBotProcessor;
import com.maystrovyy.bot.processors.DefaultClassBotProcessor;
import io.vavr.control.Try;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;

@Slf4j
public final class ClassBot extends TelegramLongPollingBot {

    @Getter
    private final String botToken;

    @Getter
    private final String botUsername;

    private ClassBotProcessor processor;

    @Builder(builderMethodName = "of")
    public ClassBot(@NotNull String botToken, @NotNull String botUsername, @NotNull DefaultClassBotProcessor processor) {
        super();
        this.botToken = botToken;
        this.botUsername = botUsername;
        this.processor = processor;
    }

    @PostConstruct
    private void init() {
        Try.of(() -> new TelegramBotsApi().registerBot(this));
        log.info("Telegram bot successfully initialized!");
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            processor.processUpdate(update);
        }

        if (update.hasCallbackQuery()) {
            processor.processCallback(update);
        }
    }

}