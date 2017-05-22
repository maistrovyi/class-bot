package com.maystrovyy.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import javax.persistence.*;
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
@Entity(name = "user")
@Table(name = "users")
public final class User implements Serializable {

    private static final long serialVersionUID = -1541082715017001139L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TEACHER_ID")
    private Long id;

    @Column(name = "FIRST_NAME")
    private String firstName;

    @Column(name = "LAST_NAME")
    private String lastName;

    @Column(name = "USER_NAME", unique = true)
    private String userName;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "ROLE")
    private Role role;

    /*@URL
    @Column(name = "URL")
    private String url;*/

    /*@Size(min = 10, max = 10)
    @Pattern(regexp = "(^$|[0-9]{10})")
    @Column(name = "PHONE_NUMBER")
    private String phoneNumber;*/

    /*@Email
    @Column(name = "EMAIL")
    private String email;*/

}