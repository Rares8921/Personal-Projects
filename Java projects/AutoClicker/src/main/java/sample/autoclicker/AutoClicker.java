package sample.autoclicker;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;

import java.awt.*;
import java.awt.event.InputEvent;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class AutoClicker {
    public static Robot robot;
    public static Point mousePosition;

    // Initialize the action
    private static boolean firstClick = true;

    public static boolean toggled = false, activated = false, skip = false, multipleButtonPress = false;
    private static int delay = (1000 / 11);
    public static int CPS = 10, mouseButton = 1;
    public static long lastTime = 0;

    public static int toggleMouseButton = 3;

    private static final int Button1Mask = InputEvent.BUTTON1_DOWN_MASK; // Left Click
    private static final int Button2Mask = InputEvent.BUTTON2_DOWN_MASK; // Right Click

    public static final Timeline task = new Timeline(new KeyFrame(Duration.millis(1), e -> clicker()));

    static { // Firstly, load the listeners
        // Clear the logger
        LogManager.getLogManager().reset();
        // getPackage().getName();
        // No logger messages
        Logger.getLogger(GlobalScreen.class.getPackageName()).setLevel(Level.OFF);
        try {
            robot = new Robot();
            GlobalScreen.registerNativeHook();
            GlobalScreen.addNativeMouseListener(new AutoMouseListener());
            GlobalScreen.addNativeKeyListener(new AutoKeyListener());
        } catch(NativeHookException|AWTException e) {
            e.printStackTrace(); //TODO: Replace with logger
        }
    }

    public static void click() {
        // Skip time based on cps
        skip = true;
        // Press left click
        robot.mousePress(Button1Mask);
        robot.mouseRelease(Button1Mask);
        // Press right click
        if(multipleButtonPress) {
            robot.mousePress((mouseButton == 1) ? Button2Mask : Button1Mask);
            robot.mouseRelease((mouseButton == 1) ? Button2Mask : Button1Mask);
        }
    }

    public static void toggle() {
        // TODO: On and off button activate and deactivate
        // code:
        AutoClicker.toggled = !AutoClicker.toggled;
        if(AutoClicker.toggled && !AutoClicker.firstClick) {
            task.play();
        } else {
            task.pause();
        }
        AutoClicker.activated = false;
        AutoClicker.skip = false;
        AutoClicker.multipleButtonPress = false;
        if(AutoClicker.firstClick) {
            AutoClicker.main(new String[]{});
        }
    }

    public static void main(String[] args) {
        if(AutoClicker.firstClick) {
            task.setCycleCount(Animation.INDEFINITE);
            task.play();
            AutoClicker.firstClick = false;
        }
    }

    private static void clicker() {
        // Cycle for auto-click
        if (activated && toggled) {
            if (System.currentTimeMillis() - lastTime >= delay) {
                click();
                lastTime = System.currentTimeMillis();
                delay = (1000 / (CPS + 3));
            }
        }
    }
}