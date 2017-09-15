package com.maystrovyy.storage;

import com.maystrovyy.models.Week.WeekNumber;
import com.maystrovyy.rozklad4j.api.WeekApiOperations;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import static com.maystrovyy.rozklad4j.Endpoints.EMPTY;

@Component
public class WeekStorage {

     @Autowired
     private WeekApiOperations weekApiOperations;

     @Getter
     private WeekNumber weekNumber;

     @PostConstruct
     @Scheduled(cron = "0 0 12 ? * SUN")
     private void init() {
          this.weekNumber = WeekNumber.integerOf(weekApiOperations.parse(EMPTY).getNumber());
     }

}