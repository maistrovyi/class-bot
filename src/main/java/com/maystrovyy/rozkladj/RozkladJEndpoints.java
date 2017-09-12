package com.maystrovyy.rozkladj;

import static java.io.File.separator;

public interface RozkladJEndpoints {

    String API_DOMAIN = "https://api.rozklad.org.ua/";
    String API_VER = "v2";
    String BASE_PATH = API_DOMAIN + API_VER + separator;

    String GROUPS = "groups";
    String LESSONS = "lessons";
    String WEEKS = "weeks";

    String EMPTY = "";

}