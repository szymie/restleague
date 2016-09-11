package org.tiwpr.szymie.usecases;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tiwpr.szymie.daos.ClubDao;
import org.tiwpr.szymie.daos.ClubLeagueSeasonEntryDao;
import org.tiwpr.szymie.daos.LeagueDao;
import org.tiwpr.szymie.daos.SeasonDao;
import org.tiwpr.szymie.entities.ClubLeagueSeasonEntryEntity;
import org.tiwpr.szymie.entities.ClubLeagueSeasonEntryId;
import org.tiwpr.szymie.entities.LeagueEntity;
import org.tiwpr.szymie.models.Error;

import java.util.Optional;

@Component
public class ClubLeagueSeasonUseCase {

    @Autowired
    private ClubLeagueSeasonEntryDao clubLeagueSeasonEntryDao;
    @Autowired
    private ClubDao clubDao;
    @Autowired
    private LeagueDao leagueDao;
    @Autowired
    private SeasonDao seasonDao;

    public Optional<Error> bindClubWithLeagueAtSeason(int clubId, int leagueId, int seasonId) {

        Optional<Error> error = validateEntitiesExistence(seasonId, leagueId, clubId);

        if(error.isPresent()) {
            return error;
        }

        if(isLeagueFullAtSeason(leagueId, seasonId)) {
            return Optional.of(new Error("Requested league is full at requested season"));
        }

        if(isClubBoundWithAnyLeagueAtSeason(clubId, seasonId)) {
            return Optional.of(new Error("Requested club is already bound to some league at requested season"));
        }

        ClubLeagueSeasonEntryEntity entryEntity = prepareEntryEntity(clubId, leagueId, seasonId);

        clubLeagueSeasonEntryDao.save(entryEntity);

        return Optional.empty();
    }

    private Optional<Error> validateEntitiesExistence(int seasonId, int leagueId, int clubId) {

        if(!seasonDao.findById(seasonId).isPresent()) {
            return Optional.of(new Error("Season has not been found"));
        }

        if(!leagueDao.findById(leagueId).isPresent()) {
            return Optional.of(new Error("League has not been found"));
        }

        if(!clubDao.findById(clubId).isPresent()) {
            return Optional.of(new Error("Club has not been found"));
        }

        return Optional.empty();
    }

    public boolean isLeagueFullAtSeason(int leagueId, int seasonId) {

        int currentLeagueSize = clubLeagueSeasonEntryDao.findBySeasonIdAndLeagueId(seasonId, leagueId).size();

        Optional<LeagueEntity> leagueEntityOptional = leagueDao.findById(leagueId);

        if(leagueEntityOptional.isPresent()) {

            LeagueEntity leagueEntity = leagueEntityOptional.get();

            short maxLeagueSize = leagueEntity.getNumberOfTeams();

            return currentLeagueSize >= maxLeagueSize;
        } else {
            return true;
        }
    }

    public boolean isClubBoundWithAnyLeagueAtSeason(int clubId, int seasonId) {
        return !clubLeagueSeasonEntryDao.findByClubIdAndSeasonId(clubId, seasonId).isEmpty();
    }

    private ClubLeagueSeasonEntryEntity prepareEntryEntity(int clubId, int leagueId, int seasonId) {

        ClubLeagueSeasonEntryId entryId = new ClubLeagueSeasonEntryId();
        entryId.setClubId(clubId);
        entryId.setLeagueId(leagueId);
        entryId.setSeasonId(seasonId);

        ClubLeagueSeasonEntryEntity entryEntity = new ClubLeagueSeasonEntryEntity(entryId);

        clubDao.findById(clubId).ifPresent(entryEntity::setClub);
        leagueDao.findById(leagueId).ifPresent(entryEntity::setLeague);
        seasonDao.findById(seasonId).ifPresent(entryEntity::setSeason);

        return entryEntity;
    }

    public boolean isClubBoundWithLeagueAtSeason(int clubId, int leagueId, int seasonId) {
        return clubLeagueSeasonEntryDao.findBySeasonIdAndLeagueId(seasonId, leagueId)
                .stream()
                .anyMatch(club -> club.getId() == clubId);
    }

    public Optional<Error> unbindClubWithLeagueAtSeason(int clubId, int leagueId, int seasonId) {

        Optional<Error> errorOptional = validateEntitiesExistence(seasonId, leagueId, clubId);

        if(errorOptional.isPresent()) {
            return errorOptional;
        }

        //TODO - są już jakieś rozpoczęte mecze w tym sezonie w tej lidze

        clubLeagueSeasonEntryDao.deleteByClubIdAndLeagueIdAndSeasonId(clubId, leagueId, seasonId);

        return Optional.empty();
    }
}
