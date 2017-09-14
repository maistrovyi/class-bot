package com.maystrovyy.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@Builder(builderMethodName = "of")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Entity(name = "teacher")
@Table(name = "teachers")
public class Teacher implements Serializable {

    private static final long serialVersionUID = 8657926332173851328L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TEACHER_ID")
    private Long id;

    @JsonProperty(value = "teacher_id")
    @Column(name = "TEACHER_API_ID", unique = true)
    private Long apiId;

    @JsonProperty(value = "teacher_name")
    @Column(name = "TEACHER_NAME")
    private String teacherName;

    @JsonProperty(value = "teacher_full_name")
    @Column(name = "TEACHER_FULL_NAME")
    private String teacherFullName;

    @JsonProperty(value = "teacher_short_name")
    @Column(name = "TEACHER_SHORT_NAME")
    private String teacherShortName;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "ROLE")
    private Role role;

}