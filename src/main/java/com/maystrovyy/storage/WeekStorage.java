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

     public WeekNumber reversed() {
          return weekNumber == WeekNumber.FIRST ? WeekNumber.SECOND : WeekNumber.FIRST;
     }

//     TODO add exceptions handling and task restarting
     @PostConstruct
     @Scheduled(cron = "0 59 23 ? * SUN")
     private void init() {
          this.weekNumber = WeekNumber.integerOf(weekApiOperations.parse(EMPTY).getNumber());
     }

}