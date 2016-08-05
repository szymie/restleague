package org.tiwpr.szymie.daos;

import org.hibernate.Session;
import org.tiwpr.szymie.entities.PlayerEntity;
import org.tiwpr.szymie.models.Player;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PlayerDao {

    @PersistenceContext
    private EntityManager entityManager;

    public PlayerDao(Session entityManager) {
        this.entityManager = entityManager;
    }

    public Optional<Player> findById(int id) {
        PlayerEntity playerEntity = entityManager.find(PlayerEntity.class, id);
        Optional<PlayerEntity> playerEntityOptional = Optional.ofNullable(playerEntity);
        return playerEntityOptional.map(PlayerEntity::toPlayer);
    }

    public List<Player> findAll(int offset, int limit) {

        TypedQuery<PlayerEntity> query = entityManager.createQuery("from PlayerEntity", PlayerEntity.class);

        query.setFirstResult(offset);
        query.setMaxResults(limit);

        List<PlayerEntity> list = query.getResultList();

        return list.stream().map(PlayerEntity::toPlayer).collect(Collectors.toList());
    }
}
