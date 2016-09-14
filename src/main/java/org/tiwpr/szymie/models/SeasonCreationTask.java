package org.tiwpr.szymie.models;

public class SeasonCreationTask implements Model {

    private int id;
    private int seasonId;
    private String status;

    public SeasonCreationTask() {
    }

    public SeasonCreationTask(int id, int seasonId, String status) {
        this.id = id;
        this.seasonId = seasonId;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSeasonId() {
        return seasonId;
    }

    public void setSeasonId(int seasonId) {
        this.seasonId = seasonId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
