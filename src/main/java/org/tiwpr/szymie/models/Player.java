package org.tiwpr.szymie.models;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.tiwpr.szymie.models.validators.DatePattern;

import javax.validation.Valid;
import javax.validation.constraints.*;

public class Player implements Model {

    @DecimalMin("0")
    private int id;

    @NotNull(message = "{player.firstName.null}")
    @Size(min = 1, max = 40, message = "{player.firstName.length}")
    private String firstName;

    @NotNull(message = "{player.lastName.null}")
    @Size(min = 1, max = 40, message = "{player.lastName.length}")
    private String lastName;

    @NotNull(message = "{player.dateOfBirth.null}")
    @DatePattern(regexp = "dd-MM-yyyy", message = "{player.dateOfBirth.pattern}")
    private String dateOfBirth;

    @DecimalMin("1")
    @DecimalMax("999")
    private int height;

    @NotNull(message = "{player.foot.null}")
    @Pattern(regexp = "right|left|both|unknown", message = "{player.foot.pattern}")
    private String foot;

    @Valid
    @JsonSerialize(using = ToStringSerializer.class)
    private Position position;

    @Valid
    @JsonSerialize(using = ToStringSerializer.class)
    private Country country;

    public Player() {
    }

    public Player(int id, String firstName, String lastName, String dateOfBirth, int height, String foot, Position position, Country country) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.height = height;
        this.foot = foot;
        this.position = position;
        this.country = country;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getFoot() {
        return foot;
    }

    public void setFoot(String foot) {
        this.foot = foot;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }
}
