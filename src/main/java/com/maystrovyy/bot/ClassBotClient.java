package com.maystrovyy.bot;

import lombok.Builder;

@Builder(builderMethodName = "of", buildMethodName = "create")
public final class ClassBotClient {

    public final String token;

    public final String username;

}