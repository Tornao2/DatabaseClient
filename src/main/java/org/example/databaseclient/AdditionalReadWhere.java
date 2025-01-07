package org.example.databaseclient;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Pair;

import java.util.ArrayList;

public class AdditionalReadWhere implements ChangeListener<Boolean>{
    private final Pane organizer;
    private final DatabaseManager DBManager;
    AdditionalReadWhere(Pane readOrganizer, DatabaseManager readManager) {
        organizer = readOrganizer;
        DBManager = readManager;
    }
    @Override
    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        if (newValue){
            VBox overallOrganizer = JavaFxObjectsManager.createVBox(4, 4);
            overallOrganizer.setId("checkBoxLogicRead");
            HBox basicToggles = setBasicToggles();
            TextField valueField = setValueField();
            VBox overallValueBox = JavaFxObjectsManager.createVBox(4, 4);
            overallValueBox.setId("ValueBox");
            overallValueBox.getChildren().add(valueField);
            ListView <String> listOfColumns = setColumn();
            listOfColumns.setId("ColumnChoice");
            listOfColumns.setPrefHeight(60);
            int id = organizer.getChildren().indexOf((organizer.lookup("#Compare")))+1;
            JavaFxObjectsManager.fillOrganizer(overallOrganizer, basicToggles);
            JavaFxObjectsManager.fillOrganizer(overallOrganizer, overallValueBox);
            JavaFxObjectsManager.fillOrganizer(overallOrganizer, listOfColumns);
            organizer.getChildren().add(id, overallOrganizer);
        } else
            organizer.getChildren().remove((organizer.lookup("#checkBoxLogicRead")));
    }
    private HBox setBasicToggles () {
        ToggleGroup toggleGroup = new ToggleGroup();
        HBox toggles = JavaFxObjectsManager.createHBox(4, 4);
        toggles.setId("Toggles");
        ToggleButton tb1 = new ToggleButton("Rowny");
        tb1.setId("Equal");
        tb1.setToggleGroup(toggleGroup);
        tb1.setSelected(true);
        ToggleButton tb2 = new ToggleButton("Mniejszy");
        tb2.setId("Lower");
        tb2.setToggleGroup(toggleGroup);
        ToggleButton tb3 = new ToggleButton("Wiekszy");
        tb3.setId("Higher");
        tb3.setToggleGroup(toggleGroup);
        ToggleButton tb4 = new ToggleButton("Pomiedzy");
        tb4.setId("Between");
        tb4.setToggleGroup(toggleGroup);
        toggleGroup.selectedToggleProperty().addListener((_, _, newValue) -> {
            if (newValue == tb4) {
                VBox BetweenBox = JavaFxObjectsManager.createVBox(4, 4);
                BetweenBox.setId("BetweenBox");
                Label moreLabel = new Label("Wiecej niz: ");
                TextField valueField = new TextField();
                valueField.setId("Dodatkowa");
                Label lessLabel = new Label("mniej niz: ");
                Control [] temp = {moreLabel, valueField, lessLabel};
                JavaFxObjectsManager.fillOrganizer(BetweenBox, temp);
                ((VBox) organizer.lookup("#checkBoxLogicRead").lookup("#ValueBox")).getChildren().addFirst(BetweenBox);
            } else {
                VBox BetweenBox = (VBox) organizer.lookup("#checkBoxLogicRead").lookup("#ValueBox");
                if (BetweenBox.getChildren().size() != 1)
                    ((VBox) organizer.lookup("#checkBoxLogicRead").lookup("#ValueBox")).getChildren().removeFirst();
            }
        });
        Control[] controls = {tb1, tb2, tb3, tb4};
        JavaFxObjectsManager.fillOrganizer(toggles, controls);
        return toggles;
    }
    private TextField setValueField() {
        TextField valueField = new TextField();
        valueField.setId("Wartosc");
        return valueField;
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
