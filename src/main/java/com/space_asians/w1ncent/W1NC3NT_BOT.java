package com.space_asians.w1ncent;

import com.space_asians.w1ncent.managers.FinanceManager;
import com.space_asians.w1ncent.managers.W1NC3NTManager;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;


@Service
public class W1NC3NT_BOT implements LongPollingSingleThreadUpdateConsumer {
    @Value("${telegram.bot.username}")
    private String username;
    @Value("${telegram.bot.test_token}")
    private String token;


    // Message properties
    @PostConstruct
    public void init(){
        this.telegramClient = new OkHttpTelegramClient(token);
    }
    private TelegramClient telegramClient;
    private W1NC3NTManager current_manager;
    @Autowired
    private FinanceManager financeManager;

    private SendMessage sm = null;
    private Message message;
    private String chat_id;

    private SendMessage greet(){
        return SendMessage
                .builder()
                .chatId(this.chat_id)
                .text("Ich begrüße Ihnen, liebe Herrinen.")
                .build();
    }

    private SendMessage handle_commands(Update update){
        String text = update.getMessage().getText();
        if(text.equals("/greet")){
            return this.greet();
        }else if(text.equals("/update_finances")) {
            this.current_manager = this.financeManager;
            return this.financeManager.initiate(update);
        }else if(text.equals("/check_finances")){
           return this.financeManager.check(update);
        }else{
            return SendMessage
                    .builder()
                    .chatId(this.chat_id)
                    .text("Ich weiÃŸ noch nicht, wie ich das beantworten soll. Tut mir leid.")
                    .build();
        }
    }

    @Override
    public void consume(Update update) {

        // a manager is engaged
        if(this.current_manager != null){
            this.sm = this.current_manager.consume(update);
        }else if (update.hasMessage() && update.getMessage().hasText()) {
            this.current_manager = null;
            this.message = update.getMessage();
            this.chat_id = String.valueOf(message.getChatId());


            this.sm = handle_commands(update);
        }

        // send a respective message
        if(this.sm != null) {
            try {
                telegramClient.execute(this.sm);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }

        this.sm = null;
        if(this.current_manager != null && this.current_manager.is_engaged == false){
            this.current_manager.end();
            this.current_manager = null;
        }

    }

}
