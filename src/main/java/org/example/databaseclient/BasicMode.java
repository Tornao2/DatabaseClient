package org.example.databaseclient;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.Pair;
import oracle.jdbc.internal.OracleTypes;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class BasicMode {
    DatabaseManager DBmanager;
    VBox topBox;

    public BasicMode(DatabaseManager readDBManager) {
        DBmanager = readDBManager;
    }

    public VBox organize() {
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
        Button addRoomButton = JavaFxObjectsManager.createButton("Dodaj nowa sale", this::addRoom);
        Button deleteRoomButton = JavaFxObjectsManager.createButton("Usun sale", this::deleteRoom);
        Button addSess = JavaFxObjectsManager.createButton("Dodaj nowy seans", this::addSess);
        Button removeSess = JavaFxObjectsManager.createButton("Usun seans", this::removeSess);
        Button addRes = JavaFxObjectsManager.createButton("Dodaj nowa rezerwacje", this::addRes);
        Button removeRes = JavaFxObjectsManager.createButton("Usun rezerwacje", this::removeRes);
        Button addPrac = JavaFxObjectsManager.createButton("Dodaj nowego pracownika", this::addPrac);
        Button removePrac = JavaFxObjectsManager.createButton("Usun pracownika", this::removePrac);
        Button addOcen = JavaFxObjectsManager.createButton("Dodaj nowa ocene", this::addOcen);
        Button removeOcen = JavaFxObjectsManager.createButton("Usun ocene", this::removeOcen);
        Button addKonto = JavaFxObjectsManager.createButton("Dodaj nowe konto", this::addKonto);
        Button removeKonto = JavaFxObjectsManager.createButton("Usun konto", this::removeKonto);
        Button addKino = JavaFxObjectsManager.createButton("Dodaj nowe kino", this::addKino);
        Button removeKino = JavaFxObjectsManager.createButton("Usun kino", this::removeKino);
        Button addFilm = JavaFxObjectsManager.createButton("Dodaj nowy film", this::addFilm);
        Button removeFilm = JavaFxObjectsManager.createButton("Usun film", this::removeFilm);
        Button addBilet = JavaFxObjectsManager.createButton("Dodaj nowy bilet", this::addBilet);
        Button removeBilet = JavaFxObjectsManager.createButton("Usun bilet", this::removeBilet);
        Button getSessionChairs = JavaFxObjectsManager.createButton("Dostan stan miejsc w seansach", this::getSessionChairs);
        Button changeSessionChairs = JavaFxObjectsManager.createButton("Zmien stan rezerwacji dla seansu", this::changeSessionChairs);
        Button getAllSessions = JavaFxObjectsManager.createButton("Dostan wszystkie seansy w przedziale czasu", this::getSessions);
        Button getAllIncome = JavaFxObjectsManager.createButton("Dostan liczbe sprzedanych biletow dla filmow", this::getAmount);
        Button[] buttons = {deleteData, addChairs, removeChairs, addRoomButton, deleteRoomButton, addSess, removeSess, addRes, removeRes,
                addPrac, removePrac, addOcen, removeOcen, addKonto, removeKonto, addKino, removeKino, addFilm, removeFilm, addBilet, removeBilet,
                getSessionChairs, changeSessionChairs, getAllSessions, getAllIncome};
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
                                            } else {
                                                Label label = JavaFxObjectsManager.createLabel("Dzialanie sie nie udalo");
                                                ((ScrollPane) topBox.lookup("#RESULTS")).setContent(label);
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
                                                ListView<String> list = JavaFxObjectsManager.createHorizontalListView(resInfo);
                                                list.setOnMousePressed(_ -> {
                                                    if (!list.getSelectionModel().isEmpty()) {
                                                        VBox change = JavaFxObjectsManager.createVBox(4, 4);
                                                        Button setToFree = JavaFxObjectsManager.createButton("Ustaw na wolne", () -> {
                                                            ResultSet ra = DBmanager.pushSqlRaw("UPDATE REZERWACJE SET STAN = " +
                                                                    "'WOLNE' WHERE ID = " + resId.get(list.getSelectionModel().getSelectedIndex()));
                                                            if (ra == null) {
                                                                Label label = JavaFxObjectsManager.createLabel("Dzialanie sie nie udalo");
                                                                ((ScrollPane) topBox.lookup("#RESULTS")).setContent(label);
                                                                return;
                                                            }
                                                            Label label = JavaFxObjectsManager.createLabel("Zmieniono");
                                                            ((ScrollPane) topBox.lookup("#RESULTS")).setContent(label);
                                                        });
                                                        Button setToTaken = JavaFxObjectsManager.createButton("Ustaw na zajete", () -> {
                                                            ResultSet ra = DBmanager.pushSqlRaw("UPDATE REZERWACJE SET STAN = " +
                                                                    "'Zajete' WHERE ID = " + resId.get(list.getSelectionModel().getSelectedIndex()));
                                                            if (ra == null) {
                                                                Label label = JavaFxObjectsManager.createLabel("Dzialanie sie nie udalo");
                                                                ((ScrollPane) topBox.lookup("#RESULTS")).setContent(label);
                                                                return;
                                                            }
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
    private void addChair() {
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
                                ArrayList<Integer> salaId = new ArrayList<>();
                                ResultSet re = DBmanager.pushSqlRaw("SELECT ID, NUMERSALI FROM SALE s " +
                                        "where s.kina_id = " + cinemaId.get(selectedCinemas.getSelectionModel().getSelectedIndex()));
                                arguments.clear();
                                if (re != null) {
                                    while (true) {
                                        try {
                                            if (!re.next()) break;
                                            salaId.add(re.getInt("ID"));
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
                                                            ResultSet r = DBmanager.pushSqlRaw("INSERT INTO MIEJSCA VALUES(" + idInt + ", " + colId
                                                                    + ", " + rowId + ", " + salaId.get(selectedRooms.getSelectionModel().getSelectedIndex()) + ")");
                                                            if (r == null) {
                                                                Label label = JavaFxObjectsManager.createLabel("Nie udalo sie wstawianie");
                                                                ((ScrollPane) topBox.lookup("#RESULTS")).setContent(label);
                                                                return;
                                                            }
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
                                            if (r != null) {
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
                                                for (Pair<Integer, Integer> pair : chairData)
                                                    roomDataFor.add("Kolumna: " + pair.getKey() + ", Rzad: " + pair.getValue());
                                                ListView<String> selectedChair = JavaFxObjectsManager.createHorizontalListView(roomDataFor);
                                                selectedChair.setOnMousePressed(_ -> {
                                                    if (!selectedChair.getSelectionModel().isEmpty()) {
                                                        int chairIdNum = chairId.get(selectedChair.getSelectionModel().getSelectedIndex());
                                                        DBmanager.pushSqlRaw("DELETE FROM BILETY WHERE REZERWACJE_ID IN (SELECT ID FROM REZERWACJE WHERE MIEJSCA_ID = " + chairIdNum + ")");
                                                        DBmanager.pushSqlRaw("DELETE FROM REZERWACJE WHERE MIEJSCA_ID = " + chairIdNum);
                                                        DBmanager.pushSqlRaw("DELETE FROM MIEJSCA WHERE ID = " + chairIdNum);
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
    private void getSessions() {
        Label dateFromLabel = JavaFxObjectsManager.createLabel("Od: ");
        DatePicker dateFromField = new DatePicker();
        Label dateToLabel = JavaFxObjectsManager.createLabel("Do: ");
        DatePicker dateToField = new DatePicker();
        Button send = JavaFxObjectsManager.createButton("Wyszukaj", () -> {
            if (dateFromField.getValue() != null && dateToField.getValue() != null) {
                LocalDate dateFrom = dateFromField.getValue();
                LocalDate dateTo = dateToField.getValue();
                Connection con = DBmanager.getDBConnection();
                try {
                    CallableStatement stmt = con.prepareCall("{CALL ZnajdzSeanseCursor(?, ?, ?)}");
                    stmt.setDate(1, Date.valueOf(dateFrom));
                    stmt.setDate(2, Date.valueOf(dateTo));
                    stmt.registerOutParameter(3, OracleTypes.CURSOR);
                    stmt.execute();
                    ResultSet rs = (ResultSet) stmt.getObject(3);
                    if (rs != null) {
                        ArrayList<String> columns = new ArrayList<>();
                        ObservableList<ObservableList<String>> allRows = FXCollections.observableArrayList();
                        columns.add("DATA");
                        columns.add("TYTULFILMU");
                        columns.add("NUMERSALI");
                        columns.add("MIASTO");
                        columns.add("PSEUDONIM");
                        TableView<ObservableList<String>> table = JavaFxObjectsManager.createTableView(columns);
                        while (true) {
                            try {
                                if (!rs.next()) break;
                                ObservableList<String> row = FXCollections.observableArrayList();
                                for (String column : columns) row.add(rs.getString(column));
                                allRows.add(row);
                            } catch (SQLException e) {
                                return;
                            }
                        }
                        table.setItems(allRows);
                        ((ScrollPane) topBox.lookup("#RESULTS")).setContent(table);
                        rs.close();
                    }
                } catch (SQLException e) {
                    Label result = JavaFxObjectsManager.createLabel("Nie udalo sie wywolanie");
                    ((ScrollPane) topBox.lookup("#RESULTS")).setContent(result);
                }

            }
        });
        VBox box = JavaFxObjectsManager.createVBox(4, 4);
        Control[] controls = {dateFromLabel, dateFromField, dateToLabel, dateToField, send};
        JavaFxObjectsManager.fillOrganizer(box, controls);
        ((ScrollPane) topBox.lookup("#RESULTS")).setContent(box);
    }
    private void getAmount() {
        String sql = "{CALL ZliczSprzedaneBilety(?)}";
        CallableStatement cstmt;
        try {
            cstmt = DBmanager.getDBConnection().prepareCall(sql);
            cstmt.registerOutParameter(1, OracleTypes.CURSOR);
            cstmt.execute();
            ResultSet rs = (ResultSet) cstmt.getObject(1);
            ArrayList<String> columns = new ArrayList<>();
            ObservableList<ObservableList<String>> allRows = FXCollections.observableArrayList();
            columns.add("TYTULFILMU");
            columns.add("LICZBA_BILETOW");
            if (rs != null) {
                TableView<ObservableList<String>> table = JavaFxObjectsManager.createTableView(columns);
                while (true) {
                    try {
                        if (!rs.next()) break;
                        ObservableList<String> row = FXCollections.observableArrayList();
                        for (String column : columns) row.add(rs.getString(column));
                        allRows.add(row);
                    } catch (SQLException e) {
                        return;
                    }
                }
                table.setItems(allRows);
                ((ScrollPane) topBox.lookup("#RESULTS")).setContent(table);
                rs.close();
            }

        } catch (SQLException e) {
            Label result = JavaFxObjectsManager.createLabel("Nie udalo sie wywolanie");
            ((ScrollPane) topBox.lookup("#RESULTS")).setContent(result);
        }
    }
    private void addSess() {
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
                                ArrayList<Integer> salaId = new ArrayList<>();
                                ResultSet re = DBmanager.pushSqlRaw("SELECT ID, NUMERSALI FROM SALE s " +
                                        "where s.kina_id = " + cinemaId.get(selectedCinemas.getSelectionModel().getSelectedIndex()));
                                arguments.clear();
                                if (re != null) {
                                    while (true) {
                                        try {
                                            if (!re.next()) break;
                                            salaId.add(re.getInt("ID"));
                                            arguments.add(re.getString("NUMERSALI"));
                                        } catch (SQLException e) {
                                            return;
                                        }
                                    }
                                    ListView<String> selectedRooms = JavaFxObjectsManager.createHorizontalListView(arguments);
                                    selectedRooms.setOnMousePressed(_ -> {
                                        if (!selectedRooms.getSelectionModel().isEmpty()) {
                                            Integer salaIdRes = salaId.get(selectedRooms.getSelectionModel().getSelectedIndex());
                                            ArrayList<Integer> filmyId = new ArrayList<>();
                                            ResultSet r = DBmanager.pushSqlRaw("SELECT ID, TYTULFILMU FROM FILMY");
                                            arguments.clear();
                                            if (r != null) {
                                                while (true) {
                                                    try {
                                                        if (!r.next()) break;
                                                        filmyId.add(r.getInt("ID"));
                                                        arguments.add(r.getString("TYTULFILMU"));
                                                    } catch (SQLException e) {
                                                        return;
                                                    }
                                                }
                                                ListView<String> selectedFilms = JavaFxObjectsManager.createHorizontalListView(arguments);
                                                selectedFilms.setOnMousePressed(_ -> {
                                                    if (selectedFilms.getSelectionModel().getSelectedItem() != null) {
                                                        int selectedFilmId = filmyId.get(selectedFilms.getSelectionModel().getSelectedIndex());
                                                        Label dateLabel = JavaFxObjectsManager.createLabel("Podaj date i godzine seansu");
                                                        DatePicker date = new DatePicker();
                                                        TextField text = new TextField();
                                                        VBox box = JavaFxObjectsManager.createVBox(4, 4);
                                                        Button sendButton = JavaFxObjectsManager.createButton("Stworz seans", () -> {
                                                            if (date.getValue() != null && !text.getText().isEmpty()) {
                                                                LocalDate dateString = date.getValue();
                                                                String hourText = text.getText();
                                                                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
                                                                LocalTime time;
                                                                try {
                                                                    time = LocalTime.parse(hourText, timeFormatter);
                                                                } catch (Exception e) {
                                                                    Label format = JavaFxObjectsManager.createLabel("Wymagany format godziny to HH:mm");
                                                                    format.setId("Format");
                                                                    if (box.lookup("#Format") == null)
                                                                        box.getChildren().add(format);
                                                                    return;
                                                                }
                                                                LocalDateTime dateTime = LocalDateTime.of(dateString, time);
                                                                Timestamp timestamp = Timestamp.valueOf(dateTime);
                                                                ResultSet id = DBmanager.pushSqlRaw("SELECT MAX(ID) FROM SEANSE");
                                                                try {
                                                                    id.next();
                                                                    int seansId = id.getInt("MAX(ID)") + 1;
                                                                    String sql = "INSERT INTO SEANSE VALUES(" + seansId + ", TO_TIMESTAMP('" + timestamp + "','YYYY-MM-DD HH24:MI:SS.FF1'), "
                                                                            + selectedFilmId + ", " + salaIdRes + ")";
                                                                    DBmanager.pushSqlRaw(sql);
                                                                    Label success = JavaFxObjectsManager.createLabel("Udalo sie wstawic");
                                                                    ((ScrollPane) topBox.lookup("#RESULTS")).setContent(success);
                                                                } catch (SQLException e) {
                                                                    Label fail = JavaFxObjectsManager.createLabel("Nie udalo sie wyslac");
                                                                    ((ScrollPane) topBox.lookup("#RESULTS")).setContent(fail);
                                                                }
                                                            }
                                                        });
                                                        Control[] controls = {dateLabel, date, text, sendButton};
                                                        JavaFxObjectsManager.fillOrganizer(box, controls);
                                                        ((ScrollPane) topBox.lookup("#RESULTS")).setContent(box);
                                                    }
                                                });
                                                ((ScrollPane) topBox.lookup("#RESULTS")).setContent(selectedFilms);
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
    private void addRoom() {
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
                                Label label = JavaFxObjectsManager.createLabel("Podaj numer sali");
                                TextField roomNum = new TextField();
                                VBox box = JavaFxObjectsManager.createVBox(4, 4);
                                Button addButton = JavaFxObjectsManager.createButton("Dodaj sale", () -> {
                                    ResultSet id = DBmanager.pushSqlRaw("SELECT MAX(ID) FROM SALE");
                                    if (id != null) {
                                        Label finish = JavaFxObjectsManager.createLabel("");
                                        try {
                                            id.next();
                                            int idInt = id.getInt(1) + 1;
                                            DBmanager.pushSqlRaw("INSERT INTO SALE VALUES(" + idInt + ", " +
                                                    roomNum.getText() + ", " + cinemaId.get(selectedCinemas.getSelectionModel().getSelectedIndex()) + ")");
                                            finish.setText("Udalo sie dodac");
                                        } catch (SQLException e) {
                                            System.err.println("Nie udalo sie insertowanie w sale");
                                            finish.setText("Nie Udalo sie dodac");
                                        } finally {
                                            ((ScrollPane) topBox.lookup("#RESULTS")).setContent(finish);
                                        }
                                    }
                                });
                                Control[] controls = {label, roomNum, addButton};
                                JavaFxObjectsManager.fillOrganizer(box, controls);
                                ((ScrollPane) topBox.lookup("#RESULTS")).setContent(box);
                            }
                        });
                        ((ScrollPane) topBox.lookup("#RESULTS")).setContent(selectedCinemas);
                    }
                }
            });
            ((ScrollPane) topBox.lookup("#RESULTS")).setContent(cities);
        }
    }
    private void deleteRoom() {
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
                                ArrayList<Integer> salaId = new ArrayList<>();
                                ResultSet re = DBmanager.pushSqlRaw("SELECT ID, NUMERSALI FROM SALE s " +
                                        "where s.kina_id = " + cinemaId.get(selectedCinemas.getSelectionModel().getSelectedIndex()));
                                arguments.clear();
                                if (re != null) {
                                    while (true) {
                                        try {
                                            if (!re.next()) break;
                                            salaId.add(re.getInt("ID"));
                                            arguments.add(re.getString("NUMERSALI"));
                                        } catch (SQLException e) {
                                            return;
                                        }
                                    }
                                    ListView<String> selectedRooms = JavaFxObjectsManager.createHorizontalListView(arguments);
                                    selectedRooms.setOnMousePressed(_ -> {
                                        if (!selectedRooms.getSelectionModel().isEmpty()) {
                                            int id = salaId.get(selectedRooms.getSelectionModel().getSelectedIndex());
                                            Label finish = JavaFxObjectsManager.createLabel("");
                                            DBmanager.pushSqlRaw("DELETE FROM BILETY WHERE REZERWACJE_ID IN (SELECT ID FROM REZERWACJE WHERE MIEJSCA_ID IN  (SELECT M.ID FROM MIEJSCA M WHERE M.SALA_ID = " + id + "))");
                                            DBmanager.pushSqlRaw("DELETE FROM REZERWACJE WHERE MIEJSCA_ID IN (SELECT M.ID FROM MIEJSCA M WHERE M.SALA_ID = " + id + ")");
                                            DBmanager.pushSqlRaw("DELETE FROM MIEJSCA WHERE SALA_ID = " + id);
                                            DBmanager.pushSqlRaw("DELETE FROM SEANSE WHERE SALA_ID = " + id);
                                            DBmanager.pushSqlRaw("DELETE FROM SALE WHERE ID = " + id);
                                            finish.setText("Udalo sie");
                                            ((ScrollPane) topBox.lookup("#RESULTS")).setContent(finish);
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
    private void removeSess() {
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
                                ArrayList<Integer> salaId = new ArrayList<>();
                                ResultSet re = DBmanager.pushSqlRaw("SELECT ID, NUMERSALI FROM SALE s " +
                                        "where s.kina_id = " + cinemaId.get(selectedCinemas.getSelectionModel().getSelectedIndex()));
                                arguments.clear();
                                if (re != null) {
                                    while (true) {
                                        try {
                                            if (!re.next()) break;
                                            salaId.add(re.getInt("ID"));
                                            arguments.add(re.getString("NUMERSALI"));
                                        } catch (SQLException e) {
                                            return;
                                        }
                                    }
                                    ListView<String> selectedRooms = JavaFxObjectsManager.createHorizontalListView(arguments);
                                    selectedRooms.setOnMousePressed(_ -> {
                                        if (!selectedRooms.getSelectionModel().isEmpty()) {
                                            Integer salaIdRes = salaId.get(selectedRooms.getSelectionModel().getSelectedIndex());
                                            ResultSet r = DBmanager.pushSqlRaw("SELECT S.ID, s.DATA,F.TYTULFILMU FROM SEANSE S join filmy f" +
                                                    " on s.filmy_id = f.id WHERE s.SALA_ID = " + salaIdRes);
                                            ArrayList<Integer> seanseId = new ArrayList<>();
                                            arguments.clear();
                                            if (r != null) {
                                                while (true) {
                                                    try {
                                                        if (!r.next()) break;
                                                        seanseId.add(r.getInt("ID"));
                                                        arguments.add(r.getString("TYTULFILMU") + " " + r.getString("DATA"));
                                                    } catch (SQLException e) {
                                                        return;
                                                    }
                                                }
                                                ListView<String> selectedFilms = JavaFxObjectsManager.createHorizontalListView(arguments);
                                                selectedFilms.setOnMousePressed(_ -> {
                                                    if (!selectedFilms.getSelectionModel().isEmpty()) {
                                                        int seansId = seanseId.get(selectedFilms.getSelectionModel().getSelectedIndex());
                                                        DBmanager.pushSqlRaw("DELETE FROM BILETY WHERE REZERWACJE_ID IN (SELECT ID FROM REZERWACJE WHERE SEANSE_ID = " + seansId + ")");
                                                        DBmanager.pushSqlRaw("DELETE FROM REZERWACJE WHERE SEANSE_ID = " + seansId);
                                                        DBmanager.pushSqlRaw("DELETE FROM SEANSE WHERE ID = " + seansId);
                                                        Label label = JavaFxObjectsManager.createLabel("Usunieto");
                                                        ((ScrollPane) topBox.lookup("#RESULTS")).setContent(label);
                                                    }
                                                });
                                                ((ScrollPane) topBox.lookup("#RESULTS")).setContent(selectedFilms);
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
    private void addRes() {
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
                                ArrayList<Integer> salaId = new ArrayList<>();
                                ResultSet re = DBmanager.pushSqlRaw("SELECT ID, NUMERSALI FROM SALE s " +
                                        "where s.kina_id = " + cinemaId.get(selectedCinemas.getSelectionModel().getSelectedIndex()));
                                arguments.clear();
                                if (re != null) {
                                    while (true) {
                                        try {
                                            if (!re.next()) break;
                                            salaId.add(re.getInt("ID"));
                                            arguments.add(re.getString("NUMERSALI"));
                                        } catch (SQLException e) {
                                            return;
                                        }
                                    }
                                    ListView<String> selectedRooms = JavaFxObjectsManager.createHorizontalListView(arguments);
                                    selectedRooms.setOnMousePressed(_ -> {
                                        if (!selectedRooms.getSelectionModel().isEmpty()) {
                                            Integer salaIdRes = salaId.get(selectedRooms.getSelectionModel().getSelectedIndex());
                                            ArrayList<Integer> filmyId = new ArrayList<>();
                                            ResultSet r = DBmanager.pushSqlRaw("SELECT ID, TYTULFILMU FROM FILMY");
                                            arguments.clear();
                                            if (r != null) {
                                                while (true) {
                                                    try {
                                                        if (!r.next()) break;
                                                        filmyId.add(r.getInt("ID"));
                                                        arguments.add(r.getString("TYTULFILMU"));
                                                    } catch (SQLException e) {
                                                        return;
                                                    }
                                                }
                                                ListView<String> selectedFilms = JavaFxObjectsManager.createHorizontalListView(arguments);
                                                selectedFilms.setOnMousePressed(_ -> {
                                                    if (!selectedFilms.getSelectionModel().isEmpty()) {
                                                        int filmId = filmyId.get(selectedFilms.getSelectionModel().getSelectedIndex());
                                                        ResultSet resu = DBmanager.pushSqlRaw("SELECT ID, DATA FROM SEANSE WHERE FILMY_ID = " + filmId
                                                                + " AND SALA_ID = " + salaIdRes);
                                                        ArrayList<Integer> seansIdy = new ArrayList<>();
                                                        arguments.clear();
                                                        if (resu != null) {
                                                            while (true) {
                                                                try {
                                                                    if (!resu.next()) break;
                                                                    seansIdy.add(resu.getInt("ID"));
                                                                    arguments.add(resu.getString("DATA"));
                                                                } catch (SQLException e) {
                                                                    return;
                                                                }
                                                            }
                                                            ListView<String> selectedSeans = JavaFxObjectsManager.createHorizontalListView(arguments);
                                                            selectedSeans.setOnMousePressed(_ -> {
                                                                if (!selectedSeans.getSelectionModel().isEmpty()) {
                                                                    int seansId = seansIdy.get(selectedSeans.getSelectionModel().getSelectedIndex());
                                                                    ResultSet resul = DBmanager.pushSqlRaw("SELECT KOLUMNA, RZAD, ID FROM MIEJSCA WHERE SALA_ID = " + salaIdRes);
                                                                    ArrayList<Integer> miejscaId = new ArrayList<>();
                                                                    arguments.clear();
                                                                    if (resul != null) {
                                                                        while (true) {
                                                                            try {
                                                                                if (!resul.next()) break;
                                                                                miejscaId.add(resul.getInt("ID"));
                                                                                arguments.add("Kolumna: " + resul.getInt("KOLUMNA") + ", Rzad: " + resul.getInt("RZAD"));
                                                                            } catch (SQLException e) {
                                                                                return;
                                                                            }
                                                                        }
                                                                        ListView<String> selectedSeat = JavaFxObjectsManager.createHorizontalListView(arguments);
                                                                        selectedSeat.setOnMousePressed(_ -> {
                                                                            if (!selectedSeat.getSelectionModel().isEmpty()) {
                                                                                int miejsceId = miejscaId.get(selectedSeat.getSelectionModel().getSelectedIndex());
                                                                                VBox box = JavaFxObjectsManager.createVBox(4, 4);
                                                                                Label stanLabel = JavaFxObjectsManager.createLabel("Stan");
                                                                                TextField stan = new TextField();
                                                                                Button stanSend = JavaFxObjectsManager.createButton("Stworz", () -> {
                                                                                    ResultSet id = DBmanager.pushSqlRaw("SELECT MAX(ID) FROM REZERWACJE");
                                                                                    if (id != null) {
                                                                                        try {
                                                                                            id.next();
                                                                                            int idInt = id.getInt(1) + 1;
                                                                                            DBmanager.pushSqlRaw("INSERT INTO REZERWACJE VALUES(" + idInt + ", '" + stan.getText() + "'," + miejsceId
                                                                                                    + ", " + seansId + ")");
                                                                                            stanLabel.setText("Dodano");
                                                                                        } catch (SQLException _) {
                                                                                            stanLabel.setText("Nie udalo sie dodac");
                                                                                        } finally {
                                                                                            stanLabel.setVisible(true);
                                                                                            ((ScrollPane) topBox.lookup("#RESULTS")).setContent(stanLabel);
                                                                                        }
                                                                                    }
                                                                                });
                                                                                Control[] control = {stanLabel, stan, stanSend};
                                                                                JavaFxObjectsManager.fillOrganizer(box, control);
                                                                                ((ScrollPane) topBox.lookup("#RESULTS")).setContent(box);
                                                                            }
                                                                        });
                                                                        ((ScrollPane) topBox.lookup("#RESULTS")).setContent(selectedSeat);
                                                                    }
                                                                }
                                                            });
                                                            ((ScrollPane) topBox.lookup("#RESULTS")).setContent(selectedSeans);
                                                        }
                                                    }
                                                });
                                                ((ScrollPane) topBox.lookup("#RESULTS")).setContent(selectedFilms);
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
    private void removeRes() {
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
                                ArrayList<Integer> salaId = new ArrayList<>();
                                ResultSet re = DBmanager.pushSqlRaw("SELECT ID, NUMERSALI FROM SALE s " +
                                        "where s.kina_id = " + cinemaId.get(selectedCinemas.getSelectionModel().getSelectedIndex()));
                                arguments.clear();
                                if (re != null) {
                                    while (true) {
                                        try {
                                            if (!re.next()) break;
                                            salaId.add(re.getInt("ID"));
                                            arguments.add(re.getString("NUMERSALI"));
                                        } catch (SQLException e) {
                                            return;
                                        }
                                    }
                                    ListView<String> selectedRooms = JavaFxObjectsManager.createHorizontalListView(arguments);
                                    selectedRooms.setOnMousePressed(_ -> {
                                        if (!selectedRooms.getSelectionModel().isEmpty()) {
                                            Integer salaIdRes = salaId.get(selectedRooms.getSelectionModel().getSelectedIndex());
                                            ArrayList<Integer> filmyId = new ArrayList<>();
                                            ResultSet r = DBmanager.pushSqlRaw("SELECT ID, TYTULFILMU FROM FILMY");
                                            arguments.clear();
                                            if (r != null) {
                                                while (true) {
                                                    try {
                                                        if (!r.next()) break;
                                                        filmyId.add(r.getInt("ID"));
                                                        arguments.add(r.getString("TYTULFILMU"));
                                                    } catch (SQLException e) {
                                                        return;
                                                    }
                                                }
                                                ListView<String> selectedFilms = JavaFxObjectsManager.createHorizontalListView(arguments);
                                                selectedFilms.setOnMousePressed(_ -> {
                                                    if (!selectedFilms.getSelectionModel().isEmpty()) {
                                                        int filmId = filmyId.get(selectedFilms.getSelectionModel().getSelectedIndex());
                                                        ResultSet resu = DBmanager.pushSqlRaw("SELECT ID, DATA FROM SEANSE WHERE FILMY_ID = " + filmId
                                                                + " AND SALA_ID = " + salaIdRes);
                                                        ArrayList<Integer> seansIdy = new ArrayList<>();
                                                        arguments.clear();
                                                        if (resu != null) {
                                                            while (true) {
                                                                try {
                                                                    if (!resu.next()) break;
                                                                    seansIdy.add(resu.getInt("ID"));
                                                                    arguments.add(resu.getString("DATA"));
                                                                } catch (SQLException e) {
                                                                    return;
                                                                }
                                                            }
                                                            ListView<String> selectedSeans = JavaFxObjectsManager.createHorizontalListView(arguments);
                                                            selectedSeans.setOnMousePressed(_ -> {
                                                                if (!selectedSeans.getSelectionModel().isEmpty()) {
                                                                    int seansId = seansIdy.get(selectedSeans.getSelectionModel().getSelectedIndex());
                                                                    ResultSet resul = DBmanager.pushSqlRaw("SELECT KOLUMNA, RZAD, ID FROM MIEJSCA WHERE SALA_ID = " + salaIdRes);
                                                                    ArrayList<Integer> miejscaId = new ArrayList<>();
                                                                    arguments.clear();
                                                                    if (resul != null) {
                                                                        while (true) {
                                                                            try {
                                                                                if (!resul.next()) break;
                                                                                miejscaId.add(resul.getInt("ID"));
                                                                                arguments.add("Kolumna: " + resul.getInt("KOLUMNA") + ", Rzad: " + resul.getInt("RZAD"));
                                                                            } catch (SQLException e) {
                                                                                return;
                                                                            }
                                                                        }
                                                                        ListView<String> selectedSeat = JavaFxObjectsManager.createHorizontalListView(arguments);
                                                                        selectedSeat.setOnMousePressed(_ -> {
                                                                            if (!selectedSeat.getSelectionModel().isEmpty()) {
                                                                                int miejsceId = miejscaId.get(selectedSeat.getSelectionModel().getSelectedIndex());
                                                                                ResultSet resultS = DBmanager.pushSqlRaw("SELECT ID FROM REZERWACJE WHERE MIEJSCA_ID = " + miejsceId
                                                                                        + " AND SEANSE_ID = " + seansId);
                                                                                ArrayList<Integer> rez = new ArrayList<>();
                                                                                while (true) {
                                                                                    try {
                                                                                        if (!resultS.next()) break;
                                                                                        rez.add(resultS.getInt("ID"));
                                                                                    } catch (SQLException e) {
                                                                                        return;
                                                                                    }
                                                                                }
                                                                                for (Integer i : rez) {
                                                                                    DBmanager.pushSqlRaw("DELETE FROM BILETY WHERE REZERWACJE_ID = " + i);
                                                                                    DBmanager.pushSqlRaw("DELETE FROM REZERWACJE WHERE ID = " + i);
                                                                                }
                                                                                Label label = JavaFxObjectsManager.createLabel("Usunieto");
                                                                                ((ScrollPane) topBox.lookup("#RESULTS")).setContent(label);
                                                                            }
                                                                        });
                                                                        ((ScrollPane) topBox.lookup("#RESULTS")).setContent(selectedSeat);
                                                                    }
                                                                }
                                                            });
                                                            ((ScrollPane) topBox.lookup("#RESULTS")).setContent(selectedSeans);
                                                        }
                                                    }
                                                });
                                                ((ScrollPane) topBox.lookup("#RESULTS")).setContent(selectedFilms);
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
    private void addPrac() {
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
                                int kinoId = cinemaId.get(selectedCinemas.getSelectionModel().getSelectedIndex());
                                VBox box = JavaFxObjectsManager.createVBox(4, 4);
                                Label labelPesel = JavaFxObjectsManager.createLabel("Pesel");
                                TextField pesel = new TextField();
                                Label labelData = JavaFxObjectsManager.createLabel("Data");
                                DatePicker pick = new DatePicker();
                                TextField data = new TextField();
                                Label labelImie = JavaFxObjectsManager.createLabel("Imie");
                                TextField imie = new TextField();
                                Label labelKonto = JavaFxObjectsManager.createLabel("Konto");
                                TextField konto = new TextField();
                                Label labelNazwisko = JavaFxObjectsManager.createLabel("Nazwisko");
                                TextField nazwisko = new TextField();
                                Label labelPensja = JavaFxObjectsManager.createLabel("Pensja");
                                TextField pensja = new TextField();
                                Label labelStanowisko = JavaFxObjectsManager.createLabel("Stanowisko");
                                TextField stanowisko = new TextField();
                                Button sendButton = JavaFxObjectsManager.createButton("Stworz", () -> {
                                    if (!pesel.getText().isEmpty() && pick.getValue() != null && !data.getText().isEmpty() &&
                                            !imie.getText().isEmpty() && !konto.getText().isEmpty() && !nazwisko.getText().isEmpty() && !pensja.getText().isEmpty()
                                            && !stanowisko.getText().isEmpty()) {
                                        LocalDate dateString = pick.getValue();
                                        String hourText = data.getText();
                                        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
                                        LocalTime time;
                                        try {
                                            time = LocalTime.parse(hourText, timeFormatter);
                                        } catch (Exception e) {
                                            Label format = JavaFxObjectsManager.createLabel("Wymagany format godziny to HH:mm");
                                            format.setId("Format");
                                            if (box.lookup("#Format") == null)
                                                box.getChildren().add(format);
                                            return;
                                        }
                                        LocalDateTime dateTime = LocalDateTime.of(dateString, time);
                                        Timestamp timestamp = Timestamp.valueOf(dateTime);
                                        String sql = "Insert into pracownicy values('" + pesel.getText() + "', " + konto.getText() + ", " + pensja.getText() +
                                                ", '" + imie.getText() + "', '" + nazwisko.getText() + "', '" + stanowisko.getText() + "', TO_TIMESTAMP('" + timestamp + "','YYYY-MM-DD HH24:MI:SS.FF1'), "
                                                + kinoId + ")";
                                        ResultSet resi = DBmanager.pushSqlRaw(sql);
                                        Label label;
                                        if (resi != null)
                                            label = JavaFxObjectsManager.createLabel("Stworzono");
                                        else
                                            label = JavaFxObjectsManager.createLabel("Nie udalo sie, najprawdopodobniej pesel juz zajety");
                                        ((ScrollPane) topBox.lookup("#RESULTS")).setContent(label);
                                    }
                                });
                                Control[] controls = {labelPesel, pesel, labelData, pick, data, labelImie, imie, labelKonto, konto, labelNazwisko,
                                        nazwisko, labelPensja, pensja, labelStanowisko, stanowisko, sendButton};
                                JavaFxObjectsManager.fillOrganizer(box, controls);
                                ((ScrollPane) topBox.lookup("#RESULTS")).setContent(box);
                            }
                        });
                        ((ScrollPane) topBox.lookup("#RESULTS")).setContent(selectedCinemas);
                    }
                }
            });
            ((ScrollPane) topBox.lookup("#RESULTS")).setContent(cities);
        }
    }
    private void removePrac() {
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
                                int kinoId = cinemaId.get(selectedCinemas.getSelectionModel().getSelectedIndex());
                                ResultSet re = DBmanager.pushSqlRaw("SELECT PESEL, IMIE, NAZWISKO, STANOWISKO FROM PRACOWNICY WHERE KINA_ID = " + kinoId);
                                arguments.clear();
                                ArrayList<String> pesel = new ArrayList<>();
                                if (re != null) {
                                    while (true) {
                                        try {
                                            if (!re.next()) break;
                                            pesel.add(re.getString("PESEL"));
                                            arguments.add(re.getString("IMIE") + " " + re.getString("NAZWISKO") + " "
                                                    + re.getString("STANOWISKO") + " " + re.getString("PESEL"));
                                        } catch (SQLException e) {
                                            return;
                                        }
                                    }
                                    ListView<String> selectedPrac = JavaFxObjectsManager.createHorizontalListView(arguments);
                                    selectedPrac.setOnMousePressed(_ -> {
                                        if (!selectedPrac.getSelectionModel().isEmpty()) {
                                            String prac = pesel.get(selectedPrac.getSelectionModel().getSelectedIndex());
                                            DBmanager.pushSqlRaw("DELETE FROM PRACOWNICY WHERE PESEL = '" + prac + "'");
                                            Label label = JavaFxObjectsManager.createLabel("Usunieto");
                                            ((ScrollPane) topBox.lookup("#RESULTS")).setContent(label);
                                        }
                                    });
                                    ((ScrollPane) topBox.lookup("#RESULTS")).setContent(selectedPrac);
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
    private void addOcen() {
        ResultSet result = DBmanager.pushSqlRaw("SELECT LOGIN, EMAIL FROM KONTA");
        ArrayList<String> arguments = new ArrayList<>();
        ArrayList<String> emails = new ArrayList<>();
        if (result != null) {
            while (true) {
                try {
                    if (!result.next()) break;
                    arguments.add(result.getString("LOGIN"));
                    emails.add(result.getString("EMAIL"));
                } catch (SQLException e) {
                    return;
                }
            }
            ListView<String> logins = JavaFxObjectsManager.createHorizontalListView(arguments);
            logins.setOnMousePressed(_ -> {
                if (!logins.getSelectionModel().isEmpty()) {
                    String chosenEmail = emails.get(logins.getSelectionModel().getSelectedIndex());
                    ResultSet resul = DBmanager.pushSqlRaw("SELECT ID, TYTULFILMU, ROKPRODUKCJI FROM FILMY");
                    ArrayList<Integer> filmyId = new ArrayList<>();
                    arguments.clear();
                    if (resul != null) {
                        while (true) {
                            try {
                                if (!resul.next()) break;
                                arguments.add(resul.getString("TYTULFILMU") + "(" + resul.getInt("ROKPRODUKCJI") + ")");
                                filmyId.add(resul.getInt("ID"));
                            } catch (SQLException e) {
                                return;
                            }
                        }
                        ListView<String> filmy = JavaFxObjectsManager.createHorizontalListView(arguments);
                        filmy.setOnMousePressed(_ -> {
                            if (!filmy.getSelectionModel().isEmpty()) {
                                int filmId = filmyId.get(filmy.getSelectionModel().getSelectedIndex());
                                VBox box = JavaFxObjectsManager.createVBox(4, 4);
                                Label labelKomentarz = JavaFxObjectsManager.createLabel("Komentarz:");
                                TextField komentarz = new TextField();
                                Label labelOcena = JavaFxObjectsManager.createLabel("Ocena:");
                                TextField ocena = new TextField();
                                Button but = JavaFxObjectsManager.createButton("Stworz", () -> {
                                    if (!komentarz.getText().isEmpty() && !ocena.getText().isEmpty()) {
                                        ResultSet id = DBmanager.pushSqlRaw("SELECT MAX(ID) FROM OCENY");
                                        if (id != null) {
                                            Label stanLabel = JavaFxObjectsManager.createLabel("");
                                            try {
                                                id.next();
                                                int idInt = id.getInt(1) + 1;
                                                DBmanager.pushSqlRaw("INSERT INTO OCENY VALUES(" + idInt + ", " + ocena.getText() + ", '"
                                                        + komentarz.getText() + "', '" + chosenEmail + "' , " + filmId + ")");
                                                stanLabel.setText("Dodano");
                                            } catch (SQLException _) {
                                                stanLabel.setText("Nie udalo sie dodac");
                                            } finally {
                                                stanLabel.setVisible(true);
                                                ((ScrollPane) topBox.lookup("#RESULTS")).setContent(stanLabel);
                                            }
                                        }
                                    }
                                });
                                Control[] controls = {labelKomentarz, komentarz, labelOcena, ocena, but};
                                JavaFxObjectsManager.fillOrganizer(box, controls);
                                ((ScrollPane) topBox.lookup("#RESULTS")).setContent(box);
                            }
                        });
                        ((ScrollPane) topBox.lookup("#RESULTS")).setContent(filmy);
                    }
                }
            });
            ((ScrollPane) topBox.lookup("#RESULTS")).setContent(logins);
        }
    }
    private void removeOcen() {
        ResultSet result = DBmanager.pushSqlRaw("SELECT LOGIN, EMAIL FROM KONTA");
        ArrayList<String> arguments = new ArrayList<>();
        ArrayList<String> emails = new ArrayList<>();
        if (result != null) {
            while (true) {
                try {
                    if (!result.next()) break;
                    arguments.add(result.getString("LOGIN"));
                    emails.add(result.getString("EMAIL"));
                } catch (SQLException e) {
                    return;
                }
            }
            ListView<String> logins = JavaFxObjectsManager.createHorizontalListView(arguments);
            logins.setOnMousePressed(_ -> {
                if (!logins.getSelectionModel().isEmpty()) {
                    String chosenEmail = emails.get(logins.getSelectionModel().getSelectedIndex());
                    ResultSet resul = DBmanager.pushSqlRaw("SELECT ID, TYTULFILMU, ROKPRODUKCJI FROM FILMY");
                    ArrayList<Integer> filmyId = new ArrayList<>();
                    arguments.clear();
                    if (resul != null) {
                        while (true) {
                            try {
                                if (!resul.next()) break;
                                arguments.add(resul.getString("TYTULFILMU") + "(" + resul.getInt("ROKPRODUKCJI") + ")");
                                filmyId.add(resul.getInt("ID"));
                            } catch (SQLException e) {
                                return;
                            }
                        }
                        ListView<String> filmy = JavaFxObjectsManager.createHorizontalListView(arguments);
                        filmy.setOnMousePressed(_ -> {
                            if (!filmy.getSelectionModel().isEmpty()) {
                                int filmId = filmyId.get(filmy.getSelectionModel().getSelectedIndex());
                                DBmanager.pushSqlRaw("DELETE FROM OCENY WHERE FILMY_ID = " + filmId + " AND KONTA_EMAIL = '"
                                        + chosenEmail + "'");
                                Label label = JavaFxObjectsManager.createLabel("Usunieto");
                                ((ScrollPane) topBox.lookup("#RESULTS")).setContent(label);
                            }
                        });
                        ((ScrollPane) topBox.lookup("#RESULTS")).setContent(filmy);
                    }
                }
            });
            ((ScrollPane) topBox.lookup("#RESULTS")).setContent(logins);
        }
    }
    private void addKonto() {
        VBox box = JavaFxObjectsManager.createVBox(4, 4);
        Label emailLabel = JavaFxObjectsManager.createLabel("Email");
        TextField email = new TextField();
        Label hasloLabel = JavaFxObjectsManager.createLabel("Haslo");
        TextField haslo = new TextField();
        Label loginLabel = JavaFxObjectsManager.createLabel("Login");
        TextField login = new TextField();
        Button send = JavaFxObjectsManager.createButton("Stworz", () -> {
            if (!email.getText().isEmpty() && !haslo.getText().isEmpty() && !login.getText().isEmpty()) {
                ResultSet result = DBmanager.pushSqlRaw("insert into konta values('" + email.getText() + "', '" + login.getText() + "', '" +
                        haslo.getText() + "')");
                Label label;
                if (result != null) {
                    label = JavaFxObjectsManager.createLabel("Stworzono");
                } else {
                    label = JavaFxObjectsManager.createLabel("Nie udalo sie, najprawdopodbniej email juz zajety");
                }
                ((ScrollPane) topBox.lookup("#RESULTS")).setContent(label);
            }
        });
        Control[] controls = {emailLabel, email, hasloLabel, haslo, loginLabel, login, send};
        JavaFxObjectsManager.fillOrganizer(box, controls);
        ((ScrollPane) topBox.lookup("#RESULTS")).setContent(box);
    }
    private void removeKonto() {
        ResultSet result = DBmanager.pushSqlRaw("SELECT EMAIL FROM KONTA");
        ArrayList<String> arguments = new ArrayList<>();
        if (result != null) {
            while (true) {
                try {
                    if (!result.next()) break;
                    arguments.add(result.getString("EMAIL"));
                } catch (SQLException e) {
                    return;
                }
            }
            ListView<String> emails = JavaFxObjectsManager.createHorizontalListView(arguments);
            emails.setOnMousePressed(_ -> {
                if (emails.getSelectionModel().getSelectedItem() != null) {
                    DBmanager.pushSqlRaw("DELETE FROM BILETY WHERE KONTA_EMAIL = '" + emails.getSelectionModel().getSelectedItem() + "'");
                    DBmanager.pushSqlRaw("DELETE FROM OCENY WHERE KONTA_EMAIL = '" + emails.getSelectionModel().getSelectedItem() + "'");
                    DBmanager.pushSqlRaw("DELETE FROM KONTA WHERE EMAIL = '" + emails.getSelectionModel().getSelectedItem() + "'");
                    Label label = JavaFxObjectsManager.createLabel("Usunieto");
                    ((ScrollPane) topBox.lookup("#RESULTS")).setContent(label);
                }
            });
            ((ScrollPane) topBox.lookup("#RESULTS")).setContent(emails);
        }
    }
    private void addKino() {
        VBox box = JavaFxObjectsManager.createVBox(4, 4);
        Label miastoLabel = JavaFxObjectsManager.createLabel("Miasto");
        TextField miasto = new TextField();
        Label pseudoLabel = JavaFxObjectsManager.createLabel("Pseudonim kina");
        TextField pseudo = new TextField();
        Button send = JavaFxObjectsManager.createButton("Stworz", () -> {
            if (!miasto.getText().isEmpty() && !pseudo.getText().isEmpty()) {
                ResultSet id = DBmanager.pushSqlRaw("SELECT MAX(ID) FROM KINA");
                if (id != null) {
                    try {
                        id.next();
                        int idInt = id.getInt(1) + 1;
                        DBmanager.pushSqlRaw("INSERT INTO KINA VALUES(" + idInt + ", '" +
                                miasto.getText() + "', '" + pseudo.getText() + "')");
                        Label label = JavaFxObjectsManager.createLabel("Wprowadzono");
                        ((ScrollPane) topBox.lookup("#RESULTS")).setContent(label);
                    } catch (SQLException _) {
                        Label label = JavaFxObjectsManager.createLabel("Nie udalo sie wprowadzenie");
                        ((ScrollPane) topBox.lookup("#RESULTS")).setContent(label);
                    }
                }
            }
        });
        Control[] controls = {miastoLabel, miasto, pseudoLabel, pseudo, send};
        JavaFxObjectsManager.fillOrganizer(box, controls);
        ((ScrollPane) topBox.lookup("#RESULTS")).setContent(box);
    }
    private void removeKino() {
        ResultSet result = DBmanager.pushSqlRaw("SELECT MIASTO FROM KINA group by miasto");
        ArrayList<String> arguments = new ArrayList<>();
        if (result != null) {
            while (true) {
                try {
                    if (!result.next()) break;
                    arguments.add(result.getString("miasto"));
                } catch (SQLException e) {
                    return;
                }
            }
            ListView<String> miasta = JavaFxObjectsManager.createHorizontalListView(arguments);
            miasta.setOnMousePressed(_ -> {
                if (miasta.getSelectionModel().getSelectedItem() != null) {
                    ResultSet resul = DBmanager.pushSqlRaw("SELECT ID, PSEUDONIM FROM KINA where miasto = '" + miasta.getSelectionModel().getSelectedItem() + "'");
                    ArrayList<Integer> idList = new ArrayList<>();
                    arguments.clear();
                    if (resul != null) {
                        while (true) {
                            try {
                                if (!resul.next()) break;
                                idList.add(resul.getInt("id"));
                                arguments.add(resul.getString("pseudonim"));
                            } catch (SQLException e) {
                                return;
                            }
                        }
                        ListView<String> pseudo = JavaFxObjectsManager.createHorizontalListView(arguments);
                        pseudo.setOnMousePressed(_ -> {
                            if (pseudo.getSelectionModel().getSelectedItem() != null) {
                                int id = idList.get(pseudo.getSelectionModel().getSelectedIndex());
                                DBmanager.pushSqlRaw("Delete from bilety where rezerwacje_id in " +
                                        "(SELECT ID FROM REZERWACJE WHERE SEANSE_ID IN (SELECT ID FROM SEANSE WHERE SALA_ID IN " +
                                        "(SELECT ID FROM SALE WHERE KINA_ID = " + id + ")))");
                                DBmanager.pushSqlRaw("Delete from REZERWACJE where SEANSE_ID in (SELECT ID FROM SEANSE WHERE SALA_ID IN (SELECT ID FROM SALE WHERE KINA_ID = " + id + "))");
                                DBmanager.pushSqlRaw("Delete from miejsca where sala_id in (SELECT ID FROM SALE WHERE KINA_ID = " + id + ")");
                                DBmanager.pushSqlRaw("Delete from seanse where sala_id in (SELECT ID FROM SALE WHERE KINA_ID = " + id + ")");
                                DBmanager.pushSqlRaw("Delete from sale where kina_id = " + id);
                                DBmanager.pushSqlRaw("Delete from pracownicy where kina_id = " + id);
                                DBmanager.pushSqlRaw("Delete from kina where id = " + id);
                                Label label = JavaFxObjectsManager.createLabel("Usunieto");
                                ((ScrollPane) topBox.lookup("#RESULTS")).setContent(label);
                            }
                        });
                        ((ScrollPane) topBox.lookup("#RESULTS")).setContent(pseudo);
                    }
                }
            });
            ((ScrollPane) topBox.lookup("#RESULTS")).setContent(miasta);
        }
    }
    private void addFilm() {
        VBox box = JavaFxObjectsManager.createVBox(4, 4);
        Label dlugoscLabel = JavaFxObjectsManager.createLabel("Dlugosc");
        TextField dlugosc = new TextField();
        Label rezyserLabel = JavaFxObjectsManager.createLabel("Rezyser");
        TextField rezyser = new TextField();
        Label rokprodukcjiLabel = JavaFxObjectsManager.createLabel("Rok produkcji");
        TextField rok = new TextField();
        Label tytulfilmuLabel = JavaFxObjectsManager.createLabel("Tytul filmu");
        TextField tytul = new TextField();
        Button send = JavaFxObjectsManager.createButton("Stworz", () -> {
            if (!dlugosc.getText().isEmpty() && !rezyser.getText().isEmpty() && !rok.getText().isEmpty() && !tytul.getText().isEmpty()) {
                ResultSet id = DBmanager.pushSqlRaw("SELECT MAX(ID) FROM FILMY");
                if (id != null) {
                    try {
                        id.next();
                        int idInt = id.getInt(1) + 1;
                        DBmanager.pushSqlRaw("INSERT INTO FILMY VALUES(" + idInt + "," +
                                dlugosc.getText() + ", '" + rezyser.getText() + "', " + rok.getText() + ", '" + tytul.getText() + "')");
                        Label label = JavaFxObjectsManager.createLabel("Wprowadzono");
                        ((ScrollPane) topBox.lookup("#RESULTS")).setContent(label);
                    } catch (SQLException _) {
                        Label label = JavaFxObjectsManager.createLabel("Nie udalo sie wprowadzenie");
                        ((ScrollPane) topBox.lookup("#RESULTS")).setContent(label);
                    }
                }
            }
        });
        Control[] controls = {dlugoscLabel, dlugosc, rezyserLabel, rezyser, rokprodukcjiLabel, rok, tytulfilmuLabel, tytul, send};
        JavaFxObjectsManager.fillOrganizer(box, controls);
        ((ScrollPane) topBox.lookup("#RESULTS")).setContent(box);
    }
    private void removeFilm() {
        ResultSet result = DBmanager.pushSqlRaw("SELECT ID, tytulfilmu FROM filmy");
        ArrayList<String> arguments = new ArrayList<>();
        ArrayList<Integer> idList = new ArrayList<>();
        if (result != null) {
            while (true) {
                try {
                    if (!result.next()) break;
                    idList.add(result.getInt("ID"));
                    arguments.add(result.getString("tytulfilmu"));
                } catch (SQLException e) {
                    return;
                }
            }
            ListView<String> filmy = JavaFxObjectsManager.createHorizontalListView(arguments);
            filmy.setOnMousePressed(_ -> {
                if (filmy.getSelectionModel().getSelectedItem() != null) {
                    int id = idList.get(filmy.getSelectionModel().getSelectedIndex());
                    DBmanager.pushSqlRaw("Delete from bilety where rezerwacje_id in " +
                            "(SELECT ID FROM REZERWACJE WHERE SEANSE_ID IN (SELECT ID FROM SEANSE WHERE filmy_id = " + id + "))");
                    DBmanager.pushSqlRaw("Delete from REZERWACJE where SEANSE_ID in (SELECT ID FROM SEANSE WHERE filmy_id = " + id + ")");
                    DBmanager.pushSqlRaw("Delete from seanse where filmy_id = " + id);
                    DBmanager.pushSqlRaw("Delete from oceny where filmy_id = " + id);
                    DBmanager.pushSqlRaw("Delete from filmy where id = " + id);
                    Label label = JavaFxObjectsManager.createLabel("Usunieto");
                    ((ScrollPane) topBox.lookup("#RESULTS")).setContent(label);
                }
            });
            ((ScrollPane) topBox.lookup("#RESULTS")).setContent(filmy);
        }
    }
    private void addBilet() {
        ResultSet result = DBmanager.pushSqlRaw("SELECT LOGIN, EMAIL FROM KONTA");
        ArrayList<String> arguments = new ArrayList<>();
        ArrayList<String> emails = new ArrayList<>();
        if (result != null) {
            while (true) {
                try {
                    if (!result.next()) break;
                    arguments.add(result.getString("LOGIN"));
                    emails.add(result.getString("EMAIL"));
                } catch (SQLException e) {
                    return;
                }
            }
            ListView<String> logins = JavaFxObjectsManager.createHorizontalListView(arguments);
            logins.setOnMousePressed(_ -> {
                if (logins.getSelectionModel().getSelectedItem() != null) {
                    String email = emails.get(logins.getSelectionModel().getSelectedIndex());
                    ResultSet results = DBmanager.pushSqlRaw("SELECT MIASTO FROM KINA GROUP BY MIASTO");
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
                                            ArrayList<Integer> salaId = new ArrayList<>();
                                            ResultSet re = DBmanager.pushSqlRaw("SELECT ID, NUMERSALI FROM SALE s " +
                                                    "where s.kina_id = " + cinemaId.get(selectedCinemas.getSelectionModel().getSelectedIndex()));
                                            arguments.clear();
                                            if (re != null) {
                                                while (true) {
                                                    try {
                                                        if (!re.next()) break;
                                                        salaId.add(re.getInt("ID"));
                                                        arguments.add(re.getString("NUMERSALI"));
                                                    } catch (SQLException e) {
                                                        return;
                                                    }
                                                }
                                                ListView<String> selectedRooms = JavaFxObjectsManager.createHorizontalListView(arguments);
                                                selectedRooms.setOnMousePressed(_ -> {
                                                    if (!selectedRooms.getSelectionModel().isEmpty()) {
                                                        Integer salaIdRes = salaId.get(selectedRooms.getSelectionModel().getSelectedIndex());
                                                        ArrayList<Integer> filmyId = new ArrayList<>();
                                                        ResultSet r = DBmanager.pushSqlRaw("SELECT ID, TYTULFILMU FROM FILMY");
                                                        arguments.clear();
                                                        if (r != null) {
                                                            while (true) {
                                                                try {
                                                                    if (!r.next()) break;
                                                                    filmyId.add(r.getInt("ID"));
                                                                    arguments.add(r.getString("TYTULFILMU"));
                                                                } catch (SQLException e) {
                                                                    return;
                                                                }
                                                            }
                                                            ListView<String> selectedFilms = JavaFxObjectsManager.createHorizontalListView(arguments);
                                                            selectedFilms.setOnMousePressed(_ -> {
                                                                if (!selectedFilms.getSelectionModel().isEmpty()) {
                                                                    int filmId = filmyId.get(selectedFilms.getSelectionModel().getSelectedIndex());
                                                                    ResultSet resu = DBmanager.pushSqlRaw("SELECT ID, DATA FROM SEANSE WHERE FILMY_ID = " + filmId
                                                                            + " AND SALA_ID = " + salaIdRes);
                                                                    ArrayList<Integer> seansIdy = new ArrayList<>();
                                                                    arguments.clear();
                                                                    if (resu != null) {
                                                                        while (true) {
                                                                            try {
                                                                                if (!resu.next()) break;
                                                                                seansIdy.add(resu.getInt("ID"));
                                                                                arguments.add(resu.getString("DATA"));
                                                                            } catch (SQLException e) {
                                                                                return;
                                                                            }
                                                                        }
                                                                        ListView<String> selectedSeans = JavaFxObjectsManager.createHorizontalListView(arguments);
                                                                        selectedSeans.setOnMousePressed(_ -> {
                                                                            if (!selectedSeans.getSelectionModel().isEmpty()) {
                                                                                int seansId = seansIdy.get(selectedSeans.getSelectionModel().getSelectedIndex());
                                                                                ResultSet resul = DBmanager.pushSqlRaw("SELECT KOLUMNA, RZAD, ID FROM MIEJSCA WHERE SALA_ID = " + salaIdRes);
                                                                                ArrayList<Integer> miejscaId = new ArrayList<>();
                                                                                arguments.clear();
                                                                                if (resul != null) {
                                                                                    while (true) {
                                                                                        try {
                                                                                            if (!resul.next()) break;
                                                                                            miejscaId.add(resul.getInt("ID"));
                                                                                            arguments.add("Kolumna: " + resul.getInt("KOLUMNA") + ", Rzad: " + resul.getInt("RZAD"));
                                                                                        } catch (SQLException e) {
                                                                                            return;
                                                                                        }
                                                                                    }
                                                                                    ListView<String> selectedSeat = JavaFxObjectsManager.createHorizontalListView(arguments);
                                                                                    selectedSeat.setOnMousePressed(_ -> {
                                                                                        if (!selectedSeat.getSelectionModel().isEmpty()) {
                                                                                            int miejsceId = miejscaId.get(selectedSeat.getSelectionModel().getSelectedIndex());
                                                                                            ResultSet ra = DBmanager.pushSqlRaw("SELECT ID FROM REZERWACJE WHERE MIEJSCA_ID = " + miejsceId +
                                                                                                    " AND SEANSE_ID = " + seansId);
                                                                                            ArrayList<Integer> rezerwacje = new ArrayList<>();
                                                                                            if (ra != null) {
                                                                                                while (true) {
                                                                                                    try {
                                                                                                        if (!ra.next())
                                                                                                            break;
                                                                                                        rezerwacje.add(ra.getInt("ID"));
                                                                                                    } catch (SQLException e) {
                                                                                                        return;
                                                                                                    }
                                                                                                }
                                                                                                if (rezerwacje.size() > 1){
                                                                                                    Label label = JavaFxObjectsManager.createLabel("Niepoprawna liczba rezerwacji");
                                                                                                    ((ScrollPane) topBox.lookup("#RESULTS")).setContent(label);
                                                                                                } else {
                                                                                                    int reserveId = rezerwacje.getFirst();
                                                                                                    VBox box = JavaFxObjectsManager.createVBox(4, 4);
                                                                                                    Label label = JavaFxObjectsManager.createLabel("Cena");
                                                                                                    TextField cena = new TextField();
                                                                                                    Button send = JavaFxObjectsManager.createButton("Stworz", () -> {
                                                                                                        if (!cena.getText().isEmpty()) {
                                                                                                            ResultSet id = DBmanager.pushSqlRaw("SELECT MAX(ID) FROM BILETY");
                                                                                                            if (id != null) {
                                                                                                                try {
                                                                                                                    id.next();
                                                                                                                    int idInt = id.getInt(1) + 1;
                                                                                                                    DBmanager.pushSqlRaw("INSERT INTO BILETY VALUES(" + idInt + ", " +
                                                                                                                            cena.getText() + ", '" + email +"', " + reserveId + ")");
                                                                                                                    Label ress = JavaFxObjectsManager.createLabel("Wprowadzono");
                                                                                                                    ((ScrollPane) topBox.lookup("#RESULTS")).setContent(ress);
                                                                                                                } catch (
                                                                                                                        SQLException _) {
                                                                                                                    Label ress = JavaFxObjectsManager.createLabel("Nie udalo sie wprowadzenie");
                                                                                                                    ((ScrollPane) topBox.lookup("#RESULTS")).setContent(ress);
                                                                                                                }
                                                                                                            }
                                                                                                        }
                                                                                                    });
                                                                                                    Control [] controls = {label, cena, send};
                                                                                                    JavaFxObjectsManager.fillOrganizer(box, controls);
                                                                                                    ((ScrollPane) topBox.lookup("#RESULTS")).setContent(box);
                                                                                                }
                                                                                            }
                                                                                        }
                                                                                    });
                                                                                    ((ScrollPane) topBox.lookup("#RESULTS")).setContent(selectedSeat);
                                                                                }
                                                                            }
                                                                        });
                                                                        ((ScrollPane) topBox.lookup("#RESULTS")).setContent(selectedSeans);
                                                                    }
                                                                }
                                                            });
                                                            ((ScrollPane) topBox.lookup("#RESULTS")).setContent(selectedFilms);
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
            });
            ((ScrollPane) topBox.lookup("#RESULTS")).setContent(logins);
        }
    }
    private void removeBilet() {
        ResultSet result = DBmanager.pushSqlRaw("SELECT LOGIN, EMAIL FROM KONTA");
        ArrayList<String> arguments = new ArrayList<>();
        ArrayList<String> emails = new ArrayList<>();
        if (result != null) {
            while (true) {
                try {
                    if (!result.next()) break;
                    arguments.add(result.getString("LOGIN"));
                    emails.add(result.getString("EMAIL"));
                } catch (SQLException e) {
                    return;
                }
            }
            ListView<String> logins = JavaFxObjectsManager.createHorizontalListView(arguments);
            logins.setOnMousePressed(_ -> {
                if (logins.getSelectionModel().getSelectedItem() != null) {
                    String email = emails.get(logins.getSelectionModel().getSelectedIndex());
                    ResultSet results = DBmanager.pushSqlRaw("SELECT MIASTO FROM KINA GROUP BY MIASTO");
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
                                            ArrayList<Integer> salaId = new ArrayList<>();
                                            ResultSet re = DBmanager.pushSqlRaw("SELECT ID, NUMERSALI FROM SALE s " +
                                                    "where s.kina_id = " + cinemaId.get(selectedCinemas.getSelectionModel().getSelectedIndex()));
                                            arguments.clear();
                                            if (re != null) {
                                                while (true) {
                                                    try {
                                                        if (!re.next()) break;
                                                        salaId.add(re.getInt("ID"));
                                                        arguments.add(re.getString("NUMERSALI"));
                                                    } catch (SQLException e) {
                                                        return;
                                                    }
                                                }
                                                ListView<String> selectedRooms = JavaFxObjectsManager.createHorizontalListView(arguments);
                                                selectedRooms.setOnMousePressed(_ -> {
                                                    if (!selectedRooms.getSelectionModel().isEmpty()) {
                                                        Integer salaIdRes = salaId.get(selectedRooms.getSelectionModel().getSelectedIndex());
                                                        ArrayList<Integer> filmyId = new ArrayList<>();
                                                        ResultSet r = DBmanager.pushSqlRaw("SELECT ID, TYTULFILMU FROM FILMY");
                                                        arguments.clear();
                                                        if (r != null) {
                                                            while (true) {
                                                                try {
                                                                    if (!r.next()) break;
                                                                    filmyId.add(r.getInt("ID"));
                                                                    arguments.add(r.getString("TYTULFILMU"));
                                                                } catch (SQLException e) {
                                                                    return;
                                                                }
                                                            }
                                                            ListView<String> selectedFilms = JavaFxObjectsManager.createHorizontalListView(arguments);
                                                            selectedFilms.setOnMousePressed(_ -> {
                                                                if (!selectedFilms.getSelectionModel().isEmpty()) {
                                                                    int filmId = filmyId.get(selectedFilms.getSelectionModel().getSelectedIndex());
                                                                    ResultSet resu = DBmanager.pushSqlRaw("SELECT ID, DATA FROM SEANSE WHERE FILMY_ID = " + filmId
                                                                            + " AND SALA_ID = " + salaIdRes);
                                                                    ArrayList<Integer> seansIdy = new ArrayList<>();
                                                                    arguments.clear();
                                                                    if (resu != null) {
                                                                        while (true) {
                                                                            try {
                                                                                if (!resu.next()) break;
                                                                                seansIdy.add(resu.getInt("ID"));
                                                                                arguments.add(resu.getString("DATA"));
                                                                            } catch (SQLException e) {
                                                                                return;
                                                                            }
                                                                        }
                                                                        ListView<String> selectedSeans = JavaFxObjectsManager.createHorizontalListView(arguments);
                                                                        selectedSeans.setOnMousePressed(_ -> {
                                                                            if (!selectedSeans.getSelectionModel().isEmpty()) {
                                                                                int seansId = seansIdy.get(selectedSeans.getSelectionModel().getSelectedIndex());
                                                                                ResultSet resul = DBmanager.pushSqlRaw("SELECT KOLUMNA, RZAD, ID FROM MIEJSCA WHERE SALA_ID = " + salaIdRes);
                                                                                ArrayList<Integer> miejscaId = new ArrayList<>();
                                                                                arguments.clear();
                                                                                if (resul != null) {
                                                                                    while (true) {
                                                                                        try {
                                                                                            if (!resul.next()) break;
                                                                                            miejscaId.add(resul.getInt("ID"));
                                                                                            arguments.add("Kolumna: " + resul.getInt("KOLUMNA") + ", Rzad: " + resul.getInt("RZAD"));
                                                                                        } catch (SQLException e) {
                                                                                            return;
                                                                                        }
                                                                                    }
                                                                                    ListView<String> selectedSeat = JavaFxObjectsManager.createHorizontalListView(arguments);
                                                                                    selectedSeat.setOnMousePressed(_ -> {
                                                                                        if (!selectedSeat.getSelectionModel().isEmpty()) {
                                                                                            int miejsceId = miejscaId.get(selectedSeat.getSelectionModel().getSelectedIndex());
                                                                                            ResultSet ra = DBmanager.pushSqlRaw("SELECT ID FROM REZERWACJE WHERE MIEJSCA_ID = " + miejsceId +
                                                                                                    " AND SEANSE_ID = " + seansId);
                                                                                            ArrayList<Integer> rezerwacje = new ArrayList<>();
                                                                                            if (ra != null) {
                                                                                                while (true) {
                                                                                                    try {
                                                                                                        if (!ra.next())
                                                                                                            break;
                                                                                                        rezerwacje.add(ra.getInt("ID"));
                                                                                                    } catch (SQLException e) {
                                                                                                        return;
                                                                                                    }
                                                                                                }
                                                                                                if (rezerwacje.size() > 1){
                                                                                                    Label label = JavaFxObjectsManager.createLabel("Niepoprawna liczba rezerwacji");
                                                                                                    ((ScrollPane) topBox.lookup("#RESULTS")).setContent(label);
                                                                                                } else {
                                                                                                    int reserveId = rezerwacje.getFirst();
                                                                                                    ResultSet raa = DBmanager.pushSqlRaw("SELECT ID FROM BILETY WHERE KONTA_EMAIL = '" + email +
                                                                                                            "' AND REZERWACJE_ID = " + reserveId);
                                                                                                    arguments.clear();
                                                                                                    if (raa != null) {
                                                                                                        while (true) {
                                                                                                            try {
                                                                                                                if (!raa.next())
                                                                                                                    break;
                                                                                                                arguments.add(String.valueOf(raa.getInt("ID")));
                                                                                                            } catch (SQLException e) {
                                                                                                                return;
                                                                                                            }
                                                                                                        }
                                                                                                        ListView<String> selectedBilet = JavaFxObjectsManager.createHorizontalListView(arguments);
                                                                                                        selectedBilet.setOnMousePressed(_ -> {
                                                                                                            if (selectedBilet.getSelectionModel().getSelectedItem() != null){
                                                                                                                DBmanager.pushSqlRaw("DELETE FROM BILETY WHERE ID = " + selectedBilet.getSelectionModel().getSelectedItem());
                                                                                                                Label resulta = JavaFxObjectsManager.createLabel("Usunieto");
                                                                                                                ((ScrollPane) topBox.lookup("#RESULTS")).setContent(resulta);
                                                                                                            }
                                                                                                        });
                                                                                                        ((ScrollPane) topBox.lookup("#RESULTS")).setContent(selectedBilet);
                                                                                                    }
                                                                                                }
                                                                                            }
                                                                                        }
                                                                                    });
                                                                                    ((ScrollPane) topBox.lookup("#RESULTS")).setContent(selectedSeat);
                                                                                }
                                                                            }
                                                                        });
                                                                        ((ScrollPane) topBox.lookup("#RESULTS")).setContent(selectedSeans);
                                                                    }
                                                                }
                                                            });
                                                            ((ScrollPane) topBox.lookup("#RESULTS")).setContent(selectedFilms);
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
            });
            ((ScrollPane) topBox.lookup("#RESULTS")).setContent(logins);
        }
    }
}
