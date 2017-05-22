package com.maystrovyy.utills;

import com.maystrovyy.models.User;

public interface UserConverter {

    static User toUser(org.telegram.telegrambots.api.objects.User telegramUser) {
        return User.of()
                .firstName(telegramUser.getFirstName())
                .lastName(telegramUser.getLastName())
                .userName(telegramUser.getUserName())
                .create();

    }

}