package com.maystrovyy.bot.processors;

import com.maystrovyy.bot.Messages;
import com.maystrovyy.bot.core.ClassBot;
import com.maystrovyy.models.Group;
import com.maystrovyy.models.Period;
import com.maystrovyy.models.Teacher;
import com.maystrovyy.models.User;
import com.maystrovyy.models.dto.PeriodDto;
import com.maystrovyy.rozklad4j.api.GroupApiOperations;
import com.maystrovyy.rozklad4j.api.PeriodApiOperations;
import com.maystrovyy.services.GroupService;
import com.maystrovyy.services.PeriodService;
import com.maystrovyy.services.TeacherService;
import com.maystrovyy.services.UserService;
import com.maystrovyy.storage.WeekStorage;
import com.maystrovyy.utils.managers.ScheduleManager;
import com.maystrovyy.utils.managers.UserDtoManager;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.BotApiMethod;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.updateshandlers.SentCallback;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static com.maystrovyy.models.Role.STUDENT;
import static com.maystrovyy.models.Role.TEACHER;

@Slf4j
@Component
public final class DefaultClassBotProcessor implements ClassBotProcessor {

    @Autowired
    private WeekStorage weekStorage;

    @Autowired
    private ClassBot classBot;

    @Autowired
    private UserService userService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private PeriodApiOperations periodApiOperations;

    @Autowired
    private PeriodService periodService;

    @Autowired
    private GroupApiOperations groupApiOp;

    @Autowired
    private ScheduleManager scheduleManager;

    @Autowired
    private TeacherService teacherService;

    @Override
    public void processUpdate(Update update) {
        Message message = update.getMessage();
        Long chatId = message.getChatId();
        String telegramUserName = message.getFrom().getUserName();
        User persistedUser = userService.findByUserName(telegramUserName);
        if (persistedUser == null) {
            sendAsync(createMessageWithButton(chatId, Messages.GREETING));
            User user = UserDtoManager.toUser(update.getMessage().getFrom());
            userService.save(user);
        } else if (persistedUser.getRole() == null) {
            sendAsync(createMessageWithButton(chatId, "Будь ласка, вибери свою роль!"));
        } else if (persistedUser.getGroup() == null) {
            String groupName = message.getText().toLowerCase();
            if (groupApiOp.isValidGroupName(groupName)) {
                Group persistedGroup = groupService.findByName(groupName);
                if (persistedGroup != null) {
                    persistedUser.setGroup(persistedGroup);
                    userService.update(persistedUser);
                } else {
                    Group group = groupApiOp.parse(groupName);
                    if (group.getName() != null) {
                        sendAsync(createMessageWithKeyboard(chatId, "Вітаю, @" + persistedUser.getUserName() + ", ти студент групи " + group.getName() + ", ось тобі меню для юзабіліті!", menuKeyboard()));
//                        TODO fix Group mapping
                        persistedUser.setGroup(group);
                        userService.update(persistedUser);

                        //schedule processing, add trying of parsing
                        PeriodDto dto = periodApiOperations.parse(groupName);

                        dto.periods.stream()
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

                        periodService.save(dto.periods);
                    } else {
                        sendAsync(createMessage(chatId, "На жаль, я не знайшов такої групи..."));
                    }
                }
            } else {
                sendAsync(createMessage(chatId, "Будь ласка, вкажи свою групу!"));
            }
        } else {
            String text = message.getText();
            switch (text) {
                case "Розклад":
                    sendAsync(createMessageWithKeyboard(chatId, "Уточни на коли...", scheduleMenuKeyboard()));
                    break;
                case "Сьогодні":
                    sendDailyPeriods(persistedUser, chatId, LocalDate.now());
                    break;
                case "Завтра":
                    sendDailyPeriods(persistedUser, chatId, LocalDate.now().plus(1, ChronoUnit.DAYS));
                    break;
                case "Тиждень":
                    sendWeeklyPeriods(persistedUser, chatId);
                    break;
                case "Екстра":
                    sendAsync(createMessageWithKeyboard(chatId, "Викладачі (в розробці) Плани: змінити групу, нагадування, екзамени ...", extraMenuKeyboard()));
                    break;
                case "Змінити групу":
                    String oldGroup = persistedUser.getGroup().getName();
                    persistedUser.setGroup(null);
                    userService.update(persistedUser);
                    sendAsync(createMessageWithKeyboard(chatId, "Ооокей, забули про " + oldGroup + ", тепер тобі потрібно обрати собі нову групу, інакше я тобі нічим не зможу допомогти.", menuKeyboard()));
                    break;
                case "Викладачі":
                    sendAsync(createMessageWithKeyboard(chatId, "Викладачі (в розробці) ...", menuKeyboard()));
                    break;
                case "Нагадування":
                    sendAsync(createMessageWithKeyboard(chatId, "Нагадування (в розробці) ...", menuKeyboard()));
                    break;
                case "Екзамени":
                    sendAsync(createMessageWithKeyboard(chatId, "Екзамени (в розробці) ...", menuKeyboard()));
                    break;
                case "Допомога":
                    sendAsync(createMessageWithKeyboard(chatId, "Допомога (в розробці) ...", menuKeyboard()));
                    break;
                case "Назад": {
                    sendAsync(createMessageWithKeyboard(chatId, "На головну...", menuKeyboard()));
                    break;
                }
                default:
                    sendAsync(createMessageWithKeyboard(chatId, "Сорян, але я тебе не зрозумів...", menuKeyboard()));
            }
        }
    }

