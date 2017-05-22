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
                send(createMessageWithButton(message.getChatId(), Messages.GREETING));
                User user = UserConverter.toUser(update.getMessage().getFrom());
                userService.save(user);
            } else if (persistedUser.getRole() == null) {
                send(createMessageWithButton(message.getChatId(), "Пожалуйста, укажи свою роль!"));
            }
        }

        if (update.hasCallbackQuery()) {
            String data = update.getCallbackQuery().getData();
//            TODO check for null
            User persistedUser = userService.findByUserName(update.getCallbackQuery().getFrom().getUserName());
            switch (data) {
                case "%iamstudent%":
                    persistedUser.setRole(User.Role.STUDENT);
                    userService.update(persistedUser);
                    send(createMessage(update.getCallbackQuery().getMessage().getChatId(), "Отлично, а теперь укажи свою группу! Например \"ВВ-41\" или же \"vv-41\""));
                    break;
                case "%iamteacher%":
                    persistedUser.setRole(User.Role.TEACHER);
                    userService.update(persistedUser);
                    send(createMessage(update.getCallbackQuery().getMessage().getChatId(), "Отлично, препод!"));
                    break;
            }
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

    private SendMessage createMessageWithButton(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setText(text);
        message.setChatId(chatId);

        ArrayList<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton firstButton = new InlineKeyboardButton();
        firstButton.setText("Студент");
        firstButton.setCallbackData("%iamstudent%");
        row.add(firstButton);

        InlineKeyboardButton secondButton = new InlineKeyboardButton();
        secondButton.setText("Преподаватель");
        secondButton.setCallbackData("%iamteacher%");
        row.add(secondButton);

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