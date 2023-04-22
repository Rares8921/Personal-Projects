package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Objects;

public class Main extends Application {

    public static FlappyBird game = null;
    public static Stage stage = null;
    public static boolean GAME; // public static so it can be accessed from FlappyBird class

    public static void main(String[] args) {
        launch(args);
    }

    public void startGame() {
        if (!GAME) {
            GAME = true;
            game = new FlappyBird();
            Stage st = new Stage();
            try {
                game.start(st);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    @Override
    public void start(Stage fxstage) throws Exception {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("resources/game.fxml")));

        Scene scene = new Scene(root);

        scene.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                startGame();
            }
        });

        fxstage.getIcons().add(new Image("sample/icon.png"));
        fxstage.initStyle(StageStyle.UNDECORATED);
        fxstage.setScene(scene);
        fxstage.setResizable(false);
        fxstage.setTitle("Flappy bird");
        stage = fxstage;
        stage.show();
    }
}
