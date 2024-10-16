package com.jannusuraj.ai.model;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public record Country(@JsonPropertyDescription("Name of the country") String name,
                      @JsonPropertyDescription("States within the country") State[] states) {
}
