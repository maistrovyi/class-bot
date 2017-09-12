package com.maystrovyy;

import com.maystrovyy.bot.core.ClassBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.TelegramBotsApi;

@SpringBootApplication
public class ClassBotApplication implements CommandLineRunner {

	@Autowired
	private ClassBot classBot;

	public static void main(String[] args) {
		SpringApplication.run(ClassBotApplication.class, args);
	}

	@Override
	public void run(String... strings) throws Exception {
//		ApiContextInitializer.init();
		TelegramBotsApi botsApi = new TelegramBotsApi();
		botsApi.registerBot(classBot);
	}

}