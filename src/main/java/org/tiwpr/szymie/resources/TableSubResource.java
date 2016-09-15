package org.tiwpr.szymie.resources;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.tiwpr.szymie.daos.ClubDao;
import org.tiwpr.szymie.daos.FixtureDao;
import org.tiwpr.szymie.daos.LeagueDao;
import org.tiwpr.szymie.daos.SeasonDao;
import org.tiwpr.szymie.entities.LeagueEntity;
import org.tiwpr.szymie.entities.SeasonEntity;
import org.tiwpr.szymie.models.*;
import org.tiwpr.szymie.models.Error;
import org.tiwpr.szymie.usecases.TableUseCase;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TableSubResource extends BaseResource {

    @Autowired
    private SeasonDao seasonDao;
    @Autowired
    private LeagueDao leagueDao;

    @Autowired
    private TableUseCase tableUseCase;

    @GET
    @Transactional
    public Response getTable(@PathParam("seasonId") int seasonId, @PathParam("leagueId") int leagueId) {

        Optional<SeasonEntity> seasonEntityOptional = seasonDao.findById(seasonId);
        Optional<League> leagueOptional = leagueDao.findById(leagueId).map(LeagueEntity::toModel);

        if(!seasonEntityOptional.isPresent()) {
            return notFoundResponseBuilder(new Error("Season has not been found")).build();
        }

        if(!leagueOptional.isPresent()) {
            return notFoundResponseBuilder(new Error("League has not been found")).build();
        }

        Table table = tableUseCase.getTableForLeagueAtSeason(leagueOptional.get(), leagueId, seasonId);

        return Response.ok().entity(table).build();
    }
}