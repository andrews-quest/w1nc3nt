package com.space_asians.w1ncent;

import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;


public class W1NC3NT_BOT implements LongPollingSingleThreadUpdateConsumer {

    @Value("${telegram.bot.username}")
    private String username;
    // @Value("${telegram.bot.test_token}")
    // private String token;

    private final TelegramClient telegramClient;
    public W1NC3NT_BOT(String token){
       telegramClient = new OkHttpTelegramClient(token);
    }

    @Override
    public void consume(Update update) {
        System.out.println("consumed");
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            String text = message.getText();
            SendMessage sm = SendMessage.builder().chatId(message.getChatId()).text(text).build();
            // sm.setText(text);
            // sm.setChatId(String.valueOf(message.getChatId()));
            try{
                telegramClient.execute(sm);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
