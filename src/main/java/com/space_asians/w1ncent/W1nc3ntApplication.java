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

    @Value("${environment}")
    private String env;
    @Value("${telegram.bot.token}")
    private String production_token;
    @Value("${telegram.bot.group.token}")
    private String production_group_token;
    @Value("${telegram.bot.test_token}")
    private String test_token;
    @Value("${telegram.bot.group.test_token}")
    private String test_group_token;

    @Autowired
    private W1nc3ntPrivateBot w1nc3ntPrivateBot;
    @Autowired
    private W1nc3ntGroupBot w1nc3ntGroupBot;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        String token = null;
        String group_token = null;
        if (this.env.equalsIgnoreCase("DEV")) {
            token = this.test_token;
            group_token = this.test_group_token;
        }

        if (this.env.equalsIgnoreCase("PRODUCTION")){
            token = this.production_token;
            group_token = this.production_group_token;
        }

        MultithreadedTelegramApplication botsPrivateApplication = new MultithreadedTelegramApplication(token,
                this.w1nc3ntPrivateBot);
        MultithreadedTelegramApplication botsGroupApplication = new MultithreadedTelegramApplication(group_token,
                this.w1nc3ntGroupBot);

        botsGroupApplication.start();
        botsPrivateApplication.start();
    }
}
