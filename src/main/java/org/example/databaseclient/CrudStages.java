package org.example.databaseclient;

import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.util.Pair;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
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
    private void displayColumnsForInsert (String selectedTable) {
        ArrayList<Pair<String, Integer>> columns = new ArrayList<>();
        DBmanager.updateListView(columns, selectedTable);
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
                    temp = new Control[]{columnName, dateField};
                    break;
                default:
                    System.err.println("Nie obslugiwany typ " + pair.getValue() + " " + pair.getKey());
                    type = -1;
                    break;
            }
            if (type == -1) continue;
            JavaFxObjectsManager.fillOrganizer(organizer, temp);
        }
        Button insert = JavaFxObjectsManager.createButton("Insert into table", this::sendInsertStatement);
        Label placeholder = JavaFxObjectsManager.createLabel("");
        Control [] finishControls = {insert, placeholder};
        JavaFxObjectsManager.fillOrganizer(organizer, finishControls);
    }
    private void sendInsertStatement() {
        ArrayList<String> data = new ArrayList<>();
        organizer.getChildren().forEach(node -> {
            if (node instanceof TextField) {
                String text = ((TextField) node).getText();
                data.add(text);
            } else if (node instanceof DatePicker) {
                LocalDate date = ((DatePicker) node).getValue();
                data.add(String.valueOf(date));
            }
        });
        ListView<String> listView = (ListView<String>) organizer.getChildren().getFirst();
        String resultMessage = DBmanager.insertIntoTable(listView.getSelectionModel().getSelectedItem(), data);
        Label result = (Label) organizer.getChildren().getLast();
        result.setText(resultMessage);
        organizer.getChildren().set(organizer.getChildren().size() - 1, result);
    }
}
