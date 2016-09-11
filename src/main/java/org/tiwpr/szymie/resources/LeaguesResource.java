package org.tiwpr.szymie.resources;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.tiwpr.szymie.daos.LeagueDao;
import org.tiwpr.szymie.entities.LeagueEntity;
import org.tiwpr.szymie.models.Error;
import org.tiwpr.szymie.models.League;
import org.tiwpr.szymie.usecases.LeagueUseCase;
import org.tiwpr.szymie.usecases.SeasonUseCase;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;
import java.util.Optional;
import static javax.ws.rs.core.Response.ResponseBuilder;

@Component
@Path("leagues")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class LeaguesResource extends BaseResource {

    @Autowired
    private SeasonUseCase seasonUseCase;
    @Autowired
    private LeagueUseCase leaguesUseCase;
    @Autowired
    private LeagueDao leagueDao;

    @GET
    @Transactional
    public List<League> getLeagues() {
        return leagueDao.findAll();
    }

    @GET
    @Path("{leagueId}")
    @Transactional
    public Response getLeague(@PathParam("leagueId") int id) {

        Optional<LeagueEntity> leagueEntityOptional =  leagueDao.findById(id);
        Optional<League> leagueOptional = leagueEntityOptional.map(LeagueEntity::toModel);

        ResponseBuilder responseBuilder = leagueOptional
                .map(Response::ok)
                .orElse(notFoundResponseBuilder(new Error("League has not been found")));

        leagueEntityOptional.ifPresent(leagueEntity -> responseBuilder.header(HttpHeaders.LAST_MODIFIED, leagueEntity.getLastModified().toString()));

        return responseBuilder.build();
    }

    @PUT
    @Transactional
    public Response putLeagues(@Context UriInfo uriInfo, @Valid List<League> leagues) {

        fillWithLevelIndexes(leagues);

        Optional<Error> errorOptional = validateLeagues(leagues);

        if(errorOptional.isPresent()) {
            Error error = errorOptional.get();
            return Response.status(Response.Status.FORBIDDEN).entity(error).build();
        } else {
            leaguesUseCase.updateLeagues(leagues);
            return Response.created(uriInfo.getAbsolutePath()).build();
        }
    }

    private void fillWithLevelIndexes(List<League> leagues) {

        short levelIndex = 1;

        for(League league : leagues) {
            league.setLevel(levelIndex++);
        }
    }

    private Optional<Error> validateLeagues(List<League> leagues) {

        if(seasonUseCase.isThereAnySeason()) {
            return Optional.of(new Error("It is not possible to modify leagues because there are started seasons"));
        }

        int leagueSize = leagues.size();

        if(leagues.stream().allMatch(league -> validateLeague(league, leagueSize))) {
            return Optional.empty();
        } else {
            return Optional.of(new Error("Relegation and/or promotion positions are not specified correctly"));
        }
    }

    private boolean validateLeague(League league, int leagueSize) {

        int level = league.getLevel();

        if(level == 1) {
            return isIntArrayNotEmpty(league.getRelegationPositions());
        } else if(level == leagueSize) {
            return isIntArrayNotEmpty(league.getPromotionPositions());
        } else {
            return isIntArrayNotEmpty(league.getRelegationPositions()) && isIntArrayNotEmpty(league.getPromotionPositions());
        }
    }

    private boolean isIntArrayNotEmpty(int[] array) {
        return array.length != 0;
    }

    @DELETE
    @Transactional
    public Response deleteLeagues() {

        if(!seasonUseCase.isThereAnySeason()) {
            leagueDao.deleteAll();
            return Response.noContent().build();
        } else {
            return Response.status(Response.Status.CONFLICT).entity(new Error("It is not possible to modify leagues because there are started seasons")).build();
        }
    }
}
