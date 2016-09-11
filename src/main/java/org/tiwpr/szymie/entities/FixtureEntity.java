package org.tiwpr.szymie.entities;

import org.springframework.beans.factory.annotation.Autowired;
import org.tiwpr.szymie.daos.ClubDao;
import org.tiwpr.szymie.models.Fixture;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@Table(name = "fixtures")
public class FixtureEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "match_day")
    private short matchDay;
    private Date date;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "home_club_id")
    private ClubEntity homeClub;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "away_club_id")
    private ClubEntity awayClub;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "season_id")
    private SeasonEntity season;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "league_id")
    private LeagueEntity league;
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "result_id")
    private ResultEntity result;
    @Column(name = "last_modified")
    private Timestamp lastModified;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public short getMatchDay() {
        return matchDay;
    }

    public void setMatchDay(short matchDay) {
        this.matchDay = matchDay;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public ClubEntity getHomeClub() {
        return homeClub;
    }

    public void setHomeClub(ClubEntity homeClub) {
        this.homeClub = homeClub;
    }

    public ClubEntity getAwayClub() {
        return awayClub;
    }

    public void setAwayClub(ClubEntity awayClub) {
        this.awayClub = awayClub;
    }

    public SeasonEntity getSeason() {
        return season;
    }

    public void setSeason(SeasonEntity season) {
        this.season = season;
    }

    public LeagueEntity getLeague() {
        return league;
    }

    public void setLeague(LeagueEntity league) {
        this.league = league;
    }

    public ResultEntity getResult() {
        return result;
    }

    public void setResult(ResultEntity result) {
        this.result = result;
    }

    public Timestamp getLastModified() {
        return lastModified;
    }

    public void setLastModified(Timestamp lastModified) {
        this.lastModified = lastModified;
    }

    @Override
    public Fixture toModel() {

        Fixture fixture = new Fixture();

        fixture.setId(id);
        fixture.setMatchDay(matchDay);
        fixture.setHomeClubId(homeClub.getId());
        fixture.setAwayClubId(awayClub.getId());
        fixture.setDate(stringFromDate(date));
        fixture.setResult(result.toModel());

        return fixture;
    }

    public static FixtureEntity fromModel(Fixture fixture) {

        FixtureEntity fixtureEntity = new FixtureEntity();

        fixtureEntity.setMatchDay(fixture.getMatchDay());
        fixtureEntity.setDate(dateFromString(fixture.getDate()));
        fixtureEntity.setResult(ResultEntity.fromModel(fixture.getResult()));

        return fixtureEntity;
    }
}
