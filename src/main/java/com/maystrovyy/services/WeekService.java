package com.maystrovyy.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;

import java.io.IOException;

import static com.maystrovyy.rozkladj.RozkladJEndpoints.BASE_PATH;
import static com.maystrovyy.rozkladj.RozkladJEndpoints.WEEKS;

@Service
public class WeekService {

    @Autowired
    private RestTemplate restTemplate;

    @PostConstruct
    public void parseCurrentWeek() {
        ResponseEntity<String> entity = restTemplate.getForEntity(BASE_PATH + WEEKS, String.class);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = null;
        try {
            root = mapper.readTree(entity.getBody());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (root != null) {
            JsonNode data = root.path("data");
            int week = data.asInt();
            System.out.println(week);
        }
    }

}