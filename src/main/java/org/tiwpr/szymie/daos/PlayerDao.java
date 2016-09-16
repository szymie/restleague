package org.tiwpr.szymie.daos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.tiwpr.szymie.entities.BaseEntity;
import org.tiwpr.szymie.entities.CountryEntity;
import org.tiwpr.szymie.entities.PlayerEntity;
import org.tiwpr.szymie.entities.PositionEntity;
import org.tiwpr.szymie.models.Player;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class PlayerDao extends BaseDao {


    @Autowired
    private PositionDao positionDao;
    @Autowired
    private CountryDao countryDao;

    public Optional<PlayerEntity> findById(int id) {
        PlayerEntity playerEntity = entityManager.find(PlayerEntity.class, id);
        return Optional.ofNullable(playerEntity);
    }

    public List<Player> findValidByClubId(int id, int offset, int limit) {
        return findByClubId(id, true, offset, limit);
    }

    private List<Player> findByClubId(int id, boolean valid, int offset, int limit) {

        TypedQuery<PlayerEntity> query = entityManager.createQuery("select distinct m.player from MembershipEntity m where m.club.id = :club and m.valid = :valid", PlayerEntity.class);

        query.setParameter("club", id);
        query.setParameter("valid", valid);

        query.setFirstResult(offset);
        query.setMaxResults(limit);

        List<PlayerEntity> list = query.getResultList();

        return list.stream().map(PlayerEntity::toModel).collect(Collectors.toList());
    }

    public List<Player> findNotValidByClubId(int id, int offset, int limit) {
        return findByClubId(id, false, offset, limit);
    }

    public List<Player> findAll(int offset, int limit) {

        TypedQuery<PlayerEntity> query = entityManager.createQuery("from PlayerEntity", PlayerEntity.class);

        query.setFirstResult(offset);
        query.setMaxResults(limit);

        List<PlayerEntity> list = query.getResultList();

        return list.stream().map(PlayerEntity::toModel).collect(Collectors.toList());
    }

    public int save(Player player) {

        PlayerEntity playerEntity = PlayerEntity.fromModel(player);

        fillPositionForPlayer(player, playerEntity);
        fillCountryForPlayer(player, playerEntity);

        entityManager.persist(playerEntity);

        return playerEntity.getId();
    }

    private void fillPositionForPlayer(Player player, PlayerEntity playerEntity) {
        String positionName = player.getPosition().getName();
        Optional<PositionEntity> positionEntityOptional = positionDao.findByName(positionName);
        positionEntityOptional.ifPresent(playerEntity::setPosition);
    }

    private void fillCountryForPlayer(Player player, PlayerEntity playerEntity) {

        String countryName = player.getCountry().getName();
        Optional<CountryEntity> countryEntityOptional = countryDao.findByName(countryName);

        if(countryEntityOptional.isPresent()) {
            playerEntity.setCountry(countryEntityOptional.get());
        } else {

            CountryEntity countryEntity = new CountryEntity();
            countryEntity.setName(countryName);

            entityManager.persist(countryEntity);

            playerEntity.setCountry(countryEntity);
        }
    }

    public void update(Player player) {

        Optional<PlayerEntity> playerEntityOptional = findById(player.getId());

        if(playerEntityOptional.isPresent()) {

            PlayerEntity playerEntity = playerEntityOptional.get();

            playerEntity.setFirstName(player.getFirstName());
            playerEntity.setLastName(player.getLastName());
            playerEntity.setDateOfBirth(BaseEntity.dateFromString(player.getDateOfBirth()));
            playerEntity.setHeight(player.getHeight());
            playerEntity.setFoot(player.getFoot());

            fillPositionForPlayer(player, playerEntity);
            fillCountryForPlayer(player, playerEntity);

            playerEntity.setLastModified(null);

            entityManager.persist(playerEntity);
        }
    }

    public void delete(int id) {
        findById(id).ifPresent(entityManager::remove);
    }
}
