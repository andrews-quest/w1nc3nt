package com.space_asians.w1ncent;

import com.space_asians.w1ncent.managers.FinanceManager;
import com.space_asians.w1ncent.managers.W1NC3NTManager;
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


    public W1NC3NT_BOT(String token){
        telegramClient = new OkHttpTelegramClient(token);
    }

    // Message properties
    private final TelegramClient telegramClient;
    private W1NC3NTManager current_manager;
    private FinanceManager financeManager = new FinanceManager();

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

    private SendMessage handle_commands(){
        if(message.getText().equals("/greet")){
            return this.greet();
        }if(message.getText().equals("/update_finances")) {
            this.current_manager = this.financeManager;
            return this.financeManager.initiate(this.message);
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
            this.current_manager.consume(this.message);
        }

        // default state handling
        if (update.hasMessage() && update.getMessage().hasText()) {

            this.message = update.getMessage();
            this.chat_id = String.valueOf(message.getChatId());


            this.sm = handle_commands();
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

    }

}
