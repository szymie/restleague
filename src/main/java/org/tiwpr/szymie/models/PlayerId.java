package org.tiwpr.szymie.models;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

public class PlayerId {

    @DecimalMin("0")
    @NotNull(message = "{playerId.null}")
    private int playerId;

    public PlayerId() {
    }

    public PlayerId(int playerId) {
        this.playerId = playerId;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }
}
