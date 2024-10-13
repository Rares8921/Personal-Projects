package sample.autoclicker;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseListener;

import java.awt.event.MouseEvent;

public class AutoMouseListener implements NativeMouseListener {

    private boolean leftClick, rightClick;
    private long lastClick = 0;

    @Override
    public void nativeMousePressed(NativeMouseEvent nativeMouseEvent) {

        // Turn off auto if the user presses the toggle mouse button
        if(nativeMouseEvent.getButton() == AutoClicker.toggleMouseButton) {
            AutoClicker.toggle();
            Platform.runLater(() -> HelloController.staticOnOffLabel.setText(AutoClicker.toggled ? "- ON" : "- OFF"));
        }
        if(AutoClicker.toggled && !AutoClicker.skip) {
            // Check which is click is pressed
            if(nativeMouseEvent.getButton() == MouseEvent.BUTTON1) {
                leftClick = true;
            } else if(nativeMouseEvent.getButton() == MouseEvent.BUTTON2) {
                rightClick = true;
            }
            if(leftClick && rightClick) {
                AutoClicker.multipleButtonPress = true;
            }
            // If the click pressed by user matches the selected click in the application
            if(nativeMouseEvent.getButton() == AutoClicker.mouseButton) {
                AutoClicker.mousePosition = nativeMouseEvent.getPoint();
                AutoClicker.activated = true;
                AutoClicker.lastTime = System.currentTimeMillis();
            }
        }

        if(nativeMouseEvent.getButton() == AutoClicker.mouseButton) {
            // Check if 1 second passed between clicks
            if(System.currentTimeMillis() - lastClick > 1000 && lastClick != 0) {
                lastClick = 0;
            }
            // If 1 second passed, get the current time to calculate further
            if(lastClick == 0) {
                lastClick = System.currentTimeMillis();
            } else if(System.currentTimeMillis() != lastClick) {
                // Some reference values: 1000 millis => 1 cps; 500 millis => 2 cps; 400 millis => 2.45 cps, 200 => 4.9 cps, 100 => 9.9 cps, 50 => 19.6 cps
                lastClick = 0;
            }
        }
    }

    @Override
    public void nativeMouseReleased(NativeMouseEvent nativeMouseEvent) {
        if(!AutoClicker.skip) { // Turn off auto if left click is not being pressed anymore.
            if(nativeMouseEvent.getButton() == AutoClicker.mouseButton) {
                leftClick = false;
                AutoClicker.activated = false;
            } else if(nativeMouseEvent.getButton() == ((AutoClicker.mouseButton == 1) ? 2 : 1)) { // Same logic, but on right click
                rightClick = false;
                AutoClicker.multipleButtonPress = false;
            }
        } else {
            // Skip = true <=> Both mouse buttons were clicked at the same time
            AutoClicker.skip = nativeMouseEvent.getButton() == AutoClicker.mouseButton && AutoClicker.multipleButtonPress;
        }
    }

    @Override
    public void nativeMouseClicked(NativeMouseEvent nativeMouseEvent) {
        // Empty
    }
}
