package com.space_asians.w1ncent.managers;

import com.space_asians.w1ncent.entities.Transaction;
import com.space_asians.w1ncent.repositories.MembersRepository;
import com.space_asians.w1ncent.repositories.TransactionsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.*;

@Component
public class FinanceManager extends W1NC3NTManager{

    @Value("${main.members}")
    private String[] members;

    private String state = "none";

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
    @Value("${text.finance.history}")
    private String text_history;
    @Value("${text.finance.check}")
    private String text_finances_check;
    @Value("${text.error}")
    private String text_error;
    @Value("${text.finance.exit}")
    private String text_exit;
    @Value("${text.finance.cancel}")
    private String text_cancel;
    @Value("${text.finance.cancel.yes}")
    private String text_cancel_yes;
    @Value("${text.finance.cancel.no}")
    private String text_cancel_no;
    @Value("${text.error.db}")
    private String text_error_db;
    @Value("${text.finance.summary}")
    private String text_summary;



    // Reply Keyboard Markups
    private ReplyKeyboardMarkup YesNoMarkup;
    private ReplyKeyboardMarkup whoMarkup;
    private ReplyKeyboardMarkup whomMarkup;

   private void create_markups() {

       // date Markup

       List<KeyboardRow> keyboard = new ArrayList<KeyboardRow>();
       KeyboardRow row = new KeyboardRow();

       row.add("Firuz");
       row.add("Dasha");
       row.add("Nikita");
       keyboard.add(row);

       row = new KeyboardRow();
       row.add("Katia");
       row.add("Andrii");
       row.add("Beenden");

       keyboard.add(row);
       this.whoMarkup = new ReplyKeyboardMarkup(keyboard);

   }

   private ReplyKeyboardMarkup create_yes_no_markup(boolean has_end_option){
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
       YesNoMarkup = new ReplyKeyboardMarkup(keyboard);
       return YesNoMarkup;
   }

   private ReplyKeyboardMarkup create_who_markup(boolean is_inline, boolean include_all_button, String excluded_member){
       List<KeyboardRow> keyboard = new ArrayList<KeyboardRow>();
       KeyboardRow row = new KeyboardRow();
       for(String member : Arrays.copyOfRange(this.members, 0, 2)){
           if(!member.equals(excluded_member)){row.add(member);};
       }
       keyboard.add(row);

       row = new KeyboardRow();
       for(String member : Arrays.copyOfRange(this.members, 2, this.members.length)){
           if(!member.equals(excluded_member)){row.add(member);};
       }
       keyboard.add(row);

       row = new KeyboardRow();
       if(include_all_button){row.add("Alle");};
       row.add("Beenden");
       keyboard.add(row);
       whomMarkup = new ReplyKeyboardMarkup(keyboard);
       return whomMarkup;
   }

    public SendMessage update(Update update){
        this.is_engaged = true;
        this.state = "update";
        this.create_markups();
        return this.consume(update);
    }


    private SendMessage summary(long chat_id){
        return SendMessage.
                builder()
                .chatId(chat_id)
                .replyMarkup(null)
                .text(this.text_summary + this.short_format(false))
                .build();

    }

    private String short_format(boolean date_first){
       if(date_first){
           return String.format("%s %s -> %s %s für %s", this.date, this.who, this.whom, this.how_much, this.for_what);
       }else{
           return String.format("%s -> %s %s für %s am %s", this.who, this.whom, this.how_much, this.for_what, this.date);
       }
    }

    private void db_save(){
        this.transaction.setWhen(this.date);
        this.transaction.setWho(this.who);
        this.transaction.setWhom(this.whom);
        this.transaction.setHow_much(Float.parseFloat(this.how_much));
        this.transaction.setFor_what(this.for_what);
        transactionsRepository.save(this.transaction);

        float balance = this.membersRepository.findBalanceByName(this.who) - Float.parseFloat(this.how_much);
        this.membersRepository.updateBalance(this.who, balance);

        balance = this.membersRepository.findBalanceByName(this.whom) + Float.parseFloat(this.how_much);
        this.membersRepository.updateBalance(this.whom, balance);
   }

   private boolean db_restore_prev_balance(){
       try {
           int last_transaction = (int) this.transactionsRepository.count();
           this.who = this.transactionsRepository.findById(last_transaction).orElse(null).getWho();
           this.whom = this.transactionsRepository.findById(last_transaction).orElse(null).getWhom();
           float how_much = this.transactionsRepository.findById(last_transaction).orElse(null).getHow_much();
           float who_balance = this.membersRepository.findBalanceByName(this.who);
           float whom_balance = this.membersRepository.findBalanceByName(this.whom);
           this.membersRepository.updateBalance(this.who, who_balance + how_much);
           this.membersRepository.updateBalance(this.whom, whom_balance - how_much);
           this.transactionsRepository.deleteById(last_transaction);
           return true;
       }catch (Exception e){
           System.out.println(e);
           return false;
       }
   }




