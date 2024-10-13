package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class Main extends Application {

    public static Stage stage = null;
    private static final ArrayList<Boolean> keyPressed = new ArrayList<>(); // To check if a key is already pressed so that the sound is not repeated infinitely
    public static Controller controller;

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/virtual-piano.fxml"));
        Parent root = loader.load();
        controller = loader.getController(); // To call controller functions
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT); // For rounded corners
        Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icon.png")));
        primaryStage.getIcons().add(icon);
        primaryStage.initStyle(StageStyle.UNDECORATED); // To eliminate the title bar
        primaryStage.initStyle(StageStyle.TRANSPARENT); // For rounded corners
        primaryStage.setTitle("Virtual piano"); // Respecting the case when the user hovers the app in taskbar
        primaryStage.setScene(scene);
        stage = primaryStage;
        stage.show();

        // There are 61 keys
        // 0-35 - white keys, 36-60 - black keys
        for(int i = 0; i < 61; i++) {
            keyPressed.add(false);
        }

        scene.setOnKeyPressed(e -> {
            switch(e.getCode()) {
                case DIGIT1:
                    if (e.isShiftDown() && !keyPressed.get(36)) {
                        controller.playMedia("Db1", "!");
                        keyPressed.set(36, true);
                        controller.Db1.setStyle("-fx-background-color: #1a1a1a");
                    } else if (!e.isShiftDown() && !keyPressed.get(0)) {
                        controller.playMedia("C1", "1");
                        keyPressed.set(0, true);
                        controller.C1.setStyle("-fx-background-color: #d9d9d9");
                    }
                    break;
                case DIGIT2:
                    if (e.isShiftDown() && !keyPressed.get(37)) {
                        controller.playMedia("Eb1", "@");
                        keyPressed.set(37, true);
                        controller.Eb1.setStyle("-fx-background-color: #1a1a1a");
                    } else if (!e.isShiftDown() && !keyPressed.get(1)) {
                        controller.playMedia("D1", "2");
                        keyPressed.set(1, true);
                        controller.D1.setStyle("-fx-background-color: #d9d9d9");
                    }
                    break;
                case DIGIT3:
                    controller.playMedia("E1", "3");
                    keyPressed.set(2, true);
                    controller.E1.setStyle("-fx-background-color: #d9d9d9");
                    break;
                case DIGIT4:
                    if (e.isShiftDown() && !keyPressed.get(38)) {
                        controller.playMedia("Gb1", "$");
                        keyPressed.set(38, true);
                        controller.Gb1.setStyle("-fx-background-color: #1a1a1a");
                    } else if (!keyPressed.get(3)) {
                        controller.playMedia("F1", "4");
                        keyPressed.set(3, true);
                        controller.F1.setStyle("-fx-background-color: #d9d9d9");
                    }
                    break;
                case DIGIT5:
                    if (e.isShiftDown() && !keyPressed.get(39)) {
                        controller.playMedia("Ab2", "%");
                        keyPressed.set(39, true);
                        controller.Ab2.setStyle("-fx-background-color: #1a1a1a");
                    } else if (!keyPressed.get(4)) {
                        controller.playMedia("G1", "5");
                        keyPressed.set(4, true);
                        controller.G1.setStyle("-fx-background-color: #d9d9d9");
                    }
                    break;
                case DIGIT6:
                    if (e.isShiftDown() && !keyPressed.get(40)) {
                        controller.playMedia("Bb2", "");
                        keyPressed.set(40, true);
                        controller.Bb2.setStyle("-fx-background-color: #1a1a1a");
                    } else if (!keyPressed.get(5)) {
                        controller.playMedia("A2", "6");
                        keyPressed.set(5, true);
                        controller.A2.setStyle("-fx-background-color: #d9d9d9");
                    }
                    break;
                case DIGIT7:
                    controller.playMedia("B2", "7");
                    keyPressed.set(6, true);
                    controller.B2.setStyle("-fx-background-color: #d9d9d9");
                    break;
                case DIGIT8:
                    if (e.isShiftDown() && !keyPressed.get(41)) {
                        controller.playMedia("Db2", "*");
                        keyPressed.set(41, true);
                        controller.Db2.setStyle("-fx-background-color: #1a1a1a");
                    } else if (!keyPressed.get(7)) {
                        controller.playMedia("C2", "8");
                        keyPressed.set(7, true);
                        controller.C2.setStyle("-fx-background-color: #d9d9d9");
                    }
                    break;
                case DIGIT9:
                    if (e.isShiftDown() && !keyPressed.get(42)) {
                        controller.playMedia("Eb2", "(");
                        keyPressed.set(42, true);
                        controller.Eb2.setStyle("-fx-background-color: #1a1a1a");
                    } else if (!keyPressed.get(8)) {
                        controller.playMedia("D2", "9");
                        keyPressed.set(8, true);
                        controller.D2.setStyle("-fx-background-color: #d9d9d9");
                    }
                    break;
                case DIGIT0:
                    controller.playMedia("E2", "0");
                    keyPressed.set(9, true);
                    controller.E2.setStyle("-fx-background-color: #d9d9d9");
                    break;
                case Q:
                    if (e.isShiftDown() && !keyPressed.get(43)) {
                        controller.playMedia("Gb2", "Q");
                        keyPressed.set(43, true);
                        controller.Gb2.setStyle("-fx-background-color: #1a1a1a");
                    } else if (!keyPressed.get(10)) {
                        controller.playMedia("F2", "q");
                        keyPressed.set(10, true);
                        controller.F2.setStyle("-fx-background-color: #d9d9d9");
                    }
                    break;
                case W:
                    if (e.isShiftDown() && !keyPressed.get(44)) {
                        controller.playMedia("Ab3", "W");
                        keyPressed.set(44, true);
                        controller.Ab3.setStyle("-fx-background-color: #1a1a1a");
                    } else if (!keyPressed.get(11)) {
                        controller.playMedia("G2", "w");
                        keyPressed.set(11, true);
                        controller.G2.setStyle("-fx-background-color: #d9d9d9");
                    }
                    break;
                case E:
                    if (e.isShiftDown() && !keyPressed.get(45)) {
                        controller.playMedia("Bb3", "E");
                        keyPressed.set(45, true);
                        controller.Bb3.setStyle("-fx-background-color: #1a1a1a");
                    } else if (!keyPressed.get(12)) {
                        controller.playMedia("A3", "e");
                        keyPressed.set(12, true);
                        controller.A3.setStyle("-fx-background-color: #d9d9d9");
                    }
                    break;
                case R:
                    controller.playMedia("B3", "r");
                    keyPressed.set(13, true);
                    controller.B3.setStyle("-fx-background-color: #d9d9d9");
                    break;
                case T:
                    if (e.isShiftDown() && !keyPressed.get(46)) {
                        controller.playMedia("Db3", "T");
                        keyPressed.set(46, true);
                        controller.Db3.setStyle("-fx-background-color: #1a1a1a");
                    } else if (!keyPressed.get(14)) {
                        controller.playMedia("C3", "t");
                        keyPressed.set(14, true);
                        controller.C3.setStyle("-fx-background-color: #d9d9d9");
                    }
                    break;
                case Y:
                    if (e.isShiftDown() && !keyPressed.get(47)) {
                        controller.playMedia("Eb3", "Y");
                        keyPressed.set(47, true);
                        controller.Eb3.setStyle("-fx-background-color: #1a1a1a");
                    } else if (!keyPressed.get(15)) {
                        controller.playMedia("D3", "y");
                        keyPressed.set(15, true);
                        controller.D3.setStyle("-fx-background-color: #d9d9d9");
                    }
                    break;
                case U:
                    controller.playMedia("E3", "u");
                    keyPressed.set(16, true);
                    controller.E3.setStyle("-fx-background-color: #d9d9d9");
                    break;
                case I:
                    if (e.isShiftDown() && !keyPressed.get(48)) {
                        controller.playMedia("Gb3", "I");
                        keyPressed.set(48, true);
                        controller.Gb3.setStyle("-fx-background-color: #1a1a1a");
                    } else if (!keyPressed.get(17)) {
                        controller.playMedia("F3", "i");
                        keyPressed.set(17, true);
                        controller.F3.setStyle("-fx-background-color: #d9d9d9");
                    }
                    break;
                case O:
                    if (e.isShiftDown() && !keyPressed.get(49)) {
                        controller.playMedia("Ab4", "O");
                        keyPressed.set(49, true);
                        controller.Ab4.setStyle("-fx-background-color: #1a1a1a");
                    } else if (!keyPressed.get(18)) {
                        controller.playMedia("G3", "o");
                        keyPressed.set(18, true);
                        controller.G3.setStyle("-fx-background-color: #d9d9d9");
                    }
                    break;
                case P:
                    if (e.isShiftDown() && !keyPressed.get(50)) {
                        controller.playMedia("Bb4", "P");
                        keyPressed.set(50, true);
                        controller.Bb4.setStyle("-fx-background-color: #1a1a1a");
                    } else if (!keyPressed.get(19)) {
                        controller.playMedia("A4", "p");
                        keyPressed.set(19, true);
                        controller.A4.setStyle("-fx-background-color: #d9d9d9");
                    }
                    break;
                case A:
                    controller.playMedia("B4", "a");
                    keyPressed.set(20, true);
                    controller.B4.setStyle("-fx-background-color: #d9d9d9");
                    break;
                case S:
                    if (e.isShiftDown() && !keyPressed.get(51)) {
                        controller.playMedia("Db4", "S");
                        keyPressed.set(51, true);
                        controller.Db4.setStyle("-fx-background-color: #1a1a1a");
                    } else if (!keyPressed.get(21)) {
                        controller.playMedia("C4", "s");
                        keyPressed.set(21, true);
                        controller.C4.setStyle("-fx-background-color: #d9d9d9");
                    }
                    break;
                case D:
                    if (e.isShiftDown() && !keyPressed.get(52)) {
                        controller.playMedia("Eb4", "D");
                        keyPressed.set(52, true);
                        controller.Eb4.setStyle("-fx-background-color: #1a1a1a");
                    } else if (!keyPressed.get(22)) {
                        controller.playMedia("D4", "d");
                        keyPressed.set(22, true);
                        controller.D4.setStyle("-fx-background-color: #d9d9d9");
                    }
                    break;
                case F:
                    controller.playMedia("E4", "f");
                    keyPressed.set(23, true);
                    controller.E4.setStyle("-fx-background-color: #d9d9d9");
                    break;
                case G:
                    if (e.isShiftDown() && !keyPressed.get(53)) {
                        controller.playMedia("Gb4", "G");
                        keyPressed.set(53, true);
                        controller.Gb4.setStyle("-fx-background-color: #1a1a1a");
                    } else if (!keyPressed.get(24)) {
                        controller.playMedia("F4", "g");
                        keyPressed.set(24, true);
                        controller.F4.setStyle("-fx-background-color: #d9d9d9");
                    }
                    break;
                case H:
                    if (e.isShiftDown() && !keyPressed.get(54)) {
                        controller.playMedia("Ab5", "H");
                        keyPressed.set(54, true);
                        controller.Ab5.setStyle("-fx-background-color: #1a1a1a");
                    } else if (!keyPressed.get(25)) {
                        controller.playMedia("G4", "h");
                        keyPressed.set(25, true);
                        controller.G4.setStyle("-fx-background-color: #d9d9d9");
                    }
                    break;
                case J:
                    if (e.isShiftDown() && !keyPressed.get(55)) {
                        controller.playMedia("Bb5", "J");
                        keyPressed.set(55, true);
                        controller.Bb5.setStyle("-fx-background-color: #1a1a1a");
                    } else if (!keyPressed.get(26)) {
                        controller.playMedia("A5", "j");
                        keyPressed.set(26, true);
                        controller.A5.setStyle("-fx-background-color: #d9d9d9");
                    }
                    break;
                case K:
                    controller.playMedia("B5", "k");
                    keyPressed.set(27, true);
                    controller.B5.setStyle("-fx-background-color: #d9d9d9");
                    break;
                case L:
                    if (e.isShiftDown() && !keyPressed.get(56)) {
                        controller.playMedia("Db5", "L");
                        keyPressed.set(56, true);
                        controller.Db5.setStyle("-fx-background-color: #1a1a1a");
                    } else if (!keyPressed.get(28)) {
                        controller.playMedia("C5", "l");
                        keyPressed.set(28, true);
                        controller.C5.setStyle("-fx-background-color: #d9d9d9");
                    }
                    break;
                case Z:
                    if (e.isShiftDown() && !keyPressed.get(57)) {
                        controller.playMedia("Eb5", "Z");
                        keyPressed.set(57, true);
                        controller.Eb5.setStyle("-fx-background-color: #1a1a1a");
                    } else if (!keyPressed.get(29)) {
                        controller.playMedia("D5", "z");
                        keyPressed.set(29, true);
                        controller.D5.setStyle("-fx-background-color: #d9d9d9");
                    }
                    break;
                case X:
                    controller.playMedia("E5", "x");
                    keyPressed.set(30, true);
                    controller.E5.setStyle("-fx-background-color: #d9d9d9");
                    break;
                case C:
                    if (e.isShiftDown() && !keyPressed.get(58)) {
                        controller.playMedia("Gb5", "C");
                        keyPressed.set(58, true);
                        controller.Gb5.setStyle("-fx-background-color: #1a1a1a");
                    } else if (!keyPressed.get(31)) {
                        controller.playMedia("F5", "c");
                        keyPressed.set(31, true);
                        controller.F5.setStyle("-fx-background-color: #d9d9d9");
                    }
                    break;
                case V:
                    if (e.isShiftDown() && !keyPressed.get(59)) {
                        controller.playMedia("Ab6", "V");
                        keyPressed.set(59, true);
                        controller.Ab6.setStyle("-fx-background-color: #1a1a1a");
                    } else if (!keyPressed.get(32)) {
                        controller.playMedia("G5", "v");
                        keyPressed.set(32, true);
                        controller.G5.setStyle("-fx-background-color: #d9d9d9");
                    }
                    break;
                case B:
                    if (e.isShiftDown() && !keyPressed.get(60)) {
                        controller.playMedia("Bb6", "B");
                        keyPressed.set(60, true);
                        controller.Bb6.setStyle("-fx-background-color: #1a1a1a");
                    } else if (!keyPressed.get(33)) {
                        controller.playMedia("A6", "b");
                        keyPressed.set(33, true);
                        controller.A6.setStyle("-fx-background-color: #d9d9d9");
                    }
                    break;
                case N:
                    controller.playMedia("B6", "n");
                    keyPressed.set(34, true);
                    controller.B6.setStyle("-fx-background-color: #d9d9d9");
                    break;
                case M:
                    controller.playMedia("C6", "m");
                    keyPressed.set(35, true);
                    controller.C6.setStyle("-fx-background-color: #d9d9d9");
                    break;
                default:
                    break;
            }
        });

        // Reset after release
        scene.setOnKeyReleased(e -> {
            switch(e.getCode()) {
                case DIGIT1:
                    if (e.isShiftDown()) {
                        keyPressed.set(36, false); // Release Db1
                        controller.Db1.setStyle("-fx-background-color: #292929");
                    } else {
                        keyPressed.set(0, false); // Release C1
                        controller.C1.setStyle("-fx-background-color: #ffffff");
                    }
                    break;
                case DIGIT2:
                    if (e.isShiftDown()) {
                        keyPressed.set(37, false); // Release Eb1
                        controller.Eb1.setStyle("-fx-background-color: #292929");
                    } else {
                        keyPressed.set(1, false); // Release D1
                        controller.D1.setStyle("-fx-background-color: #ffffff");
                    }
                    break;
                case DIGIT3:
                    keyPressed.set(2, false); // Release E1
                    controller.E1.setStyle("-fx-background-color: #ffffff");
                    break;
                case DIGIT4:
                    if (e.isShiftDown()) {
                        keyPressed.set(38, false); // Release Gb1
                        controller.Gb1.setStyle("-fx-background-color: #292929");
                    } else {
                        keyPressed.set(3, false); // Release F1
                        controller.F1.setStyle("-fx-background-color: #ffffff");
                    }
                    break;
                case DIGIT5:
                    if (e.isShiftDown()) {
                        keyPressed.set(39, false); // Release Ab2
                        controller.Ab2.setStyle("-fx-background-color: #292929");
                    } else {
                        keyPressed.set(4, false); // Release G1
                        controller.G1.setStyle("-fx-background-color: #ffffff");
                    }
                    break;
                case DIGIT6:
                    if (e.isShiftDown()) {
                        keyPressed.set(40, false); // Release Bb2
                        controller.Bb2.setStyle("-fx-background-color: #292929");
                    } else {
                        keyPressed.set(5, false); // Release A2
                        controller.A2.setStyle("-fx-background-color: #ffffff");
                    }
                    break;
                case DIGIT7:
                    keyPressed.set(6, false); // Release B2
                    controller.B2.setStyle("-fx-background-color: #ffffff");
                    break;
                case DIGIT8:
                    if (e.isShiftDown()) {
                        keyPressed.set(41, false); // Release Db2
                        controller.Db2.setStyle("-fx-background-color: #292929");
                    } else {
                        keyPressed.set(7, false); // Release C2
                        controller.C2.setStyle("-fx-background-color: #ffffff");
                    }
                    break;
                case DIGIT9:
                    if (e.isShiftDown()) {
                        keyPressed.set(42, false); // Release Eb2
                        controller.Eb2.setStyle("-fx-background-color: #292929");
                    } else {
                        keyPressed.set(8, false); // Release D2
                        controller.D2.setStyle("-fx-background-color: #ffffff");
                    }
                    break;
                case DIGIT0:
                    keyPressed.set(9, false); // Release E2
                    controller.E2.setStyle("-fx-background-color: #ffffff");
                    break;
                case Q:
                    if (e.isShiftDown()) {
                        keyPressed.set(43, false); // Release Gb2
                        controller.Gb2.setStyle("-fx-background-color: #292929");
                    } else {
                        keyPressed.set(10, false); // Release F2
                        controller.F2.setStyle("-fx-background-color: #ffffff");
                    }
                    break;
                case W:
                    if (e.isShiftDown()) {
                        keyPressed.set(44, false); // Release Ab3
                        controller.Ab3.setStyle("-fx-background-color: #292929");
                    } else {
                        keyPressed.set(11, false); // Release G2
                        controller.G2.setStyle("-fx-background-color: #ffffff");
                    }
                    break;
                case E:
                    if (e.isShiftDown()) {
                        keyPressed.set(45, false); // Release Bb3
                        controller.Bb3.setStyle("-fx-background-color: #292929");
                    } else {
                        keyPressed.set(12, false); // Release A3
                        controller.A3.setStyle("-fx-background-color: #ffffff");
                    }
                    break;
                case R:
                    keyPressed.set(13, false); // Release B3
                    controller.B3.setStyle("-fx-background-color: #ffffff");
                    break;
                case T:
                    if (e.isShiftDown()) {
                        keyPressed.set(46, false); // Release Db3
                        controller.Db3.setStyle("-fx-background-color: #292929");
                    } else {
                        keyPressed.set(14, false); // Release C3
                        controller.C3.setStyle("-fx-background-color: #ffffff");
                    }
                    break;
                case Y:
                    if (e.isShiftDown()) {
                        keyPressed.set(47, false); // Release Eb3
                        controller.Eb3.setStyle("-fx-background-color: #292929");
                    } else {
                        keyPressed.set(15, false); // Release D3
                        controller.D3.setStyle("-fx-background-color: #ffffff");
                    }
                    break;
                case U:
                    keyPressed.set(16, false); // Release E3
                    controller.E3.setStyle("-fx-background-color: #ffffff");
                    break;
                case I:
                    if (e.isShiftDown()) {
                        keyPressed.set(48, false); // Release Gb3
                        controller.Gb3.setStyle("-fx-background-color: #292929");
                    } else {
                        keyPressed.set(17, false); // Release F3
                        controller.F3.setStyle("-fx-background-color: #ffffff");
                    }
                    break;
                case O:
                    if (e.isShiftDown()) {
                        keyPressed.set(49, false); // Release Ab4
                        controller.Ab4.setStyle("-fx-background-color: #292929");
                    } else {
                        keyPressed.set(18, false); // Release G3
                        controller.G3.setStyle("-fx-background-color: #ffffff");
                    }
                    break;
                case P:
                    if (e.isShiftDown()) {
                        keyPressed.set(50, false); // Release Bb4
                        controller.Bb4.setStyle("-fx-background-color: #292929");
                    } else {
                        keyPressed.set(19, false); // Release A4
                        controller.A4.setStyle("-fx-background-color: #ffffff");
                    }
                    break;
                case A:
                    keyPressed.set(20, false); // Release B4
                    controller.B4.setStyle("-fx-background-color: #ffffff");
                    break;
                case S:
                    if (e.isShiftDown()) {
                        keyPressed.set(51, false); // Release Db4
                        controller.Db4.setStyle("-fx-background-color: #292929");
                    } else {
                        keyPressed.set(21, false); // Release C4
                        controller.C4.setStyle("-fx-background-color: #ffffff");
                    }
                    break;
                case D:
                    if (e.isShiftDown()) {
                        keyPressed.set(52, false); // Release Eb4
                        controller.Eb4.setStyle("-fx-background-color: #292929");
                    } else {
                        keyPressed.set(22, false); // Release D4
                        controller.D4.setStyle("-fx-background-color: #ffffff");
                    }
                    break;
                case F:
                    keyPressed.set(23, false); // Release E4
                    controller.E4.setStyle("-fx-background-color: #ffffff");
                    break;
                case G:
                    if (e.isShiftDown()) {
                        keyPressed.set(53, false); // Release Gb4
                        controller.Gb4.setStyle("-fx-background-color: #292929");
                    } else {
                        keyPressed.set(24, false); // Release F4
                        controller.F4.setStyle("-fx-background-color: #ffffff");
                    }
                    break;
                case H:
                    if (e.isShiftDown()) {
                        keyPressed.set(54, false); // Release Ab5
                        controller.Ab5.setStyle("-fx-background-color: #292929");
                    } else {
                        keyPressed.set(25, false); // Release G4
                        controller.G4.setStyle("-fx-background-color: #ffffff");
                    }
                    break;
                case J:
                    if (e.isShiftDown()) {
                        keyPressed.set(55, false); // Release Bb5
                        controller.Bb5.setStyle("-fx-background-color: #292929");
                    } else {
                        keyPressed.set(26, false); // Release A5
                        controller.A5.setStyle("-fx-background-color: #ffffff");
                    }
                    break;
                case K:
                    keyPressed.set(27, false); // Release B5
                    controller.B5.setStyle("-fx-background-color: #ffffff");
                    break;
                case L:
                    if (e.isShiftDown()) {
                        keyPressed.set(56, false); // Release Db5
                        controller.Db5.setStyle("-fx-background-color: #292929");
                    } else {
                        keyPressed.set(28, false); // Release C5
                        controller.C5.setStyle("-fx-background-color: #ffffff");
                    }
                    break;
                case Z:
                    if (e.isShiftDown()) {
                        keyPressed.set(57, false); // Release Eb5
                        controller.Eb5.setStyle("-fx-background-color: #292929");
                    } else {
                        keyPressed.set(29, false); // Release D5
                        controller.D5.setStyle("-fx-background-color: #ffffff");
                    }
                    break;
                case X:
                    keyPressed.set(30, false); // Release E5
                    controller.E5.setStyle("-fx-background-color: #ffffff");
                    break;
                case C:
                    if (e.isShiftDown()) {
                        keyPressed.set(58, false); // Release Gb5
                        controller.Gb5.setStyle("-fx-background-color: #292929");
                    } else {
                        keyPressed.set(31, false); // Release F5
                        controller.F5.setStyle("-fx-background-color: #ffffff");
                    }
                    break;
                case V:
                    if (e.isShiftDown()) {
                        keyPressed.set(59, false); // Release Ab6
                        controller.Ab6.setStyle("-fx-background-color: #292929");
                    } else {
                        keyPressed.set(33, false); // Release G5
                        controller.G5.setStyle("-fx-background-color: #ffffff");
                    }
                    break;
                case B:
                    if (e.isShiftDown()) {
                        keyPressed.set(60, false); // Release Bb6
                        controller.Bb6.setStyle("-fx-background-color: #292929");
                    } else {
                        keyPressed.set(35, false); // Release A6
                        controller.A6.setStyle("-fx-background-color: #ffffff");
                    }
                    break;
                case N:
                    keyPressed.set(34, false); // Release B6
                    controller.B6.setStyle("-fx-background-color: #ffffff");
                    break;
                case M:
                    keyPressed.set(35, false); // Release C6
                    controller.C6.setStyle("-fx-background-color: #ffffff");
                    break;
                default:
                    break;
            }
        });
    }

    public static void main(String[] args) {
        launch();
    }
}

// :)