package com.space_asians.w1ncent;

import com.space_asians.w1ncent.bots.W1nc3ntGroupBot;
import com.space_asians.w1ncent.bots.W1nc3ntPrivateBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@Primary
public class W1nc3ntApplication implements ApplicationRunner {

	@Value("${telegram.bot.test_token}")
	String token;

	@Value("${telegram.bot.group.test_token}")
	String group_token;

	@Autowired
	private W1nc3ntPrivateBot w1nc3ntPrivateBot;
	@Autowired
	private W1nc3ntGroupBot w1nc3ntGroupBot;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		MultithreadedTelegramApplication botsPrivateApplication = new MultithreadedTelegramApplication(this.token,
				this.w1nc3ntPrivateBot);
		MultithreadedTelegramApplication botsGroupApplication = new MultithreadedTelegramApplication(this.group_token,
				this.w1nc3ntGroupBot);

		botsGroupApplication.start();
		botsPrivateApplication.start();
	}
}
