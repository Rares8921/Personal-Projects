module sample.autoclicker {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires java.desktop;
    requires jnativehook;
    requires java.logging;

    opens sample.autoclicker to javafx.fxml;
    exports sample.autoclicker;
}