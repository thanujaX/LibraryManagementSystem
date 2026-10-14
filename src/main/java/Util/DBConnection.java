package com.library.Util;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
    private static DBConnection instance;
    private Connection connection;

    private DBConnection() throws Exception {
        connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/library_db",
                "root",
                "1234"
        );
    }

    public static DBConnection getInstance() throws Exception {
        if(instance == null) instance = new DBConnection();
        return instance;
    }

    public Connection getConnection(){
        return connection;
    }
}