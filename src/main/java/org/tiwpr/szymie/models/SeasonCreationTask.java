package org.tiwpr.szymie.models;

public class SeasonCreationTask implements Model {

    private int id;
    private Integer seasonId;
    private String status;

    public SeasonCreationTask() {
    }

    public SeasonCreationTask(int id, Integer seasonId, String status) {
        this.id = id;
        this.seasonId = seasonId;
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getSeasonId() {
        return seasonId;
    }

    public void setSeasonId(Integer seasonId) {
        this.seasonId = seasonId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
