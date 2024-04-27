package com.space_asians.w1ncent.managers;

import com.space_asians.w1ncent.entities.Member;
import com.space_asians.w1ncent.repositories.MembersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
public class AccountManager extends W1nc3ntManager{

    @Autowired
    MembersRepository membersRepository;

    public boolean is_logged_in(Update update){
        Long chat_id = update.getMessage().getChatId();
        Member member = membersRepository.findByChatId(chat_id);
        if(member == null){
            return false;
        }else{
            return true;
        }
    }

    public SendMessage authenticate(Update update){
        return null;
    }

    public SendMessage options(Update update){
        return null;
    }

    public SendMessage account_info(){
        return null;
    }

    public SendMessage log_out(){
        return null;
    }
}
