package com.maystrovyy.bot.processors;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

public interface ClassBotProcessor {

    void processUpdate(Update update);
    void processCallback(Update update);

    default SendMessage createMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        return message;
    }

    default SendMessage createMessageWithKeyboard(Long chatId, String text) {
        SendMessage message = createMessage(chatId, text);
        message.setReplyMarkup(menuKeyboard());
        return message;
    }

    default SendMessage createMessageWithButton(Long chatId, String text) {
        SendMessage message = createMessage(chatId, text);

        ArrayList<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton firstButton = new InlineKeyboardButton();
        firstButton.setText("Студент");
        firstButton.setCallbackData("%iamstudent%");
        row.add(firstButton);

        InlineKeyboardButton secondButton = new InlineKeyboardButton();
        secondButton.setText("Викладач");
        secondButton.setCallbackData("%iamteacher%");
        row.add(secondButton);

        ArrayList<List<InlineKeyboardButton>> rows = new ArrayList<>();
        rows.add(row);

        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        keyboard.setKeyboard(rows);

        message.setReplyMarkup(keyboard);
        return message;
    }

    default ReplyKeyboardMarkup menuKeyboard() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow keyboardFirstRow = new KeyboardRow();
        KeyboardButton button1 = new KeyboardButton("Розклад");
        KeyboardButton button2 = new KeyboardButton("Тиждень");
        keyboardFirstRow.add(button1);
        keyboardFirstRow.add(button2);

        KeyboardRow keyboardSecondRow = new KeyboardRow();
        KeyboardButton button3 = new KeyboardButton("Нагадування");
        KeyboardButton button4 = new KeyboardButton("Екзамени");
        keyboardSecondRow.add(button3);
        keyboardSecondRow.add(button4);

        KeyboardRow keyboardFourthRow = new KeyboardRow();
        KeyboardButton button6 = new KeyboardButton("Допомога");
        keyboardFourthRow.add(button6);

        keyboard.add(keyboardFirstRow);
        keyboard.add(keyboardSecondRow);
        keyboard.add(keyboardFourthRow);
        replyKeyboardMarkup.setKeyboard(keyboard);

        return replyKeyboardMarkup;
    }

}
