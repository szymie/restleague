package org.tiwpr.szymie.models;

import org.tiwpr.szymie.models.validators.DatePattern;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class Transfer implements Model {

    @DecimalMin("0")
    private int id;

    @NotNull(message = "{transfer.playerId.null}")
    @DecimalMin("0")
    private int playerId;

    @NotNull(message = "{transfer.sourceClubId.null}")
    @DecimalMin("0")
    private int sourceClubId;

    @NotNull(message = "{transfer.destinationClubId.null}")
    @DecimalMin("0")
    private int destinationClubId;

    @NotNull(message = "{transfer.value.null}")
    private int value;

    @DatePattern(regexp = "dd-MM-yyyy", message = "{transfer.date.pattern}")
    private String date;

    @Size(min = 1, max = 11, message = "{transfer.status.length")
    @Pattern(regexp = "in progress|done", message = "{transfer.status.pattern}")
    private String status;

    public Transfer() {
    }

    public Transfer(int id, int playerId, int sourceClubId, int destinationClubId, int value, String date, String status) {
        this.id = id;
        this.playerId = playerId;
        this.sourceClubId = sourceClubId;
        this.destinationClubId = destinationClubId;
        this.value = value;
        this.date = date;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public int getSourceClubId() {
        return sourceClubId;
    }

    public void setSourceClubId(int sourceClubId) {
        this.sourceClubId = sourceClubId;
    }

    public int getDestinationClubId() {
        return destinationClubId;
    }

    public void setDestinationClubId(int destinationClubId) {
        this.destinationClubId = destinationClubId;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
