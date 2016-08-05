package org.tiwpr.szymie.models;

public class Position {

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
