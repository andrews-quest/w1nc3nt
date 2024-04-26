package com.space_asians.w1ncent.managers;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import java.io.IOException;
import org.json.JSONObject;

@Service
public class MoonAPIManager extends W1nc3ntManager {
    private OkHttpClient httpClient = new OkHttpClient();

    private Response response;
    private SendMessage sm;

    @Value("${moon_api.x-rapidapi-key}")
    private String rapidAPIKey;

    @Value("${moon_api.x-rapidapi-host}")
    private String rapidAPIHost;

    @Value("${text.moon_api.failure}")
    private String text_failure;

     @Value("${text.moon_api.basic_moonapi_responce}")
     private String text_basic_responce;

    public String decipherMoonAPIBasic(String body){
        JSONObject body_json = new JSONObject(body);
        String phase_name = body_json.getString("phase_name");
        String stage = body_json.getString("stage");
        String days_until_next_full_moon = String.valueOf(body_json.getInt("days_until_next_full_moon"));
        String days_until_next_new_moon = String.valueOf(body_json.getInt("days_until_next_new_moon"));
        return String.format(this.text_basic_responce, phase_name, stage, days_until_next_full_moon, days_until_next_new_moon);
    }

    public SendMessage consume(Update update){
        Request request = new Request.Builder()
                .url("https://moon-phase.p.rapidapi.com/basic")
                .addHeader("X-RapidAPI-Key", rapidAPIKey)
                .addHeader("X-RapidAPI-Host", rapidAPIHost)
                .build();

        try {
           this.response = httpClient.newCall(request).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            String body = this.response.body().string();
            this.sm = this.respond(update.getMessage().getChatId(), this.decipherMoonAPIBasic(body), null);
        } catch (IOException e) {
            this.sm = this.respond(update.getMessage().getChatId(), this.text_failure, null);
        }

        return this.sm;
    }
}
