package com.maystrovyy.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Data;

@Data
@JsonRootName(value = "data")
public class Group {

    @JsonProperty(value = "group_full_name")
    private String groupFullName;

    @JsonProperty(value = "group_okr")
    private String groupOkr;

}