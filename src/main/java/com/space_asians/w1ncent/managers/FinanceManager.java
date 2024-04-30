package com.space_asians.w1ncent.managers;

import com.space_asians.w1ncent.entities.Transaction;
import com.space_asians.w1ncent.repositories.MembersRepository;
import com.space_asians.w1ncent.repositories.TransactionsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.time.*;
import java.time.format.DateTimeParseException;
import java.util.*;

@Component
public class FinanceManager extends W1nc3ntManager {

    @Value("${main.members}")
    private String[] members;

    private Transaction transaction = new Transaction();
    @Autowired
    private TransactionsRepository transactionsRepository;
    @Autowired
    private MembersRepository membersRepository;
    private LocalDate date;
    private String who;
    private String whom;
    private float how_much;
    private String for_what;

    private boolean custom_date = false;

    // SendMessage texts
    @Value("${text.finance.date}")
    private String text_date;
    @Value("${text.finance.false_input}")
    private String text_false_input;
    @Value("${text.finance.ask_date}")
    private String text_ask_date;
    @Value("${text.finance.false_date}")
    private String text_false_date;
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
    @Value("${text.error.date}")
    private String text_error_date;
    @Value("${text.error.unknown_member}")
    private String text_unknown_member;
    @Value("${text.error.sum_format}")
    private String text_error_sum_format;
    @Value("${text.error.sum_negative}")
    private String text_error_sum_negative;
    @Value("${text.finance.summary}")
    private String text_summary;



    // Reply Keyboard Markups
    private ReplyKeyboardMarkup YesNoMarkup;
    private ReplyKeyboardMarkup whoMarkup;
    private ReplyKeyboardMarkup whomMarkup;


    private SendMessage custom_date(String text, Long chat_id){
        if(text.matches(" *\\d\\d[ +|/|-]\\d\\d *")){
            String[] day_and_month = text.split(" |-");
            int year = LocalDate.now().getYear();
            try{
                this.date = LocalDate.of(year, Integer.parseInt(day_and_month[1]), Integer.parseInt(day_and_month[0]));
            }catch (DateTimeException e){
                return this.respond(chat_id, this.text_error_date, null);
            }
            this.custom_date = false;
            return this.respond(chat_id, this.text_who, this.whoMarkup);
        }else{
            return this.respond(chat_id, this.text_false_date, null);
        }
    }
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


    private SendMessage summary(long chat_id){
        return respond(chat_id,
                this.text_summary + this.short_format_simple_date(false,
                        this.date,
                        this.who,
                        this.whom,
                        this.how_much,
                        this.for_what),
                null);
    }

    private String short_format(boolean date_first, String date, String who, String whom, float how_much, String for_what){
        who = who.substring(0,1);
        whom = whom.substring(0,1);
        if (date_first) {
            return String.format("%s %s -> %s %.2f€ für %s", date, who, whom, how_much, for_what);
        } else {
            return String.format("%s -> %s %.2f€ für %s %s", who, whom, how_much, for_what, date);
        }
    }

    private String short_format_simple_date(boolean date_first, LocalDate date, String who, String whom, float how_much, String for_what){
       if(date.equals(LocalDate.now())) {
            return short_format(date_first, "heute", who, whom, how_much, for_what);
       }else if(date.equals(LocalDate.now().minusDays(1))){
            return short_format(date_first, "gestern", who, whom, how_much, for_what);
       }else{
           String date_str = date.format(this.dateFormatterPartial);
           return short_format(date_first, date_str, who, whom, how_much, for_what);
       }
    }

    private boolean db_save(){
       try {
           this.transaction.setWhen(this.date);
           this.transaction.setWho(this.who);
           this.transaction.setWhom(this.whom);
           this.transaction.setHow_much(this.how_much);
           this.transaction.setFor_what(this.for_what);
           transactionsRepository.save(this.transaction);

           float balance = this.membersRepository.findBalanceByName(this.who) - this.how_much;
           this.membersRepository.updateBalance(this.who, balance);

           balance = this.membersRepository.findBalanceByName(this.whom) + this.how_much;
           this.membersRepository.updateBalance(this.whom, balance);
           return true;
       }catch (Exception e){
           System.out.println(e);
           return false;
       }
   }

