package sample;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.*;
import java.util.*;

public class Main extends Application {

    private static final int WIDTH = 1140;
    private static final int HEIGHT = 800;
    private static final int ROWS = 20;
    private static final int COLUMNS = 20;
    private static final int SQUARE = 40;

    private final int[] di = {0, 0, -1, 1, -1, -1, 1, 1};
    private final int[] dj = {1, -1, 0, 0, -1, 1, 1, -1};

    private final int[] dli = {0, 0, -1, 1};
    private final int[] dlj = {1, -1, 0, 0};

    private final int[] kdi = {-2, -2, -1, -1, 1, 1, 2, 2};
    private final int[] kdj = {-1, 1, -2, 2, -2, 2, -1, 1};

    public static boolean obstacles, lasers, tp, laser_tp;
    public static boolean horizontal, vertical, diagonal, knight;
    public static boolean start, end;

    public static char[][] matr = new char[25][25];
    public static int[][] matrLee = new int[25][25];
    public static int[][][] s = new int[25][25][2]; // for tps and laser_tps

    public static int iStart = -1, jStart = -1, iFinish = -1, jFinish = -1, cntTp, cntLaserTp;
    public static int lastTpI = -1, lastTpJ = -1, currentTpI = -1, currentTpJ = -1;

    public static boolean algStarted;

    public static Canvas canvas;
    public static Stage stage = null;

    /*
        TODO:  *obstacole
               *lasere - obstacole
               *teleporturi
               *tpRays
               *4 directii/8 directii
               *movement de cal
                SI
                design
               animatii - movement, lasere, tp
               editarea tablei (fara suprapunere (matrLee[i][j] == 0); la lasere pe linie si col diferite; )
               obstacole - 'X', raze - '-', '|', '+', tp - 'O';
               log info - notatii, prima matr, si matr lee la final

     */

    private boolean inMat(int i, int j) {
        return i >= 0 && i < 20 && j >= 0 && j < 20;
    }

