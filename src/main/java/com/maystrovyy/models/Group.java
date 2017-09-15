package com.maystrovyy.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Group {

    @JsonProperty(value = "group_full_name")
    private String groupFullName;

    @JsonProperty(value = "group_okr")
    private String groupOkr;

}