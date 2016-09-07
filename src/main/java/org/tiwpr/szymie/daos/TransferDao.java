package org.tiwpr.szymie.daos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.tiwpr.szymie.entities.BaseEntity;
import org.tiwpr.szymie.entities.TransferEntity;
import org.tiwpr.szymie.models.Transfer;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Repository
public class TransferDao {

    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private PlayerDao playerDao;
    @Autowired
    private ClubDao clubDao;

    public Optional<TransferEntity> findById(int id) {
        TransferEntity transferEntity = entityManager.find(TransferEntity.class, id);
        return Optional.ofNullable(transferEntity);
    }

    public int save(Transfer transfer) {

        TransferEntity transferEntity = TransferEntity.fromModel(transfer);

        fillPlayerForTransfer(transfer, transferEntity);
        fillClubsForTransfer(transfer, transferEntity);

        entityManager.persist(transferEntity);

        return transferEntity.getId();
    }

    private void fillPlayerForTransfer(Transfer transfer, TransferEntity transferEntity) {
        playerDao.findById(transfer.getPlayerId()).ifPresent(transferEntity::setPlayer);
    }

    private void fillClubsForTransfer(Transfer transfer, TransferEntity transferEntity) {
        clubDao.findById(transfer.getSourceClubId()).ifPresent(transferEntity::setSourceClub);
        clubDao.findById(transfer.getDestinationClubId()).ifPresent(transferEntity::setDestinationClub);
    }

    public List<TransferEntity> findByPlayerIdAndSourceClubIdAndStatus(int playerId, int sourceClubId, String status) {

        TypedQuery<TransferEntity> query = entityManager.createQuery(
                "select t from TransferEntity t" +
                        " where t.player.id = :player" +
                        " and t.sourceClub.id = :sourceClub" +
                        " and t.status = :status", TransferEntity.class);

        query.setParameter("player", playerId);
        query.setParameter("sourceClub", sourceClubId);
        query.setParameter("status", status);

        return query.getResultList();
    }

    public void update(Transfer transfer) {

        Optional<TransferEntity> transferEntityOptional = findById(transfer.getId());

        if(transferEntityOptional.isPresent()) {

            TransferEntity transferEntity = transferEntityOptional.get();

            transferEntity.setStatus(transfer.getStatus());
            transferEntity.setValue(transfer.getValue());
            transferEntity.setDate(BaseEntity.dateFromString(transfer.getDate()));

            fillPlayerForTransfer(transfer, transferEntity);
            fillClubsForTransfer(transfer, transferEntity);

            update(transferEntity);
        }
    }

    public void update(TransferEntity transferEntity) {
        entityManager.persist(transferEntity);
    }
}
