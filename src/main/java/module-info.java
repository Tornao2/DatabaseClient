module org.example.databaseclient {
    requires javafx.controls;
    requires javafx.fxml;

    opens org.example.databaseclient to javafx.fxml;
    exports org.example.databaseclient;
}