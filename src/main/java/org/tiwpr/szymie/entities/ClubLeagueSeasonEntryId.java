package org.tiwpr.szymie.entities;


import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class ClubLeagueSeasonEntryId implements Serializable {

    @Column(name = "club_id")
    private int clubId;
    @Column(name = "league_id")
    private int leagueId;
    @Column(name = "season_id")
    private int seasonId;

    public int getClubId() {
        return clubId;
    }

    public void setClubId(int clubId) {
        this.clubId = clubId;
    }

    public int getLeagueId() {
        return leagueId;
    }

    public void setLeagueId(int leagueId) {
        this.leagueId = leagueId;
    }

    public int getSeasonId() {
        return seasonId;
    }

    public void setSeasonId(int seasonId) {
        this.seasonId = seasonId;
    }
}