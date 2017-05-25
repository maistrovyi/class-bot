package com.maystrovyy.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Set;

import static com.maystrovyy.configs.ApplicationConstants.GROUP_NAME_REGEXP;

@Getter
@Setter
@Builder(builderMethodName = "of", buildMethodName = "create")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Entity(name = "schedule")
@Table(name = "schedules")
public class Schedule implements Serializable {

    private static final long serialVersionUID = -8337174819231107633L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SCHEDULE_ID")
    private Long id;

    @Size(min = 5, max = 5)
    @Pattern(regexp = GROUP_NAME_REGEXP)
    @Column(name = "GROUP_NAME")
    private String groupName;

//    TODO transliteration support
//    private String localizedGroupName;

    @JsonProperty(value = "data")
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "SCHEDULE_FK", referencedColumnName = "PERIOD_FK")
    private Set<Period> periods;

}