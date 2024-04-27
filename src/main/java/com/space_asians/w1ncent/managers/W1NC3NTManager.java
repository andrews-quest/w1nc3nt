package com.space_asians.w1ncent.managers;

import com.space_asians.w1ncent.repositories.MembersRepository;
import com.space_asians.w1ncent.repositories.TransactionsRepository;
import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

public class W1nc3ntManager {

    protected MembersRepository membersRepository;
    protected TransactionsRepository transactionsRepository;

    public boolean is_engaged = false;
    protected ReplyKeyboardMarkup markup;

    @Value("${text.error.default_manager}")
    protected String text_error_default_manager;

    protected SendMessage respond(long chat_id, String text, ReplyKeyboardMarkup markup){
       return SendMessage
               .builder()
               .chatId(chat_id)
               .text(text)
               .replyMarkup(markup)
               .build();
    }

    public SendMessage consume(Update update){
        return this.respond(update.getMessage().getChatId(), this.text_error_default_manager, null);
    }

    public void end(){
        this.is_engaged = false;
    }
}
