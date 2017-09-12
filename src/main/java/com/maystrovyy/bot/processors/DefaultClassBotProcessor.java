package com.maystrovyy.bot.processors;

import com.maystrovyy.bot.Messages;
import com.maystrovyy.bot.core.ClassBot;
import com.maystrovyy.models.Group;
import com.maystrovyy.models.Schedule;
import com.maystrovyy.models.User;
import com.maystrovyy.rozkladj.api.GroupApiOperations;
import com.maystrovyy.rozkladj.api.ScheduleApiOperations;
import com.maystrovyy.services.ScheduleService;
import com.maystrovyy.services.UserService;
import com.maystrovyy.utils.managers.ScheduleManager;
import com.maystrovyy.utils.managers.UserDtoManager;
import io.vavr.control.Try;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;

@Component
public final class DefaultClassBotProcessor implements ClassBotProcessor {

    @Autowired
    private ClassBot classBot;

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

    @Override
    public void processUpdate(Update update) {
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

    @Override
    public void processCallback(Update update) {
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

    private void send(SendMessage message) {
        Try.of(() -> classBot.execute(message));
    }

}