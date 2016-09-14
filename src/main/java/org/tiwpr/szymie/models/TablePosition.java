package org.tiwpr.szymie.models;

public class TablePosition {

    public Short rank;
    public String clubFullName;
    public int clubId;
    public short gamesPlayed;
    public short points;
    public short gamesWon;
    public short gamesDrawn;
    public short gamesLost;
    public short goalsFor;
    public short goalsAgainst;

    public TablePosition() {
    }

    public TablePosition(String clubFullName, int clubId) {
        this.clubFullName = clubFullName;
        this.clubId = clubId;
    }

    public static TablePosition fromClub(Club club) {

        TablePosition tablePosition = new TablePosition();

        tablePosition.clubFullName = club.getFullName();
        tablePosition.clubId = club.getId();

        return tablePosition;
    }
}

