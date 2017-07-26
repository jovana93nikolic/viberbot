package com.teamshort.viberbot.service;

import com.viber.bot.api.ViberBot;

/*ViberBotService.java should be interface inside service package.
ViberBotServiceImpl.java is ViberBotService implementation class (inside service package).
Service has 4 functions:

onConversationStarted
onSubscribe
onUnsubscribe
onMessageReceived
These functions should be used for Viber bot events manipulations*/



public interface ViberBotService {

	void onMessageReceived(ViberBot bot);

	void subscribe(ViberBot bot);

	void unsubscribe(ViberBot bot);

	void onConversationStarted(ViberBot bot);

	
}