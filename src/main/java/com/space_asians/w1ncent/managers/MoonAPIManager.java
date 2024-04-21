package com.space_asians.w1ncent.managers;


import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.IOException;

@Service
public class MoonAPIManager extends W1NC3NTManager{
    private OkHttpClient httpClient = new OkHttpClient();

    private Response response;
    private String body;

    @Value("${text.moon_api.failure}")
    private String text_failure;

    public SendMessage consume(Update update){
        Request request = new Request.Builder()
                .url("https://moon-phase.p.rapidapi.com/basic")
                .addHeader("X-RapidAPI-Key", "7312b6599amsh96040c69a1f02e1p1de824jsndc8c781ac7bd")
                .addHeader("X-RapidAPI-Host", "moon-phase.p.rapidapi.com")
                .build();

        try {
           this.response = httpClient.newCall(request).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            this.body = this.response.body().string();
        } catch (IOException e) {
            this.body = this.text_failure;
        }

        return SendMessage
                .builder()
                .chatId(update.getMessage().getChatId())
                .text(String.valueOf(this.body))
                .build();
    }
}
