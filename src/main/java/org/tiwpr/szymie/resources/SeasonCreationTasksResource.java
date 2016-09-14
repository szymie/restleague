package org.tiwpr.szymie.resources;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.tiwpr.szymie.models.Season;
import org.tiwpr.szymie.usecases.SeasonCreationTaskUseCase;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.Optional;
import org.tiwpr.szymie.models.Error;

@Component
@Path("/season-creation-tasks")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SeasonCreationTasksResource {

    @Autowired
    private SeasonCreationTaskUseCase seasonCreationTaskUseCase;

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

}
