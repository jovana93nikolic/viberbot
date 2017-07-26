package com.teamshort.viberbot.service;

import com.viber.bot.Request;
import com.viber.bot.Response;
import com.viber.bot.ViberSignatureValidator;
import com.viber.bot.api.ViberBot;
import com.viber.bot.event.callback.OnMessageReceived;
import com.viber.bot.event.callback.OnSubscribe;
import com.viber.bot.event.callback.OnUnsubscribe;
import com.viber.bot.event.incoming.IncomingMessageEvent;
import com.viber.bot.event.incoming.IncomingSubscribedEvent;
import com.viber.bot.event.incoming.IncomingUnsubscribeEvent;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import javax.annotation.Nonnull;
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
import com.teamshort.viberbot.database.entity.Room;
import com.teamshort.viberbot.database.entity.User;
import com.viber.bot.message.Message;
import com.viber.bot.message.MessageKeyboard;
import com.teamshort.viberbot.service.room.RoomService;
//import com.teamshort.viberbot.message.MessageKeyboard;
import com.teamshort.viberbot.service.user.UserService;
import com.viber.bot.message.TextMessage;
import com.viber.bot.message.TrackingData;
import com.viber.bot.profile.UserProfile;

import aj.org.objectweb.asm.TypeReference;

@Service
public class ViberBotServiceImpl implements ViberBotService {

    @Autowired
	private UserService userService;

    @Autowired
    private RoomService roomService;
    
