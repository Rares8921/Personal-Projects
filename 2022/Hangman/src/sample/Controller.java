package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

import java.io.File;
import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Scanner;

public class Controller implements Initializable {

    @FXML
    private AnchorPane parent;

    @FXML
    private Label wordLabel, wordLength, gameStatus;

    @FXML
    public Label letterA, letterB, letterC, letterD, letterE, letterF, letterG, letterH, letterI, letterJ, letterK, letterL, letterM,
                  letterN, letterO, letterP, letterQ, letterR, letterS, letterT, letterU, letterV, letterW, letterX, letterY, letterZ;

    @FXML
    private Line leftArm, rightArm, body, leftLeg, rightLeg;

    @FXML
    private Circle head;

    @FXML
    private void change(MouseEvent e) {
        if(gameStarted) {
            Label label = (Label) e.getSource();
            String str = label.getText().toLowerCase();
            if(!letter[str.charAt(0) - 'a']) {
                letter[str.charAt(0) - 'a'] = true;
                if (word.contains(str)) {
                    textAnimation(str.charAt(0));
                    label.setTextFill(Color.GREENYELLOW);
                } else {
                    hangman(++wrongLetterCnt);
                    label.setTextFill(Color.ORANGERED);
                }
            }
        }
    }

    private double xOffSet;
    private double yOffSet;

    private static boolean gameStarted;
    private static int wrongLetterCnt;
    private final boolean[] letter = new boolean[30]; // 0 - unverified, 1 - verified;

    private static String word = "";

    public void close() {
        Main.stage.close();
    }

    public void minimize() {
        Main.stage.setIconified(true);
    }

    public void setDraggable() {
        parent.setOnMousePressed((event) -> { // getting the position
            xOffSet = event.getSceneX();
            yOffSet = event.getSceneY();
        });
        parent.setOnMouseDragged((event) -> { // reposition
            Main.stage.setX(event.getScreenX() - xOffSet);
            Main.stage.setY(event.getScreenY() - yOffSet);
        });
    }

    private void textAnimation(char c) {
        StringBuilder strB = new StringBuilder(wordLabel.getText());
        for(int i = 0; i < word.length(); i++) {
            if(word.charAt(i) == c) {
                strB.setCharAt(i, c);
            }
        }
        wordLabel.setText(strB.toString());
        if(!strB.toString().contains("*")) {
            gameStatus.setLayoutX(394);
            gameStatus.setVisible(true);
            gameStarted = false;
        }
    }

    private void hangman(int wrongLetterCnt) {
        switch(wrongLetterCnt) {
            case 1:
                head.setVisible(true);
                break;
            case 2:
                body.setVisible(true);
                break;
            case 3:
                leftArm.setVisible(true);
                break;
            case 4:
                rightArm.setVisible(true);
                break;
            case 5:
                leftLeg.setVisible(true);
                break;
            case 6:
                rightLeg.setVisible(true);
                break;
            default:
                gameStatus.setLayoutX(399);
                gameStatus.setText("Game lost!");
                wordLabel.setText(word);
                gameStarted = false;
                gameStatus.setVisible(true);
                break;
        }
    }

