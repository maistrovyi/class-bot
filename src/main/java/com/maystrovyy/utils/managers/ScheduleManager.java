package com.maystrovyy.utils.managers;

import com.maystrovyy.models.Period;
import com.maystrovyy.models.Week.WeekNumber;
import com.maystrovyy.storage.WeekStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static java.time.DayOfWeek.*;

@Component
public class ScheduleManager {

    @Autowired
    private WeekStorage weekStorage;

    private List<Period> getPeriods(List<Period> periods, DayOfWeek dayOfWeek, WeekNumber weekNumber) {
        return periods.stream()
                .filter(period -> period.getDayOfWeek() == dayOfWeek)
                .filter(Objects::nonNull)
                .filter(period -> period.getLessonWeek() == weekNumber.getValue())
                .collect(Collectors.toList());
    }

    public String getTodaySchedule(List<Period> periods) {
        return dailyScheduleToTelegramText(periods, LocalDate.now().getDayOfWeek());
    }

    public String getTomorrowSchedule(List<Period> periods) {
        return dailyScheduleToTelegramText(periods, LocalDate.now().plusDays(1).getDayOfWeek());
    }

    public String getWeekSchedule(List<Period> periods) {
        StringBuilder builder = new StringBuilder();
        WeekNumber weekNumber = weekStorage.getWeekNumber();

        List<Period> validPeriods = periods.stream()
                .filter(period -> period.getLessonWeek() == weekNumber.getValue())
                .sorted()
                .collect(Collectors.toList());

        List<Period> list = new ArrayList<>();

        EnumSet.allOf(DayOfWeek.class).forEach(dayOfWeek -> {
            List<Period> periodsPerDay = validPeriods.stream().filter(period -> period.getDayOfWeek() == dayOfWeek).collect(Collectors.toList());
            if (!periodsPerDay.isEmpty()) {
                Collections.sort(periodsPerDay);
                list.addAll(periodsPerDay);
            }
        });

        Map<DayOfWeek, List<Period>> map = list.stream().collect(Collectors.groupingBy(Period::getDayOfWeek));

        Map<DayOfWeek, List<Period>> linkedMap = new LinkedHashMap<>();
        EnumSet.allOf(DayOfWeek.class).forEach(dayOfWeek -> {
            if (map.containsKey(dayOfWeek)) {
                linkedMap.put(dayOfWeek, map.get(dayOfWeek));
            }
        });

        linkedMap.forEach((dayOfWeek, periodList) -> {
            builder.append(getUkrainianDayOfWeek(dayOfWeek));
            periodList.forEach(period -> builder.append("\n \t")
                    .append(period.getLessonNumber())
                    .append(". ")
                    .append(period.getLessonName())
                    .append("\n")
                    .append("\t \t \t ")
                    .append(period.getLessonRoom())
                    .append(", ")
                    .append(period.getLessonType())
                    .append(", (")
                    .append(period.getFromTime())
                    .append(" - ")
                    .append(period.getToTime())
                    .append(")")
                    .append("\n")
                    .append("\t \t \t ")
                    .append(period.getTeacherName()));
            builder.append("\n \n");
        });

        return builder.toString();
    }

    private String dailyScheduleToTelegramText(List<Period> periods, DayOfWeek dayOfWeek) {
        StringBuilder builder = new StringBuilder();
        List<Period> dailyPeriods;

        if (dayOfWeek == SATURDAY) {
            if (periods.stream().noneMatch(period -> period.getDayOfWeek() == SATURDAY)) {
                dayOfWeek = MONDAY;
            }
        }

        dailyPeriods = getPeriods(periods, dayOfWeek, weekStorage.getWeekNumber());
        Collections.sort(dailyPeriods);

        builder.append(getUkrainianDayOfWeek(dayOfWeek));
        dailyPeriods.forEach(period -> builder.append("\n \t")
                .append(period.getLessonNumber())
                .append(". ")
                .append(period.getLessonName())
                .append("\n")
                .append("\t \t \t ")
                .append(period.getLessonRoom())
                .append(", ")
                .append(period.getLessonType())
                .append(", (")
                .append(period.getFromTime())
                .append(" - ")
                .append(period.getToTime())
                .append(")")
                .append("\n")
                .append("\t \t \t ")
                .append(period.getTeacherName()));
        return builder.toString();
    }

    private DayOfWeek getCorrectDay() {
        DayOfWeek dayOfWeek = LocalDate.now().getDayOfWeek();
        return (dayOfWeek == SUNDAY) ? MONDAY : dayOfWeek;
    }

    private String getUkrainianDayOfWeek(DayOfWeek dayOfWeek) {
        switch (dayOfWeek) {
            case TUESDAY:
                return "Вівторок:";
            case WEDNESDAY:
                return "Середа:";
            case THURSDAY:
                return "Четвер:";
            case FRIDAY:
                return "П\'ятниця:";
            case SATURDAY:
                return "Субота:";
            default:
                return "Понеділок:";
        }
    }

}