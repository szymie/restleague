package org.tiwpr.szymie.daos;

import org.springframework.stereotype.Repository;
import org.tiwpr.szymie.entities.CountryEntity;
import org.tiwpr.szymie.models.Country;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.Optional;

@Repository
public class CountryDao {

    @PersistenceContext
    private EntityManager entityManager;

    public Optional<CountryEntity> findByName(String name) {

        TypedQuery<CountryEntity> query = entityManager.createQuery("from CountryEntity where name = :name", CountryEntity.class);
        query.setParameter("name", name);

        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException exception) {
            return Optional.empty();
        }
    }

    public CountryEntity save(Country country) {

        CountryEntity countryEntity = new CountryEntity();
        countryEntity.setName(country.getName());

        entityManager.persist(countryEntity);

        return countryEntity;
    }

}
