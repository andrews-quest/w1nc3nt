package com.space_asians.w1ncent;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;

@Component("Application")
@Primary
public class W1ncentApplication {

	@Value("${telegram.bot.test_token}")
	String token;

	@PostConstruct
	public void run() {
		try (TelegramBotsLongPollingApplication botsApplication = new TelegramBotsLongPollingApplication()) {
			botsApplication.registerBot(this.token, new W1NC3NT_BOT(this.token));
			System.out.println("Success");
			Thread.currentThread().join();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Failure");
	}
}
