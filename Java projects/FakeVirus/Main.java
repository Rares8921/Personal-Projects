package sample;

import javafx.application.Application;
import javafx.event.Event;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.Toolkit;

public class Main extends Application implements Runnable {

    @Override
    public void start(Stage primaryStage) {
        Pane pane = new Pane();

        Media media = new Media(getClass().getResource("video.mp4").toExternalForm());
        MediaPlayer player = new MediaPlayer(media);
        player.setAutoPlay(true);
        player.setCycleCount(MediaPlayer.INDEFINITE);
        MediaView virus = new MediaView();
        virus.setMediaPlayer(player);

        virus.setX(300);
        virus.setY(200);
        pane.getChildren().add(virus);
        pane.setStyle("-fx-background-color: #000000");

        Image icon = new Image("rick.png");
        primaryStage.getIcons().add(icon);

        primaryStage.setTitle("What happened?");
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setScene(new Scene(pane));
        primaryStage.centerOnScreen();
        primaryStage.setMaximized(true);
        primaryStage.setResizable(false);
        primaryStage.setAlwaysOnTop(true);
        primaryStage.iconifiedProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue)
                primaryStage.setIconified(false);
        });
        primaryStage.setOnCloseRequest(Event::consume);
        primaryStage.show();

        Thread thread = new Thread(new Main());
        thread.start();

    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void run() {
        while(true) {
            Toolkit.getDefaultToolkit().beep();
        }
    }
}
