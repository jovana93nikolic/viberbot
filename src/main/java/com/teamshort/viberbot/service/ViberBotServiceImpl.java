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
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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
import com.teamshort.viberbot.database.repository.ReservationRepository;
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

		bot.onMessageReceived(new OnMessageReceived() {

			@Override
			public void messageReceived(IncomingMessageEvent event, Message message, Response response) {

				System.out.println(message.getMapRepresentation().get("text"));

				if (message.getMapRepresentation().get("text").equals("Cancel")) {
					System.out.println("In Cancel");
					response.send(welcomeScreen(event.getSender().getName()));

				}

				// reserve or show reservations
				if (message.getTrackingData().get("welcome").equals("welcomeObj")) {
					System.out.println("In Welcome");

					// user wants to reserve a room
					if (message.getMapRepresentation().get("text").equals("Reserve room")) {
						System.out.println("In Reserve room");

						Map<String, Object> roomsTrackingData = new HashMap<>();
						roomsTrackingData.put("welcome", "roomsObj");
						TrackingData roomsTr = new TrackingData(roomsTrackingData);

						MessageKeyboard roomsKeyboard = createRoomKeyboard();

						response.send(new TextMessage("Please choose one of the available rooms:", roomsKeyboard,
								roomsTr, new Integer(1)));

					}

					// user wants to see previous reservations
					else if (message.getMapRepresentation().get("text").equals("See previous reservations")) {
						System.out.println("In Previous reservations");

						Map<String, Object> reservationsTrackingData = new HashMap<>();
						reservationsTrackingData.put("welcome", "reservationObj");
						TrackingData reservationsTr = new TrackingData(reservationsTrackingData);

						MessageKeyboard reservationsKeyboard = createReservationsKeyboard(event.getSender().getId());

						response.send(new TextMessage("Please choose a reservation for more details:",
								reservationsKeyboard, reservationsTr, new Integer(1)));

					}
				}

				// user chooses a reservation
				if (message.getTrackingData().get("welcome").equals("reservationObj")) {

					System.out.println("To cancel or not to cancel");

					if (message.getMapRepresentation().get("text").equals("Cancel")) {
						response.send(welcomeScreen(event.getSender().getName()));

					}

					System.out.println("We get into reservations to cancel");
					System.out.println(message);
					String resId = (String) message.getMapRepresentation().get("text");

					Map<String, Object> resDelTrackingData = new HashMap<>();
					resDelTrackingData.put("welcome", "resDelObj");
					resDelTrackingData.put("resId", resId);
					TrackingData resDelTr = new TrackingData(resDelTrackingData);

					MessageKeyboard cancelResKeyboard = cancelResKeyboard();

					System.out.println("IN USER CHOOSING A RESERVATION, resID" + resId);

					response.send(new TextMessage("Please choose one of the options below:", cancelResKeyboard,
							resDelTr, new Integer(1)));

				}

				else if (message.getTrackingData().get("welcome").equals("resDelObj")) {

					System.out.println("In RESDELOBJ text is " + message.getMapRepresentation().get("Text"));
					System.out.println("In RESDELOBJ resID is " + message.getTrackingData().get("resId"));

					if (message.getMapRepresentation().get("text").equals("CancelReservation")) {
						// reservationService.delete(Long.parseLong((String)message.getTrackingData().get("resId")));

						MessageKeyboard confCancelKeyboard = createConfirmKeyboard();

						Map<String, Object> confCancelTrackingData = new HashMap<>();
						confCancelTrackingData.put("welcome", "cancelObj");
						confCancelTrackingData.put("resID", message.getTrackingData().get("resId"));

						TrackingData confCancelTr = new TrackingData(confCancelTrackingData);

						response.send(new TextMessage("Are you sure you want to cancel your reservation?",
								confCancelKeyboard, confCancelTr, new Integer(1)));

					}

				}

				else if (message.getTrackingData().get("welcome").equals("cancelObj")) {

					reservationService.delete(Long.parseLong((String) message.getTrackingData().get("resID")));
					response.send("You have successfully canceled your reservation!");
					response.send(welcomeScreen(event.getSender().getName()));

				}

				// from get available rooms to enter date
				else if (message.getTrackingData().get("welcome").equals("roomsObj")) {

					if (message.getMapRepresentation().get("text").equals("Cancel")) {
						System.out.println("In Cancel");
						response.send(welcomeScreen(event.getSender().getName()));

					} else {

						System.out.println("In choosing date");

						String roomIdString = (String) message.getMapRepresentation().get("text"); // which
																									// room

						System.out.println("Room is" + roomIdString);

						MessageKeyboard cancelKeyboard = createCancelKeyboard();

						Map<String, Object> dateTrackingData = new HashMap<>();
						dateTrackingData.put("welcome", "dateObj");
						dateTrackingData.put("RoomID", roomIdString);

						TrackingData dateTr = new TrackingData(dateTrackingData);

						response.send(new TextMessage("Please choose date (dd.MM.yyyy):", cancelKeyboard, dateTr,
								new Integer(1)));

					}

				}

				// check for date parsing exception
				else if (message.getTrackingData().get("welcome").equals("dateObj")) {

					if (message.getMapRepresentation().get("text").equals("Cancel")) {
						System.out.println("In Cancel");
						response.send(welcomeScreen(event.getSender().getName()));

					}

					else {

						try {

							DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
							LocalDate date = LocalDate.parse((String) message.getMapRepresentation().get("text"),
									formatter);

							String roomId = (String) message.getTrackingData().get("RoomID");

							System.out.println("RoomID in DATE is: " + roomId);

							Map<String, Object> timeTrackingData = new HashMap<>();
							timeTrackingData.put("welcome", "timeObj");
							timeTrackingData.put("RoomID", roomId);
							timeTrackingData.put("Date", date.toString());

							System.out.println("Time tracking Data HASHMAP created");

							TrackingData timeTr = new TrackingData(timeTrackingData);

							MessageKeyboard timeKeyboard = createTimeKeyboard(roomService.getRoomById(roomId),
									date.toString());

							System.out.println("Time keyboard created");

							response.send(new TextMessage("Please choose time:", timeKeyboard, timeTr, new Integer(1)));

						} catch (Exception e) {

							response.send(new TextMessage(
									"Invalid format. Please enter the date again in format : dd.mm.yyyy",
									createCancelKeyboard(), message.getTrackingData(), new Integer(1)));
						}
					}
				}

				/*
				 * //user enters the date else
				 * if(message.getTrackingData().get("welcome").equals(
				 * "dateCheckedObj")) {
				 * 
				 * String dateStr = (String)
				 * message.getMapRepresentation().get("text");
				 * 
				 * //LocalDate resDate = LocalDate.parse(dateStr);
				 * 
				 * 
				 * //response.send("Your date is: " + resDate.toString());
				 * 
				 * String roomId = (String)
				 * message.getTrackingData().get("RoomID");
				 * 
				 * System.out.println("RoomID in DATE is: " + roomId);
				 * 
				 * 
				 * Map<String, Object> timeTrackingData = new HashMap<>();
				 * timeTrackingData.put("welcome", "timeObj");
				 * timeTrackingData.put("RoomID", roomId);
				 * timeTrackingData.put("Date", dateStr);
				 * 
				 * System.out.println("Time tracking Data HASHMAP created");
				 * 
				 * 
				 * TrackingData timeTr = new TrackingData(timeTrackingData);
				 * 
				 * MessageKeyboard timeKeyboard =
				 * createTimeKeyboard(roomService.getRoomById(roomId), dateStr);
				 * 
				 * 
				 * System.out.println("Time keyboard created");
				 * 
				 * 
				 * response.send(new TextMessage("Please choose time:",
				 * timeKeyboard, timeTr, new Integer(1)));
				 * 
				 * }
				 */

				// user enters the time
				else if (message.getTrackingData().get("welcome").equals("timeObj")) {
					if (message.getMapRepresentation().get("text").equals("Cancel")) {
						System.out.println("In Cancel");
						response.send(welcomeScreen(event.getSender().getName()));

					} else {
						String timeSlotString = (String) message.getMapRepresentation().get("text");
						String roomIdString = (String) message.getTrackingData().get("RoomID");
						String userViberId = event.getSender().getId();
						String dateStr = (String) message.getTrackingData().get("Date");

						Map<String, Object> confTrackingData = new HashMap<>();
						confTrackingData.put("welcome", "confObj");
						confTrackingData.put("Time", timeSlotString);
						confTrackingData.put("Date", dateStr);
						confTrackingData.put("RoomID", roomIdString);
						confTrackingData.put("UserViberID", userViberId);

						DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

						LocalDate date = LocalDate.parse(dateStr, formatter);

						TrackingData confTr = new TrackingData(confTrackingData);

						MessageKeyboard confKeyboard = createConfirmKeyboard();

						String reservationDetails = "Would you like to confirm your reservation?" + "\nROOM: "
								+ roomService.getRoomById(roomIdString).getName() + " "
								+ roomService.getRoomById(roomIdString).getNumber() + "\nDATE: " + date.toString()
								+ "\nTIME: " + timeSlotString;

						response.send(new TextMessage(reservationDetails, confKeyboard, confTr, new Integer(1)));

					}

				}
				// user confirmed the reservation
				else if (message.getTrackingData().get("welcome").equals("confObj")) {

					TrackingData data = message.getTrackingData();

					reservationService
							.reserve(new Reservation(userService.getByViberId((String) data.get("UserViberID")),
									roomService.getRoomById((String) data.get("RoomID")), (String) data.get("Date"),
									(String) data.get("Time")));

					response.send("You have successfully confirmed your reservation!");

					response.send(welcomeScreen(event.getSender().getName()));

				}
			}

		});

	}

	private MessageKeyboard createReservationsKeyboard(String userViberId) {
		List<Reservation> resList = (List<Reservation>) reservationService.getByUser(userViberId);

		ArrayList<Map> buttonsList = new ArrayList<>();

		for (Reservation res : resList) {

			String resString = res.getRoom().getName() + ": " + res.getDate() + ", " + res.getTime();

			Map<String, Object> resButton = new HashMap<>();
			resButton.put("Rows", "1");
			resButton.put("BgColor", "#BC86AF");
			resButton.put("Text", resString);
			resButton.put("TextVAlign", "middle");
			resButton.put("TextHAlign", "center");
			resButton.put("TextOpacity", "60");
			resButton.put("TextSize", "regular");
			resButton.put("ActionType", "reply");
			resButton.put("ActionBody", res.getId().toString());
			resButton.put("TextSize", "regular");

			buttonsList.add(resButton);

			System.out.println("Reservation id is: " + res.getId());

		}

		Map<String, Object> cancelButton = new HashMap<>();
		cancelButton.put("Rows", "1");
		cancelButton.put("BgColor", "#DDC2D7");
		cancelButton.put("Text", "Cancel");
		cancelButton.put("TextVAlign", "middle");
		cancelButton.put("TextHAlign", "center");
		cancelButton.put("TextOpacity", "60");
		cancelButton.put("TextSize", "regular");
		cancelButton.put("ActionType", "reply");
		cancelButton.put("ActionBody", "Cancel");
		cancelButton.put("TextSize", "regular");

		buttonsList.add(cancelButton);

		Map<String, Object> keyboard = new HashMap<>();
		keyboard.put("Buttons", buttonsList);
		keyboard.put("DefaultHeight", false);
		keyboard.put("Type", "keyboard");

		System.out.println("RESERVATIONS keyboard successsfully created!");

		return new MessageKeyboard(keyboard);

	}

	private MessageKeyboard createRoomKeyboard() {

		List<Room> rooms = (List<Room>) roomService.listAllRooms();

		ArrayList<Map> buttonsList = new ArrayList<>();

		for (Room room : rooms) {

			Map<String, Object> roomButton = new HashMap<>();
			roomButton.put("Rows", "1");
			roomButton.put("BgColor", "#770B5E");
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
		cancelButton.put("BgColor", "#DDC2D7");
		cancelButton.put("Text", "Cancel");
		cancelButton.put("TextVAlign", "middle");
		cancelButton.put("TextHAlign", "center");
		cancelButton.put("TextOpacity", "60");
		cancelButton.put("TextSize", "regular");
		cancelButton.put("ActionType", "reply");
		cancelButton.put("ActionBody", "Cancel");
		cancelButton.put("TextSize", "regular");

		buttonsList.add(cancelButton);

		Map<String, Object> keyboard = new HashMap<>();
		keyboard.put("Buttons", buttonsList);
		keyboard.put("DefaultHeight", false);
		keyboard.put("Type", "keyboard");

		return new MessageKeyboard(keyboard);

	}

	private MessageKeyboard cancelResKeyboard() {

		ArrayList<Map> buttonsList = new ArrayList<>();

		Map<String, Object> cancelRes = new HashMap<>();
		cancelRes.put("Rows", "1");
		cancelRes.put("BgColor", "#770B5E");
		cancelRes.put("Text", "Cancel Reservation");
		cancelRes.put("TextVAlign", "middle");
		cancelRes.put("TextHAlign", "center");
		cancelRes.put("TextOpacity", "60");
		cancelRes.put("TextSize", "regular");
		cancelRes.put("ActionType", "reply");
		cancelRes.put("ActionBody", "CancelReservation");
		cancelRes.put("TextSize", "regular");

		buttonsList.add(cancelRes);

		Map<String, Object> cancelButton = new HashMap<>();
		cancelButton.put("Rows", "1");
		cancelButton.put("BgColor", "#DDC2D7");
		cancelButton.put("Text", "Cancel");
		cancelButton.put("TextVAlign", "middle");
		cancelButton.put("TextHAlign", "center");
		cancelButton.put("TextOpacity", "60");
		cancelButton.put("TextSize", "regular");
		cancelButton.put("ActionType", "reply");
		cancelButton.put("ActionBody", "Cancel");
		cancelButton.put("TextSize", "regular");

		buttonsList.add(cancelButton);

		Map<String, Object> keyboard = new HashMap<>();
		keyboard.put("Buttons", buttonsList);
		keyboard.put("DefaultHeight", false);
		keyboard.put("Type", "keyboard");

		return new MessageKeyboard(keyboard);

	}

	private MessageKeyboard createConfirmKeyboard() {

		ArrayList<Map> buttonsList = new ArrayList<>();

		Map<String, Object> confButton = new HashMap<>();
		confButton.put("Rows", "1");
		confButton.put("BgColor", "#770B5E");
		confButton.put("Text", "Confirm");
		confButton.put("TextVAlign", "middle");
		confButton.put("TextHAlign", "center");
		confButton.put("TextOpacity", "60");
		confButton.put("TextSize", "regular");
		confButton.put("ActionType", "reply");
		confButton.put("ActionBody", "Confirm");
		confButton.put("TextSize", "regular");

		buttonsList.add(confButton);

		Map<String, Object> cancelButton = new HashMap<>();
		cancelButton.put("Rows", "1");
		cancelButton.put("BgColor", "#DDC2D7");
		cancelButton.put("Text", "Cancel");
		cancelButton.put("TextVAlign", "middle");
		cancelButton.put("TextHAlign", "center");
		cancelButton.put("TextOpacity", "60");
		cancelButton.put("TextSize", "regular");
		cancelButton.put("ActionType", "reply");
		cancelButton.put("ActionBody", "Cancel");
		cancelButton.put("TextSize", "regular");

		buttonsList.add(cancelButton);

		Map<String, Object> keyboard = new HashMap<>();
		keyboard.put("Buttons", buttonsList);
		keyboard.put("DefaultHeight", false);
		keyboard.put("Type", "keyboard");

		return new MessageKeyboard(keyboard);

	}

	private MessageKeyboard createTimeKeyboard(Room room, String date) {

		System.out.println("IN createTimeKeyboard, room  " + room.getId() + " date: " + date);

		List<LocalTime> notavAilableTimes = (List<LocalTime>) reservationService
				.getFreeRoomCapacitiesOnDate(room.getId(), LocalDate.parse(date));

		System.out.println(notavAilableTimes.toString());

		for (LocalTime time : notavAilableTimes) {
			System.out.println("NOTAvailable times: " + time.toString());
		}

		ArrayList<Map> buttonsList = new ArrayList<>();
		LocalTime time = room.getStartWorkTime();
		LocalTime endTime = room.getEndWorkTime();

		// System.out.println("time is: " + time + " endTime is: " + endTime);

		while (time.isBefore(endTime)) {

			// System.out.println("time is: " + time + " endTime is: " +
			// endTime);
			if (!notavAilableTimes.contains(time)) {
				Map<String, Object> timeButton = new HashMap<>();
				timeButton.put("Rows", "1");
				timeButton.put("BgColor", "#770B5E");
				timeButton.put("Text", time.toString());
				timeButton.put("TextVAlign", "middle");
				timeButton.put("TextHAlign", "center");
				timeButton.put("TextOpacity", "60");
				timeButton.put("TextSize", "regular");
				timeButton.put("ActionType", "reply");
				timeButton.put("ActionBody", time.toString());
				timeButton.put("TextSize", "regular");

				buttonsList.add(timeButton);
			}
			time = time.plusHours(1);

		}

		Map<String, Object> cancelButton = new HashMap<>();
		cancelButton.put("Rows", "1");
		cancelButton.put("BgColor", "#DDC2D7");
		cancelButton.put("Text", "Cancel");
		cancelButton.put("TextVAlign", "middle");
		cancelButton.put("TextHAlign", "center");
		cancelButton.put("TextOpacity", "60");
		cancelButton.put("TextSize", "regular");
		cancelButton.put("ActionType", "reply");
		cancelButton.put("ActionBody", "Cancel");
		cancelButton.put("TextSize", "regular");

		buttonsList.add(cancelButton);

		Map<String, Object> keyboard = new HashMap<>();
		keyboard.put("Buttons", buttonsList);
		keyboard.put("DefaultHeight", false);
		keyboard.put("Type", "keyboard");

		return new MessageKeyboard(keyboard);

	}

	private MessageKeyboard createCancelKeyboard() {

		ArrayList<Map> buttonsList = new ArrayList<>();

		Map<String, Object> cancelButton = new HashMap<>();
		cancelButton.put("Rows", "1");
		cancelButton.put("BgColor", "#ffffff");
		cancelButton.put("Text", "Cancel");
		cancelButton.put("TextVAlign", "middle");
		cancelButton.put("TextHAlign", "center");
		cancelButton.put("TextOpacity", "60");
		cancelButton.put("TextSize", "regular");
		cancelButton.put("ActionType", "reply");
		cancelButton.put("ActionBody", "Cancel");
		cancelButton.put("TextSize", "regular");

		buttonsList.add(cancelButton);

		Map<String, Object> keyboard = new HashMap<>();
		keyboard.put("Buttons", buttonsList);
		keyboard.put("DefaultHeight", false);
		keyboard.put("Type", "keyboard");

		return new MessageKeyboard(keyboard);

	}

	@Override
	public void subscribe(ViberBot bot) {

		bot.onSubscribe((new OnSubscribe() {
			@Override
			public void subscribe(IncomingSubscribedEvent event, Response response) {
				UserProfile userPr = event.getUser();

				if (userService.getByViberId(userPr.getId()) == null) {
					userService.add(new User(userPr.getId(), userPr.getName(), true));
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

	private MessageKeyboard createWelcomeKeyboard() {
		Map<String, Object> button1 = new HashMap<>();
		button1.put("Columns", "3");
		button1.put("Rows", "2");
		button1.put("BgColor", "#770B5E");
		button1.put("Text", "Reserve room");
		button1.put("TextVAlign", "middle");
		button1.put("TextHAlign", "center");
		button1.put("TextOpacity", "60");
		button1.put("TextSize", "regular");
		button1.put("ActionType", "reply");
		button1.put("ActionBody", "Reserve room");
		button1.put("TextSize", "regular");

		Map<String, Object> button2 = new HashMap<>();
		button2.put("Columns", "3");
		button2.put("Rows", "2");
		button2.put("BgColor", "#BC86AF");
		button2.put("Text", "See previous reservations");
		button2.put("TextVAlign", "middle");
		button2.put("TextHAlign", "center");
		button2.put("TextOpacity", "60");
		button2.put("TextSize", "regular");
		button2.put("ActionType", "reply");
		button2.put("ActionBody", "See previous reservations");
		button2.put("TextSize", "regular");

		ArrayList<Map> button12 = new ArrayList<>();
		button12.add(button1);
		button12.add(button2);

		Map<String, Object> buttons = new HashMap<>();
		buttons.put("Buttons", button12);

		Map<String, Object> keyboard = new HashMap<>();
		keyboard.put("Buttons", button12);
		keyboard.put("DefaultHeight", false);
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

	private TextMessage welcomeScreen(String userName) {

		MessageKeyboard welcomeKeyboard = createWelcomeKeyboard();

		Map<String, Object> welcomeTrackingData = new HashMap<>();
		welcomeTrackingData.put("welcome", "welcomeObj");
		TrackingData trackingData = new TrackingData(welcomeTrackingData);

		System.out.println("In WELCOME SCREEN");
		System.out.println("TrackingData:");
		System.out.println(welcomeTrackingData.toString());

		return new TextMessage(
				"Hello, " + userName
						+ "! Welcome to ViberBot Room Reservation. Please choose one of the options below:",
				new MessageKeyboard(welcomeKeyboard), trackingData, new Integer(1));

		// Test

	}

}
