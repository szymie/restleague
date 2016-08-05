package org.tiwpr.szymie.entities;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.tiwpr.szymie.models.Country;

import java.util.Date;

import static org.junit.Assert.*;

public class CountryEntityTest {

    private Session session;
    private static SessionFactory sessionFactory;

    @BeforeClass
    public static void setUpClass() {
        Configuration configuration = new Configuration();
        sessionFactory = configuration.configure().buildSessionFactory();
    }

    @Before
    public void setUp() throws Exception {
        session = sessionFactory.openSession();
    }

    @After
    public void tearDown() throws Exception {
        session.close();
    }

    @Test
    public void test() {



        CountryEntity country0 = new CountryEntity();
        CountryEntity country1 = new CountryEntity();

        country0.setName("c0");
        country1.setName("c0");

        country1.setId(16);

        PlayerEntity playerEntity = new PlayerEntity();
        playerEntity.setFirstName("Eden");
        playerEntity.setLastName("Hazard");
        playerEntity.setDateOfBirth(new Date());
        playerEntity.setHeight(173);
        playerEntity.setFoot("both");
        playerEntity.setPosition(new PositionEntity("midfielder"));

        session.beginTransaction();
        session.save(country0);
        session.getTransaction().commit();

        session.beginTransaction();

        CountryEntity country00 = (CountryEntity) session.get(CountryEntity.class, country0.getId());
        playerEntity.setCountry(country00);
        session.persist(playerEntity);

        CountryEntity c = (CountryEntity) session.get(CountryEntity.class, 17);
        System.out.println(c);

        session.getTransaction().commit();

    }
}

