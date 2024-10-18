package com.jannusuraj.ai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class MoviesController {

    private static final String DOCUMENTS = "documents";
    private static final String QUESTION = "question";

    @Autowired
    private ChatModel chatModel;

    @Autowired
    private SimpleVectorStore simpleVectorStore;

    @Value("classpath:templates/top-100-imdb-movies-prompt.st")
    private Resource top100ImdbMoviesPrompt;

    @GetMapping("/movies")
    public ResponseEntity<String> movies(@RequestParam("question") String question) {
        final SearchRequest request = SearchRequest.query(question).withTopK(3);
        final List<Document> vectorDocuments = simpleVectorStore.similaritySearch(request);
        final List<String> documentList = vectorDocuments.stream().map(Document::getContent).toList();
        final String documents = String.join(",", documentList);
        final PromptTemplate promptTemplate = new PromptTemplate(top100ImdbMoviesPrompt);
        final Prompt prompt = promptTemplate.create(Map.of(DOCUMENTS,documents,QUESTION,question));
        final String response = chatModel.call(prompt).getResult().getOutput().getContent();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
