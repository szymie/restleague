package org.tiwpr.szymie.resources;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.tiwpr.szymie.daos.SeasonDao;
import org.tiwpr.szymie.entities.SeasonEntity;
import org.tiwpr.szymie.models.Error;
import org.tiwpr.szymie.models.ModelWithLinks;
import org.tiwpr.szymie.models.Season;
import org.tiwpr.szymie.usecases.LeagueUseCase;
import org.tiwpr.szymie.usecases.SeasonUseCase;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import static javax.ws.rs.core.Response.ResponseBuilder;

@Component
@Path("seasons")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SeasonsResource extends BaseResource {

    @Autowired
    private SeasonDao seasonDao;
    @Autowired
    private LeaguesClubsSubResource leaguesClubsSubResource;
    @Autowired
    private FixturesSubResource fixturesSubResource;
    @Autowired
    private TableSubResource tableSubResource;
    @Autowired
    private SeasonUseCase seasonUseCase;

    @GET
    @Transactional
    public ModelWithLinks<List<Season>> getSeasons(@Context UriInfo uriInfo, @BeanParam PaginationFilter paginationFilter) {

        List<Season> seasons = seasonDao.findAll();
        List<Season> subSeasons = subList(seasons, paginationFilter);

        ModelWithLinks<List<Season>> modelWithLinks = new ModelWithLinks<>();
        fillModelWithLinks(modelWithLinks, subSeasons, seasons.size(), uriInfo, paginationFilter);

        return modelWithLinks;
    }

    @GET
    @Path("/{seasonId}")
    @Transactional
    public Response getSeason(@PathParam("seasonId") int id) {

        Optional<SeasonEntity> seasonEntityOptional = seasonDao.findById(id);
        Optional<Season> seasonOptional = seasonEntityOptional.map(SeasonEntity::toModel);

        ResponseBuilder responseBuilder = seasonOptional
                .map(Response::ok)
                .orElse(notFoundResponseBuilder(new Error("Season has not been found")));

        seasonEntityOptional.ifPresent(seasonEntity -> responseBuilder.header(HttpHeaders.LAST_MODIFIED, seasonEntity.getLastModified().toString()));

        return responseBuilder.build();
    }

    @POST
    @Transactional
    public Response postSeason(@MatrixParam("poe") String poeKey, @Context UriInfo uriInfo, @Valid Season season) {

        if(poeKey == null) {
            return responseWithNewPoeKey(uriInfo);
        } else {

            if(poeKeyDao.isValid(poeKey)) {

                poeKeyDao.invalidate(poeKey);

                season.setStatus("in progress");
                int seasonId = seasonDao.save(season);

                URI seasonLocation = uriInfo.getBaseUriBuilder().path(SeasonsResource.class).path(Integer.toString(seasonId)).build();
                return Response.created(seasonLocation).build();
            } else {
                return Response.status(Response.Status.METHOD_NOT_ALLOWED).build();
            }
        }
    }

    @PUT
    @Path("/{seasonId}")
    @Transactional
    public Response putSeason(
            @HeaderParam(HttpHeaders.IF_UNMODIFIED_SINCE) Timestamp lastModified,
            @PathParam("seasonId") int id,
            @Valid Season season) {

        Optional<SeasonEntity> seasonEntityOptional = seasonDao.findById(id);

        if(seasonEntityOptional.isPresent()) {

            season.setId(id);
            SeasonEntity seasonEntity = seasonEntityOptional.get();
            Timestamp seasonLastModified = seasonEntity.getLastModified();

            Optional<ResponseBuilder> preconditionsResponseBuilderOptional = evaluatePreconditions(lastModified, seasonLastModified);

            if(!preconditionsResponseBuilderOptional.isPresent()) {
                return updateSeason(season);
            } else {
                return preconditionsResponseBuilderOptional.get().build();
            }
        } else {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new Error("Season has not been found")).build();
        }
    }

    private Response updateSeason(Season season) {

        if(season.getStatus().equals("completed")) {
            if(!seasonUseCase.areAllLeaguesFinishedAtSeason(season.getId())) {
                return Response
                        .status(Response.Status.CONFLICT)
                        .entity(new Error("Season cannot be marked as completed because there are fixture left to be played"))
                        .build();
            }
        }

        seasonDao.update(season);

        return Response.ok().build();
    }

    @DELETE
    @Path("/{seasonId}")
    @Transactional
    public Response deleteSeason(@PathParam("seasonId") int id) {
        seasonDao.delete(id);
        return Response.noContent().build();
    }

    @GET
    @Path("/{seasonId}/leagues")
    @Transactional
    public Response redirectToLeagues(@Context UriInfo uriInfo) {
        URI leagueUri = uriInfo.getBaseUriBuilder().path(LeaguesResource.class).build();
        return Response.status(Response.Status.TEMPORARY_REDIRECT).location(leagueUri).build();
    }

    @GET
    @Path("/{seasonId}/leagues/{leagueId}")
    @Transactional
    public Response redirectToLeague(@Context UriInfo uriInfo, @PathParam("leagueId") int id) {
        URI leagueUri = uriInfo.getBaseUriBuilder().path(LeaguesResource.class).path(Integer.toString(id)).build();
        return Response.status(Response.Status.TEMPORARY_REDIRECT).location(leagueUri).build();
    }

    @Path("/{seasonId}/leagues/{leagueId}/clubs")
    public LeaguesClubsSubResource leagueClubs() {
        return leaguesClubsSubResource;
    }

    @Path("/{seasonId}/leagues/{leagueId}/fixtures")
    public FixturesSubResource leagueFixtures() {
        return fixturesSubResource;
    }

    @Path("/{seasonId}/leagues/{leagueId}/table")
    public TableSubResource leagueTable() {
        return tableSubResource;
    }
}
