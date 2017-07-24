package com.teamshort.viberbot.service;

import com.viber.bot.Response;
import com.viber.bot.api.ViberBot;
import com.viber.bot.event.callback.OnSubscribe;
import com.viber.bot.event.incoming.IncomingSubscribedEvent;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.util.concurrent.Futures;
import com.teamshort.viberbot.database.entity.User;
import com.viber.bot.message.TextMessage;
import com.viber.bot.profile.UserProfile;

@Service
public class ViberBotServiceImpl implements ViberBotService {

	private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
    
    @Override
	public void onMessageReceived(ViberBot bot) {

		bot.onMessageReceived((event, message, response) -> response.send("Welcome to our public account!"));
	}
    
	@Override
	public void subscribe(ViberBot bot) {

		bot.onSubscribe((new OnSubscribe() {
			@Override
			public void subscribe(IncomingSubscribedEvent event, Response response) {
				UserProfile userPr = event.getUser();

				if (userService.getByViberId(userPr.getId()) == null ) {    
					userService.add(new User(userPr.getId(),userPr.getName(),true));
					response.send("Your name has been remembered.");
				}
				
				
				else { 
					
					userService.subscribe(userPr.getId());
					response.send("You have already been here.");
				}
			}
			
		}));

	}

	@Override
	public void unsubscribe(ViberBot bot) {
		// TODO Auto-generated method stub
		//userService.unsubscribe(viberId);
	}

    @Override
	public void onConversationStarted(ViberBot bot) {
		bot.onConversationStarted(event -> Futures.immediateFuture(Optional.of( // send 'Hi UserName' when conversation
																				// is started
				new TextMessage("Hello  ,  " + event.getUser().getName() + "! Welcome to ViberBot Room Reservation" ) )));
	}

}
