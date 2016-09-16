package org.tiwpr.szymie.resources;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.tiwpr.szymie.daos.ClubDao;
import org.tiwpr.szymie.daos.TransferDao;
import org.tiwpr.szymie.entities.TransferEntity;
import org.tiwpr.szymie.models.Error;
import org.tiwpr.szymie.models.Transfer;
import org.tiwpr.szymie.tasks.TransferAsyncTask;
import org.tiwpr.szymie.usecases.ClubPlayerUseCase;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.Optional;
import static javax.ws.rs.core.Response.*;

@Component
@Path("transfers")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@EnableAsync
public class TransfersResource extends BaseResource {

    @Autowired
    private ClubDao clubDao;
    @Autowired
    private TransferDao transferDao;
    @Autowired
    private ClubPlayerUseCase clubPlayerUseCase;
    @Autowired
    private TransferAsyncTask transferAsyncTask;

    @GET
    @Path("/{transferId}")
    @Transactional
    public Response getTransfer(@PathParam("transferId") int id) {

        Optional<TransferEntity> transferEntityOptional = transferDao.findById(id);

        Optional<Transfer> transferOptional = transferEntityOptional.map(TransferEntity::toModel);

        ResponseBuilder responseBuilder = transferOptional
                .map(Response::ok)
                .orElse(notFoundResponseBuilder(new Error("Transfer has not been found")));

        transferEntityOptional.ifPresent(transferEntity -> responseBuilder.header(HttpHeaders.LAST_MODIFIED, transferEntity.getLastModified().toString()));

        return responseBuilder.build();
    }

    @POST
    @Transactional
    public Response postTransfer(@Context UriInfo uriInfo, @Valid Transfer transfer) {

        Optional<Error> errorOptional = validateTransfer(transfer);

        if(errorOptional.isPresent()) {
            Error error = errorOptional.get();
            return Response.status(Response.Status.FORBIDDEN).entity(error).build();
        } else {

            int transferId = saveTransfer(transfer);

            transferAsyncTask.performTransfer(transfer);

            URI locationUri = uriInfo.getBaseUriBuilder().path(TransfersResource.class).path(Integer.toString(transferId)).build();
            return Response.accepted().location(locationUri).build();
        }
    }

    private Optional<Error> validateTransfer(Transfer transfer) {

        if(transfer.getSourceClubId() == transfer.getDestinationClubId()) {
            return Optional.of(new Error("sourceClubId and destinationClubId cannot be the same"));
        }

        if(!clubPlayerUseCase.isPlayerBoundWithClub(transfer.getPlayerId(), transfer.getSourceClubId())) {
            return Optional.of(new Error("Requested player is not bound to requested source club"));
        }

        if(!clubDao.findById(transfer.getDestinationClubId()).isPresent()) {
            return Optional.of(new Error("Destination club does not exist"));
        }

        if(clubPlayerUseCase.hasPlayerInProgressTransfer(transfer.getPlayerId(), transfer.getSourceClubId())) {
            return Optional.of(new Error("Requested player already has a transfer in progress"));
        }

        return Optional.empty();
    }

    @Transactional
    private int saveTransfer(Transfer transfer) {

        transfer.setStatus("in progress");
        int transferId = transferDao.save(transfer);
        transfer.setId(transferId);

        return transferId;
    }
}
