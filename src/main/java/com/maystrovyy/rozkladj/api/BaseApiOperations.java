package com.maystrovyy.rozkladj.api;

import com.maystrovyy.configs.ApplicationConstants;

import javax.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;

@FunctionalInterface
public interface BaseApiOperations<T> {

    T parse(@NotNull String url);

    default URI getURI(String url) {
        URI uri = null;
        try {
            uri = new URI(url);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return uri;
    }

    default boolean isValidGroupName(@NotNull String groupName) {
        if (groupName.length() != 5) {
            groupName = groupName.replaceAll("\\s+","");
            if (groupName.length() != 5) {
                return false;
            }
        }
        return groupName.matches(ApplicationConstants.GROUP_NAME_REGEXP);
    }

}