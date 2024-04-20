package com.space_asians.w1ncent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;

@Configuration
@Primary
public class W1nc3ntApplication implements ApplicationRunner {

	@Value("${telegram.bot.test_token}")
	String token;

	@Autowired
	private W1NC3NT_BOT w1nc3ntBot;

	@Override
	public void run(ApplicationArguments args) throws Exception {
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
