package org.tiwpr.szymie.resources;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.tiwpr.szymie.daos.ClubDao;
import org.tiwpr.szymie.daos.PlayerDao;
import org.tiwpr.szymie.entities.ClubEntity;
import org.tiwpr.szymie.entities.PlayerEntity;
import org.tiwpr.szymie.models.ModelWithLinks;
import org.tiwpr.szymie.models.PlayerId;
import org.tiwpr.szymie.models.Error;
import org.tiwpr.szymie.models.Player;
import org.tiwpr.szymie.usecases.ClubPlayerUseCase;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@Component
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PlayersSubResource extends BaseResource {

    @Autowired
    private PlayerDao playerDao;
    @Autowired
    private ClubDao clubDao;
    @Autowired
    private ClubPlayerUseCase clubPlayerUseCase;

    @GET
    @Transactional
    public ModelWithLinks<List<Player>> getPlayers(@Context UriInfo uriInfo, @PathParam("clubId") int clubId, @BeanParam PaginationFilter paginationFilter) {

        List<Player> players = playerDao.findValidByClubId(clubId);
        List<Player> subPlayers = subList(players, paginationFilter);

        ModelWithLinks<List<Player>> modelWithLinks = new ModelWithLinks<>();
        fillModelWithLinks(modelWithLinks, subPlayers, playerDao.countAll(), uriInfo, paginationFilter);

        return modelWithLinks;
    }

    @GET
    @Path("{playerId}")
    @Transactional
    public Response getPlayer(
            @PathParam("clubId") int clubId,
            @PathParam("playerId") int playerId,
            @Context UriInfo uriInfo) {

        if(clubPlayerUseCase.isPlayerBoundWithClub(playerId, clubId)) {
            URI playerUri = uriInfo.getBaseUriBuilder().path(PlayersResource.class).path(Integer.toString(playerId)).build();
            return Response.status(Response.Status.TEMPORARY_REDIRECT).location(playerUri).build();
        } else {
            return notFoundResponseBuilder(new Error("Requested player is not bound to requested club")).build();
        }
    }

    @POST
    @Transactional
    public Response postPlayer(@PathParam("clubId") int clubId, @Valid PlayerId playerId) {

        Optional<ClubEntity> clubEntityOptional = clubDao.findById(clubId);

        if(clubEntityOptional.isPresent()) {

            Optional<PlayerEntity> playerEntityOptional = playerDao.findById(playerId.getPlayerId());

            if(playerEntityOptional.isPresent()) {

                boolean playerFree = clubPlayerUseCase.isPlayerFree(playerId.getPlayerId());

                if(playerFree) {
                    clubPlayerUseCase.bindPlayerWithClub(playerEntityOptional.get(), clubEntityOptional.get());
                    return Response.ok().build();
                } else {
                    return Response.status(Response.Status.FORBIDDEN).entity(new Error("Requested player already has a club")).build();
                }
            } else {
                return notFoundResponseBuilder(new Error("Player has not been found")).build();
            }
        } else {
            return notFoundResponseBuilder(new Error("Club has not been found")).build();
        }
    }

    @DELETE
    @Path("{playerId}")
    @Transactional
    public Response deletePlayer(@PathParam("clubId") int clubId, @PathParam("playerId") int playerId) {

        Optional<ClubEntity> clubEntityOptional = clubDao.findById(clubId);

        if(clubEntityOptional.isPresent()) {

            Optional<PlayerEntity> playerEntityOptional = playerDao.findById(playerId);

            if(playerEntityOptional.isPresent()) {
                clubPlayerUseCase.unbindPlayerWithClub(playerEntityOptional.get(), clubEntityOptional.get());
                return Response.noContent().build();
            } else {
                return notFoundResponseBuilder(new Error("Player has not been found")).build();
            }
        } else {
            return notFoundResponseBuilder(new Error("Club has not been found")).build();
        }
    }
}
