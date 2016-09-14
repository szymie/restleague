package org.tiwpr.szymie.usecases;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tiwpr.szymie.daos.ClubDao;
import org.tiwpr.szymie.daos.FixtureDao;
import org.tiwpr.szymie.daos.LeagueDao;
import org.tiwpr.szymie.daos.SeasonDao;
import org.tiwpr.szymie.entities.FixtureEntity;
import org.tiwpr.szymie.models.Error;
import org.tiwpr.szymie.models.Fixture;
import org.tiwpr.szymie.resources.SaveResult;

import java.util.Optional;

@Component
public class FixtureUseCase {

    @Autowired
    private ClubDao clubDao;
    @Autowired
    private LeagueDao leagueDao;
    @Autowired
    private SeasonDao seasonDao;
    @Autowired
    private SeasonUseCase seasonUseCase;
    @Autowired
    private ClubLeagueSeasonUseCase clubLeagueSeasonUseCase;
    @Autowired
    private FixtureDao fixtureDao;

    public SaveResult addFixtureToLeagueAtSeason(Fixture fixture, int leagueId, int seasonId) {

        Optional<Error> errorOptional = validateAddFixture(fixture, leagueId, seasonId);

        if(errorOptional.isPresent()) {
            return new SaveResult(errorOptional.get());
        }

        int fixtureId = fixtureDao.save(fixture, seasonId, leagueId);

        return new SaveResult(fixtureId);
    }

    private Optional<Error> validateAddFixture(Fixture fixture, int leagueId, int seasonId) {

        Optional<Error> errorOptional = validateAddEntitiesExistence(fixture, leagueId, seasonId);

        if(errorOptional.isPresent()) {
            return errorOptional;
        }

        errorOptional = validatePreliminaryAddFixtureConditions(fixture, leagueId, seasonId);

        if(errorOptional.isPresent()) {
            return errorOptional;
        }

        if(fixtureDao.findByClubsAndLeagueIdAndSeasonId(fixture, leagueId, seasonId).isPresent()) {
            return Optional.of(new Error("This fixture has already been played at requested league on requested season"));
        }

        return Optional.empty();
    }

    private Optional<Error> validatePreliminaryAddFixtureConditions(Fixture fixture, int leagueId, int seasonId) {

        if(seasonUseCase.isSeasonCompleted(seasonId)) {
            return Optional.of(new Error("It is not possible to add fixture to completed season"));
        }

        if(!areClubsBoundWithLeagueAtSeason(fixture, leagueId, seasonId)) {
            return Optional.of(new Error("Requested clubs are not from requested league at requested season"));
        }

        if(fixture.getHomeClubId() == fixture.getAwayClubId()) {
            return Optional.of(new Error("Home and away club cannot be the same"));
        }

        return Optional.empty();
    }

    private Optional<Error> validateAddEntitiesExistence(Fixture fixture, int leagueId, int seasonId) {

        if(!seasonDao.findById(seasonId).isPresent()) {
            return Optional.of(new Error("Season has not been found"));
        }

        if(!leagueDao.findById(leagueId).isPresent()) {
            return Optional.of(new Error("League has not been found"));
        }

        if(!clubDao.findById(fixture.getHomeClubId()).isPresent()) {
            return Optional.of(new Error("Home club has not been found"));
        }

        if(!clubDao.findById(fixture.getAwayClubId()).isPresent()) {
            return Optional.of(new Error("Away club has not been found"));
        }

        return Optional.empty();
    }


    private boolean areClubsBoundWithLeagueAtSeason(Fixture fixture, int leagueId, int seasonId) {
        return clubLeagueSeasonUseCase.isClubBoundWithLeagueAtSeason(fixture.getHomeClubId(), leagueId, seasonId) &&
                clubLeagueSeasonUseCase.isClubBoundWithLeagueAtSeason(fixture.getAwayClubId(), leagueId, seasonId);
    }

    public Optional<Error> removeFixtureFromLeagueAtSeason(int fixtureId, int leagueId, int seasonId) {

        Optional<Error> errorOptional = validateRemoveFixture(fixtureId, leagueId, seasonId);

        if(errorOptional.isPresent()) {
            return errorOptional;
        }

        fixtureDao.delete(fixtureId);

        return Optional.empty();
    }

    private Optional<Error> validateRemoveFixture(int fixtureId, int leagueId, int seasonId) {

        Optional<Error> errorOptional = validateRemoveEntitiesExistence(fixtureId, leagueId, seasonId);

        if(errorOptional.isPresent()) {
            return errorOptional;
        }

        if(seasonUseCase.isSeasonCompleted(seasonId)) {
            return Optional.of(new Error("It is not possible to remove fixture from completed season"));
        }

        return Optional.empty();
    }

    private Optional<Error> validateRemoveEntitiesExistence(int fixtureId, int leagueId, int seasonId) {

        if(!seasonDao.findById(seasonId).isPresent()) {
            return Optional.of(new Error("Season has not been found"));
        }

        if(!leagueDao.findById(leagueId).isPresent()) {
            return Optional.of(new Error("League has not been found"));
        }

        return Optional.empty();
    }

    public Optional<Error> updateFixtureFromLeagueAtSeason(Fixture fixture, int leagueId, int seasonId) {

        Optional<Error> errorOptional = validateUpdateFixture(fixture, leagueId, seasonId);

        if(errorOptional.isPresent()) {
            return errorOptional;
        }

        fixtureDao.update(fixture);

        return Optional.empty();
    }

    private Optional<Error> validateUpdateFixture(Fixture fixture, int leagueId, int seasonId) {

        Optional<Error> errorOptional = validateAddEntitiesExistence(fixture, leagueId, seasonId);

        if(errorOptional.isPresent()) {
            return errorOptional;
        }

        errorOptional = validatePreliminaryAddFixtureConditions(fixture, leagueId, seasonId);

        if(errorOptional.isPresent()) {
            return errorOptional;
        }

        Optional<FixtureEntity> fixtureEntityToUpdateOptional = fixtureDao.findById(fixture.getId());

        if(fixtureEntityToUpdateOptional.isPresent()) {
            FixtureEntity fixtureEntityToUpdate = fixtureEntityToUpdateOptional.get();

            if(!areClubIdsAtFixturesEqual(fixture, fixtureEntityToUpdate.toModel())) {

                if(fixtureDao.findByClubsAndLeagueIdAndSeasonId(fixture, leagueId, seasonId).isPresent()) {
                    return Optional.of(new Error("This fixture has already been played at requested league on requested season"));
                }
            }
        }

        return Optional.empty();
    }

    private boolean areClubIdsAtFixturesEqual(Fixture leftFixture, Fixture rightFixture) {
        return leftFixture.getHomeClubId() == rightFixture.getHomeClubId() &&
                leftFixture.getAwayClubId() == rightFixture.getAwayClubId();
    }
}
