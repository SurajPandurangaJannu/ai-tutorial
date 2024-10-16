package com.jannusuraj.ai.controller;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StateController {

    @Autowired
    private ChatModel chatModel;

    @GetMapping("/listdownstates")
    public ResponseEntity<String> listDownStateOfCountry(@RequestParam("country") String country) {
        final PromptTemplate stateTemplate = new PromptTemplate("List down the states of the country " + country);
        final Prompt prompt = stateTemplate.create();
        final String response = chatModel.call(prompt).getResult().getOutput().getContent();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
