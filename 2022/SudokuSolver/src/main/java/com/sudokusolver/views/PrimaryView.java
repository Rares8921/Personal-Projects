package com.sudokusolver.views;

import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.mvc.View;
import com.sudokusolver.PopUp;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.concurrent.atomic.AtomicInteger;

public class PrimaryView extends View {

    private static int[][] v = new int[9][9];

    private boolean inLin(int k, int nr) {
        // check if nr is already in line
        for(int i = 0; i < 9; i++) {
            if(v[k][i] == nr) {
                return true;
            }
        }
        return false;
    }

    private boolean inCol(int l, int nr) {
        // check if nr is already in column
        for(int i = 0; i < 9; i++) {
            if(v[i][l] == nr) {
                return true;
            }
        }
        return false;
    }

    private boolean inSquare(int k, int l, int nr) {
        // check if nr is already in the 3x3 square
        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 3; j++) {
                if(v[i + k][j + l] == nr) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean valid(int k, int l, int i) {
        return !inLin(k, i) && !inCol(l, i) && !inSquare(k - k % 3, l - l % 3, i);
    }

    private Object[] empty() {
        for(int k = 0; k < 9; k++) {
            for(int l = 0; l < 9; l++) {
                if(v[k][l] == 0) {
                    return new Object[]{true, k, l};
                }
            }
        }
        return new Object[]{false, 9, 9};
    }

    private boolean back() {
        Object[] emptyValues = empty();
        int k = (int) emptyValues[1];
        int l = (int) emptyValues[2];
        boolean isEmpty = (boolean) emptyValues[0];
        if(!isEmpty) { // if sudoku is solved
            return true;
        }
        for(int i = 1; i <= 9; i++) {
            if(valid(k, l, i)) {
                v[k][l] = i;
                if(back()) {
                    return true;
                }
                v[k][l] = 0;
            }
        }
        return false;
    }

    public PrimaryView() {
        
        getStylesheets().add(PrimaryView.class.getResource("primary.css").toExternalForm());

        //canvas[0][0].setLayoutY(50.0);
        String canvasStyle = "-fx-pref-width: 33px; -fx-pref-height: 33px; -fx-text-fill: #c9c9c9; -fx-font-family: 'Consolas'; -fx-border-width: 2px; -fx-border-color: #7c7c7c; -fx-font-size: 15px;";
        AtomicInteger indexI = new AtomicInteger();
        AtomicInteger indexJ = new AtomicInteger();
        Label[][] canvas = new Label[9][9];
        canvas[0][0] = new Label();
        canvas[0][0].setStyle(canvasStyle + "-fx-background-color: transparent;");
        canvas[0][0].setLayoutX(0.0); canvas[0][0].setLayoutY(50.0);

        for(int i = 0; i <= 8; i++) {
            if(i != 0) {
                canvas[i][0] = new Label();
                canvas[i][0].setLayoutX(0.0);
                canvas[i][0].setLayoutY(canvas[i - 1][1].getLayoutY() + 33.0);
                canvas[i][0].setStyle(canvas[0][0].getStyle() + "-fx-background-color: transparent;");
            }
            for(int j = 1; j <= 8; j++) {
                canvas[i][j] = new Label();
                canvas[i][j].setStyle(canvas[0][0].getStyle() + "-fx-background-color: transparent;");
                canvas[i][j].setLayoutX(canvas[i][j - 1].getLayoutX() + 33.0);
                canvas[i][j].setLayoutY(canvas[i][0].getLayoutY());
            }
        }

        Button[] digits = new Button[10];
        digits[1] = new Button("1");
        digits[1].setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-family: Consolas; -fx-font-size: 20px;");
        digits[1].setLayoutX(-13.0); digits[1].setLayoutY(380.0);

        for(int i = 2; i <= 9; i++) {
            digits[i] = new Button("" + i);
            digits[i].setLayoutY(380.0);
            digits[i].setStyle(digits[1].getStyle());
            /// 30px distance from digit to digit
            digits[i].setLayoutX( digits[i - 1].getLayoutX() + 30.0 );
        }

        Button removeDigit = new Button("X");
        removeDigit.setUnderline(true);
        removeDigit.setStyle(digits[1].getStyle());
        removeDigit.setLayoutX(digits[9].getLayoutX() + 30.0);
        removeDigit.setLayoutY(380.0);
        removeDigit.setOnMouseClicked(e -> {
            int ii = Integer.parseInt(indexI.toString());
            int jj = Integer.parseInt(indexJ.toString());
            canvas[ii][jj].setText("");
            canvas[ii][jj].setTextFill(Color.web("#c9c9c9"));
        });

        AnchorPane pane = new AnchorPane();
        for(int i = 0; i <= 8; i++) {
            for(int j = 0; j <= 8; j++) {
                final int a = i, b = j;
                canvas[i][j].setOnMouseClicked(e -> {
                    indexI.set(a);
                    indexJ.set(b);
                    for(int k = 0; k <= 8; k++) {
                        for(int l = 0; l <= 8; l++) {
                            canvas[k][l].setBackground(new Background(new BackgroundFill(Color.rgb(0, 0, 0, 0.0), CornerRadii.EMPTY, Insets.EMPTY)));
                            //canvas[k][l].setStyle(canvas[k][l].getStyle() + "-fx-background-color: transparent;");
                        }
                    }
                    for(int k = 0; k <= 8; k++) {
                        canvas[a][k].setBackground(new Background(new BackgroundFill(Color.rgb(93, 95, 97, 0.5), CornerRadii.EMPTY, Insets.EMPTY)));
                        canvas[k][b].setBackground(new Background(new BackgroundFill(Color.rgb(93, 95, 97, 0.5), CornerRadii.EMPTY, Insets.EMPTY)));
                    }
                    int aa = a - a % 3, bb = b - b % 3;
                    for(int k = 0; k < 3; k++) {
                        for(int l = 0; l < 3; l++) {
                            canvas[k + aa][l + bb].setBackground(new Background(new BackgroundFill(Color.rgb(93, 95, 97, 0.5), CornerRadii.EMPTY, Insets.EMPTY)));
                        }
                    }
                    canvas[a][b].setBackground(new Background(new BackgroundFill(Color.rgb(147, 149, 153, 0.5), CornerRadii.EMPTY, Insets.EMPTY)));
                });
                pane.getChildren().add(canvas[i][j]);
            }
        }
        for(int i = 1; i <= 9; i++) {
            final int number = i;
            digits[i].setOnMouseClicked(e -> {
                int ii = Integer.parseInt(indexI.toString());
                int jj = Integer.parseInt(indexJ.toString());
                canvas[ii][jj].setText(" " + number);
                //canvas[ii][jj].setFont(new Font(25));
            });
            pane.getChildren().add(digits[i]);
        }

        pane.getChildren().add(removeDigit);


        Button solveButton = new Button("Solve");
        solveButton.setStyle("-fx-font-family: Consolas; -fx-font-size: 15px; -fx-background-color: #0e1d2b;-fx-background-radius: 13px; -fx-underline: true;");
        solveButton.setLayoutY(450);
        solveButton.setLayoutX(76);
        solveButton.setOnMouseClicked(e -> {
            boolean sudokuIsValid = true;
            // set the elements to matrix v
            // and check if there are the same elements on columns, lines and 3x3 squares
            for(int i = 0; i <= 8; i++) {
                for(int j = 0; j <= 8; j++) {
                    String s = canvas[i][j].getText().trim();
                    /// if the cell is empty
                    if(s.length() == 0) {
                        v[i][j] = 0;
                    } else {
                        int x = Integer.parseInt(s);
                        if(inLin(i, x) || inCol(j, x) || inSquare(i - i % 3, j - j % 3, x)) {
                            sudokuIsValid = false;
                        }
                        v[i][j] = x;
                    }
                }
            }
            if(sudokuIsValid && back()) {
                for(int i = 0; i <= 8; i++) {
                    for(int j = 0; j <= 8; j++) {
                        if(canvas[i][j].getText().length() != 2) {
                            canvas[i][j].setText(" " + v[i][j]);
                            canvas[i][j].setTextFill(Color.AQUA);
                        }
                    }
                }
            } else {
                /// if sudoku is invalid
                // TODO: pop-up error message
                PopUp popUp = new PopUp();
                try {
                    popUp.display();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        Button clearButton = new Button("Clear");
        clearButton.setStyle("-fx-font-family: Consolas; -fx-font-size: 15px; -fx-background-color: #0e1d2b;-fx-background-radius: 13px; -fx-underline: true;");
        clearButton.setLayoutY(450);
        clearButton.setLayoutX(166);
        clearButton.setOnMouseClicked(e -> {
            for(int i = 0; i <= 8; i++) {
                for(int j = 0; j <= 8; j++) {
                    canvas[i][j].setText("");
                    canvas[i][j].setTextFill(Color.web("#c9c9c9"));
                }
            }
        });

        pane.getChildren().addAll(solveButton, clearButton);
        setCenter(pane);

    }

    @Override
    protected void updateAppBar(AppBar appBar) {
        appBar.setTitleText("Sudoku Solver");
        appBar.setStyle("-fx-background-color: #13273b;");
        appBar.getActionItems().add(MaterialDesignIcon.IMAGE.button());
        // TODO: image input
    }
    
}
