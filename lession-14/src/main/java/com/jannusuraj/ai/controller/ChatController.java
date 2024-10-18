package com.jannusuraj.ai.controller;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatController {

    @Autowired
    private ChatModel chatModel;

    @GetMapping("/chat")
    public ResponseEntity<String> chatWithClient(@RequestParam("message") String message) {
        final String response = chatModel.call(message);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/chatresponse")
    public ResponseEntity<ChatResponse> chatWithResponse(@RequestParam("message") String message) {
        final Prompt prompt = new PromptTemplate(message).create();
        final ChatResponse response = chatModel.call(prompt);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
