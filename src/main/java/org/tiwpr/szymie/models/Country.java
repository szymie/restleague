package org.tiwpr.szymie.models;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class Country {

    @NotNull(message = "{country.name.null}")
    @Size(min = 1, max = 50, message = "{country.name.length}")
    private String name;

    public Country() {
    }

    public Country(String name) {
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
