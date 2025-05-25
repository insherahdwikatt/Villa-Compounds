package com.example.ri;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnector {

    private static final String URL = "jdbc:postgresql://localhost:5432/RealEstate";
    private static final String USER = "postgres";
    private static final String PASSWORD = "1234";


    public static Connection connect() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            System.out.println("[DB] Failed to connect to the database.");
            e.printStackTrace();
            return null;
        }
    }
}