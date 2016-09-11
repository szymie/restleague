package org.tiwpr.szymie.models;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class League implements Model {

    @DecimalMin("0")
    private int id;
    @NotNull(message = "{league.fullName.null}")
    @Size(min = 1, max = 50, message = "{league.fullName.length}")
    private String fullName;
    @DecimalMin("1")
    private Short level;
    private int[] promotionPositions;
    private int[] relegationPositions;
    @NotNull(message = "{league.numberOfTeams.null}")
    @DecimalMin("2")
    private short numberOfTeams;

    public League() {
    }

    public League(int id, String fullName, Short level, int[] promotionPositions, int[] relegationPositions, short numberOfTeams) {
        this.id = id;
        this.fullName = fullName;
        this.level = level;
        this.promotionPositions = promotionPositions;
        this.relegationPositions = relegationPositions;
        this.numberOfTeams = numberOfTeams;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Short getLevel() {
        return level;
    }

    public void setLevel(Short level) {
        this.level = level;
    }

    public int[] getPromotionPositions() {
        return promotionPositions;
    }

    public void setPromotionPositions(int[] promotionPositions) {
        this.promotionPositions = promotionPositions;
    }

    public int[] getRelegationPositions() {
        return relegationPositions;
    }

    public void setRelegationPositions(int[] relegationPositions) {
        this.relegationPositions = relegationPositions;
    }

    public short getNumberOfTeams() {
        return numberOfTeams;
    }

    public void setNumberOfTeams(short numberOfTeams) {
        this.numberOfTeams = numberOfTeams;
    }
}
