package org.example.databaseclient;

import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class BasicMode {
    DatabaseManager DBmanager;
    VBox topBox;

    public BasicMode(DatabaseManager readDBManager) {
        DBmanager = readDBManager;
    }

    public VBox organize (){
        topBox = JavaFxObjectsManager.createVBox(4, 4);
        topBox.setId("BASIC");
        GridPane Buttons = new GridPane();
        FlowPane buttonsObject = createButtons();
        ScrollPane scrollableResults = new ScrollPane();
        scrollableResults.setFitToWidth(true);
        scrollableResults.setId("RESULTS");
        scrollableResults.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        JavaFxObjectsManager.fillOrganizer(Buttons, buttonsObject);
        JavaFxObjectsManager.fillOrganizer(topBox, Buttons);
        JavaFxObjectsManager.fillOrganizer(topBox, scrollableResults);
        return topBox;
    }
    private FlowPane createButtons() {
        Button getSessionChairs = JavaFxObjectsManager.createButton("Dostan zajete miejsca w seansach", this::getSessionChairs);
        return new FlowPane(getSessionChairs);
    }
    private void getSessionChairs() {
        ResultSet results = DBmanager.pushSqlRaw("SELECT MIASTO FROM KINA GROUP BY MIASTO");
        ArrayList<String> arguments = new ArrayList<>();
        while(true) {
            try {
                if (!results.next()) break;
                arguments.add(results.getString("MIASTO"));
            } catch (SQLException e) {
                return;
            }
        }
        ListView<String> cities = JavaFxObjectsManager.createHorizontalListView(arguments);
        cities.setOnMousePressed(_ -> {
            if (!cities.getSelectionModel().isEmpty()) {
                ResultSet res = DBmanager.pushSqlRaw("SELECT PSEUDONIM FROM KINA WHERE MIASTO = " + "'" + cities.getSelectionModel().getSelectedItem() + "'");
                arguments.clear();
                while (true) {
                    try {
                        if (!res.next()) break;
                        arguments.add(res.getString("PSEUDONIM"));
                    } catch (SQLException e) {
                        return;
                    }
                }
                ListView<String> selectedCinemas = JavaFxObjectsManager.createHorizontalListView(arguments);
                ((ScrollPane) topBox.lookup("#RESULTS")).setContent(selectedCinemas);
            }
        });
        ((ScrollPane) topBox.lookup("#RESULTS")).setContent(cities);
    }
}
