package org.example.databaseclient;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Pair;

import java.util.ArrayList;

public class ReadCheckBoxListener implements ChangeListener<Boolean>{
    private final Pane organizer;
    private final DatabaseManager DBManager;
    ReadCheckBoxListener(Pane readOrganizer, DatabaseManager readManager) {
        organizer = readOrganizer;
        DBManager = readManager;
    }
    @Override
    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        if (newValue){
            VBox overallOrganizer = JavaFxObjectsManager.createVBox(4, 4);
            overallOrganizer.setId("checkBoxLogicRead");
            HBox basicToggles = setBasicToggles();
            HBox valueBox =  setValueBox();
            ListView <String> listOfColumns = setColumn();
            listOfColumns.setPrefHeight(60);
            int id = JavaFxObjectsManager.getObjectId(organizer, "ActionButton");
            JavaFxObjectsManager.fillOrganizer(overallOrganizer, basicToggles);
            JavaFxObjectsManager.fillOrganizer(overallOrganizer, valueBox);
            JavaFxObjectsManager.fillOrganizer(overallOrganizer, listOfColumns);
            organizer.getChildren().add(id, overallOrganizer);
        } else {
            int id = JavaFxObjectsManager.getObjectId(organizer, "checkBoxLogicRead");
            organizer.getChildren().remove(id);
        }
    }
    private HBox setBasicToggles () {
        ToggleGroup toggleGroup = new ToggleGroup();
        HBox toggles = JavaFxObjectsManager.createHBox(4, 4);
        ToggleButton tb1 = new ToggleButton("Rowny");
        tb1.setToggleGroup(toggleGroup);
        tb1.setSelected(true);
        ToggleButton tb2 = new ToggleButton("Mniejszy(jesli ma to sens)");
        tb2.setToggleGroup(toggleGroup);
        ToggleButton tb3 = new ToggleButton("Wiekszy(jesli ma to sens)");
        tb3.setToggleGroup(toggleGroup);
        Control[] controls = {tb1, tb2, tb3};
        JavaFxObjectsManager.fillOrganizer(toggles, controls);
        return toggles;
    }
    private HBox setValueBox() {
        Label valueLabel = JavaFxObjectsManager.createLabel("Wartosc");
        TextField valueField = new TextField();
        Control [] valueControls = {valueLabel, valueField};
        HBox valueBox = JavaFxObjectsManager.createHBox(4, 4);
        JavaFxObjectsManager.fillOrganizer(valueBox, valueControls);
        return valueBox;
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
