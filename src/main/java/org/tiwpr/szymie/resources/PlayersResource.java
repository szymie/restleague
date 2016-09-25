package org.tiwpr.szymie.resources;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.tiwpr.szymie.daos.PlayerDao;
import org.tiwpr.szymie.entities.PlayerEntity;
import org.tiwpr.szymie.models.Error;
import org.tiwpr.szymie.models.ModelWithLinks;
import org.tiwpr.szymie.models.Player;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.sql.Timestamp;
import java.util.*;

import static javax.ws.rs.core.Response.ResponseBuilder;
import org.tiwpr.szymie.models.Link;
import org.tiwpr.szymie.usecases.ClubPlayerUseCase;

@Component
@Path("players")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PlayersResource extends BaseResource {

    @Autowired
    private PlayerDao playerDao;
    @Autowired
    private ClubPlayerUseCase clubPlayerUseCase;

    @GET
    @Transactional
    public ModelWithLinks<List<Player>> getPlayers(@Context UriInfo uriInfo, @BeanParam PaginationFilter paginationFilter) {

        List<Player> players = playerDao.findAll(paginationFilter.getOffset(), paginationFilter.getLimit());

        ModelWithLinks<List<Player>> modelWithLinks = new ModelWithLinks<>();
        fillModelWithLinks(modelWithLinks, players, playerDao.countAll(), uriInfo, paginationFilter);

        return modelWithLinks;
    }

    @GET
    @Path("/{playerId}")
    @Transactional
    public Response getPlayer(@PathParam("playerId") int id) {

        Optional<PlayerEntity> playerEntityOptional = playerDao.findById(id);
        Optional<Player> playerOptional = playerEntityOptional.map(PlayerEntity::toModel);

        ResponseBuilder responseBuilder = playerOptional
                .map(Response::ok)
                .orElse(notFoundResponseBuilder(new Error("Player has not been found")));

        playerEntityOptional.ifPresent(playerEntity -> responseBuilder.header(HttpHeaders.LAST_MODIFIED, playerEntity.getLastModified().toString()));
        //playerOptional.ifPresent(player -> responseBuilder.tag(Integer.toString((player.getId()))));

        return responseBuilder.build();
    }

    @POST
    @Transactional
    public Response postPlayer(@MatrixParam("poe") String poeKey, @Context UriInfo uriInfo, @Valid Player player) {

        if(poeKey == null) {
            return responseWithNewPoeKey(uriInfo);
        } else {

            if(poeKeyDao.isValid(poeKey)) {
                poeKeyDao.invalidate(poeKey);
                int playerId = playerDao.save(player);
                URI playerLocation = uriInfo.getBaseUriBuilder().path(PlayersResource.class).path(Integer.toString(playerId)).build();
                return Response.created(playerLocation).build();
            } else {
                //TODO - UNAUTHORIZED
                return Response.status(Response.Status.METHOD_NOT_ALLOWED).build();
            }
        }
    }

    @PUT
    @Path("/{playerId}")
    @Transactional
    public Response putPLayer(
            @HeaderParam(HttpHeaders.IF_UNMODIFIED_SINCE) Timestamp lastModified,
            @PathParam("playerId") int id,
            @Valid Player player) {

        Optional<PlayerEntity> playerOptional = playerDao.findById(id);

        if(playerOptional.isPresent()) {

            player.setId(id);
            PlayerEntity playerEntity = playerOptional.get();
            Timestamp playerLastModified = playerEntity.getLastModified();

            Optional<ResponseBuilder> preconditionsResponseBuilderOptional = evaluatePreconditions(lastModified, playerLastModified);

            if(!preconditionsResponseBuilderOptional.isPresent()) {
                playerDao.update(player);
                return Response.ok().build();
            } else {
                return preconditionsResponseBuilderOptional.get().build();
            }
        } else {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new Error("Player has not been found")).build();
        }

        //wartość ETag zapisywana w DB
    }

    @DELETE
    @Path("/{playerId}")
    @Transactional
    public Response deletePlayer(@PathParam("playerId") int id) {

        if(clubPlayerUseCase.isPlayerFree(id)) {
            playerDao.delete(id);
            return Response.noContent().build();
        } else {
            return Response.status(Response.Status.CONFLICT).entity(new Error("Requested player is bound with a club")).build();
        }
    }
}
