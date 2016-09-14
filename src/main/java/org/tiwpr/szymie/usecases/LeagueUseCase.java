package org.tiwpr.szymie.usecases;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tiwpr.szymie.daos.FixtureDao;
import org.tiwpr.szymie.daos.LeagueDao;
import org.tiwpr.szymie.entities.LeagueEntity;
import org.tiwpr.szymie.models.League;
import java.util.List;
import java.util.Optional;

@Component
public class LeagueUseCase {

    @Autowired
    private LeagueDao leagueDao;
    @Autowired
    protected FixtureDao fixtureDao;

    public void updateLeagues(List<League> leagues) {
        leagueDao.deleteAll();
        leagueDao.save(leagues);
    }

    public boolean isLeagueFinishedAtSeason(int leagueId, int seasonId) {

        Optional<LeagueEntity> leagueEntityOptional = leagueDao.findById(leagueId);

        if(leagueEntityOptional.isPresent()) {

            int leagueSize = leagueEntityOptional.get().getNumberOfTeams();
            int numberOfFixturesToPlay = getFixturesNumberFromLeagueSize(leagueSize);
            int numberOfPlayedFixtures = fixtureDao.findByLeagueIdAndSeasonId(leagueId, seasonId).size();

            return numberOfPlayedFixtures == numberOfFixturesToPlay;
        }

        return false;
    }

    private int getFixturesNumberFromLeagueSize(int leagueSize) {
        return leagueSize * leagueSize - leagueSize;
    }
}
