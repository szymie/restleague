package org.tiwpr.szymie.daos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.tiwpr.szymie.KeysGenerator;
import org.tiwpr.szymie.entities.PoeKeyEntity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;

@Repository
public class PoeKeyDao {

    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private KeysGenerator keysGenerator;

    public String getNew() {

        String key = keysGenerator.nextKey();
        PoeKeyEntity poeKeyEntity = new PoeKeyEntity(key);

        entityManager.persist(poeKeyEntity);

        return key;
    }

    public boolean isValid(String key) {
        return entityManager.createQuery("select count (*) from PoeKeyEntity where value = :keyValue", Long.class)
                .setParameter("keyValue", key)
                .getSingleResult() > 0;
    }

    public void invalidate(String key) {

        Optional<PoeKeyEntity> poeKey = Optional.ofNullable(entityManager.createQuery(
                "from PoeKeyEntity where value = :keyValue",
                PoeKeyEntity.class)
                .setParameter("keyValue", key)
                .getSingleResult());

        poeKey.ifPresent(poeKeyEntity -> entityManager.remove(poeKeyEntity));
    }
}
