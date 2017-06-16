package com.maystrovyy.utils.helpers;

public interface ScheduleHelper {

    /*static String scheduleToTelegramText() {
        return "Понеділок: \n" +
                "\t 1. Мікроконтролери\n" +
                "\t \t \t 333-18, Прак\n" +
                "\t \t \t Мотін Максим Миколайович\n" +
                "\t 2. Безпека життєдіяльності\n" +
                "\t \t \t 106-07, Лек\n" +
                "\t \t \t Арламов Олександр Юрійович";
    }*/

    /*static String scheduleToTelegramText(Schedule schedule) {
        StringBuilder builder = new StringBuilder();
//        Schedule schedule = getSchedule();
        Set<Period> periods = schedule.getPeriods();

        List<Period> firstMondayPeriods = periods.stream()
                .filter(period -> period.getDayOfWeek() == DayOfWeek.MONDAY)
                .filter(period -> period.getLessonWeek() == 1)
                .collect(Collectors.toList());
        Collections.sort(firstMondayPeriods);

        builder.append("Понеділок:");
        firstMondayPeriods.forEach(period -> builder.append("\n \t")
                .append(period.getLessonNumber())
                .append(". ")
                .append(period.getLessonName())
                .append("\n")
                .append("\t \t \t ")
                .append(period.getLessonRoom())
                .append(", ")
                .append(period.getLessonType())
                .append("\n")
                .append("\t \t \t ")
                .append(period.getTeacherName()));
        return builder.toString();
    }

    static Set<Period> getPeriods() {
        return Sets.newHashSet(
                Period.of()
                        .dayNumber(1)
                        .dayOfWeek(DayOfWeek.MONDAY)
                        .lessonName("Мікроконтролери")
                        .lessonFullName("Мікроконтролери")
                        .lessonNumber(1)
                        .lessonRoom("333-18")
                        .lessonType("Прак")
                        .teacherName("Мотін Максим Миколайович")
                        .lessonWeek(1)
                        .create(),
                Period.of()
                        .dayNumber(1)
                        .dayOfWeek(DayOfWeek.MONDAY)
                        .lessonName("Безпека життєдіяльності")
                        .lessonFullName("Безпека життєдіяльності")
                        .lessonNumber(2)
                        .lessonRoom("106-07")
                        .lessonType("Лек")
                        .teacherName("Арламов Олександр Юрійович")
                        .lessonWeek(1)
                        .create());
    }*/

}