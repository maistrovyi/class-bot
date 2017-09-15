package com.maystrovyy.models.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.maystrovyy.models.Period;
import lombok.AllArgsConstructor;

import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.*;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = Include.NON_NULL)
@AllArgsConstructor
public class PeriodDto {

    @JsonProperty(value = "data")
    public final List<Period> periods;

}