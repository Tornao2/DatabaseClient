module org.example.databaseclient {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    requires jdk.compiler;

    opens org.example.databaseclient to javafx.fxml;
    exports org.example.databaseclient;
}