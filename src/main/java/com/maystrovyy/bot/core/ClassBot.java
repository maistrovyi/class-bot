package com.maystrovyy.bot.core;

import com.maystrovyy.bot.ClassBotClient;
import com.maystrovyy.models.Messages;
import com.maystrovyy.models.User;
import com.maystrovyy.services.UserService;
import com.maystrovyy.utills.UserConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public final class ClassBot extends TelegramLongPollingBot {

    @Autowired
    private ClassBotClient botClient;

    @Autowired
    private UserService userService;

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            String telegramUserName = message.getFrom().getUserName();
            User persistedUser = userService.findByUserName(telegramUserName);
            if (persistedUser == null) {
                send(createMessageWithButton(message.getChatId()));
                User user = UserConverter.toUser(update.getMessage().getFrom());
                userService.save(user);
            }
        }

        if (update.hasCallbackQuery()) {
            SendMessage message = createMessage(update.getCallbackQuery().getMessage().getChatId(), update.getCallbackQuery().getData());
            send(message);
        }
    }

    private SendMessage createMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        return message;
    }

    private void send(SendMessage message) {
        try {
            sendMessage(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private SendMessage createMessageWithButton(Long chatId) {
        SendMessage message = new SendMessage();
        message.setText(Messages.GREETING);
        message.setChatId(chatId);

        ArrayList<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText("Студент");
        button.setCallbackData("%iamstudent%");
        row.add(button);

        button.setText("Преподаватель");
        button.setCallbackData("%iamteacher%");
        row.add(button);

        ArrayList<List<InlineKeyboardButton>> rows = new ArrayList<>();
        rows.add(row);

        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        keyboard.setKeyboard(rows);

        message.setReplyMarkup(keyboard);
        return message;
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