package org.tiwpr.szymie.models;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

public class Result implements Model {

    @DecimalMin("0")
    @NotNull(message = "{result.goalsHomeClub.null}")
    private short goalsHomeClub;
    @DecimalMin("0")
    @NotNull(message = "{result.goalsAwayClub.null}")
    private short goalsAwayClub;

    public Result() {
    }

    public Result(short goalsHomeClub, short goalsAwayClub) {
        this.goalsHomeClub = goalsHomeClub;
        this.goalsAwayClub = goalsAwayClub;
    }

    public short getGoalsHomeClub() {
        return goalsHomeClub;
    }

    public void setGoalsHomeClub(short goalsHomeClub) {
        this.goalsHomeClub = goalsHomeClub;
    }

    public short getGoalsAwayClub() {
        return goalsAwayClub;
    }

    public void setGoalsAwayClub(short goalsAwayClub) {
        this.goalsAwayClub = goalsAwayClub;
    }
}
