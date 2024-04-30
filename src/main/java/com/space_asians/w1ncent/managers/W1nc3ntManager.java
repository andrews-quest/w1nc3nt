package com.space_asians.w1ncent.managers;

import com.space_asians.w1ncent.entities.Member;
import com.space_asians.w1ncent.repositories.MembersRepository;
import com.space_asians.w1ncent.repositories.TransactionsRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class W1nc3ntManager {

    @PostConstruct
    void init(){
        this.membersRepository.dropState();
        this.membersRepository.dropStateManager();
    }

    @Autowired
    protected MembersRepository membersRepository;
    @Autowired
    protected TransactionsRepository transactionsRepository;

    public boolean is_engaged = false;

    protected DateTimeFormatter dateFormatterPartial = DateTimeFormatter.ofPattern("dd-MM");
    protected DateTimeFormatter dateFormatterFull = DateTimeFormatter.ofPattern("dd-MM-uuuu");

    // protected Update update = null;
    // protected Message message = null;
    // protected Long chat_id = null;

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

    protected SendMessage respond_inline(long chat_id, String text, InlineKeyboardMarkup markup){
        return SendMessage
                .builder()
                .chatId(chat_id)
                .text(text)
                .replyMarkup(markup)
                .build();
    }


    protected ReplyKeyboardMarkup create_yes_no_markup(boolean has_end_option){
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();

        row.add("Ja");
        row.add("Nein");
        keyboard.add(row);
        row = new KeyboardRow();
        if(has_end_option){
            row.add("Beenden");
            keyboard.add(row);
        }
        ReplyKeyboardMarkup YesNoMarkup = new ReplyKeyboardMarkup(keyboard);
        return YesNoMarkup;
    }

    protected String get_state(Long chat_id){
        Member member = this.membersRepository.findByChatId(chat_id);
        return member.getState_manager();
    }

    protected void set_state(String state, Long chat_id){
        this.membersRepository.updateStateManager(state, chat_id);
    }


    public SendMessage consume(Update update){
        return this.respond(update.getMessage().getChatId(), this.text_error_default_manager, null);
    }

    public void end(Long chat_id){
        this.set_state("none", chat_id);
        this.is_engaged = false;
    }
}
