package com.teamshort.viberbot.service;

import com.viber.bot.Request;
import com.viber.bot.Response;
import com.viber.bot.ViberSignatureValidator;
import com.viber.bot.api.ViberBot;
import com.viber.bot.event.callback.OnSubscribe;
import com.viber.bot.event.callback.OnUnsubscribe;
import com.viber.bot.event.incoming.IncomingSubscribedEvent;
import com.viber.bot.event.incoming.IncomingUnsubscribeEvent;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.io.CharStreams;
import com.google.common.util.concurrent.Futures;
import com.teamshort.viberbot.database.entity.User;
import com.teamshort.viberbot.service.user.UserService;
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
		bot.onUnsubscribe((new OnUnsubscribe() {

			@Override
			public void unsubscribe(IncomingUnsubscribeEvent event) {

				String userViberId = event.getUserId();
				userService.unsubscribe(userViberId);
				
			}
			
		}));
		
		
		
	}

    @Override
	public void onConversationStarted(ViberBot bot) {
		bot.onConversationStarted(event -> Futures.immediateFuture(Optional.of( // send 'Hi UserName' when conversation
																				// is started
				new TextMessage("Hello  ,  " + event.getUser().getName() + "! Welcome to ViberBot Room Reservation" ) )));
	}
}






