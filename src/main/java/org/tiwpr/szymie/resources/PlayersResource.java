package org.tiwpr.szymie.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.tiwpr.szymie.daos.PlayerDao;
import org.tiwpr.szymie.daos.PoeKeyDao;
import org.tiwpr.szymie.entities.PlayerEntity;
import org.tiwpr.szymie.models.Error;
import org.tiwpr.szymie.models.Player;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.security.MessageDigest;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static javax.ws.rs.core.Response.ResponseBuilder;

@Component
@Path("players")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PlayersResource {

    @Autowired
    private PlayerDao playerDao;
    @Autowired
    private PoeKeyDao poeKeyDao;
    @Autowired
    private ObjectMapper objectMapper;

    @GET
    @Transactional
    public List<Player> getPlayers(@BeanParam PaginationFilter paginationFilter) {
        return playerDao.findAll(paginationFilter.getOffset(), paginationFilter.getLimit());
    }

    @GET
    @Path("/{playerId}")
    @Transactional
    public Response getPlayer(@PathParam("playerId") int id) {

        Optional<PlayerEntity> playerEntityOptional = playerDao.findById(id);
        Optional<Player> playerOptional = playerEntityOptional.map(PlayerEntity::toPlayer);

        ResponseBuilder responseBuilder = playerOptional
                .map(Response::ok)
                .orElse(notFoundResponseBuilder());

        playerEntityOptional.ifPresent(playerEntity -> responseBuilder.header(HttpHeaders.LAST_MODIFIED, new Date(playerEntity.getLastModified().getTime())));
        //playerOptional.ifPresent(player -> responseBuilder.tag(Integer.toString((player.getId()))));

        return responseBuilder.build();
    }

    private ResponseBuilder notFoundResponseBuilder() {
        return Response.status(Response.Status.NOT_FOUND)
                .entity(new Error("Player has not been found"));
    }

    @POST
    @Transactional
    public Response postPlayer(@MatrixParam("poe") String poeKey, @Context UriInfo uriInfo, @Valid Player player) {

        if(poeKey == null) {
            return getResponseWithNewPoeKey(uriInfo);
        } else {

            if(poeKeyDao.isValid(poeKey)) {
                poeKeyDao.invalidate(poeKey);
                playerDao.save(player);
                return Response.ok().build();
            } else {
                return Response.status(Response.Status.METHOD_NOT_ALLOWED).build();
            }
        }
    }

    private Response getResponseWithNewPoeKey(UriInfo uriInfo) {
        String newPoeKey = poeKeyDao.getNew();
        URI poeUri = uriInfo.getAbsolutePathBuilder().matrixParam("poe", newPoeKey).build();
        return Response.status(Response.Status.TEMPORARY_REDIRECT).location(poeUri).build();
    }

    @PUT
    @Transactional
    @Path("/{playerId}")
    public Response putPLayer(
            @HeaderParam(HttpHeaders.IF_UNMODIFIED_SINCE) Timestamp lastModified,
            @PathParam("playerId") int id,
            @Valid Player player) {

        Optional<PlayerEntity> playerOptional = playerDao.findById(id);

        if(playerOptional.isPresent()) {

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
    }

    private Optional<ResponseBuilder> evaluatePreconditions(Timestamp sentLastModified, Timestamp lastModified) {

        if(sentLastModified != null) {

            if(arePreconditionsMatched(sentLastModified, lastModified)) {
                return Optional.empty();
            } else {
                return Optional.of(Response.status(Response.Status.PRECONDITION_FAILED));
            }
        } else {
            return Optional.of(Response.status(Response.Status.FORBIDDEN));
        }
    }

    private boolean arePreconditionsMatched(Timestamp sentLastModified, Timestamp lastModified) {
        return sentLastModified.compareTo(lastModified) == 0;
    }

    private Date timestampToDate(Timestamp timestamp) {
        return new Date(timestamp.getTime());
    }
}
