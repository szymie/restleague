package org.tiwpr.szymie.entities;

import org.tiwpr.szymie.models.Season;
import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@Table(name = "seasons")
public class SeasonEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    @Column(name = "start_date")
    private Date startDate;
    @Column(name = "end_date")
    private Date endDate;
    private String status;
    @Column(name = "last_modified")
    private Timestamp lastModified;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
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
    public Season toModel() {

        Season season = new Season();

        season.setId(id);
        season.setName(name);
        season.setStartDate(stringFromDate(startDate));
        season.setEndDate(stringFromDate(endDate));
        season.setStatus(status);

        return season;
    }

    public static SeasonEntity fromModel(Season season) {

        SeasonEntity seasonEntity = new SeasonEntity();

        seasonEntity.setId(season.getId());
        seasonEntity.setName(season.getName());
        seasonEntity.setStartDate(dateFromString(season.getStartDate()));
        seasonEntity.setEndDate(dateFromString(season.getEndDate()));
        seasonEntity.setStatus(season.getStatus());

        return seasonEntity;
    }
}
