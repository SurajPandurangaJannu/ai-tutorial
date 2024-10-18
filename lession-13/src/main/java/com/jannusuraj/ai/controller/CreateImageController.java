package com.jannusuraj.ai.controller;

import org.springframework.ai.image.*;
import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.ai.openai.api.OpenAiImageApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

@RestController
public class CreateImageController {

    @Autowired
    private OpenAiImageModel imageModel;

    @GetMapping(value = "/generate-image", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> generateImage(@RequestParam("message") String message) {
        final OpenAiImageOptions imageModelOptions = OpenAiImageOptions
                .builder()
                .withHeight(1024).withWidth(1024)
                .withResponseFormat("b64_json")
                .withModel(OpenAiImageApi.ImageModel.DALL_E_3.getValue())
                .withQuality("hd") // default standard
                .withStyle("natural") // default vivid
                .build();
        final ImagePrompt request = new ImagePrompt(List.of(new ImageMessage(message)), imageModelOptions);
        final ImageResponse response = imageModel.call(request);
        final byte[] bytes = Base64.getDecoder().decode(response.getResult().getOutput().getB64Json());
        return new ResponseEntity<>(bytes, HttpStatus.OK);
    }

    @GetMapping(value = "/generate-image-url")
    public ResponseEntity<String> generateImageAsUrl(@RequestParam("message") String message) {
        final OpenAiImageOptions imageModelOptions = OpenAiImageOptions
                .builder()
                .withHeight(1024).withWidth(1024)
                .withModel(OpenAiImageApi.ImageModel.DALL_E_3.getValue())
                .withQuality("hd") // default standard
                .withStyle("natural") // default vivid
                .build();
        final String url = imageModel.call(new ImagePrompt(message,imageModelOptions)).getResult().getOutput().getUrl();
        return new ResponseEntity<>(url, HttpStatus.OK);
    }
}
