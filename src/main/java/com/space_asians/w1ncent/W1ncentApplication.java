package com.space_asians.w1ncent;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;

@Component("Application")
@Primary
public class W1ncentApplication {

	@Value("${telegram.bot.test_token}")
	String token;

	@Autowired
	private W1NC3NT_BOT w1nc3ntBot;
	@PostConstruct
	public void run() {
		try (TelegramBotsLongPollingApplication botsApplication = new TelegramBotsLongPollingApplication()) {
			botsApplication.registerBot(this.token, w1nc3ntBot);
			System.out.println("Success");
			Thread.currentThread().join();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Failure");
	}
}
