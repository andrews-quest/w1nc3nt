package com.space_asians.w1ncent;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

public class FinanceManager {

    public boolean is_engaged = false;
    private SendMessage sm = null;

    private String[] members = {"Firuz", "Nikita", "Katia", "Dasha", "Andii"};

    private String date;
    private String who;
    private String whom;
    private String how_much;
    private String for_what;

    public SendMessage initiate(Message message){
        this.is_engaged = true;
        return this.consume(message);
    }

    public SendMessage consume(Message message){
        if(date == null){
            return SendMessage // Create a message object
                    .builder()
                    .chatId(message.getChatId())
                    .text("Fand es heute statt?")
                    // Set the keyboard markup
                    .replyMarkup(InlineKeyboardMarkup
                            .builder()
                            .keyboardRow(
                                    new InlineKeyboardRow(InlineKeyboardButton
                                            .builder()
                                            .text("Ja")
                                            .callbackData("ja")
                                            .build(),
                                            InlineKeyboardButton
                                                    .builder()
                                                    .text("Nein")
                                                    .callbackData("nein")
                                                    .build()
                                    )
                            )
                            .build())
                    .build();

        }

        if(this.who.equals(null)){

            return SendMessage // Create a message object
                    .builder()
                    .chatId(message.getChatId())
                    .text("Wer ist schuld?")
                    // Set the keyboard markup
                    .replyMarkup(InlineKeyboardMarkup
                            .builder()
                            .keyboardRow(
                                    new InlineKeyboardRow(InlineKeyboardButton
                                            .builder()
                                            .text("Fizuz")
                                            .callbackData("firuz")
                                            .build(),
                                            InlineKeyboardButton
                                                    .builder()
                                                    .text("Dasha")
                                                    .callbackData("dasha")
                                                    .build()
                                    )
                            )
                            .build())
                    .build();
        }

        return null;


    }

    private void end(){
        this.is_engaged = false;
        this.date = "";
        this.who = "";
        this.whom = "";
        this.how_much = "";
        this.for_what = "";
    }
}
