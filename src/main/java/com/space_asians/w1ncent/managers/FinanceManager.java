package com.space_asians.w1ncent.managers;

import com.space_asians.w1ncent.entities.Transaction;
import com.space_asians.w1ncent.repositories.TransactionsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

@Service
public class FinanceManager extends W1NC3NTManager{

    private SendMessage sm = null;

    private String[] members = {"Firuz", "Nikita", "Katia", "Dasha", "Andii"};

    private Transaction transaction = new Transaction();
    @Autowired
    private TransactionsRepository transactionsRepository;
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

    private SendMessage false_input(long chat_id){
        return SendMessage
                .builder()
                .chatId(chat_id)
                .text("Tut mir leid, ich kann diese Eingabe nicht erkennen. Bitte, versuchen Sie es nochmal.")
                .build();
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
                .text("Wer hat die Transaktion durchgeführt?")
                .replyMarkup(InlineKeyboardMarkup
                        .builder()
                        .keyboardRow(
                                new InlineKeyboardRow(InlineKeyboardButton
                                        .builder()
                                        .text("Firuz")
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

    private SendMessage ask_whom(long chat_id){
        return SendMessage
                .builder()
                .chatId(chat_id)
                .text("Zu wessen gunsten wurde sie durchgeführt?")
                .replyMarkup(InlineKeyboardMarkup
                        .builder()
                        .keyboardRow(
                                new InlineKeyboardRow(InlineKeyboardButton
                                        .builder()
                                        .text("Firuz")
                                        .callbackData("firuz")
                                        .build(),
                                        InlineKeyboardButton
                                                .builder()
                                                .text("Nikita")
                                                .callbackData("nikita")
                                                .build()
                                )
                        )
                        .build())
                .build();
    }

    private SendMessage ask_how_much(long chat_id){
        return SendMessage
                .builder()
                .chatId(chat_id)
                .text("Wie groß war die Transaktion? Nennes Sie bitte die Summe in Euro.")
                .build();
    }

    private SendMessage ask_for_what(long chat_id){
        return SendMessage
                .builder()
                .chatId(chat_id)
                .text("Wofür wurde es geleistet?")
                .build();
    }

    private SendMessage summary(long chat_id){
        return SendMessage.
                builder()
                .chatId(chat_id)
                .text("Also die Eintrag ist wie folgend:\n" +
                        "am " + this.date + "\n" +
                        this.who + " -> " + this.whom + "\n" +
                        this.how_much + " für " + this.for_what)
                .build();

    }

    private void save_to_db(){
        this.transaction.setWhen(this.date);
        this.transaction.setWho(this.who);
        this.transaction.setWhom(this.whom);
        this.transaction.setHow_much(Float.parseFloat(this.how_much));
        this.transaction.setFor_what(this.for_what);
        transactionsRepository.save(transaction);
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
                }else if(call_data.equals("nein")) {
                    this.custom_date = true;
                    return this.ask_date(chat_id);
                }else{
                    return this.false_input(chat_id);
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

            if(update.hasCallbackQuery()){
                this.who = update.getCallbackQuery().getData();
                return this.ask_whom(update.getCallbackQuery().getMessage().getChatId());
            }
            return this.ask_who(message.getChatId());
        }

        if(this.whom == null){

            if(update.hasCallbackQuery()){
                this.whom = update.getCallbackQuery().getData();
                return this.ask_how_much(update.getCallbackQuery().getMessage().getChatId());
            }
            return this.ask_whom(message.getChatId());
        }

        if(this.how_much == null){

            if(update.hasMessage()){
                this.how_much = update.getMessage().getText();
                return this.ask_for_what(update.getMessage().getChatId());
            }
            return this.ask_how_much(message.getChatId());
        }

        if(this.for_what == null){

            if(update.hasMessage()){
                this.for_what = update.getMessage().getText();
                this.is_engaged = false;
                this.save_to_db();
                return this.summary(update.getMessage().getChatId());
            }
            return this.ask_for_what(message.getChatId());
        }

        return null;


    }

    @Override
    public void end(){
        this.is_engaged = false;
        this.date = null;
        this.who = null;
        this.whom = null;
        this.how_much = null;
        this.for_what = null;
    }
}
