package org.tiwpr.szymie.resources;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.tiwpr.szymie.daos.FixturesDao;
import org.tiwpr.szymie.entities.FixtureEntity;
import org.tiwpr.szymie.models.Fixture;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import org.tiwpr.szymie.models.Error;
import org.tiwpr.szymie.usecases.FixtureUseCase;
import static javax.ws.rs.core.Response.ResponseBuilder;

@Component
@Path("/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FixturesSubResource extends BaseResource {

    @Autowired
    private FixturesDao fixturesDao;
    @Autowired
    private FixtureUseCase fixtureUseCase;

    @GET
    @Transactional
    public List<Fixture> getFixtures(@BeanParam PaginationFilter paginationFilter) {
        return fixturesDao.findAll(paginationFilter.getOffset(), paginationFilter.getLimit());
    }

    @GET
    @Path("{fixtureId}")
    @Transactional
    public Response getFixture(
            @PathParam("fixtureId") int id,
            @Context UriInfo uriInfo) {

        Optional<FixtureEntity> fixtureEntityOptional = fixturesDao.findById(id);
        Optional<Fixture> fixtureOptional = fixtureEntityOptional.map(FixtureEntity::toModel);

        ResponseBuilder responseBuilder = fixtureOptional
                .map(Response::ok)
                .orElse(notFoundResponseBuilder(new Error("Fixture has not been found")));

        fixtureEntityOptional.ifPresent(fixtureEntity -> responseBuilder.header(HttpHeaders.LAST_MODIFIED, fixtureEntity.getLastModified().toString()));

        return responseBuilder.build();
    }

    @POST
    @Transactional
    public Response postFixture(
            @MatrixParam("poe") String poeKey,
            @PathParam("seasonId") int seasonId,
            @PathParam("leagueId") int leagueId,
            @Context UriInfo uriInfo,
            @Valid Fixture fixture) {

        if(poeKey == null) {
            return responseWithNewPoeKey(uriInfo);
        } else {

            if(poeKeyDao.isValid(poeKey)) {
                poeKeyDao.invalidate(poeKey);

                SaveResult saveResult = fixtureUseCase.addFixtureToLeagueAtSeason(fixture, leagueId, seasonId);

                Optional<Integer> entityIdOptional = saveResult.getEntityId();

                if(entityIdOptional.isPresent()) {
                    Integer entityId = entityIdOptional.get();
                    URI fixtureLocation = uriInfo.getBaseUriBuilder().path(FixturesSubResource.class).path(Integer.toString(entityId)).build();
                    return Response.created(fixtureLocation).build();
                } else {
                    Error error = saveResult.getError().orElse(new Error("Unknown error"));
                    return Response.status(Response.Status.CONFLICT).entity(error).build();
                }
            } else {
                return Response.status(Response.Status.METHOD_NOT_ALLOWED).build();
            }
        }
    }

    @DELETE
    @Path("{fixtureId}")
    @Transactional
    public Response deletePlayer(
            @PathParam("seasonId") int seasonId,
            @PathParam("leagueId") int leagueId,
            @PathParam("fixtureId") int fixtureId) {

        Optional<Error> errorOptional = fixtureUseCase.removeFixtureFromLeagueAtSeason(fixtureId, leagueId, seasonId);

        if(errorOptional.isPresent()) {
            Error error = errorOptional.get();
            return Response.status(Response.Status.CONFLICT).entity(error).build();
        } else {
            return Response.noContent().build();
        }
    }
}