    public boolean lee() {
        /// -1 - obstacle, -2 - laser, -3 - tp, -4 - tpLaser
        if(iFinish == -1 || iStart == -1 || (!vertical && !horizontal && !diagonal && !knight) || cntTp % 2 != 0) {
            ErrorBox box = new ErrorBox();
            try {
                box.display();
            } catch(Exception ex) {
                ex.printStackTrace();
            }
            return false;
        }
        try {
            dataAndAnimation();
            algStarted = true;
            int i = iStart, j = jStart, i1 = iFinish, j1 = jFinish;
            for (int ii = 0; ii < 20; ii++) {
                for (int jj = 0; jj < 20; jj++) {
                    if (matr[ii][jj] == '+' || matr[ii][jj] == '/') {
                        if(matr[ii][jj] == '/') {
                            cntLaserTp++;
                        }
                        for (int d = 0; d < 4; d++) {
                            int it = ii + dli[d];
                            int jt = jj + dlj[d];
                            while (inMat(it, jt)) {
                                if (matr[it][jt] == '|') {
                                    int x = s[it][jt][0], y = s[it][jt][1];
                                    if ((x == -1 && y == -1)) {
                                        s[it][jt] = new int[]{ii, jj};
                                    }
                                } else {
                                    matr[it][jt] = 'X';
                                    matrLee[it][jt] = -1;
                                }
                                it += dli[d];
                                jt += dlj[d];
                            }
                        }
                    }
                }
            }
            int[] tempI = di;
            int[] tempJ = dj;
            if (!horizontal) {
                tempI[0] = tempI[1] = tempJ[0] = tempJ[1] = 0;
            }
            if (!vertical) {
                tempI[2] = tempI[3] = tempJ[2] = tempJ[3] = 0;
            }
            if (!diagonal) {
                tempI[4] = tempI[5] = tempI[6] = tempI[7] = tempJ[4] = tempJ[5] = tempJ[6] = tempJ[7] = 0;
            }
            ArrayList<int[]> list = new ArrayList<>(); // list of coordinates
            Queue<int[]> Q = new LinkedList<>();
            Q.add(new int[]{i, j});
            matrLee[i][j] = 1;
            while (!Q.isEmpty()) {
                int[] v = Q.poll();
                i = v[0];
                j = v[1];
                list.add(new int[]{i, j});
                for (int d = 0; d < 8; d++) {
                    int ii = i + (knight ? kdi[d] : tempI[d]);
                    int jj = j + (knight ? kdj[d] : tempJ[d]);
                    if (inMat(ii, jj) && matrLee[ii][jj] == 0 && matr[ii][jj] != 'X') {
                        if(matr[ii][jj] == 'O' || matr[ii][jj] == '|') {
                            int it = s[ii][jj][0], jt = s[ii][jj][1];
                            matrLee[ii][jj] = matrLee[i][j] + 1;
                                if(it != - 1 && matrLee[it][jt] == 0) {
                                    matrLee[it][jt] = matrLee[ii][jj];
                                    Q.add(new int[]{ii, jj});
                                    Q.add(new int[]{it, jt});
                                }

                        } else {
                            matrLee[ii][jj] = matrLee[i][j] + 1;
                            Q.add(new int[]{ii, jj});
                        }
                    }
                }
                if (cntTp > 0 || cntLaserTp > 0) {
                    int it = s[i][j][0], jt = s[i][j][1];
                    if (it != -1 && jt != -1) {
                        if (matrLee[it][jt] > matrLee[i][j]) {
                            matrLee[it][jt] = matrLee[i][j];
                            Q.add(new int[]{it, jt});
                        }
                    }
                }
            }
            // delete text from canvas
            canvas.getGraphicsContext2D().setFill((iStart + jStart + 1) % 2 == 0 ? Color.web("1a1a1a") : Color.web("303030"));
            canvas.getGraphicsContext2D().fillRect(jStart * 40, (iStart + 1) * 40, SQUARE, SQUARE);
            canvas.getGraphicsContext2D().setFill((iFinish + jFinish + 1) % 2 == 0 ? Color.web("1a1a1a") : Color.web("303030"));
            canvas.getGraphicsContext2D().fillRect(jFinish * 40, (iFinish + 1) * 40, SQUARE, SQUARE);
            // path animation
            final int[] k = {0};
            Timeline path = new Timeline(new KeyFrame(javafx.util.Duration.millis(30), e -> {
                int ii = list.get(k[0])[0], jj = list.get(k[0]++)[1];
                canvas.getGraphicsContext2D().setStroke( (ii == iStart && jj == jStart) || (ii == i1 && jj == j1) ? Color.ORANGE : (s[ii][jj][0] != -1 || matr[ii][jj] == '/' ? Color.DARKBLUE : Color.AQUAMARINE));
                canvas.getGraphicsContext2D().strokeText("" + matrLee[ii][jj], (jj * 40) + 15, ((ii + 1) * 40) + 25);
                if(k[0] == list.size() - 1) {
                    algStarted = false;
                }
            }));
            path.setCycleCount(list.size());
            path.play();
            return true;
        } catch(Exception e) {
            e.printStackTrace();
            algStarted = false;
            ErrorBox box = new ErrorBox();
            try {
                box.display();
            } catch(Exception ex) {
                ex.printStackTrace();
            }
            return false;
        }
    }

