package com.maystrovyy.rozkladj.api;

import com.maystrovyy.models.Week;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

import static com.maystrovyy.rozkladj.RozkladJEndpoints.BASE_PATH;
import static com.maystrovyy.rozkladj.RozkladJEndpoints.WEEKS;

@Component
public class WeekApiOperations implements BaseApiOperations<Week> {

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public Week parse(String url) {
        url = BASE_PATH + WEEKS;
        URI uri = getURI(url);
        return restTemplate.getForObject(uri, Week.class);
    }
}
