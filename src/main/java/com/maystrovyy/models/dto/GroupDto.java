package com.maystrovyy.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.maystrovyy.models.Group;
import lombok.Data;

@Data
public class GroupDto {

    @JsonProperty(value = "data")
    private Group group;

}
