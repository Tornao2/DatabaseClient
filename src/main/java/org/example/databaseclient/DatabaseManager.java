package org.example.databaseclient;

import javafx.util.Pair;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class DatabaseManager {
    private Connection DBConnection;
    private DatabaseMetaData metaData;
    private final String username = "C##CLIENT";

    public void setUpConnection () throws SQLException {
        DBConnection = DriverManager.getConnection(
                "jdbc:oracle:thin:@localhost:1521:XE",
                username,
                "projekt"
        );
        metaData = DBConnection.getMetaData();
    }
    public ResultSet getTables() {
        try {
            return metaData.getTables(null, username, "%", new String[]{"TABLE"});
        } catch (SQLException e) {
            System.err.println("Nie udalo sie pobrac tabel: " + e.getMessage());
            return null;
        }
    }
    public ArrayList<Pair<String, Integer>> getColumnNames(String selectedTable) {
        ArrayList <Pair<String, Integer>> returnObj = new ArrayList<>();
        try {
            ResultSet columns = metaData.getColumns(null, username, selectedTable, "%");
            while (columns.next()) {
                String columnName = columns.getString("COLUMN_NAME");
                int columnType = columns.getInt("DATA_TYPE");
                returnObj.add(new Pair<>(columnName, columnType));
            }
        } catch (SQLException e) {
            System.err.println("Nie udalo sie pobrac kolumn: " + e.getMessage());
        }
        return returnObj;
    }

    public String insertIntoTable(String tableName, ArrayList<String>data) {
        StringBuilder values = new StringBuilder();
        ArrayList <Pair<String,Integer>> listOfColumns = getColumnNames(tableName);
        String resultMessage;
        for (int i = 0; i < data.size(); i++) {
            values.append("?");
            if (i < data.size() - 1)
                values.append(", ");
        }
        String sqlStatement = "INSERT INTO " + tableName + " VALUES (" + values + ")";
        try {
            PreparedStatement statement = DBConnection.prepareStatement(sqlStatement);
            for (int i = 0; i < data.size(); i++) {
                switch (listOfColumns.get(i).getValue()){
                    case 2:
                        statement.setInt(i + 1, Integer.parseInt(data.get(i)));
                        break;
                    case 1:
                    case 12:
                        statement.setString(i + 1, data.get(i));
                        break;
                    case 93:
                        LocalDateTime localDateTime = LocalDateTime.parse(data.get(i));
                        Timestamp sqlTimestamp = Timestamp.valueOf(localDateTime);
                        statement.setTimestamp(i + 1, sqlTimestamp);
                        break;
                    default:
                        break;
                }
            }
            statement.executeUpdate();
            resultMessage = "Row inserted";
        } catch (SQLException e) {
            System.err.println("Blad podczas insert: " + e.getMessage());
            resultMessage = e.getMessage();
        }
        return resultMessage;
    }

    public Pair<ResultSet,String> readFromTable(String tableName, ArrayList<Boolean>data, ArrayList<String> compareData){
        StringBuilder values = new StringBuilder();
        ArrayList <Pair<String,Integer>> listOfColumns = getColumnNames(tableName);
        String resultMessage = "";
        for (int i = 0; i < data.size(); i++) {
            values.append(listOfColumns.get(i).getKey());
            if (i < data.size() - 1)
                values.append(", ");
        }
        String sqlStatement = "SELECT " + values + " FROM " + tableName;
        if (compareData != null) {
            boolean check = true;
            for (String compareDatum : compareData)
                if (compareDatum == null)
                    check = false;
            if (check)
                sqlStatement = compareDataModify(sqlStatement, compareData, tableName);
        }
        ResultSet rowsQueried;
        try {
            PreparedStatement statement = DBConnection.prepareStatement(sqlStatement);
            rowsQueried = statement.executeQuery();
        } catch (SQLException e) {
            System.err.println("Blad podczas read: " + e.getMessage());
            rowsQueried = null;
            resultMessage = e.getMessage();
        }
        return new Pair<>(rowsQueried, resultMessage);
    }
    private String compareDataModify(String sqlStatement, ArrayList<String> compareData, String tableName) {
        ArrayList <Pair<String,Integer>> listOfColumns = getColumnNames(tableName);
        sqlStatement = sqlStatement.concat(" WHERE ").concat(compareData.get(1));
        String type = switch (compareData.get(0)) {
            case "Equal" -> " = ";
            case "Lower" -> " > ";
            case "Higher" -> " < ";
            case "Between" -> " BETWEEN ";
            default -> "";
        };
        sqlStatement = sqlStatement.concat(type);
        ArrayList<Pair<String, Integer>> columns = getColumnNames(tableName);
        int num = 0;
        for (int i = 0; i < columns.size(); i++) {
                if(columns.get(i).getKey().equals(compareData.get(1)))
                    num = i;
        }
        switch (listOfColumns.get(num).getValue()){
                case 2:
                    sqlStatement = sqlStatement.concat(compareData.get(2));
                    if (type.equals(" BETWEEN "))
                        sqlStatement = sqlStatement.concat(" AND ").concat(compareData.get(3));
                    break;
                case 1:
                case 12:
                case 93:
                    sqlStatement = sqlStatement.concat("'").concat(compareData.get(2)).concat("'");
                    if (type.equals(" BETWEEN "))
                        sqlStatement = sqlStatement.concat(" AND ").concat("'").concat(compareData.get(3)).concat("'");
                    break;
                default:
                    break;
        }
        return sqlStatement;
    }
}
