package com.maystrovyy.rozklad4j;

import static java.io.File.separator;

public interface Endpoints {

    String API_DOMAIN = "https://api.rozklad.org.ua/";
    String API_VER = "v2";
    String BASE_PATH = API_DOMAIN + API_VER + separator;

    String GROUPS = "groups";
    String LESSONS = "lessons";
    String WEEKS = "weeks";

    String EMPTY = "";

}