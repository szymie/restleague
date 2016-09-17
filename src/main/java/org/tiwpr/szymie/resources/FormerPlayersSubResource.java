package org.tiwpr.szymie.resources;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.tiwpr.szymie.daos.PlayerDao;
import org.tiwpr.szymie.models.Error;
import org.tiwpr.szymie.models.ModelWithLinks;
import org.tiwpr.szymie.models.Player;
import org.tiwpr.szymie.usecases.ClubPlayerUseCase;

import javax.ws.rs.*;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FormerPlayersSubResource extends BaseResource {

    @Autowired
    private PlayerDao playerDao;
    @Autowired
    private ClubPlayerUseCase clubPlayerUseCase;

    @GET
    @Transactional
    public ModelWithLinks<List<Player>> getPlayers(@Context UriInfo uriInfo, @PathParam("clubId") int clubId, @BeanParam PaginationFilter paginationFilter) {

        List<Player> players = playerDao.findNotValidByClubId(clubId);
        List<Player> subPlayers = subList(players, paginationFilter);
        
        ModelWithLinks<List<Player>> modelWithLinks = new ModelWithLinks<>();
        fillModelWithLinks(modelWithLinks, subPlayers, players.size(), uriInfo, paginationFilter);

        return modelWithLinks;
    }

    @GET
    @Path("{playerId}")
    @Transactional
    public Response getPlayer(
            @PathParam("clubId") int clubId,
            @PathParam("playerId") int playerId,
            @Context UriInfo uriInfo) {

        if (clubPlayerUseCase.wasPlayerBoundWithClub(playerId, clubId)) {
            URI playerUri = uriInfo.getBaseUriBuilder().path(PlayersResource.class).path(Integer.toString(playerId)).build();
            return Response.status(Response.Status.TEMPORARY_REDIRECT).location(playerUri).build();
        } else {
            return notFoundResponseBuilder(new Error("Requested player is not former player of requested club")).build();
        }
    }
}
