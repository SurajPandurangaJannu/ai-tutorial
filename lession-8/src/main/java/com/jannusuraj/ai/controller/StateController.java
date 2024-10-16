package com.jannusuraj.ai.controller;

import com.jannusuraj.ai.model.Country;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
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
    private static final String FORMAT_PROMPT_PARAM = "format";

    @Autowired
    private ChatClient chatClient;

    @Value("${classpath:templates/listdown-states-of-country-prompt.st")
    private Resource listdownstatesPrompt;

    @GetMapping("/listdownstates")
    public ResponseEntity<Object> listDownStateOfCountry(@RequestParam("country") String country) {
        final BeanOutputConverter<Country> converter = new BeanOutputConverter<>(Country.class);
        final String format = converter.getFormat();
        final PromptTemplate stateTemplate = new PromptTemplate(listdownstatesPrompt);
        stateTemplate.create(Map.of(COUNTRY_PROMPT_PARAM, country, FORMAT_PROMPT_PARAM, format));
        final Prompt prompt = stateTemplate.create();
        final String response = chatClient.prompt(prompt).call().content();
        try {
            return new ResponseEntity<>(converter.convert(response), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

}
