package com.space_asians.w1ncent.bots;

import com.space_asians.w1ncent.managers.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class W1nc3ntBot implements LongPollingSingleThreadUpdateConsumer {

    private String token = null;


    protected W1nc3ntManager current_manager;
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


    protected SendMessage handle_commands(Update update) {
        String text = update.getMessage().getText();

        if(this.is_private = true){
            if (text.equals("/finances_update")) {
                this.current_manager = this.financeManager;
                return this.financeManager.update(update);
            }else if (text.equals("/finances_cancel_last")) {
                this.current_manager = this.financeManager;
                return this.financeManager.cancel_last(update);
            }
        }

        if(text.equals("/start")){
            if(this.accountManager.is_logged_in()){
               return this.mainManager.start(update, false);
            }else{
                return this.mainManager.start(update, true);
            }

        }else if(text.equals("/greet")) {
            return this.mainManager.greet(update);
        } else if (text.equals("/about_w1nc3nt")) {
            return this.mainManager.about(update);
        }
        else if (text.equals("/finances_check")) {
            return this.financeManager.check(update);
        } else if (text.equals("/finances_history")) {
            this.current_manager = this.financeManager;
            return this.financeManager.history(update);
        }  else if (text.equals("/lunar_digest")) {
            return this.moonAPIManager.consume(update);
        }

        return this.mainManager.unknown(update);
    }

    @Override
    public void consume(Update update) {

    }
}
