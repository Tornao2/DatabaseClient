package org.example.databaseclient;

import javafx.util.Pair;

import java.sql.*;
import java.util.ArrayList;

public class DatabaseManager {
    private Connection DBConnection;
    private DatabaseMetaData metaData;
    private final String username = "C##CLIENT";

    public void setUpConnection () {
        try {
            DBConnection = DriverManager.getConnection(
                    "jdbc:oracle:thin:@localhost:1521:XE",
                    username,
                    "projekt"
            );
        } catch (SQLException e) {
            System.err.println("Nie polaczono z baza: " + e.getMessage());
            return;
        }
        try {
            metaData = DBConnection.getMetaData();
        } catch (SQLException e) {
            System.err.println("Nie udalo sie pobrac metadata: " + e.getMessage());
        }
    }
    public ResultSet getTables() {
        try {
            return metaData.getTables(null, username, "%", new String[]{"TABLE"});
        } catch (SQLException e) {
            System.err.println("Nie udalo sie pobrac tabel: " + e.getMessage());
            return null;
        }
    }
    public void updateListView(ArrayList<Pair<String, String>> listOfColumns, String selectedTable) {
        try {
            listOfColumns.clear();
            ResultSet columns = metaData.getColumns(null, username, selectedTable, "%");
            while (columns.next()) {
                String columnName = columns.getString("COLUMN_NAME");
                String columnType = columns.getString("DATA_TYPE");
                listOfColumns.add(new Pair<>(columnName, columnType));
            }
        } catch (SQLException e) {
            System.err.println("Nie udalo sie pobrac kolumn: " + e.getMessage());
        }
    }
}
