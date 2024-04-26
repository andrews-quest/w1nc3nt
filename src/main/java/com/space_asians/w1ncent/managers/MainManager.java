package com.space_asians.w1ncent.managers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;


@Service
public class MainManager extends W1nc3ntManager {


    @Value("${text.main.greeting}")
    private String text_greeting;
    @Value("${text.main.about}")
    private String text_about;
    @Value("${main.version}")
    private String text_version;
    @Value("${text.main.unknown_command}")
    private String text_unknown_command;
    @Value("${text.error")
    private String text_error;

    public SendMessage greet(Update update){
        return this.respond(update.getMessage().getChatId(), this.text_greeting, null);
    }

    public SendMessage about(Update update){
        return this.respond(update.getMessage().getChatId(),
                String.format(this.text_about, this.text_version),
                null);
    }

    public SendMessage error(Update update){
        return this.respond(update.getMessage().getChatId(), this.text_error, null);
    }

    public SendMessage unknown(Update update){
        return this.respond(update.getMessage().getChatId(), this.text_unknown_command, null);
    }
}
