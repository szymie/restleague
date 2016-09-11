package org.tiwpr.szymie.models;

import org.tiwpr.szymie.models.validators.DatePattern;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

public class Fixture implements Model {

    @DecimalMin("0")
    private int id;

    @DecimalMin("0")
    @NotNull(message = "{fixture.homeClubId.null}")
    private int homeClubId;

    @DecimalMin("0")
    @NotNull(message = "{fixture.awayClubId.null}")
    private int awayClubId;

    @NotNull(message = "{fixture.result.null}")
    private Result result;

    @NotNull(message = "{fixture.date.null}")
    @DatePattern(regexp = "dd-MM-yyyy", message = "{fixture.date.pattern}")
    private String date;

    @DecimalMin("0")
    @NotNull(message = "{fixture.matchDay.null}")
    private short matchDay;

    public Fixture() {
    }

    public Fixture(int id, int homeClubId, int awayClubId, Result result, String date, short matchDay) {
        this.id = id;
        this.homeClubId = homeClubId;
        this.awayClubId = awayClubId;
        this.result = result;
        this.date = date;
        this.matchDay = matchDay;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getHomeClubId() {
        return homeClubId;
    }

    public void setHomeClubId(int homeClubId) {
        this.homeClubId = homeClubId;
    }

    public int getAwayClubId() {
        return awayClubId;
    }

    public void setAwayClubId(int awayClubId) {
        this.awayClubId = awayClubId;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public short getMatchDay() {
        return matchDay;
    }

    public void setMatchDay(short matchDay) {
        this.matchDay = matchDay;
    }
}
