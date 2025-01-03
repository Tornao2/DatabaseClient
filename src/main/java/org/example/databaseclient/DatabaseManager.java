package org.example.databaseclient;

import javafx.util.Pair;

import java.sql.*;
import java.text.SimpleDateFormat;
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
    public void updateListView(ArrayList<Pair<String, Integer>> listOfColumns, String selectedTable) {
        try {
            listOfColumns.clear();
            ResultSet columns = metaData.getColumns(null, username, selectedTable, "%");
            while (columns.next()) {
                String columnName = columns.getString("COLUMN_NAME");
                int columnType = columns.getInt("DATA_TYPE");
                listOfColumns.add(new Pair<>(columnName, columnType));
            }
        } catch (SQLException e) {
            System.err.println("Nie udalo sie pobrac kolumn: " + e.getMessage());
        }
    }
    public String insertIntoTable(String tableName, ArrayList<String>data) {
        StringBuilder values = new StringBuilder();
        ArrayList <Integer> listOfColumns = new ArrayList<>();
        String resultMessage;
        try {
            ResultSet columns = metaData.getColumns(null, username, tableName, "%");
            while (columns.next()) {
                int columnType = columns.getInt("DATA_TYPE");
                listOfColumns.add(columnType);
            }
        } catch (SQLException e) {
            System.err.println("Nie udalo sie pobrac kolumn: " + e.getMessage());
        }
        for (int i = 0; i < data.size(); i++) {
            values.append("?");
            if (i < data.size() - 1)
                values.append(", ");
        }
        String sqlStatement = "INSERT INTO " + tableName + " VALUES (" + values + ")";
        try {
            PreparedStatement statement = DBConnection.prepareStatement(sqlStatement);
            for (int i = 0; i < data.size(); i++) {
                switch (listOfColumns.get(i)){
                    case 2:
                        statement.setInt(i + 1, Integer.parseInt(data.get(i)));
                        break;
                    case 1:
                    case 12:
                        statement.setString(i + 1, data.get(i));
                        break;
                    case 93:
                        statement.setDate(i + 1, Date.valueOf(data.get(i)));
                        break;
                    default:
                        break;
                }
            }
            int rowsAffected = statement.executeUpdate();
            resultMessage = "Rows inserted: " + rowsAffected;
        } catch (SQLException e) {
            System.err.println("Error during insert: " + e.getMessage());
            resultMessage = e.getMessage();
        }
        return resultMessage;
    }
}
