package com.maystrovyy.rozkladj.api.v1;

import com.maystrovyy.models.Schedule;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.net.URI;
import java.net.URISyntaxException;

@SpringBootTest
@RunWith(value = SpringRunner.class)
@WebAppConfiguration
public class ScheduleRestConnectionTest {

    @Resource
    RestTemplate restTemplate;

    @Test
    public void parseSchedule() {
        URI uri = null;
        try {
            uri = new URI("https://api.rozklad.org.ua/v2/groups/ВВ-41/lessons");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        Schedule schedule = restTemplate.getForObject(uri, Schedule.class);
        Assert.assertNotNull(schedule);
    }

}