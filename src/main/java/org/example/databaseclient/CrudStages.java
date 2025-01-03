package org.example.databaseclient;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.util.Pair;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

public class CrudStages {
    private final DatabaseManager DBmanager;
    VBox organizer;

    public CrudStages(DatabaseManager readDBManager) {
        DBmanager = readDBManager;
    }
    public VBox insertStage() {
        organizer = sharedSetUp();
        ListView<String> listView = (ListView<String>) organizer.getChildren().getFirst();
        organizer.getChildren().getFirst().setOnMousePressed(_ -> displayColumnsForInsert(listView.getSelectionModel().getSelectedItem()));
        return organizer;
    }
    public VBox readStage() {
        organizer = sharedSetUp();
        ListView<String> listView = (ListView<String>) organizer.getChildren().getFirst();
        organizer.getChildren().getFirst().setOnMousePressed(_ -> displayColumnsForRead(listView.getSelectionModel().getSelectedItem()));
        return organizer;
    }
    public VBox updateStage() {
        organizer = sharedSetUp();
        return organizer;
    }
    public VBox deleteStage() {
        organizer = sharedSetUp();
        return organizer;
    }
    private VBox sharedSetUp() {
        VBox returnObj = JavaFxObjectsManager.createVBox(4, 4);
        ListView<String> tables = createTableChoice();
        if (tables != null)
            tables.setPrefHeight(60);
        JavaFxObjectsManager.fillOrganizer(returnObj, tables);
        return returnObj;
    }
    private ArrayList <String> processTable() {
        ResultSet tables = DBmanager.getTables();
        ArrayList <String> names = new ArrayList<>();
        while (true) {
            try {
                if (!tables.next()) break;
            } catch (SQLException e) {
                System.err.println("Blad przy przechodzeniu po tabeli: " + e.getMessage());
                return null;
            }
            try {
                names.add(tables.getString("TABLE_NAME"));
            } catch (SQLException e) {
                System.err.println("Blad przy pobraniu nazwy: " + e.getMessage());
                return null;
            }
        }
        return names;
    }
    private ListView <String> createTableChoice () {
        ArrayList <String> tableNames = processTable();
        if (tableNames == null) return null;
        return JavaFxObjectsManager.createHorizontalListView(tableNames);
    }
    private void finishDisplaying(String text, Runnable function){
        Button insert = JavaFxObjectsManager.createButton(text, function);
        Label placeholder = JavaFxObjectsManager.createLabel("");
        Control [] finishControls = {insert, placeholder};
        JavaFxObjectsManager.fillOrganizer(organizer, finishControls);
    }
    private void finishSendingStatements(String text){
        Label result = (Label) organizer.getChildren().getLast();
        result.setText(text);
        organizer.getChildren().set(organizer.getChildren().size() - 1, result);
    }
    private void displayColumnsForInsert (String selectedTable) {
        ArrayList<Pair<String, Integer>> columns = DBmanager.getColumnNames(selectedTable);
        if (organizer.getChildren().size() > 1)
            organizer.getChildren().subList(1, organizer.getChildren().size()).clear();
        for(Pair <String, Integer> pair: columns){
            int type = pair.getValue();
            Label columnName = JavaFxObjectsManager.createLabel(pair.getKey());
            Control[] temp = new Control[0];
            switch (type){
                case 1:
                case 2:
                case 12:
                    TextField stringField = JavaFxObjectsManager.createTextField();
                    temp = new Control[]{columnName, stringField};
                    break;
                case 93:
                    DatePicker dateField = JavaFxObjectsManager.createDataPicker();
                    TextField hourField = JavaFxObjectsManager.createTextField();
                    temp = new Control[]{columnName, dateField, hourField};
                    break;
                default:
                    System.err.println("Nie obslugiwany typ " + pair.getValue() + " " + pair.getKey());
                    type = -1;
                    break;
            }
            if (type == -1) continue;
            JavaFxObjectsManager.fillOrganizer(organizer, temp);
        }
        finishDisplaying("Insert into table", this::sendInsertStatement);
    }
    private void sendInsertStatement() {
        ArrayList<String> data = new ArrayList<>();
        for(int i = 0; i < organizer.getChildren().size(); i++){
            Node currentItem = organizer.getChildren().get(i);
            if (currentItem instanceof TextField) {
                String text = ((TextField) currentItem).getText();
                data.add(text);
            } else if (currentItem instanceof DatePicker) {
                LocalDate date = ((DatePicker) currentItem).getValue();
                LocalTime time;
                try {
                    time = LocalTime.parse(((TextField) organizer.getChildren().get(i + 1)).getText());
                } catch (Exception e) {
                    String resultMessage = "Incorrect data format(x:y)";
                    finishSendingStatements(resultMessage);
                    return;
                }
                LocalDateTime dateTime = date.atTime(time);
                data.add(String.valueOf(dateTime));
                i++;
            }
        }
        ListView<String> listView = (ListView<String>) organizer.getChildren().getFirst();
        String resultMessage = DBmanager.insertIntoTable(listView.getSelectionModel().getSelectedItem(), data);
        finishSendingStatements(resultMessage);
    }
    private void displayColumnsForRead(String selectedTable){
        ArrayList<Pair<String, Integer>> columns = DBmanager.getColumnNames(selectedTable);
        if (organizer.getChildren().size() > 1)
            organizer.getChildren().subList(1, organizer.getChildren().size()).clear();
        for(Pair <String, Integer> pair: columns){
            CheckBox columnCheck = JavaFxObjectsManager.createCheckBox(pair.getKey());
            JavaFxObjectsManager.fillOrganizer(organizer, columnCheck);
        }
        finishDisplaying("Read from table", this::sendReadStatement);
    }
    private void sendReadStatement(){
        if (organizer.getChildren().getLast() instanceof TableView<?>)
            organizer.getChildren().removeLast();
        ArrayList<Boolean> data = new ArrayList<>();
        organizer.getChildren().forEach(node -> {
            if (node instanceof CheckBox) {
                Boolean check = ((CheckBox) node).isSelected();
                data.add(check);
            }
        });
        ListView<String> listView = (ListView<String>) organizer.getChildren().getFirst();
        Pair <ResultSet, String> results = DBmanager.readFromTable(listView.getSelectionModel().getSelectedItem(), data);
        String resultMessage = results.getValue();
        finishSendingStatements(resultMessage);
        ArrayList<Pair<String, Integer>> columnNames = DBmanager.getColumnNames(listView.getSelectionModel().getSelectedItem());
        ArrayList<String> finishedColumnNames = new ArrayList<>();
        for(int i = 0; i < columnNames.size(); i++)
            if (data.get(i)) finishedColumnNames.add(columnNames.get(i).getKey());
        TableView<ObservableList<String>> resultsTable = JavaFxObjectsManager.createTableView(finishedColumnNames);
        ObservableList<ObservableList<String>> allRows = FXCollections.observableArrayList();
        while (true){
            try {
                if (!results.getKey().next()) break;
                ObservableList<String> row = FXCollections.observableArrayList();
                for(int i = 0; i < resultsTable.getColumns().size(); i++)
                    row.add(results.getKey().getString(finishedColumnNames.get(i)));
                allRows.add(row);
            } catch (SQLException e) {
                System.err.println("Nie udalo sie pobrac wynikow: " + e.getMessage());
            }
        }
        resultsTable.setItems(allRows);
        JavaFxObjectsManager.fillOrganizer(organizer, resultsTable);
    }
}
