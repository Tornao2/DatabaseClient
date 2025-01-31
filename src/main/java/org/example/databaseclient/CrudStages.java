package org.example.databaseclient;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Pair;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

public class CrudStages {
    private final DatabaseManager DBmanager;
    VBox organizer;

    public CrudStages(DatabaseManager readDBManager) {
        DBmanager = readDBManager;
    }
    public VBox insertStage() {
        organizer = sharedSetUp();
        ListView<String> listView = (ListView<String>) organizer.getChildren().getFirst();
        organizer.getChildren().getFirst().setOnMousePressed(_ -> displayColumnsForInsert(listView.getSelectionModel().getSelectedItem()));
        return organizer;
    }
    public VBox readStage() {
        organizer = sharedSetUp();
        ListView<String> listView = (ListView<String>) organizer.getChildren().getFirst();
        organizer.getChildren().getFirst().setOnMousePressed(_ -> displayColumnsForRead(listView.getSelectionModel().getSelectedItem()));
        return organizer;
    }
    public VBox updateStage() {
        organizer = sharedSetUp();
        ListView<String> listView = (ListView<String>) organizer.getChildren().getFirst();
        organizer.getChildren().getFirst().setOnMousePressed(_ -> displayUpdate(listView.getSelectionModel().getSelectedItem()));
        return organizer;
    }
    public VBox deleteStage() {
        organizer = sharedSetUp();
        DBmanager.switchTableView(true);
        organizer.getChildren().getFirst().setOnMousePressed(_ -> displayDelete());
        return organizer;
    }

    private VBox sharedSetUp() {
        VBox returnObj = JavaFxObjectsManager.createVBox(4, 4);
        returnObj.setId("CrudMenus");
        ListView<String> tables = createTableChoice();
        JavaFxObjectsManager.fillOrganizer(returnObj, tables);
        return returnObj;
    }
    private ArrayList <String> processTable() {
        ResultSet tables = DBmanager.getTables();
        ArrayList <String> names = new ArrayList<>();
        while (true) {
            try {
                if (!tables.next()) break;
                names.add(tables.getString("TABLE_NAME"));
            } catch (SQLException e) {
                System.err.println("Couldn't process table: " + e.getMessage());
                return null;
            }
        }
        return names;
    }
    private ListView <String> createTableChoice () {
        ArrayList <String> tableNames = processTable();
        if (tableNames == null) return null;
        return JavaFxObjectsManager.createHorizontalListView(tableNames);
    }
    private void finishDisplaying(String text, Runnable function){
        Button button = JavaFxObjectsManager.createButton(text, function);
        button.setId("ActionButton");
        Label debugLabel = JavaFxObjectsManager.createLabel("");
        debugLabel.setId("debugLabel");
        Control [] finishControls = {button, debugLabel};
        JavaFxObjectsManager.fillOrganizer(organizer, finishControls);
    }
    private void finishSendingStatements(String text){
        Label debugLabel = (Label) organizer.lookup("#debugLabel");
        debugLabel.setText(text);
    }

    private void displayColumnsForInsert (String selectedTable) {
        ArrayList<Pair<String, Integer>> columns = DBmanager.getColumnNames(selectedTable);
        if (organizer.getChildren().size() > 1)
            organizer.getChildren().subList(1, organizer.getChildren().size()).clear();
        for(Pair <String, Integer> pair: columns){
            int type = pair.getValue();
            Label columnName = JavaFxObjectsManager.createLabel(pair.getKey());
            Control[] temp = new Control[0];
            switch (type){
                case 1:
                case 2:
                case 12:
                    TextField stringField = new TextField();
                    temp = new Control[]{columnName, stringField};
                    break;
                case 93:
                    DatePicker dateField = new DatePicker();
                    TextField hourField = new TextField();
                    temp = new Control[]{columnName, dateField, hourField};
                    break;
                default:
                    System.err.println("Can't handle type " + pair.getValue() + " " + pair.getKey());
                    type = -1;
                    break;
            }
            if (type == -1) continue;
            JavaFxObjectsManager.fillOrganizer(organizer, temp);
        }
        finishDisplaying("Wprowadz", this::sendInsertStatement);
    }
    private void sendInsertStatement() {
        ArrayList<String> data = new ArrayList<>();
        for(int i = 0; i < organizer.getChildren().size(); i++){
            Node currentItem = organizer.getChildren().get(i);
            if (currentItem instanceof TextField) {
                String text = ((TextField) currentItem).getText();
                data.add(text);
            } else if (currentItem instanceof DatePicker) {
                LocalDate date = ((DatePicker) currentItem).getValue();
                LocalTime time;
                try {
                    time = LocalTime.parse(((TextField) organizer.getChildren().get(i + 1)).getText());
                } catch (Exception e) {
                    String resultMessage = "Zly format daty";
                    finishSendingStatements(resultMessage);
                    return;
                }
                LocalDateTime dateTime = date.atTime(time);
                data.add(String.valueOf(dateTime));
                i++;
            }
        }
        ListView<String> listView = (ListView<String>) organizer.getChildren().getFirst();
        String resultMessage = DBmanager.insertIntoTable(listView.getSelectionModel().getSelectedItem(), data);
        finishSendingStatements(resultMessage);
    }

