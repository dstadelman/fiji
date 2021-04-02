package com.github.dstadelman.fiji.controllers;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.github.dstadelman.fiji.db.DBCPDataSource;
import com.github.dstadelman.fiji.entities.Quote;
import com.github.dstadelman.fiji.entities.TradeStrat;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.junit.Test;

public class DBControllerTest {

    @Test
    public void createTradeStrat() throws Exception {

        SessionFactory sessionFactory = DBController.getSessionFactory();
        assertNotNull(sessionFactory);

        Session session = sessionFactory.openSession();
        session.beginTransaction();

        TradeStrat ts = new TradeStrat();

        ts.name = "test";
        
        session.save(ts);

        session.getTransaction().commit();
        session.close();

        assertTrue(true);
    }

    @Test
    public void deleteTradeStrat() throws Exception {

        SessionFactory sessionFactory = DBController.getSessionFactory();
        assertNotNull(sessionFactory);

        Session session = sessionFactory.openSession();
        session.beginTransaction();

        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<TradeStrat> cr = cb.createQuery(TradeStrat.class);
        Root<TradeStrat> root = cr.from(TradeStrat.class);
        cr.select(root).where(cb.like(root.get("name"), "%test%"));

        Query<TradeStrat> query = session.createQuery(cr);
        List<TradeStrat> results = query.getResultList();

        results.forEach(r -> {
            session.remove(r);
        });

        session.getTransaction().commit();
        session.close();

        assertTrue(true);
    }    
}