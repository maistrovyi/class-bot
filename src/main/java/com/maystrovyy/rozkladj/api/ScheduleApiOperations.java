package com.maystrovyy.rozkladj.api;

import com.maystrovyy.models.Schedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Component
public class ScheduleApiOperations implements BaseApiOperations<Schedule> {

    private final String VV_41_LESSONS = "https://api.rozklad.org.ua/v2/groups/ВВ-41/lessons";

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public Schedule parse(String stingUri) {
        URI uri = getURI(stingUri);
        return restTemplate.getForObject(uri, Schedule.class);
    }

}