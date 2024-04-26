package com.space_asians.w1ncent.managers;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Service
public class AccountManager extends W1nc3ntManager{

    public boolean is_logged_in(){
        return false;
    }

    public SendMessage authenticate(){
        return null;
    }

    public SendMessage account_info(){
        return null;
    }

    public SendMessage log_out(){
        return null;
    }
}
