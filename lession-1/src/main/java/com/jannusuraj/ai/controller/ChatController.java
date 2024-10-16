package com.jannusuraj.ai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatController {

    @Autowired
    private ChatClient chatClient;

    @GetMapping("/chat")
    public ResponseEntity<String> chatWithClient(@RequestParam("message") String message) {
        final String response = chatClient.prompt(message).call().content();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/chatresponse")
    public ResponseEntity<ChatResponse> chatWithResponse(@RequestParam("message") String message) {
        final ChatResponse response = chatClient.prompt(message).call().chatResponse();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
