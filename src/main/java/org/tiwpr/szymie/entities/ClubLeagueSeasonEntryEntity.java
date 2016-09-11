package org.tiwpr.szymie.entities;

import javax.persistence.*;

@Entity
@Table(name = "club_league_season_entries")
public class ClubLeagueSeasonEntryEntity  {

    @EmbeddedId
    private ClubLeagueSeasonEntryId id;
    @MapsId("clubId")
    @JoinColumn(name = "club_id")
    @ManyToOne
    private ClubEntity club;
    @MapsId("leagueId")
    @JoinColumn(name = "league_id")
    @ManyToOne
    private LeagueEntity league;
    @MapsId("seasonId")
    @JoinColumn(name = "season_id")
    @ManyToOne
    private SeasonEntity season;

    public ClubLeagueSeasonEntryEntity() {
    }

    public ClubLeagueSeasonEntryEntity(ClubLeagueSeasonEntryId id) {
        this.id = id;
    }

    public ClubLeagueSeasonEntryEntity(ClubLeagueSeasonEntryId id, ClubEntity club, LeagueEntity league, SeasonEntity season) {
        this.id = id;
        this.club = club;
        this.league = league;
        this.season = season;
    }

    public ClubLeagueSeasonEntryId getId() {
        return id;
    }

    public void setId(ClubLeagueSeasonEntryId id) {
        this.id = id;
    }

    public ClubEntity getClub() {
        return club;
    }

    public void setClub(ClubEntity club) {
        this.club = club;
    }

    public LeagueEntity getLeague() {
        return league;
    }

    public void setLeague(LeagueEntity league) {
        this.league = league;
    }

    public SeasonEntity getSeason() {
        return season;
    }

    public void setSeason(SeasonEntity season) {
        this.season = season;
    }
}
