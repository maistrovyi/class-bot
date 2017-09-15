package com.maystrovyy.rozklad4j.api;

import com.maystrovyy.models.dto.PeriodDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

import static com.maystrovyy.models.Role.TEACHER;
import static com.maystrovyy.rozklad4j.Endpoints.*;
import static java.io.File.separator;

@Component
public class PeriodApiOperations implements BaseApiOperations<PeriodDto> {

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public PeriodDto parse(String groupName) {
        String url = BASE_PATH + GROUPS + separator + groupName + separator + LESSONS;
        URI uri = getURI(url);
        PeriodDto data = restTemplate.getForObject(uri, PeriodDto.class);
//        TODO refactor this checking
        if (data != null) {
            data.periods.forEach(period -> {
                period.setGroupName(groupName);
                period.setDayOfWeekFromDayNumber();
                period.getTeachers().forEach(teacher -> teacher.setRole(TEACHER));
            });
            return data;
        }
        throw new IllegalArgumentException("There are no group with this name!");
    }

}