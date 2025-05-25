module com.example.ri {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires java.sql;
    requires jakarta.mail;

    requires org.postgresql.jdbc;
    requires java.desktop;
    requires net.sf.jasperreports.core;


    opens com.example.ri to javafx.fxml;
    exports com.example.ri;
}

