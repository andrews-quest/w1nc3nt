package com.space_asians.w1ncent.managers;

import com.space_asians.w1ncent.entities.Member;
import com.space_asians.w1ncent.repositories.MembersRepository;
import com.space_asians.w1ncent.repositories.TransactionsRepository;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
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
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
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

    // Redis-based session set up
    protected RedisClient redisClient = RedisClient.create("redis://localhost:6379/0");
    StatefulRedisConnection<String, String> redisConnection = this.redisClient.connect();
    RedisCommands<String, String> session = this.redisConnection.sync();

    public boolean is_engaged = false;
    protected String state_name = null;

    protected DateTimeFormatter dateFormatterPartial = DateTimeFormatter.ofPattern("dd-MM");
    protected DateTimeFormatter dateFormatterFull = DateTimeFormatter.ofPattern("dd-MM-uuuu");

    // protected Update update = null;
    // protected Message message = null;
    // protected Long chat_id = null;

    @Value("${text.error.default_manager}")
    protected String text_error_default_manager;

    @Value("${text.error.state_undefined}")
    protected String text_error_state_undefined;

    protected SendMessage respond(long chat_id, String text, ReplyKeyboardMarkup markup){
        return SendMessage
               .builder()
               .chatId(chat_id)
               .text(text)
               .replyMarkup(markup == null ? new ReplyKeyboardRemove(true) : markup)
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
            row.add("X Beenden");
            keyboard.add(row);
        }
        ReplyKeyboardMarkup YesNoMarkup = new ReplyKeyboardMarkup(keyboard);
        YesNoMarkup.setResizeKeyboard(true);
        YesNoMarkup.setIsPersistent(true);
        return YesNoMarkup;
    }

    protected ReplyKeyboardMarkup create_end_markup(){
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add("< Zurück");
        row.add("X Beenden");
        keyboard.add(row);
        ReplyKeyboardMarkup EndMarkup = new ReplyKeyboardMarkup(keyboard);
        EndMarkup.setResizeKeyboard(true);
        EndMarkup.setIsPersistent(true);
        return EndMarkup;
    }

    public String get_name(){
        return this.state_name != null ? this.state_name : this.text_error_state_undefined;
    }

    protected String get_state(Long chat_id){
        Member member = this.membersRepository.findByChatId(chat_id).orElse(null);
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
