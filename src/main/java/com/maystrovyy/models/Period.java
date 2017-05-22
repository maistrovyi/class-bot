package com.maystrovyy.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalTime;

@Getter
@Setter
@Builder(builderMethodName = "of", buildMethodName = "create")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Entity(name = "period")
@Table(name = "periods")
public class Period implements Serializable {

    private static final long serialVersionUID = 7041550420150394158L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PERIOD_ID")
    private Long id;

    @Column(name = "DAY_NUMBER")
    private int dayNumber;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "DAY_OF_WEEK")
    private DayOfWeek dayOfWeek;

    @Column(name = "LESSON_NAME")
    private String lessonName;

    @Column(name = "LESSON_FULL_NAME")
    private String lessonFullName;

    @Column(name = "LESSON_NUMBER")
    private int lessonNumber;

    @Column(name = "LESSON_ROOM")
    private String lessonRoom;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "LESSON_TYPE")
    private LessonType lessonType;

    @Column(name = "TEACHER_NAME")
    private String teacherName;

    @Column(name = "LESSON_WEEK")
    private int lessonWeek;

    @Column(name = "FROM_TIME")
    private LocalTime fromTime;

    @Column(name = "TO_TIME")
    private LocalTime toTime;

    public enum LessonType {

        LECTURE, PRACTICAL, LAB

    }

}