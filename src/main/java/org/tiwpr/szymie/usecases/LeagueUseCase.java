package org.tiwpr.szymie.usecases;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tiwpr.szymie.daos.LeagueDao;
import org.tiwpr.szymie.models.League;
import java.util.List;

@Component
public class LeagueUseCase {

    @Autowired
    private LeagueDao leagueDao;

    public void updateLeagues(List<League> leagues) {
        leagueDao.deleteAll();
        leagueDao.save(leagues);
    }



}
