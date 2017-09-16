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
import com.maystrovyy.utils.managers.PeriodManager;
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
import java.time.DayOfWeek;
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
    private ClassBot classBot;

    @Autowired
    private WeekStorage weekStorage;

    @Autowired
    private UserService userService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private PeriodService periodService;

    @Autowired
    private GroupApiOperations groupApiOp;

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private PeriodManager periodManager;

    @Autowired
    private PeriodApiOperations periodApiOperations;

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
                Group group = groupApiOp.parse(groupName).getGroup();
                Group persistedGroup = groupService.findByName(group.getName());
                if (persistedGroup != null) {
                    persistedUser.setGroup(persistedGroup);
                    userService.update(persistedUser);
                    sendGreetingMessage(chatId, persistedUser, group);
                } else {
                    //TODO wrap to try
                    if (group.getName() != null) {
                        sendGreetingMessage(chatId, persistedUser, group);
                        groupService.save(group);
                        persistedUser.setGroup(group);
                        userService.update(persistedUser);

                        //schedule processing, add trying of parsing
                        PeriodDto dto = periodApiOperations.parse(persistedUser.getGroup().getName());

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
        } else if (message.getText() != null) {
            String text = message.getText();
            switch (text) {
                case "Розклад":
                    sendAsync(createMessageWithKeyboard(chatId, "Уточни на коли...", scheduleMenuKeyboard()));
                    break;
                case "Сьогодні":
                    sendDailyPeriods(persistedUser, chatId, LocalDate.now(), text.toLowerCase());
                    break;
                case "Завтра":
                    sendDailyPeriods(persistedUser, chatId, LocalDate.now().plus(1, ChronoUnit.DAYS), text.toLowerCase());
                    break;
                case "Тиждень":
                    sendWeeklyPeriods(persistedUser, chatId);
                    break;
                case "Екстра":
                    sendAsync(createMessageWithKeyboard(chatId, "Екстра (в розробці) Плани: змінити групу, нагадування ...",
                            extraMenuKeyboard()));
                    break;
                case "Змінити групу":
                    String oldGroup = persistedUser.getGroup().getName();
                    persistedUser.setGroup(null);
                    userService.update(persistedUser);
                    sendAsync(createMessageWithKeyboard(chatId, "Ооокей, забули про " + oldGroup + ", тепер тобі потрібно обрати собі нову групу, " +
                            "інакше я тобі нічим не зможу допомогти.", menuKeyboard()));
                    break;
                case "Викладачі":
                    sendAsync(createMessageWithKeyboard(chatId, "Викладачі (в розробці) ...", menuKeyboard()));
                    break;
                case "Нагадування":
                    sendAsync(createMessageWithKeyboard(chatId, "Нагадування (в розробці) ...", menuKeyboard()));
                    break;
                case "Контакти":
                case "Редагувати розклад":
                    sendAsync(createMessage(chatId, "З будь-яких пропозицій або проблем - стукайте прямо в лс - @maystrovyy"));
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
        } else {
            sendAsync(createMessageWithKeyboard(chatId, "Каммоооон, я просто якийсь нещасний бот, який хоче полегшити тобі життя, " +
                    "не присилай мені більше такого!", menuKeyboard()));
        }
    }

    private void sendGreetingMessage(Long chatId, User persistedUser, Group group) {
        if (persistedUser.getUserName() != null) {
            sendAsync(createMessageWithKeyboard(chatId, "Вітаю, @" + persistedUser.getUserName() + ", ти студент групи " + group.getName() +
                    ", ось тобі меню для юзабіліті!", menuKeyboard()));
        } else if (persistedUser.getFirstName() != null && persistedUser.getLastName() != null) {
            sendAsync(createMessageWithKeyboard(chatId, "Вітаю, " + persistedUser.getFirstName() + " " + persistedUser.getLastName() +
                    ", ти студент групи " + group.getName() + ", ось тобі меню для юзабіліті!", menuKeyboard()));
        } else {
            sendAsync(createMessageWithKeyboard(chatId, "Вітаю, ти студент групи " + group.getName() + ", ось тобі меню для юзабіліті!", menuKeyboard()));
        }
    }

    private void sendWeeklyPeriods(User persistedUser, Long chatId) {
        List<Period> periods;
        String groupName = persistedUser.getGroup().getName();
        if (LocalDate.now().getDayOfWeek() == DayOfWeek.SUNDAY || (LocalDate.now().getDayOfWeek() == DayOfWeek.SATURDAY &&
                periodService.findByGroupNameAndDayNumberAndLessonWeek(groupName, 6, weekStorage.getWeekNumber().getValue()).isEmpty())) {
            periods = periodService.findByGroupNameAndLessonWeek(groupName, weekStorage.reversed().getValue());
        } else {
            periods = periodService.findByGroupNameAndLessonWeek(groupName, weekStorage.getWeekNumber().getValue());
        }
        if (periods.isEmpty()) {
            sendAsync(createMessage(chatId, "Бляха, сталась якась помилка..."));
        } else {
            sendAsync(createMessage(chatId, periodManager.mapPeriods(periods)));
        }
    }

    private void sendDailyPeriods(User persistedUser, Long chatId, LocalDate date, String concreteDay) {
        String groupName = persistedUser.getGroup().getName();
        List<Period> periods = periodService.findByGroupNameAndDayNumberAndLessonWeek(groupName,
                date.getDayOfWeek().getValue(), weekStorage.getWeekNumber().getValue());
        if (periods.isEmpty()) {
            sendAsync(createMessage(chatId, "У тебе немає " + concreteDay + " пар - свабодєн."));
        } else {
            sendAsync(createMessageForDailyPeriods(chatId, periodManager.mapPeriods(periods, LocalDate.now().getDayOfWeek())));
        }
    }

    private void sendDailyPeriodsDetailed(Update update, User persistedUser, Long chatId) {
        List<Period> periods = periodService.findByGroupNameAndDayNumberAndLessonWeek(persistedUser.getGroup().getName(),
                LocalDate.now().getDayOfWeek().getValue(), weekStorage.getWeekNumber().getValue());
        if (periods.isEmpty()) {
            sendAsync(createMessage(chatId, "Ти шо хітрєц, не буду я тобі нічого деталізувати, відпочивай!"));
        } else {
            editAsync(editMessageForDailyPeriods(update.getCallbackQuery(), periodManager.mapPeriodsDetailed(periods, LocalDate.now().getDayOfWeek())));
        }
    }

    @Override
    public void processCallback(Update update) {
        String data = update.getCallbackQuery().getData();
        User persistedUser = userService.findByUserName(update.getCallbackQuery().getFrom().getUserName());
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        switch (data) {
            case "%details%":
                sendDailyPeriodsDetailed(update, persistedUser, chatId);
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
            public void onResult(BotApiMethod<Message> botApiMethod, Message message) {  }

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
            public void onResult(BotApiMethod<Serializable> botApiMethod, Serializable serializable) {  }

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