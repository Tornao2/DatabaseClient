package org.example.databaseclient;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainScene {
    private final VBox crudMenuOrganizer = setUpCrudMenus();
    private CrudStages crudStageMenu;
    private DatabaseManager DBmanager;
    Runnable nextFunction;

    public void start(Stage stage, DatabaseManager readManager){
        DBmanager = readManager;
        crudStageMenu = new CrudStages(DBmanager);
        setDisconnectButton(crudMenuOrganizer);
        ScrollPane scroll = new ScrollPane(crudMenuOrganizer);
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
        return new Button[]{insertButton, readButton, updateButton, deleteButton};
    }
    private VBox setUpCrudMenus() {
        Button[] crudButtons = setUpCrudButtons();
        HBox crudButtonsOrganizer = JavaFxObjectsManager.createHBox(4, 4);
        JavaFxObjectsManager.fillOrganizer(crudButtonsOrganizer, crudButtons);
        VBox emptyOrganizer = JavaFxObjectsManager.createVBox(4, 4);
        emptyOrganizer.setId("CrudMenus");
        Pane[] crudMenuChildren = {crudButtonsOrganizer, emptyOrganizer};
        VBox returnBox = JavaFxObjectsManager.createVBox(0, 0);
        JavaFxObjectsManager.fillOrganizer(returnBox, crudMenuChildren);
        return returnBox;
    }
    private void setInsertStage(){
        int id = crudMenuOrganizer.getChildren().indexOf((crudMenuOrganizer.lookup("#CrudMenus")));
        crudMenuOrganizer.getChildren().set(id, crudStageMenu.insertStage());
    }
    private void setReadStage(){
        int id = crudMenuOrganizer.getChildren().indexOf((crudMenuOrganizer.lookup("#CrudMenus")));
        crudMenuOrganizer.getChildren().set(id, crudStageMenu.readStage());
    }
    private void setUpdateStage(){
        int id = crudMenuOrganizer.getChildren().indexOf((crudMenuOrganizer.lookup("#CrudMenus")));
        crudMenuOrganizer.getChildren().set(id, crudStageMenu.updateStage());
    }
    private void setDeleteStage(){
        int id = crudMenuOrganizer.getChildren().indexOf((crudMenuOrganizer.lookup("#CrudMenus")));
        crudMenuOrganizer.getChildren().set(id, crudStageMenu.deleteStage());
    }
    private void setDisconnectButton(VBox organizer) {
        Button disconnectButton = JavaFxObjectsManager.createButton("Rozlacz", this::disconnect);
        JavaFxObjectsManager.fillOrganizer(organizer, disconnectButton);
    }

    public void setOnDisconnect(Runnable function){
        nextFunction = function;
    }
    public void disconnect(){
        DBmanager.closeConnection();
        nextFunction.run();
    }
}
