package org.tiwpr.szymie.daos;

import org.springframework.stereotype.Repository;
import org.tiwpr.szymie.entities.PositionEntity;
import org.tiwpr.szymie.models.Position;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.Optional;

@Repository
public class PositionDao {

    @PersistenceContext
    private EntityManager entityManager;

    public Optional<PositionEntity> findByName(String name) {

        TypedQuery<PositionEntity> query = entityManager.createQuery("from PositionEntity where name = :name", PositionEntity.class);
        query.setParameter("name", name);

        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException exception) {
            return Optional.empty();
        }
    }

    public PositionEntity save(Position position) {

        PositionEntity positionEntity = new PositionEntity();
        positionEntity.setName(position.getName());

        entityManager.persist(positionEntity);

        return positionEntity;
    }

}
