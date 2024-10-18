package com.jannusuraj.ai.model;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public record State(@JsonPropertyDescription("Name of the state") String state,
                    @JsonPropertyDescription("Name of the capital city") String capitalCity,
                    @JsonPropertyDescription("Population of the state") Long population,
                    @JsonPropertyDescription("Polular languages of a state") String[] languages) {
}
