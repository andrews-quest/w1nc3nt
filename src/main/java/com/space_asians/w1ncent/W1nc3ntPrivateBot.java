package com.space_asians.w1ncent;

import com.space_asians.w1ncent.managers.FinanceManager;
import com.space_asians.w1ncent.managers.MainManager;
import com.space_asians.w1ncent.managers.MoonAPIManager;
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
public class W1nc3ntPrivateBot extends W1nc3ntBot{
    @Value("${telegram.bot.username}")
    private String username;
    @Value("${telegram.bot.test_token}")
    private String token;

    @PostConstruct
    public void init(){
        this.telegramClient = new OkHttpTelegramClient(token);
    }
    protected TelegramClient telegramClient;

    // Managers
    private W1NC3NTManager current_manager;

    private SendMessage sm = null;


    private SendMessage handle_commands(Update update){
        String text = update.getMessage().getText();
        if(text.equals("/greet")){
            return this.mainManager.greet(update);
        }else if(text.equals("/about_w1nc3nt")){
            return this.mainManager.about(update);
        }if(text.equals("/finances_update")) {
            this.current_manager = this.financeManager;
            return this.financeManager.update(update);
        }else if(text.equals("/finances_check")) {
            return this.financeManager.check(update);
        }else if(text.equals("/finances_history")){
            this.current_manager = this.financeManager;
            return this.financeManager.history(update);
        }else if(text.equals("/finances_cancel_last")){
            this.current_manager = this.financeManager;
            return this.financeManager.cancel_last(update);
        }else if(text.equals("/lunar_digest")){
            return this.moonAPIManager.consume(update);
        }else{
            return this.mainManager.unknown(update);
        }
    }


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
            this.current_manager.end();
            this.current_manager = null;
        }

    }

}
