package org.example.databaseclient;

import javafx.application.Application;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Scene;

public class Client extends Application {
    private final DatabaseManager DBmanager = new DatabaseManager();
    private final VBox crudMenuOrganizer = setUpCrudMenus();
    @Override
    public void start(Stage stage)  {
        DBmanager.setUpConnection();
        BorderPane paneObject = JavaFxObjectsManager.createBorderPane(crudMenuOrganizer);
        Scene scene = JavaFxObjectsManager.createScene(paneObject, 400, 500);
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
        VBox returnBox = JavaFxObjectsManager.createVBox(0, 0);
        Button[] crudButtons = setUpCrudButtons();
        HBox crudButtonsOrganizer = JavaFxObjectsManager.createHBox(8, 8);
        JavaFxObjectsManager.fillOrganizer(crudButtonsOrganizer, crudButtons);
        VBox emptyOrganizer = JavaFxObjectsManager.createVBox(8, 8);
        Pane[] crudMenuChildren = {crudButtonsOrganizer, emptyOrganizer};
        JavaFxObjectsManager.foldOrganizers(returnBox, crudMenuChildren);
        return returnBox;
    }
    private void setInsertStage(){
        crudMenuOrganizer.getChildren().set(1, CrudStages.insertStage());
    }
    private void setReadStage(){
        crudMenuOrganizer.getChildren().set(1, CrudStages.readStage());
    }
    private void setUpdateStage(){
        crudMenuOrganizer.getChildren().set(1, CrudStages.updateStage());
    }
    private void setDeleteStage(){
        crudMenuOrganizer.getChildren().set(1, CrudStages.deleteStage());
    }
}

//Operacje crud dla tabeli
//Miec procedure z kursorem
//Zapytania ze zlaczeniami, moga byc jako joiny
//Wykorzystac operatory grupujace i agregujace
