package org.tiwpr.szymie.resources;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.tiwpr.szymie.daos.SeasonCreationTaskDao;
import org.tiwpr.szymie.entities.SeasonCreationTaskEntity;
import org.tiwpr.szymie.models.Season;
import org.tiwpr.szymie.models.SeasonCreationTask;
import org.tiwpr.szymie.usecases.SeasonCreationTaskUseCase;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.Optional;
import org.tiwpr.szymie.models.Error;
import static javax.ws.rs.core.Response.ResponseBuilder;

@Component
@Path("/season-creation-tasks")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SeasonCreationTasksResource extends BaseResource {

    @Autowired
    private SeasonCreationTaskUseCase seasonCreationTaskUseCase;
    @Autowired
    private SeasonCreationTaskDao seasonCreationTaskDao;

    @POST
    @Transactional
    public Response post(@Context UriInfo uriInfo, @Valid Season season) {

        SaveResult saveResult = seasonCreationTaskUseCase.addSeasonCreationTask(season);

        Optional<Integer> entityIdOptional = saveResult.getEntityId();

        if(entityIdOptional.isPresent()) {
            Integer entityId = entityIdOptional.get();
            URI seasonCreationTaskLocation = uriInfo.getBaseUriBuilder().path(SeasonCreationTasksResource.class).path(Integer.toString(entityId)).build();
            return Response.created(seasonCreationTaskLocation).build();
        } else {
            Error error = saveResult.getError().orElse(new Error("Unknown error"));
            return Response.status(Response.Status.CONFLICT).entity(error).build();
        }
    }

    @GET
    @Path("/{seasonCreationTasksId}")
    @Transactional
    public Response get(@PathParam("seasonCreationTasksId") int id) {

        Optional<SeasonCreationTaskEntity> seasonCreationTaskEntityOptional = seasonCreationTaskDao.findById(id);
        Optional<SeasonCreationTask> seasonCreationTaskOptional = seasonCreationTaskEntityOptional.map(SeasonCreationTaskEntity::toModel);

        ResponseBuilder responseBuilder = seasonCreationTaskOptional
                .map(Response::ok)
                .orElse(notFoundResponseBuilder(new Error("Season creation task has not been found")));

        seasonCreationTaskEntityOptional.ifPresent(playerEntity -> responseBuilder.header(HttpHeaders.LAST_MODIFIED, playerEntity.getLastModified().toString()));

        return responseBuilder.build();
    }
}
