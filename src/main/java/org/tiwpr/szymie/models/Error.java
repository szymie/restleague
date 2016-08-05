package org.tiwpr.szymie.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Error {

    @JsonProperty("error")
    private String message;

    public Error() {
    }

    public Error(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
