package com.teamshort.viberbot.service;

import com.viber.bot.Request;
import com.viber.bot.Response;
import com.viber.bot.ViberSignatureValidator;
import com.viber.bot.api.ViberBot;
import com.viber.bot.event.callback.OnConversationStarted;
import com.viber.bot.event.callback.OnMessageReceived;
import com.viber.bot.event.callback.OnSubscribe;
import com.viber.bot.event.callback.OnUnsubscribe;
import com.viber.bot.event.incoming.IncomingConversationStartedEvent;
import com.viber.bot.event.incoming.IncomingEvent;
import com.viber.bot.event.incoming.IncomingMessageEvent;
import com.viber.bot.event.incoming.IncomingSubscribedEvent;
import com.viber.bot.event.incoming.IncomingUnsubscribeEvent;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

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
import com.google.common.util.concurrent.ListenableFuture;
import com.teamshort.viberbot.database.entity.Reservation;
import com.teamshort.viberbot.database.entity.Room;
import com.teamshort.viberbot.database.entity.User;
import com.viber.bot.message.Message;
import com.viber.bot.message.MessageKeyboard;
import com.teamshort.viberbot.service.reservation.ReservationService;
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
    

    @Autowired
    private ReservationService reservationService;
    
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
				else if(message.getTrackingData().get("welcome").equals("roomsObj")){
					
					 if(message.getMapRepresentation().get("text").equals("Cancel")){
						 System.out.println("In Cancel");   
						 response.send(welcomeScreen(event.getSender().getName()));
					
						 
		                    }
					 else {


		                System.out.println("In choosing date");

		                String roomIdString = (String) message.getMapRepresentation().get("text"); //which room
		                	
		                System.out.println("Room is" + roomIdString);
	

		                Map<String, Object> dateTrackingData = new HashMap<>();
		                dateTrackingData.put("welcome", "dateObj");
		                dateTrackingData.put("RoomID", roomIdString);

		                TrackingData dateTr = new TrackingData(dateTrackingData);
		               

		                
		                response.send(new TextMessage("Please choose date (yyyy-mm-dd):", null, dateTr, new Integer(1)));
					}
					
				}
				
				//user enters the date
				else if(message.getTrackingData().get("welcome").equals("dateObj")) {
					
					String dateStr = (String) message.getMapRepresentation().get("text");					
					
				
					String roomId = (String) message.getTrackingData().get("RoomID");
		
					System.out.println("RoomID in DATE is: " + roomId);
		
					
					Map<String, Object> timeTrackingData = new HashMap<>();
		            timeTrackingData.put("welcome", "timeObj");
		            timeTrackingData.put("RoomID", roomId);
		            timeTrackingData.put("Date", dateStr);
		            
					System.out.println("Time tracking Data HASHMAP created");


		            TrackingData timeTr = new TrackingData(timeTrackingData);
		                
		            System.out.println("RoomID in TIMEE before keyboard:" + timeTr.get(roomId));
		            
		            MessageKeyboard timeKeyboard = createTimeKeyboard(roomService.getRoomById(roomId));
				    	
				    	
		            System.out.println("Time keyboard created");

		                
		            response.send(new TextMessage("Please choose time:", timeKeyboard, timeTr, new Integer(1)));
					
				}
				
				
				//user enters the time
				else if(message.getTrackingData().get("welcome").equals("timeObj")) {
					
					 if(message.getMapRepresentation().get("text").equals("Cancel")){
						 System.out.println("In Cancel");   
						 response.send(welcomeScreen(event.getSender().getName()));
					
						 
		                    }
					 else {
						 
						 String timeSlotString = (String) message.getMapRepresentation().get("text");
						 String roomIdString = (String) message.getTrackingData().get("RoomID");
						 //String userViberId = event.getSender().getId();
						 String date = (String) message.getTrackingData().get("Date");
						 
						 Room room = roomService.getRoomById(roomIdString);
						 
						 //System.out.println("time: " + timeSlotString + "date: " + date + "roomId: " + roomIdString + "user: " + userViberId);
						 
						 String reservationDetails = "Would you like to confirm your reservation?"
						 		+ "\nROOM: " + room.getName() + " " + room.getNumber()
						 		+ "\nDATE: " + date
						 		+ "\nTIME: " + timeSlotString;
						 
						 //reservationService.reserve(new Reservation(userService.getByViberId(userViberId),roomService.getRoomById(roomIdString), date, timeSlotString));
						 
						  Map<String, Object> confirmTrackingData = new HashMap<>();
						  confirmTrackingData.put("welcome", "confObj");
						  confirmTrackingData.put("RoomID", roomIdString);
						  confirmTrackingData.put("Date", date);
						  confirmTrackingData.put("Time", timeSlotString);

			              TrackingData confirmTr = new TrackingData(confirmTrackingData);
						 
						 MessageKeyboard confirmKeyboard = confirmReservationKeyboard();
			              
						 response.send(new TextMessage(reservationDetails,confirmKeyboard, confirmTr,new Integer(1)));
					 }
					 
					 
			}
				//user confirms or cancels the reservation
				else if(message.getTrackingData().get("welcome").equals("confObj")) {
					if(message.getMapRepresentation().get("text").equals("Cancel")){
						 System.out.println("In Cancel");   
						 response.send(welcomeScreen(event.getSender().getName()));
					
						 
		                    }
					 else {
						 String timeSlotString = (String) message.getTrackingData().get("Time");
						 String roomIdString = (String) message.getTrackingData().get("RoomID");
						 String userViberId = event.getSender().getId();
						 String date = (String) message.getTrackingData().get("Date");
						 
						 
			
						 System.out.println("IN CONFIRM RESERVATION \ntime: " + timeSlotString + "date: " + date + "roomId: " + roomIdString + "user: " + userViberId);
						 
						 
						 reservationService.reserve(new Reservation(userService.getByViberId(userViberId),roomService.getRoomById(roomIdString), date, timeSlotString));
						 
						 
						 //not good practice
						 response.send(new TextMessage("Your reservation was successfully created!"));
						 
						 response.send(welcomeScreen(event.getSender().getName()));
					 }
				}
			}
			
		});
	
	}

    private MessageKeyboard confirmReservationKeyboard() {

    	ArrayList<Map> buttonsList  = new ArrayList<>();
    	
    	Map<String, Object> confirmButton = new HashMap<>();
    	confirmButton.put("Columns", "2");
    	confirmButton.put("Rows", "1");
    	confirmButton.put("BgColor", "#fee398");
    	confirmButton.put("Text", "Confirm");
    	confirmButton.put("TextVAlign", "middle");
		confirmButton.put("TextHAlign", "center");
		confirmButton.put("TextOpacity", "60");
		confirmButton.put("TextSize", "regular");
		confirmButton.put("ActionType", "reply");
		confirmButton.put("ActionBody", "Confirm");
		confirmButton.put("TextSize", "regular");
    	
		Map<String, Object> cancelButton = new HashMap<>();
		confirmButton.put("Columns", "2");
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
    	
    	buttonsList.add(confirmButton);
    	buttonsList.add(cancelButton);
    	
    	Map<String, Object> keyboard  = new HashMap<>();
    	keyboard.put("Buttons", buttonsList);
    	keyboard.put("DefaultHeight", true);
    	keyboard.put("Type", "keyboard");
    	
    	
    	return new MessageKeyboard(keyboard);
    	
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
    
    private MessageKeyboard createTimeKeyboard(Room room) {
    	
    	System.out.println("IN createTimeKeyboard");
    
    	ArrayList<Map> buttonsList  = new ArrayList<>();
    	LocalTime time = room.getStartWorkTime();
    	LocalTime endTime = room.getEndWorkTime();
    	
    	System.out.println("time is: " + time + " endTime is: " + endTime);
    	
    	while (time.isBefore(endTime)) {
    		
    		System.out.println("time is: " + time + " endTime is: " + endTime);
    		
    		Map<String, Object> timeButton = new HashMap<>();
    		timeButton.put("Rows", "1");
    		timeButton.put("BgColor", "#fee398");
    		timeButton.put("Text", time.toString());
    		timeButton.put("TextVAlign", "middle");
    		timeButton.put("TextHAlign", "center");
    		timeButton.put("TextOpacity", "60");
    		timeButton.put("TextSize", "regular");
    		timeButton.put("ActionType", "reply");
    		timeButton.put("ActionBody", time.toString());
    		timeButton.put("TextSize", "regular");
        	
        	buttonsList.add(timeButton);
        	
        	time = time.plusHours(1);
        	
    		
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
	
	private MessageKeyboard createWelcomeKeyboard(){
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
    	
    	Map<String, Object> keyboard  = new HashMap<>();
    	keyboard.put("Buttons", button12);
    	keyboard.put("DefaultHeight", true);
    	keyboard.put("Type", "keyboard");
    	
    	return new MessageKeyboard(keyboard);
	}
	
	@Override
	public void onConversationStarted(ViberBot bot) {

		bot.onConversationStarted((new OnConversationStarted() {
			@Override
			public Future<Optional<Message>> conversationStarted(IncomingConversationStartedEvent event) {

				return Futures.immediateFuture(Optional.of(welcomeScreen(event.getUser().getName())));
			}

		}));

	}
    
    
    private TextMessage welcomeScreen(String userName){
    
    	MessageKeyboard welcomeKeyboard=createWelcomeKeyboard();
    	
    	Map<String, Object> welcomeTrackingData = new HashMap<>();
    	welcomeTrackingData.put("welcome", "welcomeObj");
    	TrackingData trackingData = new TrackingData(welcomeTrackingData);
    

    	System.out.println("In WELCOME SCREEN");
		System.out.println("TrackingData:");
			System.out.println(welcomeTrackingData.toString());

		
			return new TextMessage(
					"Hello, " + userName + "! Welcome to ViberBot Room Reservation. Please choose one of the options below:",
					new MessageKeyboard(welcomeKeyboard), trackingData, new Integer(1));	
		
		

    }

}




