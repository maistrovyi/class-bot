package com.maystrovyy.rozkladj.api;

import com.maystrovyy.models.Group;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.regex.Matcher;

import static com.maystrovyy.rozkladj.RozkladJEndpoints.BASE_PATH;
import static com.maystrovyy.rozkladj.RozkladJEndpoints.GROUPS;
import static java.io.File.separator;

@Slf4j
@Component
public class GroupApiOperations implements BaseApiOperations<Group> {

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public Group parse(String groupName) {
        Group group = null;
        String url = BASE_PATH + GROUPS + separator + groupName;
        URI uri = getURI(url);
        try {
            group = restTemplate.getForObject(uri, Group.class);
        } catch (RestClientException e) {
            log.warn(e.getMessage());
        }
        return group;
    }

    private boolean matchGroupNameToRegex(String s) {
        Matcher hasLatinaLetter = LATINA_LETTER.matcher(s);
        Matcher hasCyrillicLetter = CYRILLIC_LETTER.matcher(s);
        Matcher hasDigit = DIGIT.matcher(s);
        Matcher hasMinus = MINUS.matcher(s);
        return (hasLatinaLetter.find() || hasCyrillicLetter.find()) && hasDigit.find() && hasMinus.find();
    }

    public boolean isValidGroupName(String groupName) {
//        TODO check this impl
        return groupName.length() >= 5 && matchGroupNameToRegex(groupName);
    }

}