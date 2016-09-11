package org.tiwpr.szymie.entities;

import org.tiwpr.szymie.models.League;
import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "leagues")
public class LeagueEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "full_name")
    private String fullName;
    private short level;
    @Column(name = "number_of_teams")
    private short numberOfTeams;
    @OneToMany(mappedBy = "league", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Collection<LeaguePositionEntity> positions = new ArrayList<>();
    @Column(name = "last_modified")
    private Timestamp lastModified;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public short getLevel() {
        return level;
    }

    public void setLevel(short level) {
        this.level = level;
    }

    public short getNumberOfTeams() {
        return numberOfTeams;
    }

    public void setNumberOfTeams(short numberOfTeams) {
        this.numberOfTeams = numberOfTeams;
    }

    public Collection<LeaguePositionEntity> getPositions() {
        return positions;
    }

    public void setPositions(Collection<LeaguePositionEntity> positions) {
        this.positions = positions;
    }

    public void addPosition(LeaguePositionEntity leaguePosition) {
        positions.add(leaguePosition);
        leaguePosition.setLeague(this);
    }

    public void addPositions(List<LeaguePositionEntity> leaguePositions) {
        positions.addAll(leaguePositions);
        leaguePositions.forEach(leaguePositionEntity -> leaguePositionEntity.setLeague(this));
    }

    public Timestamp getLastModified() {
        return lastModified;
    }

    public void setLastModified(Timestamp lastModified) {
        this.lastModified = lastModified;
    }

    @Override
    public League toModel() {

        League league = new League();

        league.setId(id);
        league.setFullName(fullName);
        league.setLevel(level);

        int[] promotionPositions = extractPositions("promotion");
        int[] relegationPositions = extractPositions("relegation");

        league.setPromotionPositions(promotionPositions);
        league.setRelegationPositions(relegationPositions);

        league.setNumberOfTeams(numberOfTeams);

        return league;
    }

    private int[] extractPositions(String meaning) {
        return positions.stream()
                .filter(leaguePositionEntity -> leaguePositionEntity.getMeaning().equals(meaning))
                .mapToInt(LeaguePositionEntity::getNumber)
                .toArray();
    }

    public static LeagueEntity fromModel(League league) {

        LeagueEntity leagueEntity = new LeagueEntity();

        leagueEntity.setId(league.getId());
        leagueEntity.setFullName(league.getFullName());
        leagueEntity.setLevel(league.getLevel());
        leagueEntity.setNumberOfTeams(league.getNumberOfTeams());

        leagueEntity.addPositions(mapPositionsArrayToList(league.getPromotionPositions(), "promotion"));
        leagueEntity.addPositions(mapPositionsArrayToList(league.getRelegationPositions(), "relegation"));

        return leagueEntity;
    }

    private static List<LeaguePositionEntity> mapPositionsArrayToList(int[] positions, String meaning) {

        if(positions != null) {
            return Arrays.stream(positions)
                    .mapToObj(position -> new LeaguePositionEntity(position, meaning))
                    .collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }
}
