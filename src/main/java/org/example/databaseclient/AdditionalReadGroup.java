package org.example.databaseclient;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Pair;

import java.util.ArrayList;

public class AdditionalReadGroup implements ChangeListener<Boolean>  {
    private final Pane organizer;
    private final DatabaseManager DBManager;

    AdditionalReadGroup(Pane readOrganizer, DatabaseManager readManager) {
        organizer = readOrganizer;
        DBManager = readManager;
    }
    @Override
    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        if (newValue) {
            VBox overallOrganizer = JavaFxObjectsManager.createVBox(4, 4);
            overallOrganizer.setId("checkBoxGroupRead");
            ListView <String> listOfColumns = setColumn();
            listOfColumns.setId("ColumnChoice");
            listOfColumns.setPrefHeight(60);
            JavaFxObjectsManager.fillOrganizer(overallOrganizer, listOfColumns);
            int id = organizer.getChildren().indexOf((organizer.lookup("#ActionButton")));
            organizer.getChildren().add(id, overallOrganizer);
            if (organizer.lookup("#ColumnsBoxes") != null)
                organizer.getChildren().remove((organizer.lookup("#ColumnsBoxes")));
        } else {
            organizer.getChildren().remove((organizer.lookup("#checkBoxGroupRead")));
            if (!((CheckBox) organizer.lookup("#Options")).isSelected()) {
                ListView<String> listView = (ListView<String>) organizer.getChildren().getFirst();
                ArrayList<Pair<String, Integer>> columns = DBManager.getColumnNames(listView.getSelectionModel().getSelectedItem());
                VBox columnGroup = JavaFxObjectsManager.createVBox(4, 4);
                columnGroup.setId("ColumnsBoxes");
                for (Pair<String, Integer> pair : columns)
                    JavaFxObjectsManager.fillOrganizer(columnGroup, new CheckBox(pair.getKey()));
                int id = organizer.getChildren().indexOf((organizer.lookup("#Options")));
                organizer.getChildren().add(id + 1, columnGroup);
            }
        }
    }
    private ListView<String> setColumn() {
        ListView<String> listView = (ListView<String>) organizer.getChildren().getFirst();
        ArrayList<Pair<String, Integer>> columnNames = DBManager.getColumnNames(listView.getSelectionModel().getSelectedItem());
        ArrayList <String> onlyColumnNames = new ArrayList<>();
        for(Pair<String, Integer> pair: columnNames)
            onlyColumnNames.add(pair.getKey());
        return JavaFxObjectsManager.createHorizontalListView(onlyColumnNames);
    }
}
