package com.maystrovyy.rozkladj.api;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@FunctionalInterface
public interface BaseApiOperations<T> {

    Pattern LATINA_LETTER = Pattern.compile("[a-zA-z]");
    Pattern CYRILLIC_LETTER = Pattern.compile("[а-яА-я]");
    Pattern DIGIT = Pattern.compile("[0-9]");
    Pattern MINUS = Pattern.compile("[-]");

    T parse(String url);

    default URI getURI(String url) {
        URI uri = null;
        try {
            uri = new URI(url);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return uri;
    }

    default boolean matchGroupNameToRegex(String s) {
        Matcher hasLatinaLetter = LATINA_LETTER.matcher(s);
        Matcher hasCyrillicLetter = CYRILLIC_LETTER.matcher(s);
        Matcher hasDigit = DIGIT.matcher(s);
        Matcher hasMinus = MINUS.matcher(s);
        return (hasLatinaLetter.find() || hasCyrillicLetter.find()) && hasDigit.find() && hasMinus.find();
    }

    default boolean isValidGroupName(String groupName) {
        if (groupName != null) {
            if (groupName.length() != 5) {
                groupName = groupName.replaceAll("\\s+", "");
                if (groupName.length() != 5) {
                    return false;
                }
            }
            return matchGroupNameToRegex(groupName);
        }
        return false;
    }

}