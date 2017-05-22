package com.maystrovyy.bot.core;

import com.maystrovyy.bot.ClassBotClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

@Slf4j
@Component
public final class ClassBot extends TelegramLongPollingBot {

    @Autowired
    private ClassBotClient botClient;

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            SendMessage response = new SendMessage();
            Long chatId = message.getChatId();
            response.setChatId(chatId);
            String text = message.getText();
            response.setText(text);
            send(response);
        }

        if (update.hasCallbackQuery()) {
            System.out.println(update.getCallbackQuery());
        }
    }

    private void send(SendMessage message) {
        try {
            sendMessage(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return botClient.username;
    }

    @Override
    public String getBotToken() {
        return botClient.token;
    }

}