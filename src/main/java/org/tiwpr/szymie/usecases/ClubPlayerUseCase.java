package org.tiwpr.szymie.usecases;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tiwpr.szymie.daos.MembershipDao;
import org.tiwpr.szymie.daos.TransferDao;
import org.tiwpr.szymie.entities.ClubEntity;
import org.tiwpr.szymie.entities.MembershipEntity;
import org.tiwpr.szymie.entities.PlayerEntity;
import java.util.Date;
import java.util.Optional;

@Component
public class ClubPlayerUseCase {

    @Autowired
    private MembershipDao membershipDao;
    @Autowired
    private TransferDao transferDao;

    public boolean isPlayerFree(int id) {
        return membershipDao.findValidByPlayerId(id).isEmpty();
    }

    public boolean isPlayerBoundWithClub(int playerId, int clubId) {
        return membershipDao.findValidByClubIdAndPlayerId(clubId, playerId).isPresent();
    }

    public boolean wasPlayerBoundWithClub(int playerId, int clubId) {
        return !membershipDao.findNotValidByClubIdAndPlayerId(clubId, playerId).isEmpty();
    }

    public void bindPlayerWithClub(PlayerEntity playerEntity, ClubEntity clubEntity) {

        MembershipEntity membershipEntity = new MembershipEntity();

        membershipEntity.setPlayer(playerEntity);
        membershipEntity.setClub(clubEntity);
        membershipEntity.setStartDate(new Date());
        membershipEntity.setValid(true);

        membershipDao.save(membershipEntity);
    }

    public boolean unbindPlayerWithClub(PlayerEntity playerEntity, ClubEntity clubEntity) {

        Optional<MembershipEntity> membershipEntityOptional = membershipDao.findValidByClubIdAndPlayerId(clubEntity.getId(), playerEntity.getId());

        if(membershipEntityOptional.isPresent()) {

            MembershipEntity membershipEntity = membershipEntityOptional.get();
            membershipEntity.setEndDate(new Date());
            membershipEntity.setValid(false);

            membershipDao.save(membershipEntity);

            return true;
        } else {
            return false;
        }
    }

    public boolean hasPlayerInProgressTransfer(int playerId, int sourceClubId) {
        return !transferDao.findByPlayerIdAndSourceClubIdAndStatus(playerId, sourceClubId, "in progress").isEmpty();
    }
}
