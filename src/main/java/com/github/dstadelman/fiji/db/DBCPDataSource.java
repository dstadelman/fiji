package com.github.dstadelman.fiji.db;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.commons.dbcp2.ConnectionFactory;
import org.apache.commons.dbcp2.DriverManagerConnectionFactory;
import org.apache.commons.dbcp2.PoolableConnection;
import org.apache.commons.dbcp2.PoolableConnectionFactory;
import org.apache.commons.dbcp2.PoolingDataSource;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;

// import org.apache.commons.dbcp2.BasicDataSource;

public class DBCPDataSource {

    // private static BasicDataSource ds = new BasicDataSource();
    private static PoolingDataSource<PoolableConnection> ds;
    
    static {

        String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        String appConfigPath = rootPath + "app.properties";


        try {
            Properties appProps = new Properties();
            appProps.load(new FileInputStream(appConfigPath));
            
            String mysqlURL = appProps.getProperty("pgsql-url");
            String mysqlUser = appProps.getProperty("pgsql-user");
            String mysqlPass = appProps.getProperty("pgsql-pass");

            if (mysqlURL == null || mysqlURL.isEmpty())
                throw new Exception("app.properties missing pgsql-url");
            if (mysqlUser == null || mysqlUser.isEmpty())
                throw new Exception("app.properties missing pgsql-user");
            if (mysqlPass == null || mysqlPass.isEmpty())
                throw new Exception("app.properties missing pgsql-pass");

            // ds.setUrl(mysqlURL);
            // ds.setUsername(mysqlUser);
            // ds.setPassword(mysqlPass);
            // ds.setMinIdle(5);
            // ds.setMaxIdle(10);
            // ds.setMaxOpenPreparedStatements(100);

            ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(mysqlURL, mysqlUser, mysqlPass);
            PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(connectionFactory, null);
            ObjectPool<PoolableConnection> connectionPool = new GenericObjectPool<>(poolableConnectionFactory);
            poolableConnectionFactory.setPool(connectionPool);
            ds = new PoolingDataSource<>(connectionPool);

        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
        }
    }
    
    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }
    
    private DBCPDataSource(){ }    
    
}
