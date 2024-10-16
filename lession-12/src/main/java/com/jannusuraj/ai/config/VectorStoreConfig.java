package com.jannusuraj.ai.config;

import groovy.util.logging.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.util.List;

@Configuration
@Slf4j
public class VectorStoreConfig {

    private static final Logger log = LoggerFactory.getLogger(VectorStoreConfig.class);

    @Bean
    public SimpleVectorStore simpleVectorStore(EmbeddingModel embeddingModel,VectorStoreProperties vectorStoreProperties){
        final var simpleVectorStore = new SimpleVectorStore(embeddingModel);
        final File file = new File(vectorStoreProperties.getVectorStorePath());
        if (file.exists()){
            simpleVectorStore.load(file);
        }else {
            vectorStoreProperties.getDocumentsToLoad().forEach(document -> {
                log.debug("Loading document {}",document.getFilename());
                final TikaDocumentReader documentReader = new TikaDocumentReader(document);
                final List<Document> docs = documentReader.get();
                final TokenTextSplitter tokenTextSplitter = new TokenTextSplitter();
                final List<Document> splitDocs = tokenTextSplitter.apply(docs);
                simpleVectorStore.add(splitDocs);
            });
            simpleVectorStore.save(file);
        }
        return simpleVectorStore;
    }
}
