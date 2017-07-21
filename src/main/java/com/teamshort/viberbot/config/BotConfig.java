package com.teamshort.viberbot.config;

import com.viber.bot.Request;
import com.viber.bot.api.ViberBot;
import com.viber.bot.profile.BotProfile;

public class BotConfig {
	public void botExample() {
	    ViberBot bot = new ViberBot(new BotProfile("SampleBot", "http://viber.com/avatar.jpg"), "YOUR_AUTH_TOKEN_HERE");
	    bot.onMessageReceived((event, message, response) -> response.send(message));

	    // somewhere else in your web server of choice: (HEROKU!)
	    bot.incoming(Request.fromJsonString("..."));
	}
}
