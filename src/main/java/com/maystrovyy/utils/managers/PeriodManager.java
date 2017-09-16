package com.maystrovyy.utils.managers;

import com.maystrovyy.models.Period;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static java.time.DayOfWeek.MONDAY;
import static java.time.DayOfWeek.SUNDAY;

@Component
public class PeriodManager {

    private static final String NULL_LESSON_ROOM = "Хз де...";
    private static final String NULL_TEACHER = "Хз хто...";

    public String mapPeriodsDetailed(List<Period> periods, DayOfWeek dayOfWeek) {
        return formatDetailedPeriodsToTelegramText(periods, dayOfWeek);
    }

    public String mapPeriods(List<Period> periods, DayOfWeek dayOfWeek) {
        return formatPeriodsToTelegramText(periods, dayOfWeek);
    }

    public String mapPeriods(List<Period> periods) {
        StringBuilder builder = new StringBuilder();

        List<Period> list = new ArrayList<>();

        EnumSet.allOf(DayOfWeek.class).forEach(dayOfWeek -> {
            List<Period> periodsPerDay = periods.stream().filter(period -> period.getDayOfWeek() == dayOfWeek).collect(Collectors.toList());
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

        if (linkedMap.size() > 1) {
            builder.append(list.stream().mapToInt(Period::getLessonWeek).findFirst().orElse(1))
                    .append(" тиждень")
                    .append("\n \n");
        }

        linkedMap.forEach((dayOfWeek, periodList) -> {
            builder.append(getUkrainianDayOfWeek(dayOfWeek));
            periodList.forEach(period -> builder.append("\n \t")
                    .append(period.getLessonNumber())
                    .append(". ")
                    .append(period.getLessonName())
                    .append("\n")
                    .append("\t \t \t ")
                    .append(nullableOf(period.getLessonRoom(), PotentialNullableLessonParam.LESSON_ROOM))
                    .append(", ")
                    .append(period.getLessonType())
                    .append(", (")
                    .append(period.getFromTime())
                    .append(" - ")
                    .append(period.getToTime())
                    .append(")")
                    .append("\n")
                    .append("\t \t \t ")
                    .append(nullableOf(period.getTeacherName(), PotentialNullableLessonParam.TEACHER)));
            builder.append("\n \n");
        });

        return builder.toString();
    }

    private String formatDetailedPeriodsToTelegramText(List<Period> periods, DayOfWeek dayOfWeek) {
        StringBuilder builder = new StringBuilder();

        Collections.sort(periods);

        builder.append(getUkrainianDayOfWeek(dayOfWeek));
        periods.forEach(period -> {
            builder.append("\n \t")
                    .append(period.getLessonNumber())
                    .append(". ")
                    .append(period.getLessonName())
                    .append("\n")
                    .append("\t \t \t ")
                    .append(nullableOf(period.getLessonRoom(), PotentialNullableLessonParam.LESSON_ROOM))
                    .append(", ")
                    .append(period.getLessonType())
                    .append(", (")
                    .append(period.getFromTime())
                    .append(" - ")
                    .append(period.getToTime())
                    .append(")")
                    .append("\n")
                    .append("\t \t \t ");
            if (period.getTeachers().isEmpty()) {
                builder.append(NULL_TEACHER).append("\n");
            } else {
                period.getTeachers().forEach(teacher -> builder.append(teacher.getTeacherFullName()).append("\n"));
            }
        });
        return builder.toString();
    }

    private String formatPeriodsToTelegramText(List<Period> periods, DayOfWeek dayOfWeek) {
        StringBuilder builder = new StringBuilder();

        Collections.sort(periods);

        builder.append(getUkrainianDayOfWeek(dayOfWeek));
        periods.forEach(period -> builder.append("\n \t")
                .append(period.getLessonNumber())
                .append(". ")
                .append(period.getLessonName())
                .append("\n")
                .append("\t \t \t ")
                .append(nullableOf(period.getLessonRoom(), PotentialNullableLessonParam.LESSON_ROOM))
                .append(", ")
                .append(period.getLessonType())
                .append(", (")
                .append(period.getFromTime())
                .append(" - ")
                .append(period.getToTime())
                .append(")")
                .append("\n")
                .append("\t \t \t ")
                .append(nullableOf(period.getTeacherName(), PotentialNullableLessonParam.TEACHER)));
        return builder.toString();
    }

    private String nullableOf(String value, PotentialNullableLessonParam param) {
        if (value == null || value.isEmpty()) {
            switch (param) {
                case TEACHER:
                    return NULL_TEACHER;
                case LESSON_ROOM:
                    return NULL_LESSON_ROOM;
            }
        }
        return value;
    }

    private enum PotentialNullableLessonParam {

        TEACHER, LESSON_ROOM

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