    // Main Function

    @Override
    public SendMessage consume(Update update){
        Message message = null;
        String text = null;
        if(update.hasMessage()){
            message = update.getMessage();
            text = message.getText();
        };

        if(text.equalsIgnoreCase("Beenden") || text.equalsIgnoreCase("End")){
            this.end();
            return SendMessage
                    .builder()
                    .chatId(message.getChatId())
                    .text(this.text_exit)
                    .build();
        }


        if(this.state == "update"){
            if(date == null){

                if(this.custom_date){
                    this.date = message.getText();
                    this.custom_date = false;
                    return this.respond(message.getChatId(), text_who, whoMarkup);
                }

                if(update.hasMessage() && !Objects.equals(update.getMessage().getText(), "/finances_update")){
                    long chat_id = update.getMessage().getChatId();

                    if(text.equalsIgnoreCase("Ja")){
                        this.date = "today";
                        return this.respond(chat_id, text_who, this.whoMarkup);
                    }else if(text.equalsIgnoreCase("Nein")) {
                        this.custom_date = true;
                        return this.respond(chat_id, text_ask_date, null);
                    }else{
                        return this.respond(chat_id, text_false_input,null);
                    }
                }

                return this.respond(message.getChatId(), text_date, this.create_yes_no_markup(true));

            }

            if(this.who == null){

                if(update.hasMessage()){
                    this.who = update.getMessage().getText();
                    return this.respond(update.getMessage().getChatId(), text_whom, create_who_markup(false, false, this.who));
                }
                return this.respond(update.getMessage().getChatId(), text_who, this.whoMarkup);
            }

            if(this.whom == null){

                if(update.hasMessage()){
                    this.whom = update.getMessage().getText();
                    return this.respond(update.getMessage().getChatId(), text_how_much, null);
                }
                return this.respond(message.getChatId(), text_whom, null);
            }

            if(this.how_much == null){

                if(update.hasMessage()){
                    this.how_much = update.getMessage().getText();
                    return this.respond(update.getMessage().getChatId(), text_for_what, null);
                }
                return this.respond(message.getChatId(), text_how_much, null);
            }

            if(this.for_what == null){

                if(update.hasMessage()){
                    this.for_what = update.getMessage().getText();
                    this.is_engaged = false;
                    this.db_save();
                    return this.summary(update.getMessage().getChatId());
                }
                return this.respond(message.getChatId(), text_for_what, null);
            }
        }

        if(this.state == "history"){
            this.is_engaged = false;
            this.who = update.getMessage().getText();
            return SendMessage
                    .builder()
                    .chatId(update.getMessage().getChatId())
                    .text(this.transactionsRepository.findAll().toString())
                    .build();


                // if(Arrays.stream(this.members).anyMatch(update.getMessage().getText() -> update.getMessage().getText());

                // return this.history(update);
        }

        if(this.state == "cancel"){
            this.end();
            if(update.getMessage().getText().equalsIgnoreCase("ja")){
                if(this.db_restore_prev_balance()){
                    return respond(message.getChatId(), this.text_cancel_yes, null);
                }else{
                    return respond(message.getChatId(), this.text_error_db, null);
                }
            }else if (update.getMessage().getText().equalsIgnoreCase("nein")){
                return respond(message.getChatId(), this.text_cancel_no, null);
            }else{
                return respond(message.getChatId(), this.text_false_input, null);
            }
        }

        this.end();
        return respond(message.getChatId(), this.text_error, null);
    }

    @Override
    public void end(){
        this.state = "none";
        this.is_engaged = false;
        this.date = null;
        this.who = null;
        this.whom = null;
        this.how_much = null;
        this.for_what = null;
    }

    public SendMessage check(Update update){
       this.state = "check";
       String text = this.text_finances_check;
       for(String member : this.members){
           String balance = String.valueOf(this.membersRepository.findBalanceByName(member));
           text += String.format("\n %s : %s", member, balance);
       }
       return SendMessage
               .builder()
               .chatId(update.getMessage().getChatId())
               .text(text)
               .build();
    }

    public SendMessage history(Update update){
       this.is_engaged = true;
       this.state = "history";
       return SendMessage
               .builder()
               .chatId(update.getMessage().getChatId())
               .text(this.text_history)
               .replyMarkup(this.create_who_markup(true, true, null))
               .build();
    }

    public SendMessage cancel_last(Update update){
       this.is_engaged = true;
       this.state = "cancel";
       return SendMessage
               .builder()
               .chatId(update.getMessage().getChatId())
               .text(this.text_cancel)
               .replyMarkup(this.create_yes_no_markup(false))
               .build();
    }
}
