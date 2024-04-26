package com.space_asians.w1ncent;

import com.space_asians.w1ncent.managers.FinanceManager;
import com.space_asians.w1ncent.managers.MainManager;
import com.space_asians.w1ncent.managers.MoonAPIManager;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
public class W1nc3ntBot implements LongPollingSingleThreadUpdateConsumer {

    private String token = null;


    @Autowired
    protected FinanceManager financeManager;
    @Autowired
    protected MoonAPIManager moonAPIManager;
    @Autowired
    protected MainManager mainManager;


    @Override
    public void consume(Update update) {

    }
}
