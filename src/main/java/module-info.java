module mx.edu.utch.gato {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.xerial.sqlitejdbc;

    exports app;
    opens app to javafx.fxml;

    exports controller;
    opens controller to javafx.fxml;

    opens modelo to javafx.base, javafx.controls;
}
