package org.example.databaseclient;

import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class CrudStages {
    public static VBox insertStage() {
        VBox returnObj = JavaFxObjectsManager.createVBox(8, 8);
        Button placeholderButton = JavaFxObjectsManager.createButton("insert", CrudStages.EmptyFunction());
        returnObj.getChildren().addAll(placeholderButton);
        return returnObj;
    }
    public static VBox readStage() {
        VBox returnObj = JavaFxObjectsManager.createVBox(8, 8);
        Button placeholderButton = JavaFxObjectsManager.createButton("read", CrudStages.EmptyFunction());
        returnObj.getChildren().addAll(placeholderButton);
        return returnObj;
    }
    public static VBox updateStage() {
        VBox returnObj = JavaFxObjectsManager.createVBox(8, 8);
        Button placeholderButton = JavaFxObjectsManager.createButton("update", CrudStages.EmptyFunction());
        returnObj.getChildren().addAll(placeholderButton);
        return returnObj;
    }
    public static VBox deleteStage() {
        VBox returnObj = JavaFxObjectsManager.createVBox(8, 8);
        Button placeholderButton = JavaFxObjectsManager.createButton("delete", CrudStages.EmptyFunction());
        returnObj.getChildren().addAll(placeholderButton);
        return returnObj;
    }
    public static Runnable EmptyFunction() {
        return null;
    }
}
