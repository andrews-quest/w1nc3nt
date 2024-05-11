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

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;

@Component
public class FinanceManager extends W1nc3ntManager {

    private final String[] states = {"date", "payer", "receivers", "sum", "occasion", "summary"};
    @Value("${main.members}")
    private String[] members;
    @Autowired
    private TransactionsRepository transactionsRepository;
    @Autowired
    private MembersRepository membersRepository;
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
    @Value("${text.finance.no_previous_transactions}")
    private String text_no_previous_transactions;

    public FinanceManager() {
        super.state_name = "finance";
    }



    private boolean db_restore_prev_balance(Long chat_id) {
        try {
            this.membersRepository.updatePrevious(chat_id, null);

            for(String id : this.session.lrange(chat_id + ":previous_transactions", 0, -1)){
                Transaction transaction = transactionsRepository.findById(Integer.valueOf(id)).get();

                membersRepository.updateBalance(transaction.getWho(), -transaction.getHow_much());
                membersRepository.updateBalance(transaction.getWhom(), transaction.getHow_much());

                transactionsRepository.deleteById(transaction.getId());
            }
            return true;
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
    }

    private SendMessage ask_date(Update update) {
        Long chat_id = update.getMessage().getChatId();
        String text = update.getMessage().getText();

        if (this.session.get(chat_id + ":awaiting_response").equals("false")){
            this.session.set(chat_id + ":awaiting_response", "true");
            return this.respond(chat_id, this.text_date, this.create_yes_no_markup(true));
        }

        if (this.session.get(chat_id + ":custom_date").equals("true")) {
            return this.custom_date(update);
        }

        if (text.equalsIgnoreCase("Ja")) {
            this.session.set(chat_id + ":date", String.valueOf(LocalDate.now()));
            this.session.incr(chat_id.toString() + ":state_finances_update");
            this.session.set(chat_id + ":awaiting_response", "false");
            return this.consume(update);
        } else if (text.equalsIgnoreCase("Nein")) {
            this.session.set(chat_id + ":custom_date", "true");
            return this.respond(chat_id, this.text_ask_date, null);
        }

        return this.respond(chat_id, this.text_false_input, null);
    }

    private SendMessage custom_date(Update update) {
        Long chat_id = update.getMessage().getChatId();
        String text = update.getMessage().getText();

        if (text.matches(" *\\d\\d[ +|/|-]\\d\\d *")) {
            String[] day_and_month = text.split(" |-");
            LocalDate now = LocalDate.now();
            int year = now.getYear();
            LocalDate date;

            try {
                date = LocalDate.of(year, Integer.parseInt(day_and_month[1]), Integer.parseInt(day_and_month[0]));
            } catch (DateTimeException e) {
                return this.respond(chat_id, this.text_error_date, null);
            }

            if (date.until(now, DAYS) < -2 || date.until(now, DAYS) > 7) {
                return respond(chat_id,
                        String.format(this.text_error_date_boundaries,
                                now.minusDays(7).format(this.dateFormatterPartial),
                                now.plusDays(2).format(this.dateFormatterPartial)),
                        null);
            }

            this.session.set(chat_id + ":date", date.toString());
            this.session.set(chat_id + ":custom_date", "false");
            this.session.set(chat_id + ":awaiting_response", "false");
            this.session.incr(chat_id + ":state_finances_update");
            return consume(update);
        } else {
            return this.respond(chat_id, this.text_false_date, null);
        }
    }

    private ReplyKeyboardMarkup create_who_markup(boolean all_selection,
                                                  boolean multiple_selection,
                                                  boolean continue_button,
                                                  ArrayList<String> excluded_members) {
        List<KeyboardRow> keyboard = new ArrayList<KeyboardRow>();
        KeyboardRow row = new KeyboardRow();
        for (String member : Arrays.copyOfRange(this.members, 0, 2)) {
            if(excluded_members != null){
                if (!excluded_members.contains(member)) {row.add(member);}
            }else {
                row.add(member);
            }
        }
        keyboard.add(row);

        row = new KeyboardRow();
        for (String member : Arrays.copyOfRange(this.members, 2, this.members.length)) {
            if(excluded_members != null){
                if (!excluded_members.contains(member)) {row.add(member);}
            }else {
                row.add(member);
            }
        }
        keyboard.add(row);

        row = new KeyboardRow();
        row.add("< Zurück");
        if (multiple_selection) {
            row.add("Mehrere");
        }
        if (all_selection) {
            row.add("Alle");
        }
        row.add("X Beenden");
        if (continue_button) {
            row.add("Weiter >");
        }
        keyboard.add(row);

        ReplyKeyboardMarkup whoMarkup = new ReplyKeyboardMarkup(keyboard);
        whoMarkup.setResizeKeyboard(true);
        whoMarkup.setIsPersistent(true);

        return whoMarkup;
    }



    private String short_format(boolean date_first, String date, String who, String whom, Float how_much, String for_what) {
        who = who.substring(0, 1);
        whom = whom.substring(0, 1);
        if (date_first) {
            return String.format("%s %s -> %s %.2f€ für %s", date, who, whom, how_much, for_what);
        } else {
            return String.format("%s -> %s %.2f€ für %s %s", who, whom, how_much, for_what, date);
        }
    }

    private String short_format_simple_date(boolean date_first, LocalDate date, String who, String whom, float how_much, String for_what) {
        if (date.equals(LocalDate.now())) {
            return short_format(date_first, "heute", who, whom, how_much, for_what);
        } else if (date.equals(LocalDate.now().minusDays(1))) {
            return short_format(date_first, "gestern", who, whom, how_much, for_what);
        } else {
            String date_str = date.format(this.dateFormatterPartial);
            return short_format(date_first, date_str, who, whom, how_much, for_what);
        }
    }

    private boolean db_save(Long chat_id) {
        String who = this.session.get(chat_id + ":payer");
        String previous = "";
        try {
            String[] receivers = this.session.lrange(chat_id + ":receivers", 0, -1).toArray(new String[0]);
            Float sum = Float.parseFloat(this.session.get(chat_id + ":sum"));
            Float sum_part = sum / receivers.length;
            String author = this.membersRepository.findNameByChatId(chat_id);
            for (String member : receivers) {
                Transaction transaction = new Transaction();
                transaction.setWhen(LocalDate.parse(this.session.get(chat_id + ":date")));
                transaction.setWho(who);
                transaction.setWhom(member);
                transaction.setHow_much(sum_part);
                transaction.setFor_what(this.session.get(chat_id + ":occasion"));
                transaction.setAuthor(author);
                transactionsRepository.save(transaction);


                int id = transaction.getId();
                previous += String.valueOf(id) + " ";

                float balance = this.membersRepository.findBalanceByName(who) - sum;
                this.membersRepository.updateBalance(who, balance);

                balance = this.membersRepository.findBalanceByName(member) + sum;
                this.membersRepository.updateBalance(member, balance);
            }
            this.membersRepository.updatePrevious(chat_id, previous);
            return true;
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
    }
    private SendMessage ask_who (Update update){
        Long chat_id = update.getMessage().getChatId();
        String text = update.getMessage().getText();

        if (this.session.get(chat_id + ":awaiting_response").equals("false")) {
            this.session.set(chat_id + ":awaiting_response", "true");
            return this.respond(chat_id, this.text_who, this.create_who_markup(
                    false,
                    false,
                    false,
                    null));
        }

        if (Arrays.stream(this.members).toList().contains(text)) {
            this.session.set(chat_id + ":payer", text);
            this.session.lpush(chat_id + ":selected_members", text);
            this.session.incr(chat_id.toString() + ":state_finances_update");
            this.session.set(chat_id + ":awaiting_response", "false");
            return this.consume(update);
        }

        return this.respond(chat_id, this.text_unknown_member, null);
    }

    private SendMessage ask_whom(Update update) {
        Long chat_id = update.getMessage().getChatId();
        String text = update.getMessage().getText();
        ArrayList<String> selected_members = null;
        if(this.session.exists(chat_id + ":selected_memers") == 1){
            selected_members = (ArrayList<String>) this.session.lrange(chat_id + ":selected_members", 0, -1);
        }

        if (this.session.get(chat_id + ":awaiting_response").equals("false")){
            this.session.set(chat_id + ":awaiting_response", "true");
            return this.respond(chat_id, this.text_whom, create_who_markup(false,
                    true,
                    false,
                    selected_members));
        }

        if (this.session.get(chat_id + ":multiple_members").equals("true")) {
            if (text.equalsIgnoreCase("Weiter >")) {
                for( String member : selected_members){
                    this.session.lpush(chat_id + ":receivers", member);
                }
                this.session.set(chat_id + ":multiple_members", "false");
                this.session.incr(chat_id.toString() + ":state_finances_update");
                this.session.set(chat_id + ":awaiting_response", "false");
                return this.respond(chat_id,
                        this.text_how_much,
                        this.create_end_markup());
            } else if (Arrays.stream(this.members).toList().contains(text)) {
                this.session.lpush(chat_id + ":selected_members", text);
                selected_members.add(text);
                return this.respond(chat_id,
                        this.text_next_member,
                        this.create_who_markup(false, false, true, selected_members));
            } else {
                return this.respond(chat_id,
                        this.text_false_input,
                        this.create_who_markup(true,
                                false,
                                true,
                                selected_members));
            }
        }

        if (Arrays.stream(this.members).toList().contains(text)) {
            this.session.incr(chat_id.toString() + ":state_finances_update");
            this.session.set(chat_id + ":awaiting_response", "false");
            this.session.lpush(chat_id + ":receivers", text);
            return this.consume(update);
        } else if (text.equalsIgnoreCase("Mehrere")) {
            this.session.set(chat_id + ":multiple_members", "true");
            return this.respond(chat_id,
                    this.text_multiple_members,
                    this.create_who_markup(false, false, true, selected_members));
        } else {
            return this.respond(chat_id, this.text_unknown_member, null);
        }
    }

    private SendMessage ask_how_much(Update update) {
        Long chat_id = update.getMessage().getChatId();
        String text = update.getMessage().getText();
        Float sum;

        if(this.session.get(chat_id + ":awaiting_response").equals("false")){
            this.session.set(chat_id + ":awaiting_response", "true");
            return this.respond(chat_id, this.text_how_much, this.create_end_markup());
        }

        try {
            sum = Float.parseFloat(text);
        } catch (NumberFormatException e) {
            return this.respond(chat_id, this.text_error_sum_format, null);
        }

        if (sum < 0) {
            return this.respond(chat_id, this.text_error_sum_negative, null);
        }

        this.session.set(chat_id + ":sum", text);
        this.session.incr(chat_id + ":state_finances_update");
        this.session.set(chat_id + ":awaiting_response", "false");
        return this.consume(update);
    }

    private SendMessage ask_for_what(Update update) {
        Long chat_id = update.getMessage().getChatId();
        String text = update.getMessage().getText();

        if(this.session.get(chat_id + ":awaiting_response").equals("false")){
            this.session.set(chat_id + ":awaiting_response", "true");
            return this.respond(chat_id, this.text_for_what, this.create_end_markup());
        }

        this.session.set(chat_id + ":occasion", text.substring(0, 1).toUpperCase() + text.substring(1));
        this.is_engaged = false;
        if (this.db_save(chat_id)) {
            this.session.incr(chat_id + ":state_finances_update");
            this.session.set(chat_id + ":awaiting_response", "false");
            return this.consume(update);
        } else {
            return this.respond(chat_id, this.text_error_db, null);
        }
    }

    private SendMessage summary(Long chat_id) {
        String response = this.text_summary;
        for (String member : this.session.lrange(chat_id + ":receivers", 0, -1)) {
            response += this.short_format_simple_date(false,
                    LocalDate.parse(this.session.get(chat_id + ":date")),
                    this.session.get(chat_id + ":payer"),
                    member,
                    Float.parseFloat(this.session.get(chat_id + ":sum")),
                    this.session.get(chat_id + ":occasion")) + "\n";
        }
        return respond(chat_id, response, null);
    }

    // Undo Functions

    private SendMessage undo_date(Update update){
        Long chat_id = update.getMessage().getChatId();
        this.session.set(chat_id + ":date", "");
        this.session.set(chat_id + ":custom_date", "false");
        return consume(update);
    }

    private SendMessage undo_who(Update update){
        Long chat_id = update.getMessage().getChatId();
        this.session.set(chat_id + ":payer", "");
        this.session.del(chat_id + ":selected_members");
        return consume(update);
    }

    private SendMessage undo_whom(Update update){
        Long chat_id = update.getMessage().getChatId();
        this.session.del(chat_id + ":receivers");
        this.session.set(chat_id + ":multiple_members", "false");
        return consume(update);
    }

    private SendMessage undo_how_much(Update update){
        Long chat_id = update.getMessage().getChatId();
        this.session.set(chat_id + ":sum", "");
        return consume(update);
    }

    private SendMessage undo_for_what(Update update){
        Long chat_id = update.getMessage().getChatId();
        this.session.set(chat_id + ":occasion", "");
        return consume(update);
    }
    // Main Function

    @Override
    public SendMessage consume(Update update) {
        Message message = update.getMessage();
        String text = message.getText();
        Long chat_id = message.getChatId();

        if (text.equalsIgnoreCase("Beenden") || text.equalsIgnoreCase("End")) {
            this.end(chat_id);
            return respond(chat_id, this.text_exit, null);
        }

        if (text.equalsIgnoreCase("< Zurück")){
            this.session.set(chat_id + ":back", "true");
        }


        if (this.get_state(chat_id).equals("update")) {
            String state = this.states[Integer.parseInt(this.session.get(chat_id + ":state_finances_update"))];

            if (!this.session.get(chat_id + ":back").equals("true")){

                if (state.equalsIgnoreCase("date")) {
                    return this.ask_date(update);
                }

                if (state.equalsIgnoreCase("payer")) {
                    return this.ask_who(update);
                }

                if (state.equalsIgnoreCase("receivers")) {
                    return this.ask_whom(update);
                }

                if (state.equalsIgnoreCase("sum")) {
                    return this.ask_how_much(update);
                }

                if (state.equalsIgnoreCase("occasion")) {
                    return this.ask_for_what(update);
                }

                if (state.equalsIgnoreCase("summary")){
                    return this.summary(update.getMessage().getChatId());
                }
            } else {
                session.incrby(chat_id + ":state_finances_update", -1);
                session.set(chat_id + ":awaiting_response", "false");
                session.set(chat_id + ":back", "false");
                state = this.states[Integer.parseInt(this.session.get(chat_id + ":state_finances_update"))];
                message.setText("");
                update.setMessage(message);

                if (state.equalsIgnoreCase("date")) {
                    return this.undo_date(update);
                }

                if (state.equalsIgnoreCase("payer")) {
                    return this.undo_who(update);
                }

                if (state.equalsIgnoreCase("receivers")) {
                    return this.undo_whom(update);
                }

                if (state.equalsIgnoreCase("sum")) {
                    return this.undo_how_much(update);
                }

                if (state.equalsIgnoreCase("occasion")) {
                    return this.undo_for_what(update);
                }
            }

        }

        if (this.get_state(chat_id).equals("history")) {
            this.is_engaged = false;
            this.session.hset(chat_id.toString(), "who", update.getMessage().getText());
            String responce = "";
            Iterable<Transaction> transactions;
            String who = this.session.hget(chat_id.toString(), "who");
            if (who.equalsIgnoreCase("Alle")) {
                transactions = transactionsRepository.findAllOrderByWhenAsc();
            } else if (Arrays.stream(this.members).toList().contains(who)) {
                transactions = transactionsRepository.findHistory(who);
            } else {
                return respond(chat_id, this.text_false_input, null);
            }

            LocalDate previous_date = null;
            for (Transaction transaction : transactions) {
                if (!transaction.getWhen().equals(previous_date)) {
                    responce += "\n";
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
            return respond(chat_id, responce, null);
        }

        if (this.get_state(chat_id).equals("cancel")) {
            if (update.getMessage().getText().equalsIgnoreCase("ja")) {
                if (this.db_restore_prev_balance(chat_id)) {
                    this.set_state("none", chat_id);
                    return respond(chat_id, this.text_cancel_yes, null);
                } else {
                    return respond(chat_id, this.text_error_db, null);
                }
            } else if (update.getMessage().getText().equalsIgnoreCase("nein")) {
                this.set_state("none", chat_id);
                return respond(chat_id, this.text_cancel_no, null);
            } else {
                return respond(chat_id, this.text_false_input, null);
            }
        }

        this.end(chat_id);
        return respond(chat_id, this.text_error, null);
    }

    @Override
    public void end(Long chat_id) {
        super.end(chat_id);
        this.is_engaged = false;
        this.sessionRepository.create_session(chat_id);
    }

    public SendMessage update(Update update) {
        this.is_engaged = true;
        this.set_state("update", update.getMessage().getChatId());
        return this.consume(update);
    }

    public SendMessage check(Update update) {
        this.set_state("check", update.getMessage().getChatId());
        String text = this.text_finances_check;
        for (String member : this.members) {
            String balance = String.valueOf(this.membersRepository.findBalanceByName(member));
            text += String.format("\n %s : %s", member, balance);
        }
        return respond(update.getMessage().getChatId(), text, null);
    }

    public SendMessage history(Update update) {
        this.is_engaged = true;
        this.set_state("history", update.getMessage().getChatId());
        return respond(update.getMessage().getChatId(),
                this.text_history,
                this.create_who_markup(true, false, false, null));
    }

    public SendMessage cancel_last(Update update) {
        Long chat_id = update.getMessage().getChatId();
        String transactions_preview = "";
        this.is_engaged = true;

        String[] prev_transactions;
        String prev_temp = this.membersRepository.findPreviousByChatId(chat_id);
        if (prev_temp != null){
            prev_transactions = prev_temp.split(" ");
        } else {
            return respond(chat_id, this.text_no_previous_transactions, null);
        }

        for(String id : prev_transactions){
            Transaction prev_transaction = this.transactionsRepository.findById(Integer.valueOf(id)).get();
            transactions_preview += this.short_format_simple_date(false,
                    prev_transaction.getWhen(),
                    prev_transaction.getWhom(),
                    prev_transaction.getWho(),
                    prev_transaction.getHow_much(),
                    prev_transaction.getFor_what()) + "\n";

            this.session.lpush(chat_id + ":previous_transactions", id);
        }

        this.set_state("cancel", chat_id);
        return respond(chat_id,
                String.format(this.text_cancel, transactions_preview),
                this.create_yes_no_markup(false));
    }
}
