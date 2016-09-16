package org.tiwpr.szymie.tasks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.tiwpr.szymie.daos.LeagueDao;
import org.tiwpr.szymie.daos.SeasonCreationTaskDao;
import org.tiwpr.szymie.daos.SeasonDao;
import org.tiwpr.szymie.models.League;
import org.tiwpr.szymie.models.Season;
import org.tiwpr.szymie.models.Table;
import org.tiwpr.szymie.models.TablePosition;
import org.tiwpr.szymie.usecases.ClubLeagueSeasonUseCase;
import org.tiwpr.szymie.usecases.SeasonUseCase;
import org.tiwpr.szymie.usecases.TableUseCase;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class SeasonCreationAsyncTask {

    @Autowired
    private TableUseCase tableUseCase;
    @Autowired
    private SeasonDao seasonDao;
    @Autowired
    private LeagueDao leagueDao;
    @Autowired
    private SeasonCreationTaskDao seasonCreationTaskDao;
    @Autowired
    private ClubLeagueSeasonUseCase clubLeagueSeasonUseCase;

    @Async
    @Transactional
    public void createSeason(Season season, int seasonCreationTaskId) {

        delay(40000);

        season.setStatus("in progress");
        int seasonId = seasonDao.save(season);

        List<League> leagues = getLeaguesSortedByLevel();
        Season lastSeason = seasonDao.findLastCompletedSeason().orElseThrow(RuntimeException::new);

        leagues.stream().forEach(league -> {

            Table table = tableUseCase.getTableForLeagueAtSeason(league, league.getId(), lastSeason.getId());

            table.getStandings().stream().forEach(tablePosition -> {

                boolean promotion = isAtPosition(league.getPromotionPositions(), tablePosition);

                if(promotion) {
                    League higherLeague = getHigherLeague(leagues, league.getLevel());
                    bindClubWithLeagueAtSeason(tablePosition, higherLeague, seasonId);
                } else {

                    boolean relegation = isAtPosition(league.getRelegationPositions(), tablePosition);

                    if(relegation) {
                        League lowerLeague = getLowerLeague(leagues, league.getLevel());
                        bindClubWithLeagueAtSeason(tablePosition, lowerLeague, seasonId);
                    } else {
                        bindClubWithLeagueAtSeason(tablePosition, league, seasonId);
                    }
                }
            });
        });

        seasonCreationTaskDao.updateSeasonId(seasonCreationTaskId, seasonId);
        seasonCreationTaskDao.updateStatusToSuccess(seasonCreationTaskId);
    }

    private void delay(long milliseconds) {

        try {
            Thread.sleep(milliseconds);
            System.err.println("delay exit");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private boolean isAtPosition(int[] promotionPositions, TablePosition tablePosition) {
        return IntStream.of(promotionPositions)
                .anyMatch(promotionPosition -> promotionPosition == tablePosition.rank);
    }

    private void bindClubWithLeagueAtSeason(TablePosition tablePosition, League league, int seasonId) {
        clubLeagueSeasonUseCase.bindClubWithLeagueAtSeason(tablePosition.clubId, league.getId(), seasonId).ifPresent(error -> {
            throw new RuntimeException(error.getMessage());
        });
    }

    private List<League> getLeaguesSortedByLevel() {
        return leagueDao.findAll().stream()
                .sorted((l1, l2) -> l1.getLevel() - l2.getLevel())
                .collect(Collectors.toList());
    }

    private League getHigherLeague(List<League> sortedLeagues, int level) {
        return findFirstLeague(sortedLeagues, league -> league.getLevel() == level - 1)
                .orElseThrow(() -> new RuntimeException("SeasonCreationTaskUseCase::getHigherLeague : league not found"));
    }

    private Optional<League> findFirstLeague(List<League> sortedLeagues, Predicate<League> predicate) {
        return sortedLeagues.stream().filter(predicate).findFirst();
    }

    private League getLowerLeague(List<League> sortedLeagues, int level) {
        return findFirstLeague(sortedLeagues, league -> league.getLevel() == level + 1)
                .orElseThrow(() -> new RuntimeException("SeasonCreationTaskUseCase::getLowerLeague : league not found"));
    }
}