   private boolean db_restore_prev_balance(){
       try {
           this.transaction = this.transactionsRepository.findTopByOrderByIdDesc();
           this.who = this.transaction.getWho();
           this.whom = this.transaction.getWhom();
           float how_much = this.transaction.getHow_much();
           float who_balance = this.membersRepository.findBalanceByName(this.who);
           float whom_balance = this.membersRepository.findBalanceByName(this.whom);
           this.membersRepository.updateBalance(this.who, who_balance + how_much);
           this.membersRepository.updateBalance(this.whom, whom_balance - how_much);
           this.transactionsRepository.deleteById(this.transaction.getId());
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
        Long chat_id = null;
        if(update.hasMessage()){
            message = update.getMessage();
            text = message.getText();
            chat_id = message.getChatId();
        };

        if(text.equalsIgnoreCase("Beenden") || text.equalsIgnoreCase("End")){
            this.end(chat_id);
            return respond(chat_id, this.text_exit, null);
        }


        if(this.get_state(chat_id).equals("update")){
            if(date == null){

                if(this.custom_date){
                    return this.custom_date(text, chat_id);
                }

                if(update.hasMessage() && !Objects.equals(text, "/finances_update")){
                    if(text.equalsIgnoreCase("Ja")){
                        this.date = LocalDate.now();
                        return this.respond(chat_id, this.text_who, this.whoMarkup);
                    }else if(text.equalsIgnoreCase("Nein")) {
                        this.custom_date = true;
                        return this.respond(chat_id, this.text_ask_date, null);
                    }else{
                        return this.respond(chat_id, this.text_false_input,null);
                    }
                }

                return this.respond(chat_id, this.text_date, this.create_yes_no_markup(true));

            }

            if(this.who == null){

                if(update.hasMessage()){
                    if(Arrays.stream(this.members).toList().contains(text)){
                        this.who = text;
                        return this.respond(chat_id, this.text_whom, create_who_markup(false, false, this.who));
                    }else{
                        return this.respond(chat_id, this.text_unknown_member, null);
                    }
                }
                return this.respond(chat_id, this.text_who, this.whoMarkup);
            }

            if(this.whom == null){

                if(update.hasMessage()){
                    if(Arrays.stream(this.members).toList().contains(text)){
                        this.whom = text;
                        return this.respond(chat_id, this.text_how_much, null);
                    }else{
                        return this.respond(chat_id, this.text_unknown_member, null);
                    }
                }
                return this.respond(chat_id, this.text_whom, null);
            }

            if(this.how_much == 0){

                if(update.hasMessage()){
                    try{
                        this.how_much = Float.parseFloat(text);
                    }catch (NumberFormatException e){
                        return this.respond(chat_id, this.text_error_sum_format, null);
                    }
                    if(this.how_much < 0){
                        this.how_much = 0;
                        return this.respond(chat_id, this.text_error_sum_negative, null);
                    }
                    return this.respond(chat_id, this.text_for_what, null);
                }
                return this.respond(chat_id, this.text_how_much, null);
            }

            if(this.for_what == null){

                if(update.hasMessage()){
                    this.for_what = text.substring(0,1).toUpperCase() + text.substring(1);
                    this.is_engaged = false;
                    if(this.db_save()){
                        return this.summary(chat_id);
                    }else{
                        return this.respond(chat_id, this.text_error_db, null);
                    }
                }
                return this.respond(chat_id, this.text_for_what, null);
            }
        }

        if(this.get_state(chat_id).equals("history")){
            this.is_engaged = false;
            this.who = update.getMessage().getText();
            String responce = "";
            Iterable<Transaction> transactions;
            if(this.who.equalsIgnoreCase("Alle")){
                transactions = this.transactionsRepository.findAllOrderByWhenAsc();
            }else if(Arrays.stream(this.members).toList().contains(this.who)){
                transactions = this.transactionsRepository.findHistory(this.who);
            }else{
                return respond(chat_id, this.text_false_input, null);
            }

            LocalDate previous_date = null;
            for (Transaction transaction : transactions){
                if(!transaction.getWhen().equals(previous_date)){
                    responce+="\n";
                }
                responce += short_format_simple_date(true,
                        transaction.getWhen(),
                        transaction.getWho(),
                        transaction.getWhom(),
                        transaction.getHow_much(),
                        transaction.getFor_what());
                responce += "\n";
                previous_date = transaction.getWhen();
            }
            return respond(chat_id, responce,null);
        }

        if(this.get_state(chat_id).equals("cancel")){
            this.end(chat_id);
            if(update.getMessage().getText().equalsIgnoreCase("ja")){
                if(this.db_restore_prev_balance()){
                    return respond(chat_id, this.text_cancel_yes, null);
                }else{
                    return respond(chat_id, this.text_error_db, null);
                }
            }else if (update.getMessage().getText().equalsIgnoreCase("nein")){
                return respond(chat_id, this.text_cancel_no, null);
            }else{
                return respond(chat_id, this.text_false_input, null);
            }
        }

        this.end(chat_id);
        return respond(chat_id, this.text_error, null);
    }

    @Override
    public void end(Long chat_id){
        super.end(chat_id);
        this.transaction = new Transaction();
        this.is_engaged = false;
        this.date = null;
        this.who = null;
        this.whom = null;
        this.how_much = 0;
        this.for_what = null;
    }

    public SendMessage update(Update update){
        this.is_engaged = true;
        this.set_state("update", update.getMessage().getChatId());
        this.create_markups();
        return this.consume(update);
    }

    public SendMessage check(Update update){
       this.set_state("check", update.getMessage().getChatId());
       String text = this.text_finances_check;
       for(String member : this.members){
           String balance = String.valueOf(this.membersRepository.findBalanceByName(member));
           text += String.format("\n %s : %s", member, balance);
       }
       return respond(update.getMessage().getChatId(), text, null);
    }

    public SendMessage history(Update update){
       this.is_engaged = true;
       this.set_state("history", update.getMessage().getChatId());
       return respond(update.getMessage().getChatId(),
               this.text_history,
               this.create_who_markup(true, true, null));
    }

    public SendMessage cancel_last(Update update){
       this.is_engaged = true;
       Transaction prev_transaction = this.transactionsRepository.findTopByOrderByIdDesc();
       String prev_transaction_short = this.short_format_simple_date(false,
               prev_transaction.getWhen(),
               prev_transaction.getWhom(),
               prev_transaction.getWho(),
               prev_transaction.getHow_much(),
               prev_transaction.getFor_what());
       this.set_state("cancel", update.getMessage().getChatId());
       return respond(update.getMessage().getChatId(),
               String.format(this.text_cancel, prev_transaction_short),
               this.create_yes_no_markup(false));
    }
}
