package com.jannusuraj.ai.controller;

import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.Media;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
public class VisionController {

    @Autowired
    private ChatModel chatModel;

    @PostMapping(value = "/vision", consumes ={ MediaType.MULTIPART_FORM_DATA_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<String> vision(@RequestParam("file") MultipartFile file) {
        final OpenAiChatOptions chatOptions =  OpenAiChatOptions.builder()
                .withModel(OpenAiApi.ChatModel.GPT_4_O)
                .build();
        var usermessage = new UserMessage("Explain what do you see in the picture?",
                List.of(new Media(MimeTypeUtils.IMAGE_JPEG,file.getResource())));
        ChatResponse chatResponse = chatModel.call(new Prompt(List.of(usermessage), chatOptions));
        return new ResponseEntity<>(chatResponse.getResult().getOutput().getContent(), HttpStatus.OK);
    }

}
