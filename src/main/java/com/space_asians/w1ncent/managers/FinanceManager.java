package com.space_asians.w1ncent.managers;

import com.space_asians.w1ncent.entities.Transaction;
import com.space_asians.w1ncent.repositories.MembersRepository;
import com.space_asians.w1ncent.repositories.TransactionsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class FinanceManager extends W1NC3NTManager{

    private String[] members = {"Firuz", "Nikita", "Katia", "Dasha", "Andrii"};

    private Transaction transaction = new Transaction();
    @Autowired
    private TransactionsRepository transactionsRepository;
    @Autowired
    private MembersRepository membersRepository;
    private String date;
    private String who;
    private String whom;
    private String how_much;
    private String for_what;

    private boolean custom_date = false;

    // SendMessage texts
    @Value("${text.finance.date}")
    private String text_date;
    @Value("${text.finance.false_input}")
    private String text_false_input;
    @Value("${text.finance.ask_date}")
    private String text_ask_date;
    @Value("${text.finance.who}")
    private String text_who;
    @Value("${text.finance.whom}")
    private String text_whom;
    @Value("${text.finance.how_much}")
    private String text_how_much;
    @Value("${text.finance.for_what}")
    private String text_for_what;


    // Reply Keyboard Markups
    private ReplyKeyboardMarkup dateMarkup;
    private ReplyKeyboardMarkup whoMarkup;
    private ReplyKeyboardMarkup whomMarkup;

   private void create_markups(){

       // date Markup

       List<KeyboardRow> keyboard = new ArrayList<>();
       KeyboardRow row = new KeyboardRow();


       row.add("Ja");
       row.add("Nein");
       keyboard.add(row);

       row = new KeyboardRow();
       row.add("Beenden");
       keyboard.add(row);
       this.dateMarkup = new ReplyKeyboardMarkup(keyboard);

       // who Markup

       dateMarkup.setKeyboard(keyboard);
       keyboard = new ArrayList<KeyboardRow>();
       row = new KeyboardRow();


       row.add("Firuz");
       row.add("Dasha");
       row.add("Nikita");
       keyboard.add(row);

       row = new KeyboardRow();
       row.add("Katya");
       row.add("Andrii");
       row.add("Beenden");

       keyboard.add(row);
       this.whoMarkup = new ReplyKeyboardMarkup(keyboard);

       // whom Markup

       keyboard = new ArrayList<KeyboardRow>();
       row = new KeyboardRow();

       row.add("Firuz");
       row.add("Dasha");
       row.add("Nikita");
       keyboard.add(row);

       row = new KeyboardRow();
       row.add("Katya");
       row.add("Andrii");
       row.add("Beenden");


       keyboard.add(row);
       this.whomMarkup = new ReplyKeyboardMarkup(keyboard);
   }

    public SendMessage initiate(Update update){
        this.is_engaged = true;
        this.create_markups();
        return this.consume(update);
    }

    private SendMessage false_input(long chat_id){
        return SendMessage
                .builder()
                .chatId(chat_id)
                .text(this.text_false_input)
                .build();
    }

    private SendMessage ask_date(long chat_id){
        return SendMessage
                .builder()
                .chatId(chat_id)
                .text(this.text_ask_date)
                .build();
    }

    private SendMessage ask_who(long chat_id){
        return SendMessage
                .builder()
                .chatId(chat_id)
                .text(this.text_who)
                .replyMarkup(whoMarkup)
                .build();
    }

    private SendMessage ask_whom(long chat_id){
        return SendMessage
                .builder()
                .chatId(chat_id)
                .text(this.whom)
                .replyMarkup(whomMarkup)
                .build();
    }

    private SendMessage ask_how_much(long chat_id){
        return SendMessage
                .builder()
                .chatId(chat_id)
                .text(this.text_how_much)
                .build();
    }

    private SendMessage ask_for_what(long chat_id){
        return SendMessage
                .builder()
                .chatId(chat_id)
                .text(this.text_for_what)
                .build();
    }

    private SendMessage summary(long chat_id){
        return SendMessage.
                builder()
                .chatId(chat_id)
                .replyMarkup(null)
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
        transactionsRepository.save(this.transaction);
    }


    // Main Function

    @Override
    public SendMessage consume(Update update){
        Message message = null;
        if(update.hasMessage()){ message = update.getMessage();};



        if(date == null){

            if(this.custom_date){
                this.date = message.getText();
                this.custom_date = false;
                return this.ask_who(message.getChatId());
            }

            if(update.hasMessage() && !Objects.equals(update.getMessage().getText(), "/finances_update")){
                String text = update.getMessage().getText();
                long chat_id = update.getMessage().getChatId();

                if(text.equals("Ja")){
                    this.date = "today";
                    return this.ask_who(chat_id);
                }else if(text.equals("Nein")) {
                    this.custom_date = true;
                    return this.ask_date(chat_id);
                }else{
                    return this.false_input(chat_id);
                }
            }

            return SendMessage
                    .builder()
                    .chatId(message.getChatId())
                    .text(this.text_date)
                    .replyMarkup(dateMarkup)
                    .build();

        }

        if(this.who == null){

            if(update.hasMessage()){
                this.who = update.getMessage().getText();
                return this.ask_whom(update.getMessage().getChatId());
            }
            return this.ask_who(message.getChatId());
        }

        if(this.whom == null){

            if(update.hasMessage()){
                this.whom = update.getMessage().getText();
                return this.ask_how_much(update.getMessage().getChatId());
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

    public SendMessage check(Update update){
       String checkMessage = this.transactionsRepository.findAll().toString();
       return SendMessage
               .builder()
               .chatId(update.getMessage().getChatId())
               .text(checkMessage)
               .build();
    }
}
