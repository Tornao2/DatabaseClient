package org.example.databaseclient;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    private Connection DBConnection;
    public void setUpConnection () {
        try {
            DBConnection = DriverManager.getConnection(
                    "jdbc:oracle:thin:@localhost:1521:XE",
                    "C##CLIENT",
                    "projekt"
            );
        } catch (SQLException e) {
            System.err.println("Nie polaczono z baza: " + e.getMessage());
        }
    }
    public void closeUpConnection() {
        try {
            if (DBConnection != null)
                DBConnection.close();
        } catch (SQLException e) {
            System.err.println("Nie zakonczono polaczenia z baza: " + e.getMessage());
        }
    }
}
