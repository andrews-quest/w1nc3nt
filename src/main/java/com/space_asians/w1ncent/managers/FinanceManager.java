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

import java.time.*;
import java.util.*;

import static java.time.temporal.ChronoUnit.DAYS;

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
    private ArrayList<String> whom = new ArrayList<>();
    private float how_much;
    private String for_what;


    private boolean custom_date = false;
    private boolean custom_multiple_members = false;
    private ArrayList<String> excluded_members = new ArrayList<>();

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
    @Value("${text.error.date.boundaries}")
    private String text_error_date_boundaries;
    @Value("${text.error.unknown_member}")
    private String text_unknown_member;
    @Value("${text.error.sum_format}")
    private String text_error_sum_format;
    @Value("${text.error.sum_negative}")
    private String text_error_sum_negative;
    @Value("${text.finance.summary}")
    private String text_summary;
    @Value("${text.finance.next_member}")
    private String text_next_member;
    @Value("${text.finance.multiple_members}")
    private String text_multiple_members;

    public FinanceManager(){
        super.state_name = "finance";
    }

    private SendMessage custom_date(String text, Long chat_id){
        if(text.matches(" *\\d\\d[ +|/|-]\\d\\d *")){
            String[] day_and_month = text.split(" |-");
            LocalDate now = LocalDate.now();
            int year = now.getYear();

            try{
                this.date = LocalDate.of(year, Integer.parseInt(day_and_month[1]), Integer.parseInt(day_and_month[0]));
            }catch (DateTimeException e){
                return this.respond(chat_id, this.text_error_date, null);
            }

            if(this.date.until(now, DAYS) < -2 || this.date.until(now, DAYS) > 7){
                this.date = null;
                return respond(chat_id,
                        String.format(this.text_error_date_boundaries,
                                now.minusDays(7).format(this.dateFormatterPartial),
                                now.plusDays(2).format(this.dateFormatterPartial)),
                        null);
            }

            this.custom_date = false;
            return this.respond(chat_id, this.text_who, this.create_who_markup(
                    false,
                    true,
                    false,
                    null));
        }else{
            return this.respond(chat_id, this.text_false_date, null);
        }
    }

   private ReplyKeyboardMarkup create_who_markup(boolean all_selection,
                                                 boolean multiple_selection,
                                                 boolean continue_button,
                                                 ArrayList<String> excluded_members){
       List<KeyboardRow> keyboard = new ArrayList<KeyboardRow>();
       KeyboardRow row = new KeyboardRow();
       for(String member : Arrays.copyOfRange(this.members, 0, 2)){
           if(!this.excluded_members.contains(member)){row.add(member);};
       }
       keyboard.add(row);

       row = new KeyboardRow();
       for(String member : Arrays.copyOfRange(this.members, 2, this.members.length)){
           if(!this.excluded_members.contains(member)){row.add(member);};
       }
       keyboard.add(row);

       row = new KeyboardRow();
       row.add("< Zurück");
       if(multiple_selection){row.add("Mehrere");}
       if(all_selection){row.add("Alle");}
       row.add("X Beenden");
       if(continue_button){row.add("Weiter >");}
       keyboard.add(row);

       ReplyKeyboardMarkup whoMarkup = new ReplyKeyboardMarkup(keyboard);
       whoMarkup.setResizeKeyboard(true);
       whoMarkup.setIsPersistent(true);

       return whoMarkup;
   }


    private SendMessage summary(Long chat_id){
        String response = this.text_summary;
        for(String member : this.whom){
            response += this.short_format_simple_date(false,
                    this.date,
                    this.session.hget(chat_id.toString(), "who"),
                    member,
                    this.how_much,
                    this.for_what);
        }
        return respond(chat_id, response,null);
    }

    private String short_format(boolean date_first, String date, String who, String whom, Float how_much, String for_what){
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

    private boolean db_save(Long chat_id){
        String who = this.session.hget(chat_id.toString(), "who");
        try {
           Float sum_part = this.how_much / this.whom.size();
           for(String member : whom){
               this.transaction = new Transaction();
               this.transaction.setWhen(this.date);
               this.transaction.setWho(who);
               this.transaction.setWhom(member);
               this.transaction.setHow_much(sum_part);
               this.transaction.setFor_what(this.for_what);
               this.transactionsRepository.save(this.transaction);

               float balance = this.membersRepository.findBalanceByName(who) - this.how_much;
               this.membersRepository.updateBalance(who, balance);

               balance = this.membersRepository.findBalanceByName(member) + this.how_much;
               this.membersRepository.updateBalance(member, balance);
           }
           return true;
       }catch (Exception e){
           System.out.println(e);
           return false;
       }
   }

   private boolean db_restore_prev_balance(){
       try {
           this.transaction = this.transactionsRepository.findTopByOrderByIdDesc();
           this.whom.add(this.transaction.getWhom());
           float how_much = this.transaction.getHow_much();
           float who_balance = this.membersRepository.findBalanceByName(this.transaction.getWho());
           float whom_balance = this.membersRepository.findBalanceByName(this.whom.get(0));
           this.membersRepository.updateBalance(this.transaction.getWho(), who_balance + how_much);
           this.membersRepository.updateBalance(this.whom.get(0), whom_balance - how_much);
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
                        return this.respond(chat_id, this.text_who, this.create_who_markup(false,
                                false,
                                false,
                                null));
                    }else if(text.equalsIgnoreCase("Nein")){
                        this.custom_date = true;
                        return this.respond(chat_id, this.text_ask_date, null);
                    }else{
                        return this.respond(chat_id, this.text_false_input,null);
                    }
                }

                return this.respond(chat_id, this.text_date, this.create_yes_no_markup(true));

            }

            if(this.session.hget(chat_id.toString(), "who") == ""){

                if(update.hasMessage()){
                    if(Arrays.stream(this.members).toList().contains(text)){
                        this.session.hset(chat_id.toString(), "who", text);
                        this.excluded_members.add(text);
                        return this.respond(chat_id, this.text_whom, create_who_markup(false,
                                true,
                                false,
                                this.excluded_members));
                    }else{
                        return this.respond(chat_id, this.text_unknown_member, null);
                    }
                }
                return this.respond(chat_id, this.text_who, this.create_who_markup(false,
                        true,
                        false,
                        null));
            }

            if(this.whom == null){

                if(update.hasMessage()){
                    if(this.custom_multiple_members){
                        if(text.equalsIgnoreCase("Weiter >")){
                            this.whom = this.excluded_members;
                            this.custom_multiple_members = false;
                            return this.respond(chat_id,
                                    this.text_how_much,
                                    this.create_end_markup());
                        }else if(Arrays.stream(this.members).toList().contains(text)){
                            this.excluded_members.add(text);
                            return this.respond(chat_id,
                                    this.text_next_member,
                                    this.create_who_markup(false, false, true, this.excluded_members));
                        }else{
                            return this.respond(chat_id,
                                    this.text_false_input,
                                    this.create_who_markup(true,
                                            false,
                                            true,
                                            this.excluded_members));
                        }
                    }

                    if(Arrays.stream(this.members).toList().contains(text)){
                        this.whom.add(text);
                        return this.respond(chat_id, this.text_how_much, this.create_end_markup());
                    }else if(text.equalsIgnoreCase("Mehrere")){
                        this.custom_multiple_members = true;
                        return this.respond(chat_id,
                                this.text_multiple_members,
                                this.create_who_markup(false, false, true, this.excluded_members));
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
                    return this.respond(chat_id, this.text_for_what, this.create_end_markup());
                }
                return this.respond(chat_id, this.text_how_much, this.create_end_markup());
            }

            if(this.for_what == null){

                if(update.hasMessage()){
                    this.for_what = text.substring(0,1).toUpperCase() + text.substring(1);
                    this.is_engaged = false;
                    if(this.db_save(chat_id)){
                        return this.summary(chat_id);
                    }else{
                        return this.respond(chat_id, this.text_error_db, null);
                    }
                }
                return this.respond(chat_id, this.text_for_what, this.create_end_markup());
            }
        }

        if(this.get_state(chat_id).equals("history")){
            this.is_engaged = false;
            this.session.hset(chat_id.toString(), "who", update.getMessage().getText());
            String responce = "";
            Iterable<Transaction> transactions;
            String who = this.session.hget(chat_id.toString(), "who");
            if(who.equalsIgnoreCase("Alle")){
                transactions = this.transactionsRepository.findAllOrderByWhenAsc();
            }else if(Arrays.stream(this.members).toList().contains(who)){
                transactions = this.transactionsRepository.findHistory(who);
            }else{
                return respond(chat_id, this.text_false_input, null);
            }

            LocalDate previous_date = null;
            for (Transaction transaction : transactions){
                if(!transaction.getWhen().equals(previous_date)){
                    responce+="\n";
                }
                ArrayList<String> temp_whom = new ArrayList<>();
                temp_whom.add(transaction.getWhom());
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
        this.session.hset(chat_id.toString(), "who", "");
        this.whom = null;
        this.how_much = 0;
        this.for_what = null;
        this.custom_date = false;
        this.custom_multiple_members = false;
        this.excluded_members = new ArrayList<>();
    }

    public SendMessage update(Update update){
        this.is_engaged = true;
        this.set_state("update", update.getMessage().getChatId());
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
               this.create_who_markup(true, false, false, null));
    }

    public SendMessage cancel_last(Update update){
       this.is_engaged = true;
       Transaction prev_transaction = this.transactionsRepository.findTopByOrderByIdDesc();
       ArrayList<String> who_temp = new ArrayList<>();
       who_temp.add(prev_transaction.getWho());
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