    private void displayColumnsForRead(String selectedTable){
        ArrayList<Pair<String, Integer>> columns = DBmanager.getColumnNames(selectedTable);
        if (organizer.getChildren().size() > 1)
            organizer.getChildren().subList(1, organizer.getChildren().size()).clear();
        CheckBox additionalOptions = new CheckBox("Funkcje agregujace");
        additionalOptions.setId("Options");
        additionalOptions.selectedProperty().addListener(new AdditionalReadAgg(organizer, DBmanager));
        JavaFxObjectsManager.fillOrganizer(organizer, additionalOptions);
        VBox columnGroup = JavaFxObjectsManager.createVBox(4, 4);
        columnGroup.setId("ColumnsBoxes");
        for(Pair <String, Integer> pair: columns)
            JavaFxObjectsManager.fillOrganizer(columnGroup, new CheckBox(pair.getKey()));
        JavaFxObjectsManager.fillOrganizer(organizer, columnGroup);
        CheckBox additionalChoice = new CheckBox("Dodatkowe warunki wyboru");
        additionalChoice.setId("Compare");
        additionalChoice.selectedProperty().addListener(new AdditionalReadWhere(organizer, DBmanager));
        JavaFxObjectsManager.fillOrganizer(organizer, additionalChoice);
        CheckBox group = new CheckBox("Grupowanie");
        group.setId("Group");
        group.selectedProperty().addListener(new AdditionalReadGroup(organizer, DBmanager));
        JavaFxObjectsManager.fillOrganizer(organizer, group);
        finishDisplaying("Odczytaj", this::sendReadStatement);
    }
    private void sendReadStatement(){
        if (organizer.getChildren().getLast() instanceof TableView<?>)
            organizer.getChildren().removeLast();
        CheckBox isAgg = (CheckBox) (organizer.lookup("#Options"));
        CheckBox checkCompare =  (CheckBox) organizer.lookup("#Compare");
        ArrayList<String> compareData = new ArrayList<>();
        ArrayList<?> data = null;
        CheckBox groupCheck = (CheckBox) organizer.lookup("#Group");
        String groupData = null;
        if (checkCompare.isSelected())
            compareData = getCompareData();
        if (isAgg.isSelected()){
            compareData.addFirst("Agg");
            data = getOptionsData();
        }
        if (groupCheck.isSelected()) {
            groupData = ((ListView<String>) organizer.lookup("#checkBoxGroupRead").lookup("#ColumnChoice")).getSelectionModel().getSelectedItem();
            if (checkCompare.isSelected())
                compareData.addFirst("Group");
        }
        if(!groupCheck.isSelected() && !isAgg.isSelected()) {
            compareData.addFirst("Columns");
            ArrayList<Boolean> checkedColumns = new ArrayList<>();
            ((VBox) organizer.lookup("#ColumnsBoxes")).getChildren().forEach(node -> {
                if (node instanceof CheckBox && node.getId() == null)
                    checkedColumns.add(((CheckBox) node).isSelected());
            });
            data = checkedColumns;
        }
        ListView<String> listView = (ListView<String>) organizer.getChildren().getFirst();
        Pair <ResultSet, String> results = DBmanager.readFromTable(listView.getSelectionModel().getSelectedItem(), data, compareData, groupData);
        finishSendingStatements(results.getValue());
        createReadTable(listView, data, results, groupData);
    }
    private ArrayList <String> getCompareData(){
        ArrayList<String> returnData = new ArrayList<>();
        ToggleButton equalCheck = (ToggleButton) organizer.lookup("#checkBoxLogicRead").lookup("#Toggles").lookup("#Equal");
        if (equalCheck.isSelected())
            returnData.add("Equal");
        else {
            ToggleButton lowerCheck = (ToggleButton) organizer.lookup("#checkBoxLogicRead").lookup("#Toggles").lookup("#Lower");
            if(lowerCheck.isSelected())
                returnData.add("Lower");
            else{
                ToggleButton higherCheck = (ToggleButton) organizer.lookup("#checkBoxLogicRead").lookup("#Toggles").lookup("#Higher");
                if (higherCheck.isSelected())
                    returnData.add("Higher");
                else
                    returnData.add("Between");
            }
        }
        ListView <String> columnName = (ListView<String>) organizer.lookup("#checkBoxLogicRead").lookup("#ColumnChoice");
        returnData.add(columnName.getSelectionModel().getSelectedItem());
        TextField valueField = (TextField) organizer.lookup("#checkBoxLogicRead").lookup("#ValueBox").lookup("#Value");
        returnData.add(valueField.getText());
        if(returnData.getFirst().equals("Between")) {
            TextField adField = (TextField) organizer.lookup("#checkBoxLogicRead").lookup("#ValueBox").lookup("#BetweenBox").lookup("#SecondValue");
            returnData.add(adField.getText());
        }
        return returnData;
    }
    private ArrayList<String> getOptionsData() {
        ArrayList <String> returnData = new ArrayList<>();
        ToggleButton countCheck = (ToggleButton) organizer.lookup("#checkBoxOptionsRead").lookup("#Toggles").lookup("#Count");
        ToggleButton minCheck = (ToggleButton) organizer.lookup("#checkBoxOptionsRead").lookup("#Toggles").lookup("#Min");
        ToggleButton maxCheck = (ToggleButton) organizer.lookup("#checkBoxOptionsRead").lookup("#Toggles").lookup("#Max");
        ToggleButton avgCheck = (ToggleButton) organizer.lookup("#checkBoxOptionsRead").lookup("#Toggles").lookup("#Avg");
        ToggleButton sumCheck = (ToggleButton) organizer.lookup("#checkBoxOptionsRead").lookup("#Toggles").lookup("#Sum");
        if(countCheck.isSelected()) returnData.add("Count(");
        else if(minCheck.isSelected()) returnData.add("Min(");
        else if(maxCheck.isSelected()) returnData.add("Max(");
        else if(avgCheck.isSelected()) returnData.add("Avg(");
        else if(sumCheck.isSelected()) returnData.add("Sum(");
        ListView<String> columnChoice = (ListView<String>) organizer.lookup("#checkBoxOptionsRead").lookup("#ColumnChoice");
        if (columnChoice != null)
            returnData.set(0,returnData.getFirst().concat(columnChoice.getSelectionModel().getSelectedItem() + ")"));
        else returnData.set(0,returnData.getFirst().concat("*)"));
        return returnData;
    }
    private void createReadTable(ListView<String> columnList, ArrayList<?> data, Pair <ResultSet, String> resultFromRead, String groupName) {
        ArrayList<Pair<String, Integer>> columnNames = DBmanager.getColumnNames(columnList.getSelectionModel().getSelectedItem());
        ArrayList<String> finishedColumnNames = new ArrayList<>();
        CheckBox isAgg = (CheckBox) (organizer.lookup("#Options"));
        TableView<ObservableList<String>> resultsTable;
        ObservableList<ObservableList<String>> allRows = FXCollections.observableArrayList();
        if (isAgg.isSelected()) {
            ArrayList<String> optionsData = (ArrayList<String>) data;
            ArrayList<String> createTableData = new ArrayList<>();
            createTableData.add(optionsData.getFirst());
            if (groupName != null) createTableData.add(groupName);
            resultsTable = JavaFxObjectsManager.createTableView(createTableData);
            if (resultFromRead.getKey() != null) {
                while (true) {
                    try {
                        if (!resultFromRead.getKey().next()) break;
                        ObservableList<String> row = FXCollections.observableArrayList();
                        row.add(resultFromRead.getKey().getString(createTableData.getFirst()));
                        if(groupName != null) row.add(resultFromRead.getKey().getString(createTableData.get(1)));
                        allRows.add(row);
                    } catch (SQLException e) {
                        System.err.println("Couldn't get results: " + e.getMessage());
                    }
                }
                resultsTable.setItems(allRows);
                JavaFxObjectsManager.fillOrganizer(organizer, resultsTable);
            }
        } else if (groupName != null){
            resultsTable = JavaFxObjectsManager.createTableView(groupName);
            if (resultFromRead.getKey() != null) {
                while (true) {
                    try {
                        if (!resultFromRead.getKey().next()) break;
                        ObservableList<String> row = FXCollections.observableArrayList();
                        row.add(resultFromRead.getKey().getString(groupName));
                        allRows.add(row);
                    } catch (SQLException e) {
                        System.err.println("Couldn't get results: " + e.getMessage());
                    }
                }
                resultsTable.setItems(allRows);
                JavaFxObjectsManager.fillOrganizer(organizer, resultsTable);
            }
        }
        else if (data != null){
            ArrayList<Boolean> checkedColumns = (ArrayList<Boolean>) data;
            for (int i = 0; i < columnNames.size(); i++)
                if (checkedColumns.get(i)) finishedColumnNames.add(columnNames.get(i).getKey());
            resultsTable = JavaFxObjectsManager.createTableView(finishedColumnNames);
            if (resultFromRead.getKey() != null) {
                while (true) {
                    try {
                        if (!resultFromRead.getKey().next()) break;
                        ObservableList<String> row = FXCollections.observableArrayList();
                        for (int i = 0; i < resultsTable.getColumns().size(); i++)
                            row.add(resultFromRead.getKey().getString(finishedColumnNames.get(i)));
                        allRows.add(row);
                    } catch (SQLException e) {
                        System.err.println("Couldn't get results: " + e.getMessage());
                    }
                }
                resultsTable.setItems(allRows);
                JavaFxObjectsManager.fillOrganizer(organizer, resultsTable);
            }
        }
    }

