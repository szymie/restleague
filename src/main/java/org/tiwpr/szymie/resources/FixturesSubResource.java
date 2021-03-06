package org.tiwpr.szymie.resources;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.tiwpr.szymie.daos.FixtureDao;
import org.tiwpr.szymie.entities.FixtureEntity;
import org.tiwpr.szymie.models.Fixture;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import org.tiwpr.szymie.models.Error;
import org.tiwpr.szymie.models.ModelWithLinks;
import org.tiwpr.szymie.usecases.FixtureUseCase;
import static javax.ws.rs.core.Response.ResponseBuilder;

@Component
@Path("/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FixturesSubResource extends BaseResource {

    @Autowired
    private FixtureDao fixtureDao;
    @Autowired
    private FixtureUseCase fixtureUseCase;

    @GET
    @Transactional
    public ModelWithLinks<List<Fixture>> getFixtures(
            @Context UriInfo uriInfo,
            @PathParam("seasonId") int seasonId,
            @PathParam("leagueId") int leagueId,
            @BeanParam PaginationFilter paginationFilter) {

        List<Fixture> clubs = fixtureDao.findByLeagueIdAndSeasonId(leagueId, seasonId, paginationFilter.getOffset(), paginationFilter.getLimit());

        int numberOfAllEntities = fixtureDao.findByLeagueIdAndSeasonId(leagueId, seasonId).size();

        ModelWithLinks<List<Fixture>> modelWithLinks = new ModelWithLinks<>();
        fillModelWithLinks(modelWithLinks, clubs, numberOfAllEntities, uriInfo, paginationFilter);

        return modelWithLinks;
    }

    @GET
    @Path("{fixtureId}")
    @Transactional
    public Response getFixture(
            @PathParam("fixtureId") int id,
            @Context UriInfo uriInfo) {

        Optional<FixtureEntity> fixtureEntityOptional = fixtureDao.findById(id);
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

    @PUT
    @Path("/{fixtureId}")
    @Transactional
    public Response putFixture(
            @HeaderParam(HttpHeaders.IF_UNMODIFIED_SINCE) Timestamp lastModified,
            @PathParam("seasonId") int seasonId,
            @PathParam("leagueId") int leagueId,
            @PathParam("fixtureId") int fixtureId,
            @Valid Fixture fixture) {

        Optional<FixtureEntity> fixtureEntityOptional = fixtureDao.findById(fixtureId);

        if(fixtureEntityOptional.isPresent()) {

            fixture.setId(fixtureId);
            FixtureEntity fixtureEntity = fixtureEntityOptional.get();
            Timestamp fixtureLastModified = fixtureEntity.getLastModified();

            Optional<ResponseBuilder> preconditionsResponseBuilderOptional = evaluatePreconditions(lastModified, fixtureLastModified);

            if(!preconditionsResponseBuilderOptional.isPresent()) {

                Optional<Error> errorOptional = fixtureUseCase.updateFixtureFromLeagueAtSeason(fixture, leagueId, seasonId);

                if(!errorOptional.isPresent()) {
                    return Response.ok().build();
                } else {
                    Error error = errorOptional.get();
                    return Response.status(Response.Status.CONFLICT).entity(error).build();
                }
            } else {
                return preconditionsResponseBuilderOptional.get().build();
            }
        } else {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new Error("Fixture has not been found")).build();
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
