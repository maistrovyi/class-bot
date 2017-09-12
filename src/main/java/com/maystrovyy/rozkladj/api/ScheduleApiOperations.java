package com.maystrovyy.rozkladj.api;

import com.maystrovyy.models.Period;
import com.maystrovyy.models.Schedule;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

import static com.maystrovyy.rozkladj.RozkladJEndpoints.*;
import static java.io.File.separator;

@Component
public class ScheduleApiOperations implements BaseApiOperations<Schedule> {

//    @Autowired
    private RestTemplate restTemplate = new RestTemplate();

    @Override
    public Schedule parse(String groupName) {
        String url = BASE_PATH + GROUPS + separator + groupName + separator + LESSONS;
        URI uri = getURI(url);
        Schedule schedule = restTemplate.getForObject(uri, Schedule.class);
        schedule.setGroupName(groupName);
        schedule.getPeriods().forEach(Period::setDayOfWeekFromDayNumber);
        return schedule;
    }

}