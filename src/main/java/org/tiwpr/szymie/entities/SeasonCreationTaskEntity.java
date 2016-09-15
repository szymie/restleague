package org.tiwpr.szymie.entities;

import org.tiwpr.szymie.models.SeasonCreationTask;
import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "season_creation_tasks")
public class SeasonCreationTaskEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "season_id")
    private SeasonEntity season;
    private String status;
    @Column(name = "last_modified")
    private Timestamp lastModified;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public SeasonEntity getSeason() {
        return season;
    }

    public void setSeason(SeasonEntity season) {
        this.season = season;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getLastModified() {
        return lastModified;
    }

    public void setLastModified(Timestamp lastModified) {
        this.lastModified = lastModified;
    }

    @Override
    public SeasonCreationTask toModel() {

        SeasonCreationTask seasonCreationTask = new SeasonCreationTask();

        seasonCreationTask.setId(id);
        seasonCreationTask.setSeasonId(season.getId());
        seasonCreationTask.setStatus(status);

        return seasonCreationTask;
    }

    public static SeasonCreationTaskEntity fromModel(SeasonCreationTask seasonCreationTask) {

        SeasonCreationTaskEntity seasonCreationTaskEntity = new SeasonCreationTaskEntity();

        seasonCreationTaskEntity.setId(seasonCreationTask.getId());
        seasonCreationTaskEntity.setStatus(seasonCreationTask.getStatus());

        return seasonCreationTaskEntity;
    }
}
