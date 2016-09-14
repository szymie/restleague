package org.tiwpr.szymie.usecases;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tiwpr.szymie.daos.LeagueDao;
import org.tiwpr.szymie.daos.SeasonDao;

@Component
public class SeasonUseCase {

    @Autowired
    private SeasonDao seasonDao;
    @Autowired
    private LeagueDao leagueDao;
    @Autowired
    private LeagueUseCase leagueUseCase;

    public boolean isThereAnySeason() {
        return !seasonDao.findAll(0, 1).isEmpty();
    }

    public boolean isSeasonCompleted(int seasonId) {

        return seasonDao.findById(seasonId)
                .filter(seasonEntity -> seasonEntity.getStatus().equals("completed"))
                .isPresent();
    }

    public boolean areAllLeaguesFinishedAtSeason(int seasonId) {

        return leagueDao.findAll().stream()
                .allMatch(league -> leagueUseCase.isLeagueFinishedAtSeason(league.getId(), seasonId));
    }

    public boolean isLastSeasonCompleted() {
        return seasonDao.findLastSeason().filter(season -> season.getStatus().equals("completed")).isPresent();
    }
}
