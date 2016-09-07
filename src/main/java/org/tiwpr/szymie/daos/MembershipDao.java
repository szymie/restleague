package org.tiwpr.szymie.daos;


import org.springframework.stereotype.Repository;
import org.tiwpr.szymie.entities.MembershipEntity;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class MembershipDao {

    @PersistenceContext
    private EntityManager entityManager;

    public Optional<MembershipEntity> findValidByClubIdAndPlayerId(int clubId, int playerId) {

        List<MembershipEntity> result = findByClubIdAndPlayerId(clubId, playerId, true);

        if(result.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(result.get(0));
        }
    }

    private List<MembershipEntity> findByClubIdAndPlayerId(int clubId, int playerId, boolean valid) {

        TypedQuery<MembershipEntity> query = entityManager.createQuery(
                "select m from MembershipEntity m where m.club.id = :club and m.player.id = :player and m.valid = :valid", MembershipEntity.class);

        query.setParameter("club", clubId);
        query.setParameter("player", playerId);
        query.setParameter("valid", valid);

        return query.getResultList();
    }

    public List<MembershipEntity> findValidByPlayerId(int id) {

        TypedQuery<MembershipEntity> query = entityManager.createQuery("select m from MembershipEntity m where m.player.id = :player and m.valid = true", MembershipEntity.class);

        query.setParameter("player", id);

        List<MembershipEntity> list = query.getResultList();

        return list.stream().collect(Collectors.toList());
    }

    public List<MembershipEntity> findNotValidByClubIdAndPlayerId(int clubId, int playerId) {
        return findByClubIdAndPlayerId(clubId, playerId, false);
    }

    public void save(MembershipEntity membership) {
        entityManager.persist(membership);
    }
}
