package com.space_asians.w1nsent;

import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;



public class W1nsentApplication {

	public static void main(String[] args) {
		// SpringApplication.run(W1nsentApplication.class, args);



		String token = "";
		try(TelegramBotsLongPollingApplication botsApplication = new TelegramBotsLongPollingApplication()) {
			botsApplication.registerBot(token, new W1NS3NT_BOT(token));
			System.out.println("Success");
			Thread.currentThread().join();
		}catch (Exception e){
			e.printStackTrace();
		}
		System.out.println("Failure");
	}

}
