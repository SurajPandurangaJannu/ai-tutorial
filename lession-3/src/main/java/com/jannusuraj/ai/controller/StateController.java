package com.jannusuraj.ai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StateController {

    @Autowired
    private ChatClient chatClient;

    @Value("${spring.ai.prompt.listdownstates}")
    private String listdownstatesPrompt;

    @GetMapping("/listdownstates")
    public ResponseEntity<String> listDownStateOfCountry(@RequestParam("country") String country) {
        final PromptTemplate stateTemplate = new PromptTemplate(listdownstatesPrompt.formatted(country));
        final Prompt prompt = stateTemplate.create();
        final String response = chatClient.prompt(prompt).call().content();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
