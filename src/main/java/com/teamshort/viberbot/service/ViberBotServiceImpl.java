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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.io.CharStreams;
import com.google.common.util.concurrent.Futures;
import com.teamshort.viberbot.database.entity.User;
import com.viber.bot.message.MessageKeyboard;
//import com.teamshort.viberbot.message.MessageKeyboard;
import com.teamshort.viberbot.service.user.UserService;
import com.viber.bot.message.TextMessage;
import com.viber.bot.profile.UserProfile;

import aj.org.objectweb.asm.TypeReference;

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
	
	
	/*String keyboardMessageStr = {
	"keyboard": {
	"DefaultHeight": true,
	"BgColor": "#FFFFFF",
	"Buttons": [{
	"Columns": 6,
	"Rows": 1,
	"BgColor": "#2db9b9",
	"BgMediaType": "gif",
	"BgMedia": "http://www.url.by/test.gif",
	"BgLoop": true,
	"ActionType": "open-url",
	"ActionBody": "www.tut.by",
	"Image": "www.tut.by/img.jpg",
	"Text": "Key text",
	"TextVAlign": "middle",
	"TextHAlign": "center",
	"TextOpacity": 60,
	"TextSize": "regular"
	}]
	}
	}*/
	

    @Override
	public void onConversationStarted(ViberBot bot) {

    	
		//bot.onConversationStarted(event -> Futures.immediateFuture(Optional.of
    	//(/*new TextMessage("Hello  ,  " + event.getUser().getName() + 
    	//"! Welcome to ViberBot Room Reservation" )*/)));
    	
    	
    	Map<String, Object> button1  = new HashMap<>();
    	//button1.put("Type", "keyboard");
    	button1.put("Columns", "2");
    	button1.put("Rows", "1");
    	button1.put("BgColor", "#2db9b9");
    	button1.put("BgMediaType", "picture");
    	button1.put("BgMedia", "http://www.url.by/test.gif");
    	button1.put("BgLoop", "true");
    	button1.put("ActionType", "open-url");
    	button1.put("ActionBody", "www.tut.by");
    	button1.put("Image", "www.tut.by/img.jpg");
    	button1.put("Text", "Prvi button");
    	button1.put("TextVAlign", "middle");
    	button1.put("TextHAlign", "center");
    	button1.put("TextOpacity", "60");
    	button1.put("TextSize", "regular");
    	
    	Map<String, Object> button2  = new HashMap<>();
    	//button2.put("Type", "keyboard");
    	button2.put("Columns", "2");
    	button2.put("Rows", "1");
    	button2.put("BgColor", "#2db9b9");
    	button2.put("BgMediaType", "picture");
    	button2.put("BgMedia", "http://www.url.by/test.gif");
    	button2.put("BgLoop", "true");
    	button2.put("ActionType", "open-url");
    	button2.put("ActionBody", "www.tut.by");
    	button2.put("Image", "www.tut.by/img.jpg");
    	button2.put("Text", "Prvi button");
    	button2.put("TextVAlign", "middle");
    	button2.put("TextHAlign", "center");
    	button2.put("TextOpacity", "60");
    	button2.put("TextSize", "regular");
    	
    	ArrayList<Map> button12  = new ArrayList<>();
    	button12.add(button1);
    	button12.add(button2);
    	
    	Map<String, Object> buttons  = new HashMap<>();
    	buttons.put("Buttons", button12);
    	
    	Map<String, Object> height  = new HashMap<>();
    	height.put("DefaultHeight", "regular");
    	
    	Map<String, Object> col  = new HashMap<>();
    	col.put("BgColor", "#FFFFFF");
    	
    	Map<String, Object> keyboard  = new HashMap<>();
    	keyboard.put("Buttons", button12);
    	keyboard.put("DefaultHeight", "regular");
    	keyboard.put("BgColor", "#FFFFFF");
    	
    	
    	bot.onConversationStarted(event -> Futures.immediateFuture(Optional.of
    	(new TextMessage("Hello  ,  " 
    	+ event.getUser().getName()
    	+ "! Welcome to ViberBot Room Reservation" 
    	, new MessageKeyboard(keyboard), null, new Integer(1)
    	
    	
    	
    	)))); 				
				
		
			
    

		bot.onConversationStarted(event -> Futures.immediateFuture(Optional.of( // send 'Hi UserName' when conversation
																				// is started
				new TextMessage("Hello  ,  " + event.getUser().getName() + "! Welcome to ViberBot Room Reservation" ) )));
	}


}




