package com.maystrovyy.bot.core;

import com.maystrovyy.bot.ClassBotClient;
import com.maystrovyy.bot.Messages;
import com.maystrovyy.models.Group;
import com.maystrovyy.models.Schedule;
import com.maystrovyy.models.User;
import com.maystrovyy.rozkladj.api.GroupApiOperations;
import com.maystrovyy.rozkladj.api.ScheduleApiOperations;
import com.maystrovyy.services.ScheduleService;
import com.maystrovyy.services.UserService;
import com.maystrovyy.utils.managers.ScheduleManager;
import com.maystrovyy.utils.managers.UserDtoManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
//import org.telegram.telegrambots.bots.TelegramLongPollingBot;
//import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public final class ClassBot {

    @Autowired
    private ClassBotClient botClient;

    @Autowired
    private UserService userService;

    @Autowired
    private ScheduleApiOperations scheduleApiOp;

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private GroupApiOperations groupApiOp;

    @Autowired
    private ScheduleManager scheduleManager;

//    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            String telegramUserName = message.getFrom().getUserName();
            User persistedUser = userService.findByUserName(telegramUserName);
            Long chatId = message.getChatId();
            if (persistedUser == null) {
                send(createMessageWithButton(chatId, Messages.GREETING));
                User user = UserDtoManager.toUser(update.getMessage().getFrom());
                userService.save(user);
            } else if (persistedUser.getRole() == null) {
                send(createMessageWithButton(chatId, "Будь ласка, вибери свою роль!"));
            } else if (persistedUser.getGroupName() == null) {
                String groupName = message.getText().toLowerCase();
                if (groupApiOp.isValidGroupName(groupName)) {
                    Group group = groupApiOp.parse(groupName);
                    if (group != null) {
                        send(createMessage(chatId, "Крутяк, я запам\'ятав, що ти з " + groupName + "!"));
                        send(createMessageWithKeyboard(chatId, "Лови за це менюху!"));
//                        TODO fix Group mapping
                        persistedUser.setGroupName(groupName);
                        userService.update(persistedUser);
                        Schedule schedule = scheduleApiOp.parse(groupName);
                        scheduleService.save(schedule);
                    } else {
                        send(createMessage(chatId, "На жаль, я не знайшов такої групи..."));
                    }
                } else {
                    send(createMessage(chatId, "Будь ласка, вкажи свою групу!"));
                }
            } else {
                String text = message.getText();
                switch (text) {
                    case "Розклад":
                        String groupName = persistedUser.getGroupName();
                        Schedule schedule = scheduleService.findByGroupName(groupName);
                        if (schedule != null) {
                            send(createMessage(chatId, scheduleManager.dailyScheduleToTelegramText(schedule)));
                        } else {
                            send(createMessageWithKeyboard(chatId, "Бляха, сталась якась помилка..."));
                        }
                        break;
                    default:
                        send(createMessageWithKeyboard(chatId, "Сорян, але я тебе не зрозумів..."));
                }
            }
        }

        if (update.hasCallbackQuery()) {
            String data = update.getCallbackQuery().getData();
            User persistedUser = userService.findByUserName(update.getCallbackQuery().getFrom().getUserName());
            switch (data) {
                case "%iamstudent%":
                    persistedUser.setRole(User.Role.STUDENT);
                    userService.update(persistedUser);
                    send(createMessage(update.getCallbackQuery().getMessage().getChatId(), "Харош, скажи но свою групу, наприклад  \"ВВ-41\"."));
                    break;
                case "%iamteacher%":
                    persistedUser.setRole(User.Role.TEACHER);
                    userService.update(persistedUser);
                    send(createMessage(update.getCallbackQuery().getMessage().getChatId(), "Вітаю, Вас, шановний!"));
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

    private SendMessage createMessageWithKeyboard(Long chatId, String text) {
        SendMessage message = createMessage(chatId, text);
        message.setReplyMarkup(menuKeyboard());
        return message;
    }

    private void send(SendMessage message) {
//        try {
//            sendMessage(message);
//        } catch (TelegramApiException e) {
//            e.printStackTrace();
//        }
    }

    private SendMessage createMessageWithButton(Long chatId, String text) {
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

    private ReplyKeyboardMarkup menuKeyboard() {
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

    /*@Override
    public String getBotUsername() {
        return botClient.username;
    }

    @Override
    public String getBotToken() {
        return botClient.token;
    }*/

}