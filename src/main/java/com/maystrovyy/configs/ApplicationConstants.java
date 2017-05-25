package com.maystrovyy.configs;

public interface ApplicationConstants {

    String LOGIN_REGEXP = "^[_'.@A-Za-z0-9-]*$";
//    TODO bad regexp - needs at least 2 digits & 2 letters, (optional minus)
    String GROUP_NAME_REGEXP = "^[a-zA-Z0-9_.-]*$";

    String SPRING_PROFILE_DEVELOPMENT = "dev";
    String SPRING_PROFILE_PRODUCTION = "prod";

    String SYSTEM_ACCOUNT = "system";

}