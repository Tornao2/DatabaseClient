package org.example.databaseclient;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainScene {
    private VBox topBox;
    private CrudStages crudStageMenu;
    private DatabaseManager DBmanager;
    Runnable nextFunction;

    public void start(Stage stage, DatabaseManager readManager){
        DBmanager = readManager;
        crudStageMenu = new CrudStages(DBmanager);
        topBox = JavaFxObjectsManager.createVBox(4, 4);
        Button changeViewTable = JavaFxObjectsManager.createButton("Zmien na tryb podstawowy", this::changeButton);
        changeViewTable.setId("Change");
        JavaFxObjectsManager.fillOrganizer(topBox, changeViewTable);
        VBox crudMenuOrganizer = setUpCrudMenus();
        crudMenuOrganizer.setId("CRUD");
        JavaFxObjectsManager.fillOrganizer(topBox, crudMenuOrganizer);
        setDisconnectButton(topBox);
        ScrollPane scroll = new ScrollPane(topBox);
        scroll.setFitToHeight(true);
        scroll.setFitToWidth(true);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        Scene scene = new Scene(scroll, 500, 900);
        stage.setScene(scene);
    }

    private Button[] setUpCrudButtons() {
        Button insertButton = JavaFxObjectsManager.createButton("Wprowadz", this::setInsertStage);
        Button readButton = JavaFxObjectsManager.createButton("Czytaj", this::setReadStage);
        Button updateButton = JavaFxObjectsManager.createButton("Zaaktualizuj", this::setUpdateStage);
        Button deleteButton = JavaFxObjectsManager.createButton("Usun", this::setDeleteStage);
        Button readViewButton = JavaFxObjectsManager.createButton("Czytaj Widok", this::setReadViewStage);
        return new Button[]{insertButton, readButton, updateButton, deleteButton, readViewButton};
    }
    private VBox setUpCrudMenus() {
        Button[] crudButtons = setUpCrudButtons();
        HBox crudButtonsOrganizer = JavaFxObjectsManager.createHBox(4, 4);
        JavaFxObjectsManager.fillOrganizer(crudButtonsOrganizer, crudButtons);
        VBox emptyOrganizer = JavaFxObjectsManager.createVBox(4, 4);
        emptyOrganizer.setId("CrudMenus");
        Pane[] crudMenuChildren = {crudButtonsOrganizer, emptyOrganizer};
        VBox returnBox = JavaFxObjectsManager.createVBox(4, 4);
        JavaFxObjectsManager.fillOrganizer(returnBox, crudMenuChildren);
        return returnBox;
    }
    private void setInsertStage(){
        int id = ((VBox) topBox.lookup("#CRUD")).getChildren().indexOf(((topBox.lookup("#CRUD")).lookup("#CrudMenus")));
        DBmanager.switchTableView(true);
        ((VBox) topBox.lookup("#CRUD")).getChildren().set(id, crudStageMenu.insertStage());
    }
    private void setReadStage(){
        int id = ((VBox) topBox.lookup("#CRUD")).getChildren().indexOf((topBox.lookup("#CRUD")).lookup("#CrudMenus"));
        DBmanager.switchTableView(true);
        ((VBox) topBox.lookup("#CRUD")).getChildren().set(id, crudStageMenu.readStage());
    }
    private void setUpdateStage(){
        int id = ((VBox) topBox.lookup("#CRUD")).getChildren().indexOf(((topBox.lookup("#CRUD")).lookup("#CrudMenus")));
        DBmanager.switchTableView(true);
        ((VBox) topBox.lookup("#CRUD")).getChildren().set(id, crudStageMenu.updateStage());
    }
    private void setDeleteStage(){
        int id = ((VBox) topBox.lookup("#CRUD")).getChildren().indexOf(((topBox.lookup("#CRUD")).lookup("#CrudMenus")));
        DBmanager.switchTableView(true);
        ((VBox) topBox.lookup("#CRUD")).getChildren().set(id, crudStageMenu.deleteStage());
    }
    private void setReadViewStage(){
        int id = ((VBox) topBox.lookup("#CRUD")).getChildren().indexOf(((topBox.lookup("#CRUD")).lookup("#CrudMenus")));
        DBmanager.switchTableView(false);
        ((VBox) topBox.lookup("#CRUD")).getChildren().set(id, crudStageMenu.readStage());
    }
    private void setDisconnectButton(VBox topBox) {
        Button disconnectButton = JavaFxObjectsManager.createButton("Rozlacz", this::disconnect);
        JavaFxObjectsManager.fillOrganizer(topBox, disconnectButton);
    }
    private void changeButton() {
        if (((Button) topBox.lookup("#Change")).getText().equals("Zmien na tryb podstawowy")) {
            ((Button) topBox.lookup("#Change")).setText("Zmien na tryb zaawansowany");
            ((VBox) topBox.lookup("#CRUD")).getChildren().clear();
        }
        else {
            VBox crudMenu = setUpCrudMenus();
            crudMenu.setId("CRUD");
            topBox.getChildren().set(1, crudMenu);
            ((Button) topBox.lookup("#Change")).setText("Zmien na tryb podstawowy");
        }
    }
    public void setOnDisconnect(Runnable function){
        nextFunction = function;
    }
    public void disconnect(){
        DBmanager.closeConnection();
        nextFunction.run();
    }
}
