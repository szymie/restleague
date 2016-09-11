package org.tiwpr.szymie.entities;

import org.tiwpr.szymie.models.Result;
import javax.persistence.*;

@Entity
@Table(name = "results")
public class ResultEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "goals_home_club")
    private short goalsHomeClub;
    @Column(name = "goals_away_club")
    private short goalsAwayClub;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public short getGoalsHomeClub() {
        return goalsHomeClub;
    }

    public void setGoalsHomeClub(short goalsHomeClub) {
        this.goalsHomeClub = goalsHomeClub;
    }

    public short getGoalsAwayClub() {
        return goalsAwayClub;
    }

    public void setGoalsAwayClub(short goalsAwayClub) {
        this.goalsAwayClub = goalsAwayClub;
    }

    @Override
    public Result toModel() {

        Result result = new Result();

        result.setGoalsHomeClub(goalsHomeClub);
        result.setGoalsAwayClub(goalsAwayClub);

        return result;
    }

    public static ResultEntity fromModel(Result result) {

        ResultEntity resultEntity = new ResultEntity();

        resultEntity.setGoalsHomeClub(result.getGoalsHomeClub());
        resultEntity.setGoalsAwayClub(result.getGoalsAwayClub());

        return resultEntity;
    }
}