    public void dataAndAnimation() {
        for(int i = 0; i <= ROWS; i++) {
            for(int j = 0; j <= COLUMNS; j++) {
                if(matr[i][j] == '+' || matr[i][j] == '/') {
                    final boolean isTp = matr[i][j] == '/';
                    canvas.getGraphicsContext2D().setFill(isTp ? Color.LIGHTGRAY : Color.ORANGERED);
                    canvas.getGraphicsContext2D().fillRect(j * SQUARE, (i + 1) * SQUARE, SQUARE, SQUARE);
                    final int[] i1 = {i + dli[0]}; final int[] j1 = {j + dlj[0]};
                    final int[] i2 = {i + dli[1]}; final int[] j2 = {j + dlj[1]};
                    final int[] i3 = {i + dli[2]}; final int[] j3 = {j + dlj[2]};
                    final int[] i4 = {i + dli[3]}; final int[] j4 = {j + dlj[3]};
                    Timeline laser = new Timeline(new KeyFrame(javafx.util.Duration.millis(40), e -> {
                        if(inMat(i1[0], j1[0]) && i1[0] + 1 > 0) {
                            canvas.getGraphicsContext2D().setFill(isTp ? Color.WHITE : Color.DARKRED);
                            canvas.getGraphicsContext2D().fillRect(j1[0] * SQUARE, (i1[0] + 1) * SQUARE, SQUARE, SQUARE);
                            i1[0] += dli[0]; j1[0] += dlj[0];
                        }
                        if(inMat(i2[0], j2[0]) && i2[0] + 1 > 0) {
                            canvas.getGraphicsContext2D().setFill(isTp ? Color.WHITE : Color.DARKRED);
                            canvas.getGraphicsContext2D().fillRect(j2[0] * SQUARE, (i2[0] + 1) * SQUARE, SQUARE, SQUARE);
                            i2[0] += dli[1]; j2[0] += dlj[1];
                        }
                        if(inMat(i3[0], j3[0]) && i3[0] + 1 > 0) {
                            canvas.getGraphicsContext2D().setFill(isTp ? Color.WHITE : Color.DARKRED);
                            canvas.getGraphicsContext2D().fillRect(j3[0] * SQUARE, (i3[0] + 1) * SQUARE, SQUARE, SQUARE);
                            i3[0] += dli[2]; j3[0] += dlj[2];
                        }
                        if(inMat(i4[0], j4[0]) && i4[0] + 1 > 0) {
                            canvas.getGraphicsContext2D().setFill(isTp ? Color.WHITE : Color.DARKRED);
                            canvas.getGraphicsContext2D().fillRect(j4[0] * SQUARE, (i4[0] + 1) * SQUARE, SQUARE, SQUARE);
                            i4[0] += dli[3]; j4[0] += dlj[3];
                        }
                    }));
                    laser.setCycleCount(19);
                    laser.play();
                }
            }
        }
        for(int i = 0; i < ROWS; i++) {
            for(int j = 0; j < COLUMNS; j++) {
                if(matr[i][j] == '/' || matr[i][j] == '+') {
                    final boolean isTp = matr[i][j] == '/';
                    if(!isTp) {
                        matr[i][j] = 'X';
                    }
                    final int[] i1 = {i + di[0]}; final int[] j1 = {j + dj[0]};
                    final int[] i2 = {i + di[1]}; final int[] j2 = {j + dj[1]};
                    final int[] i3 = {i + di[2]}; final int[] j3 = {j + dj[2]};
                    final int[] i4 = {i + di[3]}; final int[] j4 = {j + dj[3]};
                    boolean valid = true;
                    while (valid) {
                        valid = false;
                        if (inMat(i1[0], j1[0])) {
                            matr[i1[0]][j1[0]] = (isTp ? '|' : 'X');
                            matrLee[i1[0]][j1[0]] = (isTp ? 0 : -1);
                            valid = true;
                            i1[0] += di[0];
                            j1[0] += dj[0];
                        }
                        if (inMat(i2[0], j2[0])) {
                            matr[i2[0]][j2[0]] = (isTp ? '|' : 'X');
                            matrLee[i2[0]][j2[0]] = (isTp ? 0 : -1);
                            valid = true;
                            i2[0] += di[1];
                            j2[0] += dj[1];
                        }
                        if (inMat(i3[0], j3[0])) {
                            matr[i3[0]][j3[0]] = (isTp ? '|' : 'X');
                            matrLee[i3[0]][j3[0]] = (isTp ? 0 : -1);
                            valid = true;
                            i3[0] += di[2];
                            j3[0] += dj[2];
                        }
                        if (inMat(i4[0], j4[0])) {
                            matr[i4[0]][j4[0]] = (isTp ? '|' : 'X');
                            matrLee[i4[0]][j4[0]] = (isTp ? 0 : -1);
                            valid = true;
                            i4[0] += di[3];
                            j4[0] += dj[3];
                        }
                    }
                }
            }
        }
    }
    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent parent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("sample.fxml")));

        //Image icon = new Image("/imgs/snake.png");
        //primaryStage.getIcons().add(icon);
        Group root = new Group();

        canvas = new Canvas(WIDTH, HEIGHT);
        canvas.getGraphicsContext2D().setFont(new Font("Consolas", 20));

        root.getChildren().add(canvas);
        root.getChildren().add(parent);

        primaryStage.setResizable(false);
        primaryStage.setTitle("Lee visualiser");
        primaryStage.initStyle(StageStyle.UNDECORATED);

        for(int i = 0; i <= 20; i++) {
            for(int j = 0; j <= 20; j++) {
                matr[i][j] = '_';
                s[i][j] = new int[]{-1, -1};
            }
        }

        root.setOnMouseClicked(e -> {
            if(!algStarted) {
                double x = e.getX(), y = e.getY();
                if (x >= 0 && x <= 800 && y >= 41 && y <= 800) {
                    while (x % 40 != 0) {
                        x--;
                    }
                    while (y % 40 != 0) {
                        y--;
                    }
                    double i = y / 40, j = x / 40;
                    //j--;
                    if (e.getButton() == MouseButton.SECONDARY) { // if right click is pressed
                        if (canvas.getGraphicsContext2D().getFill().toString().equals("0xadff2fff")) {
                            cntTp--;
                            cntTp = Math.max(cntTp, 0);
                            if (currentTpI != -1) {
                                s[lastTpI][lastTpJ] = new int[]{-1, -1};
                                s[currentTpI][currentTpJ] = new int[]{-1, -1};
                                currentTpI = currentTpJ = -1;
                            } else if (lastTpI != -1) {
                                lastTpI = lastTpJ = -1;
                            }
                        }
                        matr[(int) i - 1][(int) j] = '_';
                        // de verificat aici daca s-a apasat un patratel cu coord
                        if (iStart == i - 1 && jStart == j) {
                            iStart = jStart = -1;
                        } else if (iFinish == i - 1 && jFinish == j) {
                            iFinish = jFinish = -1;
                        }
                        canvas.getGraphicsContext2D().strokeText("", 1, 1);
                        canvas.getGraphicsContext2D().setFill((i + j) % 2 == 0 ? Color.web("1a1a1a") : Color.web("303030"));
                        canvas.getGraphicsContext2D().fillRect(x, y, SQUARE, SQUARE);
                    } else {
                        if (obstacles) {
                            matr[(int) i - 1][(int) j] = 'X';
                            canvas.getGraphicsContext2D().setFill(Color.DARKRED);
                            canvas.getGraphicsContext2D().fillRect(x, y, SQUARE, SQUARE);
                        } else if (tp) {
                            cntTp++;
                            if (cntTp % 2 == 0) {
                                currentTpI = (int) i - 1;
                                currentTpJ = (int) j;
                                s[lastTpI][lastTpJ] = new int[]{currentTpI, currentTpJ};
                                s[currentTpI][currentTpJ] = new int[]{lastTpI, lastTpJ};
                            } else {
                                lastTpI = (int) i - 1;
                                lastTpJ = (int) j;
                            }
                            matr[(int) i - 1][(int) j] = 'O';
                            canvas.getGraphicsContext2D().setFill(Color.GREENYELLOW);
                            canvas.getGraphicsContext2D().fillRect(x, y, SQUARE, SQUARE);
                        } else if (lasers) {
                            matr[(int) i - 1][(int) j] = '+';
                            canvas.getGraphicsContext2D().setFill(Color.BLUEVIOLET);
                            canvas.getGraphicsContext2D().fillRect(x, y, SQUARE, SQUARE);
                        } else if (laser_tp) {
                            matr[(int) i - 1][(int) j] = '/';
                            canvas.getGraphicsContext2D().setFill(Color.ALICEBLUE);
                            canvas.getGraphicsContext2D().fillRect(x, y, SQUARE, SQUARE);
                        } else if (start && iStart == -1 && (iFinish != i - 1 || jFinish != j)) {
                            iStart = (int) i - 1;
                            jStart = (int) j;
                            canvas.getGraphicsContext2D().setStroke(Color.WHITE);
                            canvas.getGraphicsContext2D().strokeText("S", x + 15, y + 25);
                            Controller.staticStart.setText(iStart + "," + jStart);
                        } else if (end && iFinish == -1 && (iStart != i - 1 || jStart != j)) {
                            iFinish = (int) i - 1;
                            jFinish = (int) j;
                            canvas.getGraphicsContext2D().setStroke(Color.WHITE);
                            canvas.getGraphicsContext2D().strokeText("F", x + 15, y + 25);
                            Controller.staticEnd.setText(iFinish + "," + jFinish);
                        }
                    }
                }
            }
        });

        Scene scene = new Scene(root);
        scene.getStylesheets().add("/sample/lee.css");
        primaryStage.setScene(scene);
        root.requestFocus();

        stage = primaryStage;
        stage.requestFocus();
        stage.show();

        GraphicsContext gc = canvas.getGraphicsContext2D();

        background(gc);
    }

    public static void background(GraphicsContext gct) {
        /// the background will be created in the form of a table
        for(int i = 0; i < ROWS + 15; i++) {
            for(int j = 0; j < COLUMNS; j++) {
                /// j = 0 => title bar
                if(j == 0 || i >= ROWS) {
                    gct.setFill(Color.web("292929"));
                } else {
                    // alternating colors on the table
                    // consider it as a matrix where every element is different from the last one, but equal to the 'last but one' element
                    gct.setFill((i + j) % 2 == 0 ? Color.web("1a1a1a") : Color.web("303030"));
                }
                // arguments: x position, y position, width, height
                gct.fillRect(i * SQUARE, j * SQUARE, SQUARE, SQUARE);
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}