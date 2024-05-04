package com.space_asians.w1ncent.bots;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Service
public class W1nc3ntGroupBot extends W1nc3ntBot {
    @Value("${telegram.bot.username}")
    private String username;
    @Value("${telegram.bot.group.test_token}")
    private String test_token;
    @Value("${telegram.bot.group.token}")
    private String token;

    @PostConstruct
    public void init(){
        String token;
        if(this.env.equalsIgnoreCase("DEV")){
            token =  this.test_token;
        }else{
            token = this.token;
        }
        this.telegramClient = new OkHttpTelegramClient(token);
    }

    private SendMessage sm = null;

}
