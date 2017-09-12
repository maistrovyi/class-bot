package com.maystrovyy.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
public class Week {

    @JsonProperty(value = "data")
    private Integer number;

    @Getter
    @AllArgsConstructor
    public enum WeekNumber {

        FIRST(1), SECOND(2);

        private Integer value;

        public static WeekNumber integerOf(Integer integer) {
            switch (integer) {
                case 1:
                    return FIRST;
                case 2:
                    return SECOND;
                default:
                    throw new IllegalArgumentException("There are no week type with this number!");
            }
        }

    }

}