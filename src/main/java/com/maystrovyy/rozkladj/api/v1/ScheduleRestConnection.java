package com.maystrovyy.rozkladj.api.v1;

import com.maystrovyy.models.Schedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;

@Component
public class ScheduleRestConnection {

    @Autowired
    private RestTemplate restTemplate;

    public Schedule parseSchedule(@NotNull String groupName) {
        URI uri = null;
        try {
            uri = new URI("https://api.rozklad.org.ua/v2/groups/ВВ-41/lessons");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        Schedule schedule = restTemplate.getForObject(uri, Schedule.class);
        return schedule;
    }

    /*public Period parseSchedule2(@NotNull String groupName) {
        URI uri = null;
        try {
            uri = new URI("https://api.rozklad.org.ua/v2/groups/ВВ-41/lessons");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        restTemplate.
        return schedule;
    }*/

}
