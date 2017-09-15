package com.maystrovyy.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.Set;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static org.springframework.format.annotation.DateTimeFormat.ISO.TIME;

@Getter
@Setter
@Builder(builderMethodName = "of")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = NON_NULL)
@Entity(name = "period")
@Table(name = "periods")
public class Period implements Serializable, Comparator<Period>, Comparable<Period> {

    private static final long serialVersionUID = 7041550420150394158L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PERIOD_ID")
    private Long id;

    @Size(min = 5, max = 5)
    @Column(name = "GROUP_NAME")
    private String groupName;

    @JsonProperty(value = "day_number")
    @Column(name = "DAY_NUMBER")
    private int dayNumber;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "DAY_OF_WEEK")
    private DayOfWeek dayOfWeek;

    @JsonProperty(value = "lesson_name")
    @Column(name = "LESSON_NAME")
    private String lessonName;

    @JsonProperty(value = "lesson_full_name")
    @Column(name = "LESSON_FULL_NAME")
    private String lessonFullName;

    @JsonProperty(value = "lesson_number")
    @Column(name = "LESSON_NUMBER")
    private int lessonNumber;

    @JsonProperty(value = "lesson_room")
    @Column(name = "LESSON_ROOM")
    private String lessonRoom;

    @JsonProperty(value = "lesson_type")
    @Column(name = "LESSON_TYPE")
    private String lessonType;

    @JsonProperty(value = "teacher_name")
    @Column(name = "TEACHER_NAME")
    private String teacherName;

    @JsonProperty(value = "teachers")
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "periods_teachers", joinColumns = @JoinColumn(name = "TEACHER_FK"),
            inverseJoinColumns = @JoinColumn(name = "PERIOD_FK"))
    private Set<Teacher> teachers;

    @JsonProperty(value = "lesson_week")
    @Column(name = "LESSON_WEEK")
    private int lessonWeek;

    @DateTimeFormat(iso = TIME)
    @JsonProperty(value = "time_start")
    @Column(name = "FROM_TIME")
    private LocalTime fromTime;

    @DateTimeFormat(iso = TIME)
    @JsonProperty(value = "time_end")
    @Column(name = "TO_TIME")
    private LocalTime toTime;

    public void setDayOfWeekFromDayNumber() {
        this.setDayOfWeek(DayOfWeek.of(this.dayNumber));
    }

    @Override
    public int compare(Period o1, Period o2) {
        return Integer.compare(o1 == null ? 0 : o1.getLessonNumber(),
                o2 == null ? 0 : o2.getLessonNumber());
    }

    @Override
    public int compareTo(Period o) {
        return Integer.compare(this.getLessonNumber(),
                o == null ? 0 : o.getLessonNumber());

    }

}