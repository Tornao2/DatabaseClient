package org.example.databaseclient;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.Pair;

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
        topBox.widthProperty().addListener((_, _, newValue) ->
                buttonsObject.setPrefWidth(newValue.doubleValue())
        );
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
        Button deleteData = JavaFxObjectsManager.createButton("Usun wszystkie dane", this::deleteData);
        Button addChairs = JavaFxObjectsManager.createButton("Dodaj nowe siedzenia", this::addChair);
        Button removeChairs = JavaFxObjectsManager.createButton("Usun nowe siedzenia", this::removeChair);
        Button getSessionChairs = JavaFxObjectsManager.createButton("Dostan zajete miejsca w seansach", this::getSessionChairs);
        Button changeSessionChairs = JavaFxObjectsManager.createButton("Zmien stan rezerwacji podczas seansu", this::changeSessionChairs);
        Button[] buttons = {deleteData, addChairs, removeChairs, getSessionChairs, changeSessionChairs};
        return new FlowPane(buttons);
    }
    private void getSessionChairs() {
        ResultSet results = DBmanager.pushSqlRaw("SELECT MIASTO FROM KINA GROUP BY MIASTO");
        ArrayList<String> arguments = new ArrayList<>();
        if (results != null) {
            while (true) {
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
                    if (res != null) {
                        while (true) {
                            try {
                                if (!res.next()) break;
                                arguments.add(res.getString("PSEUDONIM"));
                            } catch (SQLException e) {
                                return;
                            }
                        }
                        ListView<String> selectedCinemas = JavaFxObjectsManager.createHorizontalListView(arguments);
                        selectedCinemas.setOnMousePressed(_ -> {
                            if (!selectedCinemas.getSelectionModel().isEmpty()) {
                                ResultSet re = DBmanager.pushSqlRaw("SELECT S.ID, S.DATA, sa.NUMERSALI FROM SEANSE S " +
                                        "join sale sa on S.sala_id = sa.id " +
                                        "join kina k on sa.KINA_ID = k.id " +
                                        "where k.PSEUDONIM = " + "'" + selectedCinemas.getSelectionModel().getSelectedItem() + "'");
                                arguments.clear();
                                if (re != null) {
                                    ArrayList<Integer> seanseListId = new ArrayList<>();
                                    while (true) {
                                        try {
                                            if (!re.next()) break;
                                            seanseListId.add(re.getInt("ID"));
                                            arguments.add(re.getString("DATA") + " sala " + re.getString("NUMERSALI"));
                                        } catch (SQLException e) {
                                            return;
                                        }
                                    }
                                    ListView<String> selectedDates = JavaFxObjectsManager.createHorizontalListView(arguments);
                                    selectedDates.setOnMousePressed(_ -> {
                                        if (!selectedDates.getSelectionModel().isEmpty()) {
                                            ResultSet r = DBmanager.pushSqlRaw("SELECT STAN, M.KOLUMNA, M.RZAD FROM REZERWACJE R " +
                                                    "JOIN MIEJSCA M ON R.MIEJSCA_ID = M.ID " +
                                                    "join SEANSE SA ON SA.ID = R.SEANSE_ID " +
                                                    "WHERE SEANSE_ID = " + seanseListId.get(selectedDates.getSelectionModel().getSelectedIndex()));
                                            arguments.clear();
                                            if (r != null) {
                                                ArrayList<String> columns = new ArrayList<>();
                                                ObservableList<ObservableList<String>> allRows = FXCollections.observableArrayList();
                                                columns.add("STAN");
                                                columns.add("KOLUMNA");
                                                columns.add("RZAD");
                                                TableView<ObservableList<String>> table = JavaFxObjectsManager.createTableView(columns);
                                                while (true) {
                                                    try {
                                                        if (!r.next()) break;
                                                        ObservableList<String> row = FXCollections.observableArrayList();
                                                        for (String column : columns) row.add(r.getString(column));
                                                        allRows.add(row);
                                                    } catch (SQLException e) {
                                                        return;
                                                    }
                                                }
                                                table.setItems(allRows);
                                                ((ScrollPane) topBox.lookup("#RESULTS")).setContent(table);
                                            }
                                        }
                                    });
                                    ((ScrollPane) topBox.lookup("#RESULTS")).setContent(selectedDates);
                                }
                            }
                        });
                        ((ScrollPane) topBox.lookup("#RESULTS")).setContent(selectedCinemas);
                    }
                }
            });
            ((ScrollPane) topBox.lookup("#RESULTS")).setContent(cities);
        }
    }
    private void changeSessionChairs() {
        ResultSet results = DBmanager.pushSqlRaw("SELECT MIASTO FROM KINA GROUP BY MIASTO");
        ArrayList<String> arguments = new ArrayList<>();
        if (results != null) {
            while (true) {
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
                    if (res != null) {
                        while (true) {
                            try {
                                if (!res.next()) break;
                                arguments.add(res.getString("PSEUDONIM"));
                            } catch (SQLException e) {
                                return;
                            }
                        }
                        ListView<String> selectedCinemas = JavaFxObjectsManager.createHorizontalListView(arguments);
                        selectedCinemas.setOnMousePressed(_ -> {
                            if (!selectedCinemas.getSelectionModel().isEmpty()) {
                                ResultSet re = DBmanager.pushSqlRaw("SELECT S.ID, S.DATA, sa.NUMERSALI FROM SEANSE S " +
                                        "join sale sa on S.sala_id = sa.id " +
                                        "join kina k on sa.KINA_ID = k.id " +
                                        "where k.PSEUDONIM = " + "'" + selectedCinemas.getSelectionModel().getSelectedItem() + "'");
                                arguments.clear();
                                if (re != null) {
                                    ArrayList<Integer> seanseListId = new ArrayList<>();
                                    while (true) {
                                        try {
                                            if (!re.next()) break;
                                            seanseListId.add(re.getInt("ID"));
                                            arguments.add(re.getString("DATA") + " sala " + re.getString("NUMERSALI"));
                                        } catch (SQLException e) {
                                            return;
                                        }
                                    }
                                    ListView<String> selectedDates = JavaFxObjectsManager.createHorizontalListView(arguments);
                                    selectedDates.setOnMousePressed(_ -> {
                                        if (!selectedDates.getSelectionModel().isEmpty()) {
                                            ResultSet r = DBmanager.pushSqlRaw("SELECT R.ID, STAN, M.KOLUMNA, M.RZAD FROM REZERWACJE R " +
                                                    "JOIN MIEJSCA M ON R.MIEJSCA_ID = M.ID " +
                                                    "join SEANSE SA ON SA.ID = R.SEANSE_ID " +
                                                    "WHERE SEANSE_ID = " + seanseListId.get(selectedDates.getSelectionModel().getSelectedIndex()));
                                            arguments.clear();
                                            if (r != null) {
                                                ArrayList<Integer> resId = new ArrayList<>();
                                                ArrayList<String> resInfo = new ArrayList<>();
                                                while (true) {
                                                    try {
                                                        if (!r.next()) break;
                                                        resInfo.add("Stan: " + r.getString("STAN")
                                                                + " ,Kolumna: " + r.getInt("KOLUMNA") + " ,Rzad: " + r.getInt("RZAD"));
                                                        resId.add(r.getInt("ID"));
                                                    } catch (SQLException e) {
                                                        return;
                                                    }
                                                }
                                                ListView <String> list = JavaFxObjectsManager.createHorizontalListView(resInfo);
                                                list.setOnMousePressed(_ -> {
                                                    if(!list.getSelectionModel().isEmpty()) {
                                                        VBox change = JavaFxObjectsManager.createVBox(4, 4);
                                                        Button setToFree = JavaFxObjectsManager.createButton("Ustaw na wolne", () -> {
                                                            DBmanager.pushSqlRaw("UPDATE REZERWACJE SET STAN = " +
                                                                    "'WOLNE' WHERE ID = " + resId.get(list.getSelectionModel().getSelectedIndex()));
                                                            Label label = JavaFxObjectsManager.createLabel("Zmieniono");
                                                            ((ScrollPane) topBox.lookup("#RESULTS")).setContent(label);
                                                        });
                                                        Button setToTaken = JavaFxObjectsManager.createButton("Ustaw na zajete", () -> {
                                                            DBmanager.pushSqlRaw("UPDATE REZERWACJE SET STAN = " +
                                                                    "'Zajete' WHERE ID = " + resId.get(list.getSelectionModel().getSelectedIndex()));
                                                            Label label = JavaFxObjectsManager.createLabel("Zmieniono");
                                                            ((ScrollPane) topBox.lookup("#RESULTS")).setContent(label);
                                                        });
                                                        Control[] buttons = {setToFree, setToTaken};
                                                        JavaFxObjectsManager.fillOrganizer(change, buttons);
                                                        ((ScrollPane) topBox.lookup("#RESULTS")).setContent(change);
                                                    }
                                                });
                                                ((ScrollPane) topBox.lookup("#RESULTS")).setContent(list);
                                            }
                                        }
                                    });
                                    ((ScrollPane) topBox.lookup("#RESULTS")).setContent(selectedDates);
                                }
                            }
                        });
                        ((ScrollPane) topBox.lookup("#RESULTS")).setContent(selectedCinemas);
                    }
                }
            });
            ((ScrollPane) topBox.lookup("#RESULTS")).setContent(cities);
        }
    }
    private void addChair(){
        ResultSet results = DBmanager.pushSqlRaw("SELECT MIASTO FROM KINA GROUP BY MIASTO");
        ArrayList<String> arguments = new ArrayList<>();
        if (results != null) {
            while (true) {
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
                    ArrayList<Integer> cinemaId = new ArrayList<>();
                    ResultSet res = DBmanager.pushSqlRaw("SELECT ID, PSEUDONIM FROM KINA WHERE MIASTO = " + "'" + cities.getSelectionModel().getSelectedItem() + "'");
                    arguments.clear();
                    if (res != null) {
                        while (true) {
                            try {
                                if (!res.next()) break;
                                cinemaId.add(res.getInt("ID"));
                                arguments.add(res.getString("PSEUDONIM"));
                            } catch (SQLException e) {
                                return;
                            }
                        }
                        ListView<String> selectedCinemas = JavaFxObjectsManager.createHorizontalListView(arguments);
                        selectedCinemas.setOnMousePressed(_ -> {
                            if (!selectedCinemas.getSelectionModel().isEmpty()) {
                                ResultSet re = DBmanager.pushSqlRaw("SELECT NUMERSALI FROM SALE s " +
                                        "where s.kina_id = " + cinemaId.get(selectedCinemas.getSelectionModel().getSelectedIndex()));
                                arguments.clear();
                                if (re != null) {
                                    while (true) {
                                        try {
                                            if (!re.next()) break;
                                            arguments.add(re.getString("NUMERSALI"));
                                        } catch (SQLException e) {
                                            return;
                                        }
                                    }
                                    ListView<String> selectedRooms = JavaFxObjectsManager.createHorizontalListView(arguments);
                                    selectedRooms.setOnMousePressed(_ -> {
                                        if (!selectedRooms.getSelectionModel().isEmpty()) {
                                            Label rowLabel = JavaFxObjectsManager.createLabel("Podaj rzad");
                                            TextField row = new TextField();
                                            Label colLabel = JavaFxObjectsManager.createLabel("Podaj kolumne");
                                            TextField col = new TextField();
                                            Label ifWorked = JavaFxObjectsManager.createLabel("Dodano miejsce");
                                            ifWorked.setVisible(false);
                                            Button send = JavaFxObjectsManager.createButton("Utworz", () -> {
                                                if (!row.getText().isEmpty() && !col.getText().isEmpty()) {
                                                    int rowId = Integer.parseInt(row.getText());
                                                    int colId = Integer.parseInt(col.getText());
                                                    ResultSet id = DBmanager.pushSqlRaw("SELECT MAX(ID) FROM MIEJSCA");
                                                    if (id != null) {
                                                        try {
                                                            id.next();
                                                            int idInt = id.getInt(1) + 1;
                                                            DBmanager.pushSqlRaw("INSERT INTO MIEJSCA VALUES(" + idInt + ", " + colId
                                                                    + ", " + rowId + ", " + selectedRooms.getSelectionModel().getSelectedItem() + ")");
                                                            ifWorked.setText("Dodano miejsce");
                                                        } catch (SQLException _) {
                                                            ifWorked.setText("Nie udalo sie dodac miejsca");
                                                        } finally {
                                                            ifWorked.setVisible(true);
                                                        }
                                                    }
                                                }
                                            });
                                            Control[] controls = {rowLabel, row, colLabel, col, send, ifWorked};
                                            VBox tempBox = JavaFxObjectsManager.createVBox(4, 4);
                                            tempBox.getChildren().addAll(controls);
                                            ((ScrollPane) topBox.lookup("#RESULTS")).setContent(tempBox);
                                        }
                                    });
                                    ((ScrollPane) topBox.lookup("#RESULTS")).setContent(selectedRooms);
                                }
                            }
                        });
                        ((ScrollPane) topBox.lookup("#RESULTS")).setContent(selectedCinemas);
                    }
                }
            });
            ((ScrollPane) topBox.lookup("#RESULTS")).setContent(cities);
        }
    }
    private void removeChair() {
        ResultSet results = DBmanager.pushSqlRaw("SELECT MIASTO FROM KINA GROUP BY MIASTO");
        ArrayList<String> arguments = new ArrayList<>();
        if (results != null) {
            while (true) {
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
                    ArrayList<Integer> cinemaId = new ArrayList<>();
                    ResultSet res = DBmanager.pushSqlRaw("SELECT ID, PSEUDONIM FROM KINA WHERE MIASTO = " + "'" + cities.getSelectionModel().getSelectedItem() + "'");
                    arguments.clear();
                    if (res != null) {
                        while (true) {
                            try {
                                if (!res.next()) break;
                                cinemaId.add(res.getInt("ID"));
                                arguments.add(res.getString("PSEUDONIM"));
                            } catch (SQLException e) {
                                return;
                            }
                        }
                        ListView<String> selectedCinemas = JavaFxObjectsManager.createHorizontalListView(arguments);
                        selectedCinemas.setOnMousePressed(_ -> {
                            if (!selectedCinemas.getSelectionModel().isEmpty()) {
                                ResultSet re = DBmanager.pushSqlRaw("SELECT ID, NUMERSALI FROM SALE s " +
                                        "where s.kina_id = " + cinemaId.get(selectedCinemas.getSelectionModel().getSelectedIndex()));
                                arguments.clear();
                                if (re != null) {
                                    ArrayList<Integer> roomId = new ArrayList<>();
                                    while (true) {
                                        try {
                                            if (!re.next()) break;
                                            roomId.add(re.getInt("ID"));
                                            arguments.add(re.getString("NUMERSALI"));
                                        } catch (SQLException e) {
                                            return;
                                        }
                                    }
                                    ListView<String> selectedRooms = JavaFxObjectsManager.createHorizontalListView(arguments);
                                    selectedRooms.setOnMousePressed(_ -> {
                                        if (!selectedRooms.getSelectionModel().isEmpty()) {
                                            ResultSet r = DBmanager.pushSqlRaw("SELECT ID, kolumna, rzad FROM miejsca m " +
                                                    "where m.sala_id = " + roomId.get(selectedRooms.getSelectionModel().getSelectedIndex()));
                                            arguments.clear();
                                            if (r!= null) {
                                                ArrayList<Integer> chairId = new ArrayList<>();
                                                ArrayList<Pair<Integer, Integer>> chairData = new ArrayList<>();
                                                while (true) {
                                                    try {
                                                        if (!r.next()) break;
                                                        chairId.add(r.getInt("ID"));
                                                        chairData.add(new Pair<>(r.getInt("kolumna"), r.getInt("rzad")));
                                                    } catch (SQLException e) {
                                                        return;
                                                    }
                                                }
                                                ArrayList<String> roomDataFor = new ArrayList<>();
                                                for(Pair <Integer, Integer> pair: chairData)
                                                    roomDataFor.add("Kolumna: " + pair.getKey() + ", Rzad: " + pair.getValue());
                                                ListView<String> selectedChair = JavaFxObjectsManager.createHorizontalListView(roomDataFor);
                                                selectedChair.setOnMousePressed(_ -> {
                                                    if (!selectedChair.getSelectionModel().isEmpty()) {
                                                        DBmanager.pushSqlRaw("DELETE FROM MIEJSCA WHERE ID = " + chairId.get(selectedChair.getSelectionModel().getSelectedIndex()));
                                                        Label label = JavaFxObjectsManager.createLabel("Usunieto miejsce");
                                                        ((ScrollPane) topBox.lookup("#RESULTS")).setContent(label);
                                                    }
                                                });
                                                ((ScrollPane) topBox.lookup("#RESULTS")).setContent(selectedChair);
                                            }
                                        }
                                    });
                                    ((ScrollPane) topBox.lookup("#RESULTS")).setContent(selectedRooms);
                                }
                            }
                        });
                        ((ScrollPane) topBox.lookup("#RESULTS")).setContent(selectedCinemas);
                    }
                }
            });
            ((ScrollPane) topBox.lookup("#RESULTS")).setContent(cities);
        }
    }
    private void deleteData() {
        DBmanager.pushSqlRaw("call DeleteAllData()");
        Label result = JavaFxObjectsManager.createLabel("Wyczyszczono dane");
        ((ScrollPane) topBox.lookup("#RESULTS")).setContent(result);
    }
}
