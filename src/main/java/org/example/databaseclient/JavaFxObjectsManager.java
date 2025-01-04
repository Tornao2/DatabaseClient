package org.example.databaseclient;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;

public class JavaFxObjectsManager {
    static public Button createButton(String title, Runnable functionWhenClicked ){
        Button button = new Button(title);
        button.setOnAction(_ -> functionWhenClicked.run());
        return button;
    }
    static public VBox createVBox(int gaps, int padding){
        VBox returnObj = new VBox(gaps);
        returnObj.setPadding(new Insets(padding));
        returnObj.setAlignment(Pos.TOP_CENTER);
        return returnObj;
    }
    static public HBox createHBox(int gaps, int padding){
        HBox returnObj = new HBox(gaps);
        returnObj.setAlignment(Pos.BASELINE_CENTER);
        returnObj.setPadding(new Insets(padding));
        return returnObj;
    }
    static public void fillOrganizer(Pane organizer, Control[] children){
        organizer.getChildren().addAll(children);
    }
    static public void fillOrganizer(Pane organizer, Control children){
        organizer.getChildren().addAll(children);
    }
    static public void fillOrganizer(Pane parent, Pane[] children){
        parent.getChildren().addAll(children);
    }
    static public void fillOrganizer(Pane parent, Pane children){
        parent.getChildren().addAll(children);
    }
    static public void setStage(Stage stage, Scene scene, String title){
        stage.setScene(scene);
        stage.setTitle(title);
        stage.show();
    }
    static public ListView<String> createHorizontalListView(ArrayList<String> elements){
        ListView <String> returnObj = new ListView<>();
        returnObj.getItems().addAll(elements);
        returnObj.setOrientation(Orientation.HORIZONTAL);
        return returnObj;
    }
    static public Label createLabel(String text) {
        Label returnObj = new Label(text);
        returnObj.setWrapText(true);
        return returnObj;
    }
    static public TableView<ObservableList<String>> createTableView(ArrayList<String> columnNames){
        TableView<ObservableList<String>> returnObj = new TableView<>();
        for(int i = 0; i < columnNames.size(); i++) {
            TableColumn<ObservableList<String>, String> col = new TableColumn<>(columnNames.get(i));
            int finalI = i;
            col.setCellValueFactory(cellData ->
                    new SimpleStringProperty(cellData.getValue().get(finalI))
            );
            returnObj.getColumns().add(col);
        }
        return returnObj;
    }
    static public int getObjectId(Pane organizer, String idName){
        for(int i = 0; i < organizer.getChildren().size(); i++)
            if(organizer.getChildren().get(i).getId() != null && organizer.getChildren().get(i).getId().equals(idName))
                return i;
        return -1;
    }
}
