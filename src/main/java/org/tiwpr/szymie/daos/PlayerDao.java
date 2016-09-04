package org.tiwpr.szymie.daos;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.tiwpr.szymie.entities.CountryEntity;
import org.tiwpr.szymie.entities.PlayerEntity;
import org.tiwpr.szymie.entities.PositionEntity;
import org.tiwpr.szymie.models.Player;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class PlayerDao {

    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private PositionDao positionDao;
    @Autowired
    private CountryDao countryDao;

    public Optional<PlayerEntity> findById(int id) {
        PlayerEntity playerEntity = entityManager.find(PlayerEntity.class, id);
        return Optional.ofNullable(playerEntity);
    }

    public List<Player> findAll(int offset, int limit) {

        TypedQuery<PlayerEntity> query = entityManager.createQuery("from PlayerEntity", PlayerEntity.class);

        query.setFirstResult(offset);
        query.setMaxResults(limit);

        List<PlayerEntity> list = query.getResultList();

        return list.stream().map(PlayerEntity::toPlayer).collect(Collectors.toList());
    }

    public void save(Player player) {

        PlayerEntity playerEntity = PlayerEntity.fromPlayer(player);

        fillPositionForPlayer(player, playerEntity);
        fillCountryForPlayer(player, playerEntity);

        entityManager.persist(playerEntity);
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

    private Date dateFromString(String date) {

        DateFormat format = new SimpleDateFormat("dd-MM-yyyy");

        try {
            return format.parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    public void update(Player player) {

        Optional<PlayerEntity> playerEntityOptional = findById(player.getId());

        if(playerEntityOptional.isPresent()) {

            PlayerEntity playerEntity = playerEntityOptional.get();

            playerEntity.setFirstName(player.getFirstName());
            playerEntity.setLastName(player.getLastName());
            playerEntity.setDateOfBirth(dateFromString(player.getDateOfBirth()));
            playerEntity.setHeight(player.getHeight());
            playerEntity.setFoot(player.getFoot());

            fillPositionForPlayer(player, playerEntity);
            fillCountryForPlayer(player, playerEntity);

            entityManager.persist(playerEntity);
        }
    }

}
