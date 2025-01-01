package org.example.databaseclient;

import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.util.Pair;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class CrudStages {
    private final DatabaseManager DBmanager;

    public CrudStages(DatabaseManager readDBManager) {
        DBmanager = readDBManager;
    }
    public VBox insertStage() {
        VBox organizer = sharedSetUp();
        ListView<String> listView = (ListView<String>) organizer.getChildren().getFirst();
        organizer.getChildren().getFirst().setOnMousePressed(_ -> displayColumnsForInsert(organizer, listView.getSelectionModel().getSelectedItem()));
        Button insertButton = JavaFxObjectsManager.createButton("Insert into table", emptyFunc());
        JavaFxObjectsManager.fillOrganizer(organizer, insertButton);
        return organizer;
    }
    public VBox readStage() {
        VBox organizer = sharedSetUp();
        return organizer;
    }
    public VBox updateStage() {
        VBox organizer = sharedSetUp();
        return organizer;
    }
    public VBox deleteStage() {
        VBox organizer = sharedSetUp();
        return organizer;
    }
    private VBox sharedSetUp() {
        VBox returnObj = JavaFxObjectsManager.createVBox(8, 8);
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
    private Runnable emptyFunc() {
        return null;
    }
    private void displayColumnsForInsert (VBox organizer, String selectedTable) {
        ArrayList<Pair<String, String>> columns = new ArrayList<>();
        DBmanager.updateListView(columns, selectedTable);
        if (organizer.getChildren().size() > 2)
            organizer.getChildren().subList(1, organizer.getChildren().size() - 1).clear();
    }
}