    private void displayDelete(){
        if (organizer.getChildren().size() > 1)
            organizer.getChildren().subList(1, organizer.getChildren().size()).clear();
        HBox box = JavaFxObjectsManager.createHBox(4, 4);
        box.setId("box");
        ToggleGroup specialToggleGroup = new ToggleGroup();
        ToggleButton special1 = new ToggleButton("Cala tabela");
        special1.setId("table");
        special1.setToggleGroup(specialToggleGroup);
        ToggleButton special2 = new ToggleButton("Poszczegolne rzedy");
        special2.setId("rows");
        special2.setToggleGroup(specialToggleGroup);
        special1.setSelected(true);
        Control[] toggles = {special1, special2};
        JavaFxObjectsManager.fillOrganizer(box, toggles);
        JavaFxObjectsManager.fillOrganizer(organizer, box);
        specialToggleGroup.selectedToggleProperty().addListener(new AdditionalDeleteColumns(organizer, DBmanager));
        finishDisplaying("Usun", this::sendDeleteStatement);
    }
    private void sendDeleteStatement(){
        ToggleButton special1 = ((ToggleButton) organizer.lookup("#box").lookup("#table"));
        ArrayList<String> data = new ArrayList<>();
        String tableName = (((ListView<String>) organizer.getChildren().getFirst()).getSelectionModel().getSelectedItem());
        if (special1.isSelected())  data.add("Table");
        else {
            data.add("Columns");
            data.add(((ListView<String>) organizer.lookup("#whereDelete").lookup("#ColumnChoice")).getSelectionModel().getSelectedItem());
            if (((ToggleButton)organizer.lookup("#whereDelete").lookup("#Toggles").lookup("#Equal")).isSelected())
                data.add("Equal");
            else if (((ToggleButton)organizer.lookup("#whereDelete").lookup("#Toggles").lookup("#Lower")).isSelected())
                data.add("Lower");
            else if (((ToggleButton)organizer.lookup("#whereDelete").lookup("#Toggles").lookup("#Higher")).isSelected())
                data.add("Higher");
            else {
                data.add("Between");
                data.add(((TextField)organizer.lookup("#whereDelete").lookup("#ValueBox").lookup("#BetweenBox").lookup("#SecondValue")).getText());
            }
            data.add(3, ((TextField)(organizer.lookup("#whereDelete").lookup("#ValueBox").lookup("#Value"))).getText());
        }
        String resultMessage = DBmanager.deleteFromTable(tableName, data);
        finishSendingStatements(resultMessage);
    }