    @Override
	public void onMessageReceived(ViberBot bot) {

		
		bot.onMessageReceived(new OnMessageReceived(){
			
			@Override
			public void messageReceived(IncomingMessageEvent event, Message message, Response response) {
				
				
				System.out.println(message.getMapRepresentation().get("text"));
				
				//reserve or show reservations 
				if(message.getTrackingData().get("welcome").equals("welcomeObj")){
					System.out.println("In Welcome");
					
					
					//user wants to reserve a room
					if(message.getMapRepresentation().get("text").equals("Reserve room")){
						System.out.println("In Reserve room");		

				    	Map<String, Object> roomsTrackingData = new HashMap<>();
				    	roomsTrackingData.put("welcome", "roomsObj");
				    	TrackingData roomsTr = new TrackingData(roomsTrackingData);
				    	
				    	MessageKeyboard roomsKeyboard = createRoomKeyboard();
				    	
				    	
				    	response.send(new TextMessage("Please choose one of the available rooms:", roomsKeyboard
								, roomsTr, new Integer(1)));
				    	
					}
					
					
					
					
					
					//user wants to previous reservations
					else if(message.getMapRepresentation().get("text").equals("See previous reservations")){
						System.out.println("In Previous reservations");
						response.send("You want to see previous reservations!");}	
					
				}
				
				
				//from get available rooms to enter date
				if(message.getTrackingData().get("welcome").equals("roomsObj")){
					
					 if(message.getMapRepresentation().get("text").equals("Cancel")){
						 System.out.println("In Cancel");   
						 onConversationStarted(bot);
		                    }


		                System.out.println("In choosing date");

		                Long roomId = Long.parseLong((String)message.getMapRepresentation().get("text")); //which room

		                roomService.getRoomById(roomId);

		                response.send("You want to reserve room" + roomService.getRoomById(roomId).getName());

		                Map<String, Object> dateTrackingData = new HashMap<>();
		                dateTrackingData.put("welcome", "dateObj");
		                TrackingData dateTr = new TrackingData(dateTrackingData);

		                dateTr.put("RoomID", roomId);

		                response.send(new TextMessage("Please enter the date:", null, dateTr, new Integer(1)));
					
					
				}
				
				
			}
			
			
		});
		
		
		
	}
    
    
    private MessageKeyboard createRoomKeyboard() {
    	
    	List<Room> rooms = (List<Room>) roomService.listAllRooms();
    	
    	
    	ArrayList<Map> buttonsList  = new ArrayList<>();
    	
    	
    	for (Room room: rooms){
    		
    		Map<String, Object> roomButton = new HashMap<>();
    		roomButton.put("Rows", "1");
    		roomButton.put("BgColor", "#fee398");
    		roomButton.put("Text", room.getName() + " " + room.getNumber());
    		roomButton.put("TextVAlign", "middle");
    		roomButton.put("TextHAlign", "center");
        	roomButton.put("TextOpacity", "60");
        	roomButton.put("TextSize", "regular");
        	roomButton.put("ActionType", "reply");
        	roomButton.put("ActionBody", room.getId());
        	roomButton.put("TextSize", "regular");
        	
        	buttonsList.add(roomButton);
        	
    		
    	}
    	
    	Map<String, Object> cancelButton = new HashMap<>();
    	cancelButton.put("Rows", "1");
    	cancelButton.put("BgColor", "#CD3E2D");
    	cancelButton.put("Text", "Cancel");
    	cancelButton.put("TextVAlign", "middle");
    	cancelButton.put("TextHAlign", "center");
    	cancelButton.put("TextOpacity", "60");
    	cancelButton.put("TextSize", "regular");
    	cancelButton.put("ActionType", "reply");
    	cancelButton.put("ActionBody", "Cancel");
    	cancelButton.put("TextSize", "regular");
    	
    	buttonsList.add(cancelButton);
    	
    	
    	
    	
    	
    	
    	
    	Map<String, Object> keyboard  = new HashMap<>();
    	keyboard.put("Buttons", buttonsList);
    	keyboard.put("DefaultHeight", true);
    	keyboard.put("Type", "keyboard");
    	
    	
    	return new MessageKeyboard(keyboard);
    	
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

    	
		//bot.onConversationStarted(event -> Futures.immediateFuture(Optional.of
    	//(/*new TextMessage("Hello  ,  " + event.getUser().getName() + 
    	//"! Welcome to ViberBot Room Reservation" )*/)));
    	
    	
    	Map<String, Object> button1  = new HashMap<>();
    	button1.put("Columns", "3");
    	button1.put("Rows", "2");
    	button1.put("BgColor", "#fee398");
    	button1.put("Text", "Reserve room");
    	button1.put("TextVAlign", "middle");
    	button1.put("TextHAlign", "center");
    	button1.put("TextOpacity", "60");
    	button1.put("TextSize", "regular");
    	button1.put("ActionType", "reply");
    	button1.put("ActionBody", "Reserve room");
    	button1.put("TextSize", "regular");
    	
    	
    	
    	Map<String, Object> button2  = new HashMap<>();
    	button2.put("Columns", "3");
    	button2.put("Rows", "2");
    	button2.put("BgColor", "#c6e2ff");
    	button2.put("Text", "See previous reservations");
    	button2.put("TextVAlign", "middle");
    	button2.put("TextHAlign", "center");
    	button2.put("TextOpacity", "60");
    	button2.put("TextSize", "regular");
    	button2.put("ActionType", "reply");
    	button2.put("ActionBody", "See previous reservations");
    	button2.put("TextSize", "regular");
    	
    	
    	
    	
    	ArrayList<Map> button12  = new ArrayList<>();
    	button12.add(button1);
    	button12.add(button2);
    	
    	Map<String, Object> buttons  = new HashMap<>();
    	buttons.put("Buttons", button12);
    	
    //	Map<String, Object> height  = new HashMap<>();
    	//height.put("DefaultHeight", "regular");
    	
    	//Map<String, Object> col  = new HashMap<>();
    	//col.put("BgColor", "#FFFFFF");
    	
    	Map<String, Object> keyboard  = new HashMap<>();
    	keyboard.put("Buttons", button12);
    	keyboard.put("DefaultHeight", true);
//    	keyboard.put("BgColor", "#FFFFFF");
    	keyboard.put("Type", "keyboard");
    	
    	
    	Map<String, Object> welcomeTrackingData = new HashMap<>();
    	welcomeTrackingData.put("welcome", "welcomeObj");
    	TrackingData trackingData = new TrackingData(welcomeTrackingData);
    	
    	
    	

    	
		bot.onConversationStarted(event -> Futures.immediateFuture(Optional.of(new TextMessage(
				"Hello, " + event.getUser().getName() + "! Welcome to ViberBot Room Reservation. Please choose one of the options below:",
				new MessageKeyboard(keyboard), trackingData, new Integer(1)))));	
		
		

		System.out.println("TrackingData:");
			System.out.println(welcomeTrackingData.toString());
		

	}


}




