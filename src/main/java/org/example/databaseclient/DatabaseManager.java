package org.example.databaseclient;

import javafx.util.Pair;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class DatabaseManager {
    private Connection DBConnection;
    private DatabaseMetaData metaData;
    String username;

    public void setUpConnection (String url, String name, String password) throws SQLException {
        DBConnection = DriverManager.getConnection(url, name, password);
        username = name;
        metaData = DBConnection.getMetaData();
    }
    public ResultSet getTables() {
        try {
            return metaData.getTables(null, username, "%", new String[]{"TABLE"});
        } catch (SQLException e) {
            System.err.println("Couldn't get table: " + e.getMessage());
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
            System.err.println("Couldn't get columns: " + e.getMessage());
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
                        if (data.get(i).isEmpty())
                            data.set(i, "0");
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
            resultMessage = "Rzad wprowadzony";
        } catch (SQLException e) {
            System.err.println("Error during insert: " + e.getMessage());
            resultMessage = e.getMessage();
        }
        return resultMessage;
    }

    public Pair<ResultSet,String> readFromTable(String tableName, ArrayList<?>data, ArrayList<String> compareData, String groupColumn){
        StringBuilder values = new StringBuilder();
        String resultMessage = "";
        if(compareData != null && !compareData.isEmpty()) {
            if (compareData.getFirst().equals("Columns")) {
                ArrayList<Boolean> dataReal = (ArrayList<Boolean>) data;
                ArrayList<Pair<String, Integer>> listOfColumns = getColumnNames(tableName);
                for (int i = 0; i < dataReal.size(); i++)
                    if (dataReal.get(i)) {
                        values.append(listOfColumns.get(i).getKey());
                        values.append(", ");
                    }
                if(!values.isEmpty())
                    values.delete(values.length() - 2, values.length());
            } else if (compareData.getFirst().equals("Agg")){
                ArrayList<String> dataReal = (ArrayList<String>) data;
                values.append(dataReal.getFirst());
                if (groupColumn != null)
                    values.append(", ").append(groupColumn);
            } else
                values.append(groupColumn);
        } else
            values.append(groupColumn);
        String sqlStatement = "SELECT " + values + " FROM " + tableName;
        if (compareData != null && !compareData.isEmpty() && compareData.size() != 1) {
            boolean check = true;
            for (String compareDatum : compareData)
                if (compareDatum == null) {
                    check = false;
                    break;
                }
            if (check)
                sqlStatement = compareDataModify(sqlStatement, compareData, tableName);
        }
        if(groupColumn!= null)
            sqlStatement = sqlStatement.concat(" GROUP BY " + groupColumn);
        ResultSet rowsQueried;
        try {
            PreparedStatement statement = DBConnection.prepareStatement(sqlStatement);
            rowsQueried = statement.executeQuery();
        } catch (SQLException e) {
            System.err.println("Error during read: " + e.getMessage());
            rowsQueried = null;
            resultMessage = e.getMessage();
        }
        return new Pair<>(rowsQueried, resultMessage);
    }
    private String compareDataModify(String sqlStatement, ArrayList<String> compareData, String tableName) {
        ArrayList <Pair<String,Integer>> listOfColumns = getColumnNames(tableName);
        sqlStatement = sqlStatement.concat(" WHERE ").concat(compareData.get(2));
        String type = switch (compareData.get(1)) {
            case "Equal" -> " = ";
            case "Lower" -> " < ";
            case "Higher" -> " > ";
            case "Between" -> " BETWEEN ";
            default -> "";
        };
        sqlStatement = sqlStatement.concat(type);
        ArrayList<Pair<String, Integer>> columns = getColumnNames(tableName);
        int num = 0;
        for (int i = 0; i < columns.size(); i++)
                if(columns.get(i).getKey().equals(compareData.get(2)))
                    num = i;
        switch (listOfColumns.get(num).getValue()){
                case 2:
                    sqlStatement = sqlStatement.concat(compareData.get(3));
                    if (type.equals(" BETWEEN "))
                        sqlStatement = sqlStatement.concat(" AND ").concat(compareData.get(4));
                    break;
                case 1:
                case 12:
                case 93:
                    sqlStatement = sqlStatement.concat("'").concat(compareData.get(3)).concat("'");
                    if (type.equals(" BETWEEN "))
                        sqlStatement = sqlStatement.concat(" AND ").concat("'").concat(compareData.get(4)).concat("'");
                    break;
                default:
                    break;
        }
        return sqlStatement;
    }

    public String deleteFromTable(String tableName, ArrayList<String>data){
        StringBuilder values = new StringBuilder();
        String resultMessage;
        if (!data.getFirst().equals("Table")) {
            values.append(" WHERE ").append(data.get(1));
            values = deleteCompareSigns(values, data);
        }
        String sqlStatement = "DELETE FROM " + tableName + values;
        try {
            PreparedStatement statement = DBConnection.prepareStatement(sqlStatement);
            int rowsDeleted = statement.executeUpdate();
            resultMessage = "Usunieto " + rowsDeleted + " rzedow";
        } catch (SQLException e) {
            System.err.println("Error during delete: " + e.getMessage());
            resultMessage = e.getMessage();
        }
        return resultMessage;
    }
    public StringBuilder deleteCompareSigns(StringBuilder readValues, ArrayList<String> compareData){
        String type = switch (compareData.get(2)) {
            case "Equal" -> " = ";
            case "Lower" -> " < ";
            case "Higher" -> " > ";
            case "Between" -> " BETWEEN ";
            default -> "";
        };
        readValues.append(type).append(compareData.get(3));
        if (type.equals(" BETWEEN "))
            readValues.append(" AND ").append(compareData.get(4));
        return readValues;
    }

    public String updateTable(String tableName, ArrayList<String>data, ArrayList<Pair<String, String>> columnData){
        String resultMessage = "";
        if (data.size() > 1) {
            StringBuilder compare = new StringBuilder();
            StringBuilder columns = new StringBuilder();
            ArrayList<Pair<String, Integer>> listOfColumns = getColumnNames(tableName);
            compare = updateCompareSigns(compare, data, tableName);
            for (int i = 0; i < columnData.size(); i++) {
                columns.append(columnData.get(i).getKey()).append(" = ");
                Pair<String, Integer> temp = null;
                for (Pair<String, Integer> pair : listOfColumns) {
                    if (pair.getKey().equals(columnData.get(i).getKey())) {
                        temp = pair;
                        break;
                    }
                }
                if (temp != null) {
                    switch (temp.getValue()) {
                        case 2:
                            columns.append(columnData.get(i).getValue());
                            break;
                        case 1:
                        case 12:
                            columns.append("'").append(columnData.get(i).getValue()).append("'");
                            break;
                        case 93:
                            try {
                                Timestamp timestamp = Timestamp.valueOf(columnData.get(i).getValue());
                                columns.append("TO_TIMESTAMP('").append(timestamp).append("', 'YYYY-MM-DD HH24:MI:SS.ff')");
                            } catch(IllegalArgumentException _){
                                return "Zly format daty (yyyy-mm-dd hh:mm:ss)";
                            }
                            break;
                        default:
                            break;
                    }
                }
                if (i < columnData.size() - 1)
                    columns.append(", ");
            }
            String sqlStatement = "UPDATE " + tableName + " SET " + columns + compare;
            try {
                PreparedStatement statement = DBConnection.prepareStatement(sqlStatement);
                int rowsDeleted = statement.executeUpdate();
                resultMessage = "Zmieniono " + rowsDeleted + " rzedow";
            } catch (SQLException e) {
                System.err.println("Error during update: " + e.getMessage());
                resultMessage = e.getMessage();
            }
        }
        return resultMessage;
    }
    public StringBuilder updateCompareSigns(StringBuilder readValues, ArrayList<String> compareData, String tableName) {
        readValues.append(" WHERE " ).append(compareData.get(0));
        String type = switch (compareData.get(1)) {
            case "Equal" -> " = ";
            case "Lower" -> " < ";
            case "Higher" -> " > ";
            case "Between" -> " BETWEEN ";
            default -> "";
        };
        readValues.append(type);
        ArrayList<Pair<String, Integer>> listOfColumns = getColumnNames(tableName);
        Pair<String, Integer> temp = null;
        for (Pair<String, Integer> pair : listOfColumns) {
            if (pair.getKey().equals(compareData.getFirst())) {
                temp = pair;
                break;
            }
        }
        if (temp != null) {
            switch (temp.getValue()) {
                case 2:
                    readValues.append(compareData.get(2));
                    break;
                case 1:
                case 93:
                case 12:
                    readValues.append("'").append(compareData.get(2)).append("'");
                    break;
                default:
                    break;
            }
        }
        if (type.equals(" BETWEEN ")){
            readValues.append(" AND ");
        if (temp != null) {
            switch (temp.getValue()) {
                case 2:
                    readValues.append(compareData.get(3));
                    break;
                case 1:
                case 93:
                case 12:
                    readValues.append("'").append(compareData.get(3)).append("'");
                    break;
                default:
                    break;
            }
        }
        }
        return readValues;
    }

    public void closeConnection() {
        try {
            DBConnection.close();
        } catch (SQLException e) {
            System.err.println("Couldn't disconnect " + e.getMessage());
        }
    }
}
