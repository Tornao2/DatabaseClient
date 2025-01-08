package org.example.databaseclient;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;

public class Login {
    private Label debugLabel = null;
    private final DatabaseManager DBmanager = new DatabaseManager();
    private Runnable nextFunction;
    VBox manager;

    public void start(Stage stage){
        Label urlLabel = JavaFxObjectsManager.createLabel("url bazy danych");
        TextField url = new TextField("jdbc:oracle:thin:@localhost:1521:XE");
        url.setId("url");
        Label loginLabel = JavaFxObjectsManager.createLabel("login uzytkownika");
        TextField login = new TextField("C##CLIENT");
        login.setId("login");
        Label passwordLabel = JavaFxObjectsManager.createLabel("haslo uzytkownika");
        TextField password = new TextField("projekt");
        password.setId("password");
        Button testConnection = JavaFxObjectsManager.createButton("Try to connect", this::testConnection);
        debugLabel = JavaFxObjectsManager.createLabel("");
        Control[] controls = {urlLabel, url, loginLabel, login, passwordLabel, password,testConnection, debugLabel};
        manager =  JavaFxObjectsManager.createVBox(4, 4);
        JavaFxObjectsManager.fillOrganizer(manager, controls);
        Scene connectionScene = new Scene(manager, 500, 900);
        JavaFxObjectsManager.setStage(stage, connectionScene, "DatabaseClient");
    }

    private void testConnection() {
        String url = ((TextField) manager.lookup("#url")).getText();
        String login = ((TextField) manager.lookup("#login")).getText();
        String password = ((TextField) manager.lookup("#password")).getText();
        try {
            DBmanager.setUpConnection(url, login, password);
            nextFunction.run();
        } catch (SQLException e) {
            System.err.println("Nie polaczono z baza danych " + e.getMessage());
            debugLabel.setText(e.getMessage());
        }
    }
    public void setOnSucess(Runnable function){
        nextFunction = function;
    }
    public DatabaseManager getManager(){
        return DBmanager;
    }
}
