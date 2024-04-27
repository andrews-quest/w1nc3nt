package com.space_asians.w1ncent.managers;

import com.space_asians.w1ncent.entities.Member;
import com.space_asians.w1ncent.repositories.MembersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Service
public class AccountManager extends W1nc3ntManager{

    @Value("${text.account.authentication.success}")
    private String text_auth_success;
    @Value("${text.account.authentication.failure}")
    private String text_auth_failure;
    @Value("${text.account.options}")
    private String text_options;
    @Value("${text.account.info}")
    private String text_info;
    @Value("${text.account.log_out}")
    private String text_log_out;


    public boolean is_logged_in(Update update){
        Long chat_id = update.getMessage().getChatId();
        Member member = this.membersRepository.findByChatId(chat_id);
        return member == null ? false : true;
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
        String account_info = this.get_account_info(update.getMessage().getChatId());
        return respond(update.getMessage().getChatId(),
                account_info + this.text_options,
                this.create_yes_no_markup(false));
    }

    public String get_account_info(Long chat_id){
        Member member = this.membersRepository.findByChatId(chat_id);
        String name = member.getName();
        return String.format(this.text_info, name);
    }

    public SendMessage log_out(Update update){
        Long chat_id = update.getMessage().getChatId();
        this.membersRepository.dropChatId(chat_id);
        return this.respond(chat_id, this.text_log_out, null);
    }
}
