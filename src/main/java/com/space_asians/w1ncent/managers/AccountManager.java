package com.space_asians.w1ncent.managers;

import com.space_asians.w1ncent.entities.Member;
import com.space_asians.w1ncent.repositories.MembersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
public class AccountManager extends W1nc3ntManager{

    @Value("${text.account.authentication.success}")
    private String text_auth_success;
    @Value("${text.account.authentication.failure}")
    private String text_auth_failure;

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
        String text = update.getMessage().getText();
        Long chat_id = update.getMessage().getChatId();
        if(this.membersRepository.findByPassword(text) != null){
            this.membersRepository.updateChatId(chat_id, text);
            return this.respond(chat_id, this.text_auth_success, null);
        }else{
            return this.respond(chat_id, this.text_auth_failure, null);
        }
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
