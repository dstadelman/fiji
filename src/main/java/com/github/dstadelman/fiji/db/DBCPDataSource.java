package com.github.dstadelman.fiji.db;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.commons.dbcp2.BasicDataSource;

public class DBCPDataSource {

    private static BasicDataSource ds = new BasicDataSource();
    
    static {

        String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        String appConfigPath = rootPath + "app.properties";


        try {
            Properties appProps = new Properties();
            appProps.load(new FileInputStream(appConfigPath));
            
            String mysqlURL = appProps.getProperty("mysql-url");
            String mysqlUser = appProps.getProperty("mysql-user");
            String mysqlPass = appProps.getProperty("mysql-pass");

            if (mysqlURL == null || mysqlURL.isEmpty())
                throw new Exception("app.properties missing mysql-url");
            if (mysqlUser == null || mysqlUser.isEmpty())
                throw new Exception("app.properties missing mysql-user");
            if (mysqlPass == null || mysqlPass.isEmpty())
                throw new Exception("app.properties missing mysql-pass");

            ds.setUrl(mysqlURL);
            ds.setUsername(mysqlUser);
            ds.setPassword(mysqlPass);
            ds.setMinIdle(5);
            ds.setMaxIdle(10);
            ds.setMaxOpenPreparedStatements(100);

        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
        }
    }
    
    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }
    
    private DBCPDataSource(){ }    
    
}
