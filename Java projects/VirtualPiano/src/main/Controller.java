package main;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.DirectoryChooser;
import org.jcodec.platform.Platform;

import javax.sound.sampled.LineUnavailableException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Objects;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private AnchorPane parent;

    @FXML
    private TextField notesField;

    @FXML
    private TextArea lettersFields;

    @FXML
    private Button playNotesButton, keyAssistButton, recordButton, semanticsListButton, metronomeButton, volumeButton;

    @FXML
    public Button A2, A3, A4, A5, A6;

    @FXML
    public Button B2, B3, B4, B5, B6;

    @FXML
    public Button C1, C2, C3, C4, C5, C6;

    @FXML
    public Button D1, D2, D3, D4, D5;

    @FXML
    public Button E1, E2, E3, E4, E5;

    @FXML
    public Button F1, F2, F3, F4, F5;

    @FXML
    public Button G1, G2, G3, G4, G5;

    @FXML
    public Button Db1, Db2, Db3, Db4, Db5;

    @FXML
    public Button Eb1, Eb2, Eb3, Eb4, Eb5;

    @FXML
    public Button Gb1, Gb2, Gb3, Gb4, Gb5;

    @FXML
    public Button Ab2, Ab3, Ab4, Ab5, Ab6;

    @FXML
    public Button Bb2, Bb3, Bb4, Bb5, Bb6;

    @FXML
    private Label labelC1, labelC2, labelC3, labelC4, labelC5, labelC6;

    @FXML
    private Label labelD1, labelD2, labelD3, labelD4, labelD5;

    @FXML
    private Label labelE1, labelE2, labelE3, labelE4, labelE5;

    @FXML
    private Label labelF1, labelF2, labelF3, labelF4, labelF5;

    @FXML
    private Label labelG1, labelG2, labelG3, labelG4, labelG5;

    @FXML
    private Label labelA2, labelA3, labelA4, labelA5, labelA6;

    @FXML
    private Label labelB2, labelB3, labelB4, labelB5, labelB6;

    private static boolean keyboardNotesActive, letterNotesActive, musicalNotesActive;
    private double xOffSet = 0, yOffSet = 0;
    public static double applicationVolume = 0.8;

    private CustomButtonSkin recordButtonSkin;
    private static final AudioRecorder recorder = new AudioRecorder();
    private MediaPlayer currentMediaPlayer;

    public Controller() {
        this.notesField = new TextField();
        this.lettersFields = new TextArea();
    }

    public void close() {
        Metronome metronome = new Metronome();
        metronome.end();
        Main.stage.close();
    }

    public void setKeyboardNotesActive(boolean value) {
        keyboardNotesActive = value;
    }

    public void setLetterNotesActive(boolean value) {
        letterNotesActive = value;
    }

    public void setMusicalNotesActive(boolean value) {
        musicalNotesActive = value;
    }

    public void updateNotes() {
        labelC1.setStyle("-fx-alignment: CENTER;"); labelC1.setText(keyboardNotesActive ? "1" : (letterNotesActive ? "C1" : (musicalNotesActive ? "DO1" : "")));
        labelD1.setStyle("-fx-alignment: CENTER;"); labelD1.setText(keyboardNotesActive ? "2" : (letterNotesActive ? "D1" : (musicalNotesActive ? "RE1" : "")));
        labelE1.setStyle("-fx-alignment: CENTER;"); labelE1.setText(keyboardNotesActive ? "3" : (letterNotesActive ? "E1" : (musicalNotesActive ? "MI1" : "")));
        labelF1.setStyle("-fx-alignment: CENTER;"); labelF1.setText(keyboardNotesActive ? "4" : (letterNotesActive ? "F1" : (musicalNotesActive ? "FA1" : "")));
        labelG1.setStyle("-fx-alignment: CENTER;"); labelG1.setText(keyboardNotesActive ? "5" : (letterNotesActive ? "G1" : (musicalNotesActive ? "SOL1" : "")));
        labelA2.setStyle("-fx-alignment: CENTER;"); labelA2.setText(keyboardNotesActive ? "6" : (letterNotesActive ? "A2" : (musicalNotesActive ? "LA2" : "")));
        labelB2.setStyle("-fx-alignment: CENTER;"); labelB2.setText(keyboardNotesActive ? "7" : (letterNotesActive ? "B2" : (musicalNotesActive ? "SI1" : "")));
        labelC2.setStyle("-fx-alignment: CENTER;"); labelC2.setText(keyboardNotesActive ? "8" : (letterNotesActive ? "C2" : (musicalNotesActive ? "DO2" : "")));
        labelD2.setStyle("-fx-alignment: CENTER;"); labelD2.setText(keyboardNotesActive ? "9" : (letterNotesActive ? "D2" : (musicalNotesActive ? "RE2" : "")));
        labelE2.setStyle("-fx-alignment: CENTER;"); labelE2.setText(keyboardNotesActive ? "0" : (letterNotesActive ? "E2" : (musicalNotesActive ? "MI2" : "")));
        labelF2.setStyle("-fx-alignment: CENTER;"); labelF2.setText(keyboardNotesActive ? "q" : (letterNotesActive ? "F2" : (musicalNotesActive ? "FA2" : "")));
        labelG2.setStyle("-fx-alignment: CENTER;"); labelG2.setText(keyboardNotesActive ? "w" : (letterNotesActive ? "G2" : (musicalNotesActive ? "SOL2" : "")));
        labelA3.setStyle("-fx-alignment: CENTER;"); labelA3.setText(keyboardNotesActive ? "e" : (letterNotesActive ? "A3" : (musicalNotesActive ? "LA3" : "")));
        labelB3.setStyle("-fx-alignment: CENTER;"); labelB3.setText(keyboardNotesActive ? "r" : (letterNotesActive ? "B3" : (musicalNotesActive ? "SI3" : "")));
        labelC3.setStyle("-fx-alignment: CENTER;"); labelC3.setText(keyboardNotesActive ? "t" : (letterNotesActive ? "C3" : (musicalNotesActive ? "DO3" : "")));
        labelD3.setStyle("-fx-alignment: CENTER;"); labelD3.setText(keyboardNotesActive ? "y" : (letterNotesActive ? "D3" : (musicalNotesActive ? "RE3" : "")));
        labelE3.setStyle("-fx-alignment: CENTER;"); labelE3.setText(keyboardNotesActive ? "u" : (letterNotesActive ? "E3" : (musicalNotesActive ? "MI3" : "")));
        labelF3.setStyle("-fx-alignment: CENTER;"); labelF3.setText(keyboardNotesActive ? "i" : (letterNotesActive ? "F3" : (musicalNotesActive ? "FA3" : "")));
        labelG3.setStyle("-fx-alignment: CENTER;"); labelG3.setText(keyboardNotesActive ? "o" : (letterNotesActive ? "G3" : (musicalNotesActive ? "SOL3" : "")));
        labelA4.setStyle("-fx-alignment: CENTER;"); labelA4.setText(keyboardNotesActive ? "p" : (letterNotesActive ? "A4" : (musicalNotesActive ? "LA4" : "")));
        labelB4.setStyle("-fx-alignment: CENTER;"); labelB4.setText(keyboardNotesActive ? "a" : (letterNotesActive ? "B4" : (musicalNotesActive ? "SI4" : "")));
        labelC4.setStyle("-fx-alignment: CENTER;"); labelC4.setText(keyboardNotesActive ? "s" : (letterNotesActive ? "C4" : (musicalNotesActive ? "DO4" : "")));
        labelD4.setStyle("-fx-alignment: CENTER;"); labelD4.setText(keyboardNotesActive ? "d" : (letterNotesActive ? "D4" : (musicalNotesActive ? "RE4" : "")));
        labelE4.setStyle("-fx-alignment: CENTER;"); labelE4.setText(keyboardNotesActive ? "f" : (letterNotesActive ? "E4" : (musicalNotesActive ? "MI4" : "")));
        labelF4.setStyle("-fx-alignment: CENTER;"); labelF4.setText(keyboardNotesActive ? "g" : (letterNotesActive ? "F4" : (musicalNotesActive ? "FA4" : "")));
        labelG4.setStyle("-fx-alignment: CENTER;"); labelG4.setText(keyboardNotesActive ? "h" : (letterNotesActive ? "G4" : (musicalNotesActive ? "SOL4" : "")));
        labelA5.setStyle("-fx-alignment: CENTER;"); labelA5.setText(keyboardNotesActive ? "j" : (letterNotesActive ? "A5" : (musicalNotesActive ? "LA5" : "")));
        labelB5.setStyle("-fx-alignment: CENTER;"); labelB5.setText(keyboardNotesActive ? "k" : (letterNotesActive ? "B5" : (musicalNotesActive ? "SI5" : "")));
        labelC5.setStyle("-fx-alignment: CENTER;"); labelC5.setText(keyboardNotesActive ? "l" : (letterNotesActive ? "C5" : (musicalNotesActive ? "DO5" : "")));
        labelD5.setStyle("-fx-alignment: CENTER;"); labelD5.setText(keyboardNotesActive ? "z" : (letterNotesActive ? "D5" : (musicalNotesActive ? "RE5" : "")));
        labelE5.setStyle("-fx-alignment: CENTER;"); labelE5.setText(keyboardNotesActive ? "x" : (letterNotesActive ? "E5" : (musicalNotesActive ? "MI5" : "")));
        labelF5.setStyle("-fx-alignment: CENTER;"); labelF5.setText(keyboardNotesActive ? "c" : (letterNotesActive ? "F5" : (musicalNotesActive ? "FA5" : "")));
        labelG5.setStyle("-fx-alignment: CENTER;"); labelG5.setText(keyboardNotesActive ? "v" : (letterNotesActive ? "G5" : (musicalNotesActive ? "SOL5" : "")));
        labelA6.setStyle("-fx-alignment: CENTER;"); labelA6.setText(keyboardNotesActive ? "b" : (letterNotesActive ? "A6" : (musicalNotesActive ? "LA6" : "")));
        labelB6.setStyle("-fx-alignment: CENTER;"); labelB6.setText(keyboardNotesActive ? "n" : (letterNotesActive ? "B6" : (musicalNotesActive ? "SI6" : "")));
        labelC6.setStyle("-fx-alignment: CENTER;"); labelC6.setText(keyboardNotesActive ? "m" : (letterNotesActive ? "C6" : (musicalNotesActive ? "DO6" : "")));
        Db1.setStyle("-fx-alignment: CENTER;"); Db1.setText(keyboardNotesActive ? "!" : (letterNotesActive ? "C\n#\n1" : (musicalNotesActive ? "D\nO\n#\n1" : "")));
        Eb1.setStyle("-fx-alignment: CENTER;"); Eb1.setText(keyboardNotesActive ? "@" : (letterNotesActive ? "D\n#\n1" : (musicalNotesActive ? "R\nE\n#\n1" : "")));
        Gb1.setStyle("-fx-alignment: CENTER;"); Gb1.setText(keyboardNotesActive ? "$" : (letterNotesActive ? "F\n#\n1" : (musicalNotesActive ? "F\nA\n#\n1" : "")));
        Ab2.setStyle("-fx-alignment: CENTER;"); Ab2.setText(keyboardNotesActive ? "%" : (letterNotesActive ? "G\n#\n1" : (musicalNotesActive ? "L\nA\n#\n1" : "")));
        Bb2.setStyle("-fx-alignment: CENTER;"); Bb2.setText(keyboardNotesActive ? "^" : (letterNotesActive ? "A\n#\n1" : (musicalNotesActive ? "L\nA\nb\n1" : "")));
        Db2.setStyle("-fx-alignment: CENTER;"); Db2.setText(keyboardNotesActive ? "*" : (letterNotesActive ? "C\n#\n2" : (musicalNotesActive ? "D\nO\n#\n2" : "")));
        Eb2.setStyle("-fx-alignment: CENTER;"); Eb2.setText(keyboardNotesActive ? "(" : (letterNotesActive ? "D\n#\n2" : (musicalNotesActive ? "R\nE\n#\n2" : "")));
        Gb2.setStyle("-fx-alignment: CENTER;"); Gb2.setText(keyboardNotesActive ? "Q" : (letterNotesActive ? "F\n#\n2" : (musicalNotesActive ? "F\nA\n#\n2" : "")));
        Ab3.setStyle("-fx-alignment: CENTER;"); Ab3.setText(keyboardNotesActive ? "W" : (letterNotesActive ? "G\n#\n2" : (musicalNotesActive ? "L\nA\n#\n2" : "")));
        Bb3.setStyle("-fx-alignment: CENTER;"); Bb3.setText(keyboardNotesActive ? "E" : (letterNotesActive ? "A\n#\n2" : (musicalNotesActive ? "L\nA\nb\n2" : "")));
        Db3.setStyle("-fx-alignment: CENTER;"); Db3.setText(keyboardNotesActive ? "T" : (letterNotesActive ? "C\n#\n3" : (musicalNotesActive ? "D\nO\n#\n3" : "")));
        Eb3.setStyle("-fx-alignment: CENTER;"); Eb3.setText(keyboardNotesActive ? "Y" : (letterNotesActive ? "D\n#\n3" : (musicalNotesActive ? "R\nE\n#\n3" : "")));
        Gb3.setStyle("-fx-alignment: CENTER;"); Gb3.setText(keyboardNotesActive ? "I" : (letterNotesActive ? "F\n#\n3" : (musicalNotesActive ? "F\nA\n#\n3" : "")));
        Ab4.setStyle("-fx-alignment: CENTER;"); Ab4.setText(keyboardNotesActive ? "O" : (letterNotesActive ? "G\n#\n3" : (musicalNotesActive ? "L\nA\n#\n3" : "")));
        Bb4.setStyle("-fx-alignment: CENTER;"); Bb4.setText(keyboardNotesActive ? "P" : (letterNotesActive ? "A\n#\n3" : (musicalNotesActive ? "L\nA\nB\n3" : "")));
        Db4.setStyle("-fx-alignment: CENTER;"); Db4.setText(keyboardNotesActive ? "S" : (letterNotesActive ? "C\n#\n4" : (musicalNotesActive ? "D\nO\n#\n4" : "")));
        Eb4.setStyle("-fx-alignment: CENTER;"); Eb4.setText(keyboardNotesActive ? "D" : (letterNotesActive ? "D\n#\n4" : (musicalNotesActive ? "R\nE\n#\n4" : "")));
        Gb4.setStyle("-fx-alignment: CENTER;"); Gb4.setText(keyboardNotesActive ? "G" : (letterNotesActive ? "F\n#\n4" : (musicalNotesActive ? "F\nA\n#\n4" : "")));
        Ab5.setStyle("-fx-alignment: CENTER;"); Ab5.setText(keyboardNotesActive ? "H" : (letterNotesActive ? "G\n#\n4" : (musicalNotesActive ? "L\nA\n#\n4" : "")));
        Bb5.setStyle("-fx-alignment: CENTER;"); Bb5.setText(keyboardNotesActive ? "J" : (letterNotesActive ? "A\n#\n4" : (musicalNotesActive ? "L\nA\nb\n4" : "")));
        Db5.setStyle("-fx-alignment: CENTER;"); Db5.setText(keyboardNotesActive ? "L" : (letterNotesActive ? "C\n#\n5" : (musicalNotesActive ? "D\nO\n#\n5" : "")));
        Eb5.setStyle("-fx-alignment: CENTER;"); Eb5.setText(keyboardNotesActive ? "Z" : (letterNotesActive ? "D\n#\n5" : (musicalNotesActive ? "R\nE\n#\n5" : "")));
        Gb5.setStyle("-fx-alignment: CENTER;"); Gb5.setText(keyboardNotesActive ? "C" : (letterNotesActive ? "F\n#\n5" : (musicalNotesActive ? "F\nA\n#\n5" : "")));
        Ab6.setStyle("-fx-alignment: CENTER;"); Ab6.setText(keyboardNotesActive ? "V" : (letterNotesActive ? "G\n#\n5" : (musicalNotesActive ? "L\nA\n#\n5" : "")));
        Bb6.setStyle("-fx-alignment: CENTER;"); Bb6.setText(keyboardNotesActive ? "B" : (letterNotesActive ? "A\n#\n5" : (musicalNotesActive ? "L\nA\nb\n5" : "")));
    }

    public void minimize() {
        Main.stage.setIconified(true);
    }

    public static void setVolume(double volume) {
        applicationVolume = volume;
    }

    public static double getVolume() {
        return applicationVolume;
    }

    private void setDraggable() {
        parent.setOnMousePressed((event) -> { // getting the position
            xOffSet = event.getSceneX();
            yOffSet = event.getSceneY();
        });
        parent.setOnMouseDragged((event) -> { // reposition
            Main.stage.setX(event.getScreenX() - xOffSet);
            Main.stage.setY(event.getScreenY() - yOffSet);
        });
    }

    private void addButtonSkins() {
        playNotesButton.setSkin(new CustomButtonSkin(playNotesButton, "▶"));
        keyAssistButton.setSkin(new CustomButtonSkin(keyAssistButton, "⌨"));
        recordButton.setSkin(recordButtonSkin = new CustomButtonSkin(recordButton, "♫"));
        semanticsListButton.setSkin(new CustomButtonSkin(semanticsListButton, "\uD83C\uDFBC"));
        metronomeButton.setSkin(new CustomButtonSkin(metronomeButton, "⏱"));
        volumeButton.setSkin(new CustomButtonSkin(volumeButton, "\uD83D\uDD0A"));
    }

    private void addNoteToField(String note, String symbol) {
        notesField.setText(note);
        lettersFields.setStyle("-fx-text-fill: white;");
        lettersFields.setText(lettersFields.getText() + symbol);
    }

    public void playMedia(String note, String symbol) {
        Task<Void> soundTask = new Task<>() {
            @Override
            protected Void call() {
                Media media = new Media(String.valueOf(Objects.requireNonNull(getClass().getResource("/audio/" + note + ".mp3"))));
                currentMediaPlayer = new MediaPlayer(media);
                currentMediaPlayer.setVolume(applicationVolume);
                currentMediaPlayer.play();
                currentMediaPlayer.setOnEndOfMedia(() -> {
                    // Things while playing
                });
                return null;
            }
        };

        // Separate thread for sound, different from UI Thread
        Thread soundThread = new Thread(soundTask);
        soundThread.setDaemon(true); // So that the sound thread will close with the application in case.
        soundThread.start();
        if (!symbol.equals("#")) {
            addNoteToField(note, symbol);
        }
    }

    private void addButtonActions() {
        volumeButton.setOnAction(e -> {
            try {
                VolumeMenu volumeMenu = new VolumeMenu();
                volumeMenu.start(new javafx.stage.Stage());
            } catch(Exception exp) {
                System.out.print("Error: Volume menu failed!\n");
            }
        });
        recordButton.setOnAction(e -> {
            // Start the record action
            if(recordButton.getText().contains("Record")) {
                recordButtonSkin.setInitialText("Finish");
                recordButton.setText("Finish");
                try {
                    DirectoryChooser directoryChooser = new DirectoryChooser();
                    directoryChooser.setInitialDirectory(new File("../"));
                    File dst = directoryChooser.showDialog(Main.stage);
                    recorder.startRecording(dst.getPath() + "/recording.wav");
                } catch (IOException | LineUnavailableException ex) {
                    recordButtonSkin.setInitialText("Record");
                    recordButton.setText("Record");
                    throw new RuntimeException(ex);
                }
            } else {
                recordButtonSkin.setInitialText("Record");
                recordButton.setText("Record");
                recorder.stopRecording();
                try {
                    File file = recorder.getAudioFile();
                    file.createNewFile();
                } catch (IOException ex) {
                    recordButtonSkin.setInitialText("Finish");
                    recordButton.setText("Finish");
                    throw new RuntimeException(ex);
                }
            }
        });
        semanticsListButton.setOnAction(e -> {
            try {
                SemanticsMenu semanticsMenu = new SemanticsMenu();
                semanticsMenu.start(new javafx.stage.Stage());
            } catch(Exception exp) {
                System.out.print("Error: Semantics menu failed!\n");
            }
        });
        keyAssistButton.setOnAction(e -> {
            try {
                KeyAssistMenu keyAssistMenu = new KeyAssistMenu();
                keyAssistMenu.start(new javafx.stage.Stage());
            } catch(Exception exp) {
                System.out.print("Error: Key assist menu failed!\n");
            }
        });
        metronomeButton.setOnAction(e -> {
            try {
                MetronomeMenu metronomeMenu = new MetronomeMenu();
                metronomeMenu.start(new javafx.stage.Stage());
            } catch(Exception exp) {
                System.out.print("Error: main.Metronome menu failed!\n");
            }
        });
    }

    private void addActions() {
        C1.setOnAction(e -> playMedia("C1", "1"));
        D1.setOnAction(e -> playMedia("D1", "2"));
        E1.setOnAction(e -> playMedia("E1", "3"));
        F1.setOnAction(e -> playMedia("F1", "4"));
        G1.setOnAction(e -> playMedia("G1", "5"));
        A2.setOnAction(e -> playMedia("A2", "6"));
        B2.setOnAction(e -> playMedia("B2", "7"));
        C2.setOnAction(e -> playMedia("C2", "8"));
        D2.setOnAction(e -> playMedia("D2", "9"));
        E2.setOnAction(e -> playMedia("E2", "0"));
        F2.setOnAction(e -> playMedia("F2", "q"));
        G2.setOnAction(e -> playMedia("G2", "w"));
        A3.setOnAction(e -> playMedia("A3", "e"));
        B3.setOnAction(e -> playMedia("B3", "r"));
        C3.setOnAction(e -> playMedia("C3", "t"));
        D3.setOnAction(e -> playMedia("D3", "y"));
        E3.setOnAction(e -> playMedia("E3", "u"));
        F3.setOnAction(e -> playMedia("F3", "i"));
        G3.setOnAction(e -> playMedia("G3", "o"));
        A4.setOnAction(e -> playMedia("A4", "p"));
        B4.setOnAction(e -> playMedia("B4", "a"));
        C4.setOnAction(e -> playMedia("C4", "s"));
        D4.setOnAction(e -> playMedia("D4", "d"));
        E4.setOnAction(e -> playMedia("E4", "f"));
        F4.setOnAction(e -> playMedia("F4", "g"));
        G4.setOnAction(e -> playMedia("G4", "h"));
        A5.setOnAction(e -> playMedia("A5", "j"));
        B5.setOnAction(e -> playMedia("B5", "k"));
        C5.setOnAction(e -> playMedia("C5", "l"));
        D5.setOnAction(e -> playMedia("D5", "z"));
        E5.setOnAction(e -> playMedia("E5", "x"));
        F5.setOnAction(e -> playMedia("F5", "c"));
        G5.setOnAction(e -> playMedia("G5", "v"));
        A6.setOnAction(e -> playMedia("A6", "b"));
        B6.setOnAction(e -> playMedia("B6", "n"));
        C6.setOnAction(e -> playMedia("C6", "m"));
        Db1.setOnAction(e -> playMedia("Db1", "!"));
        Eb1.setOnAction(e -> playMedia("Eb1", "@"));
        Gb1.setOnAction(e -> playMedia("Gb1", "$"));
        Ab2.setOnAction(e -> playMedia("Ab2", "%"));
        Bb2.setOnAction(e -> playMedia("Bb2", "^"));
        Db2.setOnAction(e -> playMedia("Db2", "*"));
        Eb2.setOnAction(e -> playMedia("Eb2", "("));
        Gb2.setOnAction(e -> playMedia("Gb2", "Q"));
        Ab3.setOnAction(e -> playMedia("Ab3", "W"));
        Bb3.setOnAction(e -> playMedia("Bb3", "E"));
        Db3.setOnAction(e -> playMedia("Db3", "T"));
        Eb3.setOnAction(e -> playMedia("Eb3", "Y"));
        Gb3.setOnAction(e -> playMedia("Gb3", "I"));
        Ab4.setOnAction(e -> playMedia("Ab4", "O"));
        Bb4.setOnAction(e -> playMedia("Bb4", "P"));
        Db4.setOnAction(e -> playMedia("Db4", "S"));
        Eb4.setOnAction(e -> playMedia("Eb4", "D"));
        Gb4.setOnAction(e -> playMedia("Gb4", "G"));
        Ab5.setOnAction(e -> playMedia("Ab5", "H"));
        Bb5.setOnAction(e -> playMedia("Bb5", "J"));
        Db5.setOnAction(e -> playMedia("Db5", "L"));
        Eb5.setOnAction(e -> playMedia("Eb5", "Z"));
        Gb5.setOnAction(e -> playMedia("Gb5", "C"));
        Ab6.setOnAction(e -> playMedia("Ab6", "V"));
        Bb6.setOnAction(e -> playMedia("Bb6", "B"));
    }

    private void addRegex() {
        final String letterFieldsRegex = "^[a-z0-9QWETYIOPSDGHJLZCVB!@$%^*( /|\\[\\]\\n]+$";
        lettersFields.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue.matches(letterFieldsRegex)) {
                lettersFields.setText(oldValue);
            }
        });
    }

    private void createHashMap(HashMap<String, String> mp) {
        mp.put("1", "C1");
        mp.put("!", "Db1");
        mp.put("2", "D1");
        mp.put("@", "Eb1");
        mp.put("3", "E1");
        mp.put("4", "F1");
        mp.put("$", "Gb1");
        mp.put("5", "G1");
        mp.put("%", "Ab2");
        mp.put("6", "A2");
        mp.put("^", "Bb2");
        mp.put("7", "B2");
        mp.put("8", "C2");
        mp.put("*", "Db2");
        mp.put("9", "D2");
        mp.put("(", "Eb1");
        mp.put("0", "E2");
        mp.put("q", "F2");
        mp.put("Q", "Gb2");
        mp.put("w", "G2");
        mp.put("W", "Ab3");
        mp.put("e", "A3");
        mp.put("E", "Bb3");
        mp.put("r", "B3");
        mp.put("t", "C3");
        mp.put("T", "Db3");
        mp.put("y", "D3");
        mp.put("Y", "Eb3");
        mp.put("u", "E3");
        mp.put("i", "F3");
        mp.put("I", "Gb3");
        mp.put("o", "G3");
        mp.put("O", "Ab4");
        mp.put("p", "A4");
        mp.put("P", "Bb4");
        mp.put("a", "B4");
        mp.put("s", "C4");
        mp.put("S", "Db4");
        mp.put("d", "D4");
        mp.put("D", "Eb4");
        mp.put("f", "E4");
        mp.put("g", "F4");
        mp.put("G", "Gb4");
        mp.put("h", "G4");
        mp.put("H", "Ab5");
        mp.put("j", "A5");
        mp.put("J", "Bb5");
        mp.put("k", "B5");
        mp.put("l", "C5");
        mp.put("L", "Db5");
        mp.put("z", "D5");
        mp.put("Z", "Eb5");
        mp.put("x", "E5");
        mp.put("c", "F5");
        mp.put("C", "Gb5");
        mp.put("v", "G5");
        mp.put("V", "Ab6");
        mp.put("b", "A6");
        mp.put("B", "Bb6");
        mp.put("n", "B6");
        mp.put("m", "C6");
    }

    private void addPlayFunction() {
        HashMap<String, String> symbolToNote = new HashMap<>();
        createHashMap(symbolToNote);
        playNotesButton.setOnAction(e -> {
            String input = lettersFields.getText().trim();
            if (input.isEmpty()) {
                return;
            }
            playNotesButton.setDisable(true);
            lettersFields.setEditable(false);
            new Thread(() -> {
                try {
                    final MusicPlayer player = new MusicPlayer();
                    player.playSymbols(input, symbolToNote);
                } catch (InterruptedException ex) {
                    ex.printStackTrace(System.out);
                } finally {
                    playNotesButton.setDisable(false);
                    lettersFields.setEditable(true);
                }
            }).start();
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setDraggable();
        addButtonSkins();
        addButtonActions();
        addActions();
        addRegex();
        addPlayFunction();
        keyboardNotesActive = true;
        updateNotes();
    }
}