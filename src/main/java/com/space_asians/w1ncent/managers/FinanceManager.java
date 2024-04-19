package com.space_asians.w1ncent.managers;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

public class FinanceManager extends W1NC3NTManager{

    public boolean is_engaged = false;
    private SendMessage sm = null;

    private String[] members = {"Firuz", "Nikita", "Katia", "Dasha", "Andii"};

    private String date;
    private String who;
    private String whom;
    private String how_much;
    private String for_what;

    private boolean custom_date = false;

    public SendMessage initiate(Update update){
        this.is_engaged = true;
        return this.consume(update);
    }

    private SendMessage ask_date(long chat_id){
        return SendMessage
                .builder()
                .chatId(chat_id)
                .text("Nennen Sie mir bitte die gewünschte Datum im Format ... oder lassen sie es Leer, falls ...")
                .build();
    }

    private SendMessage ask_who(long chat_id){
        return SendMessage
                .builder()
                .chatId(chat_id)
                .text("Wer ist schuld?")
                .replyMarkup(InlineKeyboardMarkup
                        .builder()
                        .keyboardRow(
                                new InlineKeyboardRow(InlineKeyboardButton
                                        .builder()
                                        .text("Firuz")
                                        .callbackData("ja")
                                        .build(),
                                        InlineKeyboardButton
                                                .builder()
                                                .text("Dasha")
                                                .callbackData("nein")
                                                .build()
                                )
                        )
                        .build())
                .build();
    }

    @Override
    public SendMessage consume(Update update){
        Message message = null;
        if(update.hasMessage()){ message = update.getMessage();};



        if(date == null){

            if(this.custom_date == true){
                this.date = message.getText();
                this.custom_date = false;
                return this.ask_who(message.getChatId());
            }

            if(update.hasCallbackQuery()){
                String call_data = update.getCallbackQuery().getData();
                long message_id = update.getCallbackQuery().getMessage().getMessageId();
                long chat_id = update.getCallbackQuery().getMessage().getChatId();

                if(call_data.equals("ja")){
                    this.date = "today";
                    return this.ask_who(chat_id);
                } else if (call_data.equals("nein")) {
                    this.custom_date = true;
                    return this.ask_date(chat_id);
                }
            }

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

        if(this.who == null){

            return this.ask_who(message.getChatId());
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
