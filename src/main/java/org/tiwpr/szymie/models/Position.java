package org.tiwpr.szymie.models;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class Position {

    @NotNull(message = "{position.name.null}")
    @Size(min = 1, max = 10, message = "{position.name.length}")
    @Pattern(regexp = "striker|midfield|defence|goalkeeper", message = "{position.name.regexp}")
    private String name;

    public Position() {
    }

    public Position(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
