package com.jannusuraj.ai.loader;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jannusuraj.ai.model.Movie;
import groovy.util.logging.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class MoviesDataLoader implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(MoviesDataLoader.class);

    @Autowired
    private VectorStore vectorStore;

    @Autowired
    private JdbcClient jdbcClient;

    @Autowired
    private TokenTextSplitter tokenTextSplitter;

    @Autowired
    private ObjectMapper objectMapper;

    private static final TypeReference<Map<String, Object>> MAP_REFERENCE = new TypeReference<Map<String, Object>>() {
    };

    @Override
    public void run(String... args) throws Exception {

        var vectorStoreCount = jdbcClient.sql("select count(*) from vector_store").query(Integer.class).stream().count();

        if (vectorStoreCount != 0) {
            var movies = jdbcClient.sql("select * from movies")
                    .query(new DataClassRowMapper<>(Movie.class))
                    .list();

            movies.parallelStream().forEach(movie -> {
                final var content = movie.title() + " " + movie.description() + " " + movie.genre() + " " + movie.year();
                final var values = objectMapper.convertValue(movie, MAP_REFERENCE);
                final var document = new Document(content, values);
                final var documents = tokenTextSplitter.apply(List.of(document));
                vectorStore.add(documents);
            });

            log.info("Data loading completed");

        }else {
            log.info("Data is already preloaded");
        }
    }


}