    private void sendWeeklyPeriods(User persistedUser, Long chatId) {
        String groupName = persistedUser.getGroup().getName();
        List<Period> periods = periodService.findByGroupNameAndLessonWeek(groupName, weekStorage.getWeekNumber().getValue());
        if (periods.isEmpty()) {
            sendAsync(createMessage(chatId, "Бляха, сталась якась помилка..."));
        } else {
            sendAsync(createMessage(chatId, scheduleManager.mapPeriods(periods)));
        }
    }

    private void sendDailyPeriods(User persistedUser, Long chatId, LocalDate date) {
        String groupName = persistedUser.getGroup().getName();
        List<Period> periods = periodService.findByGroupNameAndDayNumberAndLessonWeek(groupName,
                date.getDayOfWeek().getValue(), weekStorage.getWeekNumber().getValue());
        if (periods.isEmpty()) {
            sendAsync(createMessage(chatId, "У тебе немає сьогодні пар - йди гуляй."));
        } else {
            sendAsync(createMessageForDailyPeriods(chatId, scheduleManager.mapPeriods(periods, LocalDate.now().getDayOfWeek())));
        }
    }

    @Override
    public void processCallback(Update update) {
        String data = update.getCallbackQuery().getData();
        User persistedUser = userService.findByUserName(update.getCallbackQuery().getFrom().getUserName());
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        switch (data) {
            case "%details%":
                List<Period> periods = periodService.findByGroupNameAndDayNumberAndLessonWeek(persistedUser.getGroup().getName(),
                        LocalDate.now().getDayOfWeek().getValue(), weekStorage.getWeekNumber().getValue());
                if (periods.isEmpty()) {
                    sendAsync(createMessage(chatId, "У тебе немає сьогодні пар - йди гуляй."));
                } else {
                    editAsync(editMessageForDailyPeriods(update.getCallbackQuery(), scheduleManager.mapPeriodsDetailed(periods, LocalDate.now().getDayOfWeek())));
                }
                break;
            case "%iamstudent%":
                persistedUser.setRole(STUDENT);
                userService.update(persistedUser);
                sendAsync(createMessage(chatId, "Харош, скажи но свою групу, наприклад  \"ВВ-41\"."));
                break;
            case "%iamteacher%":
                persistedUser.setRole(TEACHER);
                userService.update(persistedUser);
                sendAsync(createMessage(chatId, "Вітаю, Вас, шановний!"));
                break;
        }
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