    private String getWord() {
        try {
            File file = new File("src/words.txt");
            Scanner scan = new Scanner(file);
            int rand = new Random().nextInt(1874);
            rand++;
            String s = "";
            for(int i = 1; i <= rand; i++) {
                s = scan.next().trim();
            }
            scan.close();
            return s;
        } catch(Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private void setStyle() {

        gameStatus.setVisible(false);

        parent.getStylesheets().clear();
        parent.getStylesheets().add("sample/styles.css");

        AnchorPane.setLeftAnchor(wordLabel, 0.0);
        AnchorPane.setRightAnchor(wordLabel, 0.0);
        wordLabel.setAlignment(Pos.CENTER);

        head.setVisible(false);
        leftArm.setVisible(false); rightArm.setVisible(false);
        body.setVisible(false);
        leftLeg.setVisible(false); rightLeg.setVisible(false);
    }

    private void setLetter(char c, int status) {
        // status = 1 <=> green; status = 2 <=> red, status = 3 <=> white;
        Paint paint = status == 1 ? Color.GREENYELLOW : (status == 2 ? Color.ORANGERED : Color.WHITE);
        switch(c) {
            case 'a':
                letterA.setTextFill(paint);
                break;
            case 'b':
                letterB.setTextFill(paint);
                break;
            case 'c':
                letterC.setTextFill(paint);
                break;
            case 'd':
                letterD.setTextFill(paint);
                break;
            case 'e':
                letterE.setTextFill(paint);
                break;
            case 'f':
                letterF.setTextFill(paint);
                break;
            case 'g':
                letterG.setTextFill(paint);
                break;
            case 'h':
                letterH.setTextFill(paint);
                break;
            case 'i':
                letterI.setTextFill(paint);
                break;
            case 'j':
                letterJ.setTextFill(paint);
                break;
            case 'k':
                letterK.setTextFill(paint);
                break;
            case 'l':
                letterL.setTextFill(paint);
                break;
            case 'm':
                letterM.setTextFill(paint);
                break;
            case 'n':
                letterN.setTextFill(paint);
                break;
            case 'o':
                letterO.setTextFill(paint);
                break;
            case 'p':
                letterP.setTextFill(paint);
                break;
            case 'q':
                letterQ.setTextFill(paint);
                break;
            case 'r':
                letterR.setTextFill(paint);
                break;
            case 's':
                letterS.setTextFill(paint);
                break;
            case 't':
                letterT.setTextFill(paint);
                break;
            case 'u':
                letterU.setTextFill(paint);
                break;
            case 'v':
                letterV.setTextFill(paint);
                break;
            case 'w':
                letterW.setTextFill(paint);
                break;
            case 'x':
                letterX.setTextFill(paint);
                break;
            case 'y':
                letterY.setTextFill(paint);
                break;
            case 'z':
                letterZ.setTextFill(paint);
                break;
            default:
                break;
        }
    }

    private void setWord() {
        String temp = word;
        while(temp.equals(word)) { // to not get the same word 2 times in a row
            word = getWord();
        }
        // set the letters
        char c1 = word.charAt(0), c2 = word.charAt(word.length() - 1);
        setLetter(c1, 1); setLetter(c2, 1);

        // set the label
        StringBuilder strB = new StringBuilder();
        strB.append(c1);
        for(int i = 1; i < word.length() - 1; i++) {
            if(word.charAt(i) == c1) {
                strB.append(word.charAt(i));
            } else if(word.charAt(i) == c2) {
                strB.append(word.charAt(i));
            } else {
                strB.append('*');
            }
        }
        strB.append(c2);
        wordLabel.setText(strB.toString());

        wordLength.setText(word.length() + " letters");
    }

    private void resetVariables() {
        gameStatus.setText("Game won!");
        gameStarted = true;
        wrongLetterCnt = 0;
        java.util.Arrays.fill(letter, false);
    }

    public void refresh() {
        for(char c = 'a'; c <= 'z'; c++) {
            setLetter(c, 3);
        }
        resetVariables();
        setStyle();
        setWord();
    }

    public void setListeners() {
        parent.setOnKeyPressed(e -> {
            String keyStr = e.getCode().toString().toLowerCase();
            if(keyStr.charAt(0) >= 'a' && keyStr.charAt(keyStr.length() - 1) <= 'z') {
                if (!letter[keyStr.charAt(0) - 'a']) {
                    letter[keyStr.charAt(0) - 'a'] = true;
                    if (word.contains(keyStr)) {
                        textAnimation(keyStr.charAt(0));
                        setLetter(keyStr.charAt(0), 1);
                    } else {
                        hangman(++wrongLetterCnt);
                        setLetter(keyStr.charAt(0), 2);
                    }
                }
            }
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        gameStarted = true;
        setDraggable();
        setStyle();
        setWord();
        setListeners();
    }
}
