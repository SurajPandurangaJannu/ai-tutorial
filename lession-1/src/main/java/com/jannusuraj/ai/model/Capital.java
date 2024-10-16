package com.jannusuraj.ai.model;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public record Capital(@JsonPropertyDescription("This is the city name") String city,
                      @JsonPropertyDescription("Population of the city") Long population) {
}
