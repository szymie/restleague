package org.tiwpr.szymie.resources;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.tiwpr.szymie.daos.ClubDao;
import org.tiwpr.szymie.entities.ClubEntity;
import org.tiwpr.szymie.models.Club;
import org.tiwpr.szymie.models.ModelWithLinks;
import org.tiwpr.szymie.models.Player;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import static javax.ws.rs.core.Response.ResponseBuilder;

@Component
@Path("clubs")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ClubsResource extends BaseResource {

    @Autowired
    private ClubDao clubDao;
    @Autowired
    private PlayersSubResource playersSubResource;
    @Autowired
    private FormerPlayersSubResource formerPlayersSubResource;

    @GET
    @Transactional
    public ModelWithLinks<List<Club>> getClubs(@Context UriInfo uriInfo, @BeanParam PaginationFilter paginationFilter) {

        List<Club> clubs = clubDao.findAll(paginationFilter.getOffset(), paginationFilter.getLimit());

        ModelWithLinks<List<Club>> modelWithLinks = new ModelWithLinks<>();
        fillModelWithLinks(modelWithLinks, clubs, clubDao.countAll(), uriInfo, paginationFilter);

        return modelWithLinks;
    }

    @GET
    @Path("/{clubId}")
    @Transactional
    public Response getClub(@PathParam("clubId") int id) {

        Optional<ClubEntity> clubEntityOptional = clubDao.findById(id);
        Optional<Club> clubOptional = clubEntityOptional.map(ClubEntity::toModel);

        ResponseBuilder responseBuilder = clubOptional
                .map(Response::ok)
                .orElse(notFoundResponseBuilder(new Error("Club has not been found")));

        clubEntityOptional.ifPresent(clubEntity -> responseBuilder.header(HttpHeaders.LAST_MODIFIED, clubEntity.getLastModified().toString()));

        return responseBuilder.build();
    }

    @POST
    @Transactional
    public Response postClub(@MatrixParam("poe") String poeKey, @Context UriInfo uriInfo, @Valid Club club) {

        if(poeKey == null) {
            return responseWithNewPoeKey(uriInfo);
        } else {

            if(poeKeyDao.isValid(poeKey)) {
                poeKeyDao.invalidate(poeKey);
                int clubId = clubDao.save(club);
                URI clubLocation = uriInfo.getBaseUriBuilder().path(ClubsResource.class).path(Integer.toString(clubId)).build();
                return Response.created(clubLocation).build();
            } else {
                return Response.status(Response.Status.METHOD_NOT_ALLOWED).build();
            }
        }
    }

    @PUT
    @Transactional
    @Path("/{clubId}")
    public Response putClub(
            @HeaderParam(HttpHeaders.IF_UNMODIFIED_SINCE) Timestamp lastModified,
            @PathParam("clubId") int id,
            @Valid Club club) {

        Optional<ClubEntity> playerOptional = clubDao.findById(id);

        if(playerOptional.isPresent()) {

            club.setId(id);
            ClubEntity clubEntity = playerOptional.get();
            Timestamp clubLastModified = clubEntity.getLastModified();

            Optional<ResponseBuilder> preconditionsResponseBuilderOptional = evaluatePreconditions(lastModified, clubLastModified);

            if(!preconditionsResponseBuilderOptional.isPresent()) {
                clubDao.update(club);
                return Response.ok().build();
            } else {
                return preconditionsResponseBuilderOptional.get().build();
            }
        } else {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new Error("Club has not been found")).build();
        }
    }

    @DELETE
    @Transactional
    @Path("/{clubId}")
    public Response deleteClub(@PathParam("clubId") int id) {
        clubDao.delete(id);
        return Response.noContent().build();
    }

    @Path("/{clubId}/players")
    public PlayersSubResource players() {
        return playersSubResource;
    }

    @Path("/{clubId}/former-players")
    public FormerPlayersSubResource formerPlayers() {
        return formerPlayersSubResource;
    }
}
