package com.jannusuraj.ai.controller;

import com.jannusuraj.ai.model.Country;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class StateController {

    private static final String COUNTRY_PROMPT_PARAM = "country";
    private static final String FORMAT_PROMPT_PARAM = "format";

    /*
     * System Prompts helps when prompts are re-used across multiple scenarios
     * */
    private static final String SYSTEM_PROMPT_1 = "In case of {country} name provide is not a valid country, respond with empty JSON array";
    private static final String SYSTEM_PROMPT_2 = "Provide the state names all in capital letter";

    @Autowired
    private ChatModel chatModel;

    @Value("classpath:templates/listdown-states-of-country-prompt.st")
    private Resource listDownStatesPrompt;

    @GetMapping("/listdownstates")
    public ResponseEntity<Object> listDownStateOfCountry(@RequestParam("country") String country) {
        final List<Message> messages = new ArrayList<>();

        final BeanOutputConverter<Country> converter = new BeanOutputConverter<>(Country.class);
        final String format = converter.getFormat();
        final PromptTemplate stateTemplate = new PromptTemplate(listDownStatesPrompt);

        messages.add(stateTemplate.createMessage(Map.of(COUNTRY_PROMPT_PARAM, country, FORMAT_PROMPT_PARAM, format)));
        messages.add(new SystemPromptTemplate(SYSTEM_PROMPT_1).createMessage(Map.of(COUNTRY_PROMPT_PARAM, country)));
        messages.add(new SystemPromptTemplate(SYSTEM_PROMPT_2).createMessage());

        final Prompt prompt = new Prompt(messages);
        final String response = chatModel.call(prompt).getResult().getOutput().getContent();
        try {
            return new ResponseEntity<>(converter.convert(response), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

}
