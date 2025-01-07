package org.example.databaseclient;

import javafx.application.Application;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Scene;

import java.sql.SQLException;

public class Client extends Application {
    private final DatabaseManager DBmanager = new DatabaseManager();
    private final VBox crudMenuOrganizer = setUpCrudMenus();
    private CrudStages crudStageMenu;
    private Stage primaryStage;
    private Label debugLabel = null;

    @Override
    public void start(Stage stage)  {
        primaryStage = stage;
        Button testConnection = JavaFxObjectsManager.createButton("Try to connect", this::testConnection);
        debugLabel = JavaFxObjectsManager.createLabel("");
        Control[] controls = {testConnection, debugLabel};
        VBox manager =  JavaFxObjectsManager.createVBox(4, 4);
        JavaFxObjectsManager.fillOrganizer(manager, controls);
        Scene connectionScene = new Scene(manager, 500, 900);
        JavaFxObjectsManager.setStage(primaryStage, connectionScene, "DatabaseClient");
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

    private void testConnection() {
        try {
            DBmanager.setUpConnection();
            crudStageMenu = new CrudStages(DBmanager);
            Scene scene = new Scene(crudMenuOrganizer, 500, 900);
            JavaFxObjectsManager.setStage(primaryStage, scene, "DatabaseClient");
        } catch (SQLException e) {
            System.err.println("Nie polaczono z baza danych" + e.getMessage());
            debugLabel.setText("Nie udalo sie polaczyc");
        }
    }
}

//Operacje crud dla tabeli
//Miec procedure z kursorem
//Zapytania ze zlaczeniami, moga byc jako joiny jako widoki
