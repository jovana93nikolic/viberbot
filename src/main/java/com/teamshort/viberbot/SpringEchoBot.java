package com.teamshort.viberbot;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.io.CharStreams;
import com.google.common.util.concurrent.Futures;
import com.viber.bot.Request;
import com.viber.bot.ViberSignatureValidator;
import com.viber.bot.api.ViberBot;
import com.viber.bot.message.TextMessage;
import com.viber.bot.profile.BotProfile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Optional;
import java.util.concurrent.ExecutionException;


import com.teamshort.viberbot.service.ViberBotService;

@RestController
//@SpringBootApplication
public class SpringEchoBot implements ApplicationListener<ApplicationReadyEvent> {

    @Inject
    private ViberBot bot;

    @Inject
    private ViberSignatureValidator signatureValidator;

    @Value("${application.viber-bot.webhook-url}")
    private String webhookUrl;

    @Autowired
    private ViberBotService viberBotService;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent appReadyEvent) {
        try {
            bot.setWebhook(webhookUrl).get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //bot.onMessageReceived((event, message, response) -> response.send(message)); // echos everything back
        //bot.onConversationStarted(event -> Futures.immediateFuture(Optional.of( // send 'Hi UserName' when conversation is started
          //      new TextMessage("Hi " + event.getUser().getName()))));
        
        viberBotService.onMessageReceived(bot);
        viberBotService.subscribe(bot);
        viberBotService.unsubscribe(bot);
        viberBotService.onConversationStarted(bot);
    
    }

    @PostMapping(value = "/", produces = "application/json")
    public String incoming(@RequestBody String json,
                           @RequestHeader("X-Viber-Content-Signature") String serverSideSignature)
            throws ExecutionException, InterruptedException, IOException {
        Preconditions.checkState(signatureValidator.isSignatureValid(serverSideSignature, json), "invalid signature");
        @Nullable InputStream response = bot.incoming(Request.fromJsonString(json)).get();
        return response != null ? CharStreams.toString(new InputStreamReader(response, Charsets.UTF_16)) : null;
    }

    
    
    
}
