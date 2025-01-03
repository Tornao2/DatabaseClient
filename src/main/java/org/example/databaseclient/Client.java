package org.example.databaseclient;

import javafx.application.Application;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Scene;

public class Client extends Application {
    private final DatabaseManager DBmanager = new DatabaseManager();
    private final VBox crudMenuOrganizer = setUpCrudMenus();
    private CrudStages crudStageMenu;

    @Override
    public void start(Stage stage)  {
        DBmanager.setUpConnection();
        crudStageMenu = new CrudStages(DBmanager);
        Scene scene = JavaFxObjectsManager.createScene(crudMenuOrganizer, 500, 900);
        JavaFxObjectsManager.setStage(stage, scene, "DatabaseClient");
    }
    private Button[] setUpCrudButtons() {
        Button insertButton = JavaFxObjectsManager.createButton("Insert Data", this::setInsertStage);
        Button readButton = JavaFxObjectsManager.createButton("Read Data", this::setReadStage);
        Button updateButton = JavaFxObjectsManager.createButton("Update Data", this::setUpdateStage);
        Button deleteButton = JavaFxObjectsManager.createButton("Delete Data", this::setDeleteStage);
        return new Button[]{insertButton, readButton, updateButton, deleteButton};
    }
    private VBox setUpCrudMenus() {
        Button[] crudButtons = setUpCrudButtons();
        HBox crudButtonsOrganizer = JavaFxObjectsManager.createHBox(4, 4);
        JavaFxObjectsManager.fillOrganizer(crudButtonsOrganizer, crudButtons);
        VBox emptyOrganizer = JavaFxObjectsManager.createVBox(4, 4);
        Pane[] crudMenuChildren = {crudButtonsOrganizer, emptyOrganizer};
        VBox returnBox = JavaFxObjectsManager.createVBox(0, 0);
        JavaFxObjectsManager.fillOrganizer(returnBox, crudMenuChildren);
        return returnBox;
    }
    private void setInsertStage(){
        crudMenuOrganizer.getChildren().set(1, crudStageMenu.insertStage());
    }
    private void setReadStage(){
        crudMenuOrganizer.getChildren().set(1, crudStageMenu.readStage());
    }
    private void setUpdateStage(){
        crudMenuOrganizer.getChildren().set(1, crudStageMenu.updateStage());
    }
    private void setDeleteStage(){
        crudMenuOrganizer.getChildren().set(1, crudStageMenu.deleteStage());
    }
}

//Operacje crud dla tabeli
//Miec procedure z kursorem
//Zapytania ze zlaczeniami, moga byc jako joiny
//Wykorzystac operatory grupujace i agregujace
