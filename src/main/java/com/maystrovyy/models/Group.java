package com.maystrovyy.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

//@Getter
//@Setter
//@Builder(builderMethodName = "of")
//@NoArgsConstructor
//@AllArgsConstructor
//@EqualsAndHashCode
//@ToString
@Entity(name = "group")
@Table(name = "groups")
@Data
//@JsonInclude(value = NON_NULL)
//@JsonIgnoreProperties(ignoreUnknown = true)
public class Group implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "GROUP_ID")
    private Long id;
//
//    @JsonProperty(value = "group_id")
//    @Column(name = "GROUP_API_ID")
//    private Long apiId;
    @JsonProperty(value = "group_full_name")
//    @Column(name = "GROUP_NAME")
    private String name;

    @JsonProperty(value = "group_okr")
//    @Column(name = "GROUP_OKR")
    private String groupOkr;

}