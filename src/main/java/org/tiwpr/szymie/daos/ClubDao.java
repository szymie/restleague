package org.tiwpr.szymie.daos;

import org.springframework.stereotype.Repository;
import org.tiwpr.szymie.entities.BaseEntity;
import org.tiwpr.szymie.entities.ClubEntity;
import org.tiwpr.szymie.models.Club;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class ClubDao {

    @PersistenceContext
    private EntityManager entityManager;

    public Optional<ClubEntity> findById(int id) {
        ClubEntity playerEntity = entityManager.find(ClubEntity.class, id);
        return Optional.ofNullable(playerEntity);
    }

    public List<Club> findAll(int offset, int limit) {

        TypedQuery<ClubEntity> query = entityManager.createQuery("from ClubEntity", ClubEntity.class);

        query.setFirstResult(offset);
        query.setMaxResults(limit);

        List<ClubEntity> list = query.getResultList();

        return list.stream().map(ClubEntity::toModel).collect(Collectors.toList());
    }

    public int save(Club club) {
        ClubEntity clubEntity = ClubEntity.fromModel(club);
        entityManager.persist(clubEntity);
        return clubEntity.getId();
    }

    public void update(Club club) {

        Optional<ClubEntity> clubEntityOptional = findById(club.getId());

        if(clubEntityOptional.isPresent()) {

            ClubEntity clubEntity = clubEntityOptional.get();

            clubEntity.setId(club.getId());
            clubEntity.setFullName(club.getFullName());
            clubEntity.setNickname(club.getNickname());
            clubEntity.setDateOfFounding(BaseEntity.dateFromString(club.getDateOfFounding()));
            clubEntity.setStadium(club.getStadium());

            clubEntity.setLastModified(null);

            entityManager.persist(clubEntity);
        }
    }

    public void delete(int id) {
        findById(id).ifPresent(entityManager::remove);
    }
}
