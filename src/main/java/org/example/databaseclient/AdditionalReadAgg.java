package org.example.databaseclient;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Pair;

import java.util.ArrayList;

public class AdditionalReadAgg implements ChangeListener<Boolean> {
    private final Pane organizer;
    private final DatabaseManager DBManager;

    AdditionalReadAgg(Pane readOrganizer, DatabaseManager readManager) {
        organizer = readOrganizer;
        DBManager = readManager;
    }
    @Override
    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        if (newValue){
            VBox overallOrganizer = JavaFxObjectsManager.createVBox(4, 4);
            overallOrganizer.setId("checkBoxOptionsRead");
            HBox basicToggles = setBasicToggles(overallOrganizer);
            JavaFxObjectsManager.fillOrganizer(overallOrganizer, basicToggles);
            ListView <String> listOfColumns = setColumn();
            listOfColumns.setId("ColumnChoice");
            listOfColumns.setPrefHeight(60);
            JavaFxObjectsManager.fillOrganizer(overallOrganizer, listOfColumns);
            int id = organizer.getChildren().indexOf((organizer.lookup("#Options")));
            organizer.getChildren().add(id+1, overallOrganizer);
            if (organizer.lookup("#ColumnsBoxes") != null)
                organizer.getChildren().remove((organizer.lookup("#ColumnsBoxes")));
        } else {
            organizer.getChildren().remove((organizer.lookup("#checkBoxOptionsRead")));
            if (!((CheckBox) organizer.lookup("#Group")).isSelected()) {
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
    private HBox setBasicToggles (VBox overallOrganizer) {
        ToggleGroup toggleGroup = new ToggleGroup();
        HBox toggles = JavaFxObjectsManager.createHBox(4, 4);
        toggles.setId("Toggles");
        ToggleButton tb1 = new ToggleButton("Count");
        tb1.setId("Count");
        tb1.setToggleGroup(toggleGroup);
        ToggleButton tb2 = new ToggleButton("Avg");
        tb2.setId("Avg");
        tb2.setToggleGroup(toggleGroup);
        ToggleButton tb3 = new ToggleButton("Min");
        tb3.setId("Min");
        tb3.setToggleGroup(toggleGroup);
        ToggleButton tb4 = new ToggleButton("Max");
        tb4.setId("Max");
        tb4.setToggleGroup(toggleGroup);
        ToggleButton tb5 = new ToggleButton("Sum");
        tb5.setId("Sum");
        tb5.setToggleGroup(toggleGroup);
        tb5.setSelected(true);
        toggleGroup.selectedToggleProperty().addListener((_, _, newValue) -> {
            if (newValue == tb1) {
                HBox BetweenBox = JavaFxObjectsManager.createHBox(4, 4);
                ToggleGroup specialToggleGroup = new ToggleGroup();
                ToggleButton special1 = new ToggleButton("Table");
                special1.setId("Table");
                special1.setToggleGroup(specialToggleGroup);
                ToggleButton special2 = new ToggleButton("Columns");
                special2.setId("Columns");
                special2.setToggleGroup(specialToggleGroup);
                special2.setSelected(true);
                BetweenBox.setId("TableOrColumn");
                Control [] temp = {special1, special2};
                JavaFxObjectsManager.fillOrganizer(BetweenBox, temp);
                ((VBox) organizer.lookup("#checkBoxOptionsRead")).getChildren().add(1, BetweenBox);
                specialToggleGroup.selectedToggleProperty().addListener((_, _, specNewValue) -> {
                    if (specNewValue == special1) {
                        ((VBox)organizer.lookup("#checkBoxOptionsRead")).getChildren().remove(organizer.lookup("#checkBoxOptionsRead").lookup("#ColumnChoice"));
                    } else {
                        ListView <String> listOfColumns = setColumn();
                        listOfColumns.setId("ColumnChoice");
                        listOfColumns.setPrefHeight(60);
                        JavaFxObjectsManager.fillOrganizer(overallOrganizer, listOfColumns);
                    }
                });
            } else {
                HBox BetweenBox = (HBox) organizer.lookup("#checkBoxOptionsRead").lookup("#TableOrColumn");
                if (BetweenBox != null)
                    ((VBox) organizer.lookup("#checkBoxOptionsRead")).getChildren().remove(1);
                if (organizer.lookup("#checkBoxOptionsRead").lookup("#ColumnChoice") == null) {
                    ListView<String> listOfColumns = setColumn();
                    listOfColumns.setId("ColumnChoice");
                    listOfColumns.setPrefHeight(60);
                    JavaFxObjectsManager.fillOrganizer(overallOrganizer, listOfColumns);
                }
            }
        });
        Control[] controls = {tb1, tb2, tb3, tb4, tb5};
        JavaFxObjectsManager.fillOrganizer(toggles, controls);
        return toggles;
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
