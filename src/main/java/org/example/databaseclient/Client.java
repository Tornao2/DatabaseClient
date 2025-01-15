package org.example.databaseclient;

import javafx.application.Application;
import javafx.stage.Stage;

public class Client extends Application {
    @Override
    public void start(Stage stage)  {
        loginLogic(stage);
    }
    public void loginLogic(Stage stage){
        Login loginScene = new Login();
        loginScene.setOnSucess(() -> mainLogic(stage, loginScene.getManager()));
        loginScene.start(stage);
    }

    public void mainLogic(Stage stage, DatabaseManager dbManager) {
        MainScene mainScene = new MainScene();
        mainScene.setOnDisconnect(() -> loginLogic(stage));
        mainScene.start(stage, dbManager);
    }
}

//Miec procedure z kursorem
