package org.tiwpr.szymie.models;

import java.util.List;

public class Table {

    private String leagueFullName;
    private int matchDay;
    private List<TablePosition> standings;

    public Table() {
    }

    public Table(String leagueFullName, int matchDay, List<TablePosition> standings) {
        this.leagueFullName = leagueFullName;
        this.matchDay = matchDay;
        this.standings = standings;
    }

    public String getLeagueFullName() {
        return leagueFullName;
    }

    public void setLeagueFullName(String leagueFullName) {
        this.leagueFullName = leagueFullName;
    }

    public int getMatchDay() {
        return matchDay;
    }

    public void setMatchDay(int matchDay) {
        this.matchDay = matchDay;
    }

    public List<TablePosition> getStandings() {
        return standings;
    }

    public void setStandings(List<TablePosition> standings) {
        this.standings = standings;
    }
}
