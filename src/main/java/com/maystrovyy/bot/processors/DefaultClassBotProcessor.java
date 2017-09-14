package com.maystrovyy.bot.processors;

import com.maystrovyy.bot.Messages;
import com.maystrovyy.bot.core.ClassBot;
import com.maystrovyy.models.*;
import com.maystrovyy.rozkladj.api.GroupApiOperations;
import com.maystrovyy.rozkladj.api.ScheduleApiOperations;
import com.maystrovyy.services.ScheduleService;
import com.maystrovyy.services.TeacherService;
import com.maystrovyy.services.UserService;
import com.maystrovyy.utils.managers.ScheduleManager;
import com.maystrovyy.utils.managers.UserDtoManager;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.BotApiMethod;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageCaption;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.updateshandlers.SentCallback;

import java.io.Serializable;
import java.util.Collection;
import java.util.Optional;

import static com.maystrovyy.models.Role.STUDENT;
import static com.maystrovyy.models.Role.TEACHER;

@Slf4j
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

    @Autowired
    private TeacherService teacherService;

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
                    send(createMessageWithKeyboard(chatId, "Лови за це менюху!", menuKeyboard()));
//                        TODO fix Group mapping
                    persistedUser.setGroupName(groupName);
                    userService.update(persistedUser);

                    //schedule processing, add trying of paring
                    Schedule schedule = scheduleApiOp.parse(groupName);

                    schedule.getPeriods().stream()
                            .map(Period::getTeachers)
                            .flatMap(Collection::stream)
                            .forEach(teacher -> {
                                Optional<Teacher> persistedTeacher = teacherService.findByApiId(teacher.getApiId());
                                if (!persistedTeacher.isPresent()) {
                                    teacherService.save(teacher);
                                } else {
                                    teacher.setId(persistedTeacher.get().getId());
                                }
                            });

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
                    send(createMessageWithKeyboard(chatId, "Уточни на коли...", scheduleMenuKeyboard()));
                    break;
                case "Сьогодні":
                    String groupName = persistedUser.getGroupName();
                    Schedule schedule = scheduleService.findByGroupName(groupName);
                    if (schedule != null) {
//                        send(createMessage(chatId, scheduleManager.getTodaySchedule(schedule)));
                        sendAsync(createMessageForSchedule(chatId, scheduleManager.getTodaySchedule(schedule)));
                    } else {
                        send(createMessageWithKeyboard(chatId, "Бляха, сталась якась помилка...", menuKeyboard()));
                    }
                    break;
                case "Завтра":
                    groupName = persistedUser.getGroupName();
                    schedule = scheduleService.findByGroupName(groupName);
                    if (schedule != null) {
                        send(createMessage(chatId, scheduleManager.getTomorrowSchedule(schedule)));
                    } else {
                        send(createMessageWithKeyboard(chatId, "Бляха, сталась якась помилка...", menuKeyboard()));
                    }
                    break;
                case "Тиждень":
                    groupName = persistedUser.getGroupName();
                    schedule = scheduleService.findByGroupName(groupName);
                    if (schedule != null) {
                        send(createMessage(chatId, scheduleManager.getWeekSchedule(schedule)));
                    } else {
                        send(createMessageWithKeyboard(chatId, "Бляха, сталась якась помилка...", menuKeyboard()));
                    }
                    break;
                case "Допомога":
//                    send(test(chatId));
                    break;
                case "Назад": {
                    send(createMessageWithKeyboard(chatId, "На головну...", menuKeyboard()));
                    break;
                }
                default:
                    send(createMessageWithKeyboard(chatId, "Сорян, але я тебе не зрозумів...", menuKeyboard()));
            }
        }
    }

    @Override
    public void processCallback(Update update) {
        String data = update.getCallbackQuery().getData();
        User persistedUser = userService.findByUserName(update.getCallbackQuery().getFrom().getUserName());
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        switch (data) {
            case "%details%":
                editAsync(editMessageForSchedule(update.getCallbackQuery()));
                break;
            case "%iamstudent%":
                persistedUser.setRole(STUDENT);
                userService.update(persistedUser);
                send(createMessage(chatId, "Харош, скажи но свою групу, наприклад  \"ВВ-41\"."));
                break;
            case "%iamteacher%":
                persistedUser.setRole(TEACHER);
                userService.update(persistedUser);
                send(createMessage(chatId, "Вітаю, Вас, шановний!"));
                break;
        }
    }

    private void send(SendMessage message) {
        Try.of(() -> classBot.execute(message));
    }

    private void sendAsync(SendMessage message) {
        Try.run(() -> classBot.executeAsync(message, new SentCallback<Message>() {
            @Override
            public void onResult(BotApiMethod<Message> botApiMethod, Message message) {

            }

            @Override
            public void onError(BotApiMethod<Message> botApiMethod, TelegramApiRequestException e) {
                log.error(e.getLocalizedMessage());
            }

            @Override
            public void onException(BotApiMethod<Message> botApiMethod, Exception e) {
                log.error(e.getLocalizedMessage());
            }
        }));
    }

    private void edit(EditMessageCaption message) {
        Try.of(() -> classBot.execute(message));
    }

    private void editAsync(EditMessageText message) {
        Try.run(() -> classBot.executeAsync(message, new SentCallback<Serializable>() {
            @Override
            public void onResult(BotApiMethod<Serializable> botApiMethod, Serializable serializable) {

            }

            @Override
            public void onError(BotApiMethod<Serializable> botApiMethod, TelegramApiRequestException e) {
                log.error(e.getLocalizedMessage());
            }

            @Override
            public void onException(BotApiMethod<Serializable> botApiMethod, Exception e) {
                log.error(e.getLocalizedMessage());
            }
        }));
    }

}