    private void displayUpdate(String selectedTable){
        if (organizer.getChildren().size() > 1)
            organizer.getChildren().subList(1, organizer.getChildren().size()).clear();
        Label guideLabel = JavaFxObjectsManager.createLabel("Po czym decydowac jakie rzedy aktualizowac");
        ListView <String> deciding = setColumn();
        deciding.setId("Deciding");
        deciding.setOnMousePressed(_-> setToggles(selectedTable));
        Control[] controls = {guideLabel, deciding};
        JavaFxObjectsManager.fillOrganizer(organizer, controls);
        finishDisplaying("Zmien", this::sendUpdateStatement);
    }
    private void setToggles (String selectedTable){
        if (organizer.getChildren().size() > 3)
            organizer.getChildren().subList(3, organizer.getChildren().size()-2).clear();
        HBox overallCriteriaBox = JavaFxObjectsManager.createHBox(4, 4);
        overallCriteriaBox.setId("Toggles");
        TextField valField = new TextField();
        valField.setId("Value");
        Control [] controls = setTogglesUpdate();
        JavaFxObjectsManager.fillOrganizer(overallCriteriaBox, controls);
        int id = organizer.getChildren().indexOf((organizer.lookup("#Deciding")));
        organizer.getChildren().add(id + 1, overallCriteriaBox);
        organizer.getChildren().add(id + 2, valField);
        VBox columnsBox = JavaFxObjectsManager.createVBox(4, 4);
        columnsBox.setId("columnsBox");
        ArrayList<Pair<String, Integer>> columns = DBmanager.getColumnNames(selectedTable);
        for(Pair <String, Integer> pair: columns) {
            CheckBox tempCheck = new CheckBox(pair.getKey());
            JavaFxObjectsManager.fillOrganizer(columnsBox, tempCheck);
            tempCheck.selectedProperty().addListener((_, _, newValue) -> {
                if (newValue) {
                    ArrayList<Node> temp = new ArrayList<>();
                    int type = pair.getValue();
                    switch (type) {
                        case 1:
                        case 2:
                        case 12:
                            TextField stringField = new TextField();
                            temp.add(stringField);
                            break;
                        case 93:
                            DatePicker dateField = new DatePicker();
                            TextField hourField = new TextField();
                            temp.add(hourField);
                            temp.add(dateField);
                            break;
                        default:
                            System.err.println("Can't handle type " + pair.getValue() + " " + pair.getKey());
                            type = -1;
                            break;
                    }
                    if (type != -1) {
                        int num = ((VBox) organizer.lookup("#columnsBox")).getChildren().indexOf(tempCheck);
                        for (Node n: temp)
                            ((VBox) organizer.lookup("#columnsBox")).getChildren().add(num + 1, n);
                    }
                } else {
                    int num = ((VBox) organizer.lookup("#columnsBox")).getChildren().indexOf(tempCheck);
                    ((VBox) organizer.lookup("#columnsBox")).getChildren().remove(num + 1);
                    if (((VBox) organizer.lookup("#columnsBox")).getChildren().size() > num + 1 &&
                            !(((VBox) organizer.lookup("#columnsBox")).getChildren().get(num + 1) instanceof CheckBox))
                        ((VBox) organizer.lookup("#columnsBox")).getChildren().remove(num + 1);
                }
            });
        }
        organizer.getChildren().add(id + 3, columnsBox);
    }
    private Control[] setTogglesUpdate() {
        ToggleGroup toggleGroup = new ToggleGroup();
        ToggleButton tb1 = new ToggleButton("Rowny");
        tb1.setId("Equal");
        tb1.setToggleGroup(toggleGroup);
        tb1.setSelected(true);
        ToggleButton tb2 = new ToggleButton("Mniejszy od");
        tb2.setId("Lower");
        tb2.setToggleGroup(toggleGroup);
        ToggleButton tb3 = new ToggleButton("Wiekszy od");
        tb3.setId("Higher");
        tb3.setToggleGroup(toggleGroup);
        ToggleButton tb4 = new ToggleButton("Pomiedzy");
        tb4.setId("Between");
        tb4.setToggleGroup(toggleGroup);
        Control[] controls = {tb1, tb2, tb3, tb4};
        toggleGroup.selectedToggleProperty().addListener((_, _, newValue) -> {
            if (newValue == tb4) {
                VBox BetweenBox = JavaFxObjectsManager.createVBox(4, 4);
                BetweenBox.setId("BetweenBox");
                Label moreLabel = new Label("Mniej niz: ");
                TextField valueField = new TextField();
                valueField.setId("SecondValue");
                Label lessLabel = new Label("Wiecej niz: ");
                Control [] temp = {moreLabel, valueField, lessLabel};
                JavaFxObjectsManager.fillOrganizer(BetweenBox, temp);
                int id = organizer.getChildren().indexOf((organizer.lookup("#Value")));
                organizer.getChildren().add(id , BetweenBox);
            } else {
                VBox BetweenBox = (VBox) organizer.lookup("#BetweenBox");
                if (BetweenBox != null)
                    organizer.getChildren().remove(organizer.lookup("#BetweenBox"));
            }
        });
        return controls;
    }
    private ListView<String> setColumn() {
        ListView<String> listView = (ListView<String>) organizer.getChildren().getFirst();
        ArrayList<Pair<String, Integer>> columnNames = DBmanager.getColumnNames(listView.getSelectionModel().getSelectedItem());
        ArrayList <String> onlyColumnNames = new ArrayList<>();
        for(Pair<String, Integer> pair: columnNames)
            onlyColumnNames.add(pair.getKey());
        return JavaFxObjectsManager.createHorizontalListView(onlyColumnNames);
    }
    private void sendUpdateStatement(){
        ArrayList<String> data = new ArrayList<>();
        String tableName = (((ListView<String>) organizer.getChildren().getFirst()).getSelectionModel().getSelectedItem());
        data.add(((ListView<String>) organizer.lookup("#Deciding")).getSelectionModel().getSelectedItem());
        if (organizer.lookup("#Toggles") != null) {
            if (((ToggleButton) organizer.lookup("#Toggles").lookup("#Equal")).isSelected())
                data.add("Equal");
            else if (((ToggleButton) organizer.lookup("#Toggles").lookup("#Lower")).isSelected())
                data.add("Lower");
            else if (((ToggleButton) organizer.lookup("#Toggles").lookup("#Higher")).isSelected())
                data.add("Higher");
            else {
                data.add("Between");
                data.add(((TextField) organizer.lookup("#BetweenBox").lookup("#SecondValue")).getText());
            }
            data.add(2, ((TextField)(organizer.lookup("#Value"))).getText());
        }
        ArrayList<Pair<String, String>> columns = new ArrayList<>();
        if (organizer.lookup("#columnsBox") != null) {
            ((VBox) organizer.lookup("#columnsBox")).getChildren().forEach(node -> {
                if (node instanceof CheckBox && ((CheckBox) node).isSelected()) {
                    String columnName = ((CheckBox) node).getText();
                    int id = ((VBox) organizer.lookup("#columnsBox")).getChildren().indexOf(node);
                    String value;
                    if (((VBox) organizer.lookup("#columnsBox")).getChildren().get(id + 1) instanceof TextField) {
                        value = ((TextField) ((VBox) organizer.lookup("#columnsBox")).getChildren().get(id + 1)).getText();
                    } else {
                        value = ((DatePicker) ((VBox) organizer.lookup("#columnsBox")).getChildren().get(id + 1)).getValue()
                                + " " + ((TextField) ((VBox) organizer.lookup("#columnsBox")).getChildren().get(id + 2)).getText();
                    }
                    columns.add(new Pair<>(columnName, value));
                }
            });
        }
        String resultMessage = DBmanager.updateTable(tableName, data, columns);
        finishSendingStatements(resultMessage);
    }
}
