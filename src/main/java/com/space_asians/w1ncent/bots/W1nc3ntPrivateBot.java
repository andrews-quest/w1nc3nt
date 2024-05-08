package com.space_asians.w1ncent.bots;

import com.space_asians.w1ncent.managers.W1nc3ntManager;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;


@Service
public class W1nc3ntPrivateBot extends W1nc3ntBot {
    @Value("${telegram.bot.username}")
    private String username;
    @Value("${telegram.bot.test_token}")
    private String token;
    @Value("${telegram.bot.test_token}")
    private String test_token;

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

    private boolean is_private = true;
}
