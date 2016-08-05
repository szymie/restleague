package org.tiwpr.szymie.resources;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tiwpr.szymie.daos.DaoFactory;
import org.tiwpr.szymie.daos.PlayerDao;
import org.tiwpr.szymie.daos.PoeKeyDao;
import org.tiwpr.szymie.models.Error;
import org.tiwpr.szymie.models.Player;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@Component
@Path("players")
public class PlayersResource {

    @Autowired
    private DaoFactory daoFactory;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Player> getPlayers(@BeanParam PaginationFilter paginationFilter) {

        List<Player> players;

        try {
            daoFactory.beginSessionScope();
            PlayerDao playerDao = daoFactory.createPlayerDao();
            players = playerDao.findAll(paginationFilter.getOffset(), paginationFilter.getLimit());
        } finally {
            daoFactory.endSessionScope();
        }

        return players;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{playerId}")
    public Response getPlayer(@PathParam("playerId") int id) {

        Optional<Player> playerOptional;

        try {
            daoFactory.beginSessionScope();
            PlayerDao playerDao = daoFactory.createPlayerDao();
            playerOptional = playerDao.findById(id);
        } finally {
            daoFactory.endSessionScope();
        }

        return playerOptional.map(Response::ok)
                .orElse(
                        Response.status(Response.Status.NOT_FOUND)
                                .entity(new Error("Player has not been found"))
                )
                .build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postPlayer(@MatrixParam("poe") String poeKey, @Context UriInfo uriInfo, Player player) {

        System.out.println("poeKey= " + poeKey);

        if(poeKey == null) {
            return getResponseWithNewPoeKey(uriInfo);
        } else {

            try {

                daoFactory.beginSessionScope();

                try {

                    daoFactory.beginTransaction();

                    PoeKeyDao poeKeyDao = daoFactory.createPoeKeysDao(10);

                    if(!poeKeyDao.isValid(poeKey)) {
                        daoFactory.commitTransaction();
                        return Response.status(Response.Status.METHOD_NOT_ALLOWED).build();
                    }

                    poeKeyDao.invalidate(poeKey);

                    daoFactory.commitTransaction();


                    return Response.ok().build();


                } catch(Exception e) {
                    daoFactory.rollbackTransaction();
                }
            } finally {
                daoFactory.endSessionScope();
            }
        }

        return null;
    }

    private Response getResponseWithNewPoeKey(UriInfo uriInfo) {
        String newPoeKey = getNewPoeKey();
        URI poeUri = uriInfo.getAbsolutePathBuilder().matrixParam("poe", newPoeKey).build();
        return Response.status(Response.Status.TEMPORARY_REDIRECT).location(poeUri).build();
    }

    private String getNewPoeKey() {

        try {
            daoFactory.beginSessionScope();
            PoeKeyDao poeKeyDao = daoFactory.createPoeKeysDao(10);
            return poeKeyDao.getNew();
        } finally {
            daoFactory.endSessionScope();
        }
    }

    private boolean isPeoKeyValid(String poeKey) {
        return false;
    }
}
