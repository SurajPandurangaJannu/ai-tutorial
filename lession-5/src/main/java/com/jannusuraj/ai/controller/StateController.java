package com.jannusuraj.ai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class StateController {

    private static final String COUNTRY_PROMPT_PARAM = "country";
    @Autowired
    private ChatClient chatClient;

    @Value("${classpath:templates/listdown-states-of-country-prompt.st")
    private Resource listdownstatesPrompt;

    @GetMapping("/listdownstates")
    public ResponseEntity<String> listDownStateOfCountry(@RequestParam("country") String country) {
        final PromptTemplate stateTemplate = new PromptTemplate(listdownstatesPrompt);
        stateTemplate.create(Map.of(COUNTRY_PROMPT_PARAM,country));
        final Prompt prompt = stateTemplate.create();
        final String response = chatClient.prompt(prompt).call().content();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
