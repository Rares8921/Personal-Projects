package main;

import javafx.animation.FadeTransition;
import javafx.scene.control.Button;
import javafx.scene.control.skin.ButtonSkin;
import javafx.util.Duration;

public class CustomButtonSkin extends ButtonSkin {

    private String initialText;

    public CustomButtonSkin(Button control, String symbol) {
        super(control);

        initialText = control.getText();

        final FadeTransition fadeIn = new FadeTransition(Duration.millis(500));
        fadeIn.setNode(control);
        fadeIn.setToValue(1);
        control.setOnMouseEntered(e -> {
            fadeIn.playFromStart();
            control.setText(symbol + " " + initialText);
        });

        final FadeTransition fadeOut = new FadeTransition(Duration.millis(400));
        fadeOut.setNode(control);
        fadeOut.setToValue(0.5);
        control.setOnMouseExited(e -> {
            fadeOut.playFromStart();
            control.setText(initialText);
        });

        control.setOpacity(0.5);
    }

    public void setInitialText(String _initialText) {
        initialText = _initialText;
    }
}
