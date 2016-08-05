package org.tiwpr.szymie.daos;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.tiwpr.szymie.KeysGenerator;

import java.util.HashMap;
import java.util.Map;

public class DaoFactory {

    private Map<Thread, Session> sessions;
    private Map<Thread, Transaction> transactions;
    private SessionFactory sessionsFactory;

    public DaoFactory() {
        sessions = new HashMap<>();
        transactions = new HashMap<>();
        sessionsFactory = new Configuration().configure().buildSessionFactory();
    }

    public void beginSessionScope() {

        Thread currentThread = Thread.currentThread();

        if(sessions.containsKey(currentThread)) {
            throw new RuntimeException("beginSessionScope: end current session scope before beginning a new one.");
        }

        Session session = sessionsFactory.openSession();
        sessionsFactory.cr
        sessions.put(currentThread, session);
    }

    public void endSessionScope() {

        Thread currentThread = Thread.currentThread();

        if(sessions.containsKey(currentThread)) {
            Session currentSession = sessions.get(currentThread);
            currentSession.close();
        }
    }

    public void beginTransaction() {

        Thread currentThread = Thread.currentThread();

        if(transactions.containsKey(currentThread)) {
            throw new RuntimeException("beginTransaction: commit current transaction before beginning a new one.");
        }

        if(!sessions.containsKey(currentThread)) {
            throw new RuntimeException("beginTransaction: no current session available.");
        }

        Session currentSession = sessions.get(currentThread);
        Transaction currentTransaction = currentSession.beginTransaction();

        transactions.put(currentThread, currentTransaction);
    }

    public void commitTransaction() {

        Thread currentThread = Thread.currentThread();

        if(!transactions.containsKey(currentThread)) {
            throw new RuntimeException("commitTransaction: no current transaction available.");
        }

        Transaction currentTransaction = transactions.get(currentThread);
        currentTransaction.commit();

        transactions.remove(currentThread);
    }

    public void rollbackTransaction() {

        Thread currentThread = Thread.currentThread();

        if(!transactions.containsKey(currentThread)) {
            throw new RuntimeException("rollbackTransaction: no current transaction available.");
        }

        Transaction currentTransaction = transactions.get(currentThread);
        currentTransaction.rollback();

        transactions.remove(currentThread);
    }

    public PlayerDao createPlayerDao() {

        Thread currentThread = Thread.currentThread();

        if(!sessions.containsKey(currentThread)) {
            throw new RuntimeException("createPlayerDao: no session scope available.");
        }

        Session currentSession = sessions.get(currentThread);
        return new PlayerDao(currentSession);
    }

    public PoeKeyDao createPoeKeysDao(int length) {

        Thread currentThread = Thread.currentThread();

        if(!sessions.containsKey(currentThread)) {
            throw new RuntimeException("createPoeKeysDao: no session scope available.");
        }

        Session currentSession = sessions.get(currentThread);
        return new PoeKeyDao(currentSession, new KeysGenerator(length));
    }
}
