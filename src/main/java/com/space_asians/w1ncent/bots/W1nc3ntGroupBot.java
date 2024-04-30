package com.space_asians.w1ncent.bots;

import com.space_asians.w1ncent.managers.W1nc3ntManager;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class W1nc3ntGroupBot extends W1nc3ntBot {
    @Value("${telegram.bot.username}")
    private String username;
    @Value("${telegram.bot.group.token}")
    private String token;

    @PostConstruct
    public void init(){
        this.telegramClient = new OkHttpTelegramClient(token);
    }
    protected TelegramClient telegramClient;

    // Managers
    private W1nc3ntManager current_manager;

    private SendMessage sm = null;



    @Override
    public void consume(Update update) {

        // a manager is engaged
        if(this.current_manager != null){
            this.sm = this.current_manager.consume(update);
        }else if (update.hasMessage() && update.getMessage().hasText()) {
            this.current_manager = null;
            Message message = update.getMessage();
            Long chat_id = message.getChatId();
            this.sm = handle_commands(update);
        }

        // send a respective message
        if(this.sm != null) {
            try {
                this.telegramClient.execute(this.sm);
            } catch (TelegramApiException e) {
                this.mainManager.error(update);
            }
        }

        this.sm = null;
        if(this.current_manager != null && this.current_manager.is_engaged == false){
            this.current_manager.end(update.getMessage().getChatId());
            this.current_manager = null;
        }

    }
}
