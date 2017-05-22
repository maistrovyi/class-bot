package com.maystrovyy.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.URL;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Getter
@Setter
@Builder(builderMethodName = "of", buildMethodName = "create")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Entity(name = "teacher")
@Table(name = "teachers")
public final class User implements Serializable {

    private static final long serialVersionUID = -1541082715017001139L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TEACHER_ID")
    private Long id;

    @Column(name = "NAME")
    private String name;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "ROLE")
    private Role role;

    @URL
    @Column(name = "URL")
    private String url;

    @Size(min = 10, max = 10)
    @Pattern(regexp = "(^$|[0-9]{10})")
    @Column(name = "PHONE_NUMBER")
    private String phoneNumber;

    @Email
    @Column(name = "EMAIL")
    private String email;

}