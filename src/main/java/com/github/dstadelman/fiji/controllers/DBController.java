package com.github.dstadelman.fiji.controllers;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

public class DBController {

    static protected SessionFactory sessionFactory = null;
    static protected Boolean sessionFactoryInitialized = false;

    static public SessionFactory getSessionFactory() {

        if (sessionFactoryInitialized == true)
            return sessionFactory;

        synchronized(sessionFactoryInitialized)
        {
            // configures settings from hibernate.cfg.xml 
            StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build(); 
            try {
                sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory(); 
            } catch (Exception e) {
                System.out.println(e.getMessage());
                sessionFactoryInitialized = true;
                return null;
            }
            sessionFactoryInitialized = true;
            return sessionFactory;
        }
    }

    public static String f(String table, String field) {
        if (table != null)
            return table + "." + field;
        else
            return field;
    }

    public static void f(StringBuffer s, String table, String field) {
        if (table != null)
            s.append(table + ".");
        s.append(field);
    }

    public static String n(String table, String field) {
        if (table != null)
            return table + "_" + field;
        else
            return field;
    }

    public static void n(StringBuffer s, String table, String field) {
        if (table != null)
            s.append(table + "_");
        s.append(field);
    }
}
