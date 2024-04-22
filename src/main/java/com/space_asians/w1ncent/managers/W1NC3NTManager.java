package com.space_asians.w1ncent.managers;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

public class W1NC3NTManager {

    public boolean is_engaged = false;
    private ReplyKeyboardMarkup markup;

    protected SendMessage respond(long chat_id, String text, ReplyKeyboardMarkup markup){
       return SendMessage
               .builder()
               .chatId(chat_id)
               .text(text)
               .replyMarkup(markup)
               .build();
    }

    public SendMessage consume(Update update){
        return SendMessage
                .builder()
                .chatId(update.getMessage().getChatId())
                .text("Sie benutzen ein allgemeiner W1NC3NTManager anstatt von spezialisiertem Manager. Etwas ist Schiffgegangen.")
                .build();
    }

    public void end(){
        this.is_engaged = false;
    }
}
