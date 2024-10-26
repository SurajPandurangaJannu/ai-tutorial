package com.jannusuraj.ai.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.jannusuraj.ai.model.Movie;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class MovieController {

    public static final String FORMAT = "format";
    public static final String DOCUMENTS = "documents";
    public static final String QUESTION = "question";

    @Autowired
    private ChatModel chatModel;

    @Autowired
    private VectorStore vectorStore;

    @Value("classpath:templates/search-for-movies.st")
    private Resource moviePrompt;

    @GetMapping("/movie/documents")
    public ResponseEntity<Object> getMoviesDocuments(@RequestParam("message") String message){
        final SearchRequest query = SearchRequest.query(message).withTopK(4);
        final List<Document> documents = vectorStore.similaritySearch(query);
        return new ResponseEntity<>(documents, HttpStatus.OK);
    }

    @GetMapping("/movie")
    public ResponseEntity<Object> getMovies(@RequestParam("message") String message){
        final SearchRequest query = SearchRequest.query(message).withTopK(4);
        final List<Document> vectorDocuments = vectorStore.similaritySearch(query);
        final List<String> documentList = vectorDocuments.stream().map(Document::getContent).toList();
        final String documents = documentList.stream().collect(Collectors.joining(System.lineSeparator()));

        final ParameterizedTypeReference<List<Movie>> typeReference = new ParameterizedTypeReference<List<Movie>>() {};
        final BeanOutputConverter<List<Movie>> beanOutputConverter = new BeanOutputConverter(typeReference);
        final String format = beanOutputConverter.getFormat();
        final PromptTemplate promptTemplate = new PromptTemplate(moviePrompt);
        final Prompt prompt = promptTemplate.create(
                Map.of(QUESTION,message,DOCUMENTS,documents, FORMAT,format));
        final String response = chatModel.call(prompt).getResult().getOutput().getContent();
        try {
            final List<Movie> movies = beanOutputConverter.convert(response);
            return new ResponseEntity<>(movies,HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
        }
    }
}


