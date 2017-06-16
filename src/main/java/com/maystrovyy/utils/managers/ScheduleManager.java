package com.maystrovyy.utils.managers;

import com.maystrovyy.models.Period;
import com.maystrovyy.models.Schedule;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static java.time.DayOfWeek.*;

@Component
public class ScheduleManager {

    private List<Period> getPeriods(Set<Period> periods, DayOfWeek dayOfWeek, int week) {
        return periods.stream()
                .filter(period -> period.getDayOfWeek() == dayOfWeek)
                .filter(Objects::nonNull)
                .filter(period -> period.getLessonWeek() == week)
                .collect(Collectors.toList());
    }

    public String dailyScheduleToTelegramText(Schedule schedule) {
        StringBuilder builder = new StringBuilder();
        Set<Period> periods = schedule.getPeriods();
        List<Period> dailyPeriods;
        DayOfWeek dayOfWeek = LocalDate.now().getDayOfWeek();

        if (dayOfWeek == SATURDAY) {
            if (periods.stream().noneMatch(period -> period.getDayOfWeek() == SATURDAY)) {
                dayOfWeek = MONDAY;
            }
        }

        dailyPeriods = getPeriods(periods, dayOfWeek, 1);
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