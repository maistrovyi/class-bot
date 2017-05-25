package com.maystrovyy.rozkladj;

import com.maystrovyy.models.Period;
import com.maystrovyy.models.Schedule;
import lombok.SneakyThrows;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.net.URI;

@SpringBootTest
@RunWith(value = SpringRunner.class)
public class ScheduleApiOperationsTest {

    @Resource
    RestTemplate restTemplate;

    @Test
    @SneakyThrows
    public void parseAndMapSchedule_Success() {
        URI uri = new URI("https://api.rozklad.org.ua/v2/groups/ВВ-41/lessons");
        Schedule schedule = restTemplate.getForObject(uri, Schedule.class);
        Assert.assertNotNull(schedule);
    }

    @Test
    @SneakyThrows
    public void setDayOfWeekFromDayNumber_Success() {
        URI uri = new URI("https://api.rozklad.org.ua/v2/groups/vv-41/lessons");
        Schedule schedule = restTemplate.getForObject(uri, Schedule.class);
        schedule.setGroupName("vv-41");
        schedule.getPeriods().forEach(Period::setDayOfWeekFromDayNumber);
        schedule.getPeriods().forEach(period -> Assert.assertNotNull(period.getDayOfWeek()));
    }

}