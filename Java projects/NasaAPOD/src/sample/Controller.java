package sample;

import com.sun.javafx.scene.control.DatePickerContent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.control.skin.DateCellSkin;
import javafx.scene.control.skin.DatePickerSkin;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import org.codehaus.jackson.map.ObjectMapper;


import java.awt.Desktop;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private AnchorPane parent;

    @FXML
    private AnchorPane imageView;

    @FXML
    private Label title, author, applicationTitle;

    @FXML
    private javafx.scene.control.Button calendarButton;

    private double xOffSet;
    private double yOffSet;

    private ImageView imageOfTheDay;

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

    private ImageView getImageOfTheDay(String date) {
        try {
            URL url = new URL("https://api.nasa.gov/planetary/apod?api_key=cY6RrazmPKkEwr4bPMhbzCfhRx3pzw505ILbxbmE" + date);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Now it's "open", we can set the request method, headers etc.
            connection.setRequestProperty("accept", "application/json");
            InputStream responseStream = connection.getInputStream();

            // Manually converting the response body InputStream to APOD using Jackson api
            ObjectMapper mapper = new ObjectMapper();
            APOD apod = mapper.readValue(responseStream, APOD.class);

            if (apod.mediaType.equals("image")) {

                // Setting the title
                AnchorPane.setLeftAnchor(title, 0.0);
                AnchorPane.setRightAnchor(title, 0.0);
                title.setAlignment(Pos.CENTER);
                title.setText(apod.title);

                // Setting the author
                AnchorPane.setLeftAnchor(author, 0.0);
                AnchorPane.setRightAnchor(author, 0.0);
                author.setAlignment(Pos.CENTER);
                author.setText("Author: " + (apod.copyright == null ? "Unknown" : apod.copyright));

                // Setting the link to original site
                imageView.setOnMouseClicked(e -> {
                    try {
                        if (Desktop.isDesktopSupported()) {
                            Desktop desktop = Desktop.getDesktop();
                            if (desktop.isSupported(Desktop.Action.BROWSE)) {
                                desktop.browse(URI.create(apod.hdUrl));
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });

                // Setting the description of the photo/gif etc.
                Tooltip tip = new Tooltip(apod.explanation);
                tip.setWrapText(true);
                tip.setMaxWidth(400);
                tip.setStyle("-fx-background-color: #1a1a1a; -fx-text-fill: white; -fx-font-size: 13px;");
                tip.setShowDuration(Duration.seconds(90));
                Tooltip.install(imageView, tip);

                return new ImageView(apod.hdUrl);
            }
            return getImageOfTheDay("&date=2020-05-19");
        } catch(Exception e) {
            e.printStackTrace();
        }
        // TODO: de verificat daca in ziua respectiva e gif/video/poza
        return new ImageView();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // styling the calendar button
        ImageView calendarView = new ImageView(new Image("calendar.png"));
        calendarView.setFitWidth(40);
        calendarView.setFitHeight(27);
        calendarButton.setGraphic(calendarView);

        // creating and configuring the calendar
        DatePickerSkin datePickerSkin = new DatePickerSkin(new DatePicker(LocalDate.now()));
        datePickerSkin.getPopupContent().setOnMouseClicked(e -> {
            String dateInput = datePickerSkin.getSkinnable().getValue().toString();
            final String[] time = dateInput.split("-");
            int year = Integer.parseInt(time[0]), month = Integer.parseInt(time[1]), day = Integer.parseInt(time[2]);
            LocalDate localDate = LocalDate.of(year, month, day);
            if(!(localDate.isAfter(LocalDate.now()) || localDate.isBefore(LocalDate.of(1995, 6, 20))) || localDate.isEqual(LocalDate.of(1995, 6, 16))) {
                imageOfTheDay.setImage(getImageOfTheDay("&date=" + dateInput).getImage());
                applicationTitle.setText("Astronomy picture of the day - " + dateInput.replace("-", "."));
            }
        });

        // create a popup for the calendar
        Node popupContent = datePickerSkin.getPopupContent();
        popupContent.setStyle("-fx-font-family: Consolas;");
        popupContent.setVisible(false);
        popupContent.setLayoutX(521); popupContent.setLayoutY(35);
        parent.getChildren().add(popupContent);

        // display calendar
        calendarButton.setOnMouseClicked(e -> popupContent.setVisible(!popupContent.isVisible()));
        parent.setOnMouseClicked(e -> popupContent.setVisible(false));

        // changing the application's title
        applicationTitle.setText("Astronomy picture of the day - " + LocalDate.now().toString().replace("-", "."));

        // setting the image
        imageOfTheDay= getImageOfTheDay("");
        imageOfTheDay.setFitWidth(711.0);
        imageOfTheDay.setFitHeight(630.0);
        imageView.getChildren().add(imageOfTheDay);
        setDraggable();
    }
}
