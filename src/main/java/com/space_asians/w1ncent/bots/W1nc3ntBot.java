package com.space_asians.w1ncent.bots;

import com.space_asians.w1ncent.managers.*;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.HashMap;

@Component
public class W1nc3ntBot implements LongPollingSingleThreadUpdateConsumer {

    @Value("${environment}")
    protected String env;

    protected TelegramClient telegramClient;

    protected HashMap<String, W1nc3ntManager> managers = new HashMap<>();

    @PostConstruct
    public void init_managers(){
        this.managers.put(this.mainManager.get_name(), this.mainManager);
        this.managers.put(this.financeManager.get_name(), this.financeManager);
        this.managers.put(this.moonAPIManager.get_name(), this.moonAPIManager);
        this.managers.put(this.accountManager.get_name(), this.accountManager);
    }


    @Autowired
    protected FinanceManager financeManager;
    @Autowired
    protected MoonAPIManager moonAPIManager;
    @Autowired
    protected MainManager mainManager;
    @Autowired
    protected AccountManager accountManager;

    protected SendMessage sm = null;

    protected boolean is_private = false;

    protected W1nc3ntManager get_manager(String manager){
        return this.managers.get(manager);
    }

    protected SendMessage handle_commands(Update update) {
        String text = update.getMessage().getText();
        Long chat_id = update.getMessage().getChatId();

        if(this.is_private = true){
            if (text.equals("/finances_update")) {
                this.mainManager.set_state(this.financeManager.get_name(), chat_id);
                return this.financeManager.update(update);
            }else if (text.equals("/finances_cancel_last")) {
                this.mainManager.set_state(this.financeManager.get_name(), chat_id);
                return this.financeManager.cancel_last(update);
            }
        }

        boolean is_logged_in = this.accountManager.is_logged_in(update);

        if(text.equals("/start")) {
            if (is_logged_in) {
                return this.mainManager.start(update, false);
            } else {
                return this.mainManager.start(update, true);
            }
        }

        if(text.equals("/account")){
            if(is_logged_in){
                return this.accountManager.options(update);
            }else{
                return this.accountManager.authenticate(update);
            }
        }

        if (!is_logged_in && is_private) {
            return  this.accountManager.authenticate(update);
            // return this.mainManager.not_authenticated(update);
        }

        if(text.equals("/greet")) {
            return this.mainManager.greet(update);
        } else if (text.equals("/about_w1nc3nt")) {
            return this.mainManager.about(update);
        }
        else if (text.equals("/finances_check")) {
            return this.financeManager.check(update);
        } else if (text.equals("/finances_history")) {
            this.mainManager.set_state(this.financeManager.get_name(), chat_id);
            return this.financeManager.history(update);
        }  else if (text.equals("/lunar_digest")) {
            return this.moonAPIManager.consume(update);
        }

        if(this.is_private){
            return this.mainManager.unknown(update);
        }else{
            return null;
        }
    }

    @Override
    public void consume(Update update) {
        Long chat_id = update.getMessage().getChatId();

        // debugging messages
        System.out.println("----> Chat id : " + chat_id);
        System.out.println("----> text    : " + update.getMessage().getText());

        // a manager is engaged
        if(!this.mainManager.current_state(chat_id).equals(mainManager.get_name()) &
        this.accountManager.is_logged_in(update)){
            this.sm = this.get_manager(this.mainManager.current_state(chat_id)).consume(update);
        }else if (update.hasMessage() && update.getMessage().hasText()) {
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
        if(!this.mainManager.current_state(chat_id).equals(this.mainManager.get_name())
                && this.get_manager(this.mainManager.current_state(chat_id)).is_engaged == false){
            this.get_manager(this.mainManager.current_state(chat_id)).end(update.getMessage().getChatId());
            this.mainManager.set_state(this.mainManager.get_name(), chat_id);
        }

    }

}
