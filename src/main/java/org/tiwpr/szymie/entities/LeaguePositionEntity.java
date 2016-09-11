package org.tiwpr.szymie.entities;

import javax.persistence.*;

@Entity
@Table(name = "league_positions")
public class LeaguePositionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int number;
    private String meaning;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "league_id")
    private LeagueEntity league;

    public LeaguePositionEntity() {
    }

    public LeaguePositionEntity(int number, String meaning) {
        this.number = number;
        this.meaning = meaning;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    public LeagueEntity getLeague() {
        return league;
    }

    public void setLeague(LeagueEntity league) {
        this.league = league;
    }
}
