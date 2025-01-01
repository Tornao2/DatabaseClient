package org.example.databaseclient;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class JavaFxObjectsManager {
    static public Button createButton(String title, Runnable functionWhenClicked ){
        Button button = new Button(title);
        button.setOnAction(_ -> functionWhenClicked.run());
        return button;
    }
    static public VBox createVBox(int gaps, int padding){
        VBox returnObj = new VBox(gaps);
        returnObj.setAlignment(Pos.BASELINE_CENTER);
        returnObj.setPadding(new Insets(padding));
        return returnObj;
    }
    static public HBox createHBox(int gaps, int padding){
        HBox returnObj = new HBox(gaps);
        returnObj.setAlignment(Pos.BASELINE_CENTER);
        returnObj.setPadding(new Insets(padding));
        return returnObj;
    }
    static public BorderPane createBorderPane(Pane organizer){
        return new BorderPane(organizer);
    }
    static public void fillOrganizer(Pane organizer, Control[] children){
        organizer.getChildren().addAll(children);
    }
    static public void foldOrganizers(Pane parent, Pane[] children){
        parent.getChildren().addAll(children);
    }
    static public Scene createScene(Pane paneObject, int width, int height) {
        return new Scene(paneObject, width, height);
    }
    static public void setStage(Stage stage, Scene scene, String title){
        stage.setScene(scene);
        stage.setTitle(title);
        stage.show();
    }
}
