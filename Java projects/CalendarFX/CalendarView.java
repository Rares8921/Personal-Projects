package sample;

import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;

public class CalendarView {

    private final ArrayList<AnchorPaneNode> allCalendarDays = new ArrayList<>(35);
    private final VBox view;
    private final Text calendarTitle;
    private YearMonth currentYearMonth;

    public CalendarView(YearMonth yearMonth) {
        currentYearMonth = yearMonth;
        GridPane calendar = new GridPane();
        calendar.setGridLinesVisible(true);
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 7; j++) {
                AnchorPaneNode ap = new AnchorPaneNode();
                ap.setPrefSize(200,200);
                calendar.add(ap, j, i);
                allCalendarDays.add(ap);
            }
        }
        Text[] dayNames = new Text[] { new Text("      Sun"), new Text("      Mon"), new Text("      Tues"), new Text("      Wed"),
                                       new Text("      Thur"), new Text("       Fri"), new Text("       Sat") };
        GridPane dayLabels = new GridPane();
        int col = 0;
        for (Text txt : dayNames) {
            AnchorPane ap = new AnchorPane();
            ap.setPrefSize(250, 10);
            AnchorPane.setBottomAnchor(txt, 5.0);
            txt.setFill(Color.valueOf("#f0f0f0"));
            ap.getChildren().add(txt);
            dayLabels.add(ap, col++, 0);
        }
        calendarTitle = new Text();
        calendarTitle.setStyle("-fx-font-size: 15px");
        calendarTitle.setFill(Color.WHITE);


        Button previousMonth = new Button("<");
        previousMonth.setOnMouseClicked(e -> {
            if(e.getButton() == MouseButton.PRIMARY) {
                previousMonth();
            } else if(e.getButton() == MouseButton.SECONDARY) {
                previousYear();
            } else if(e.getButton() == MouseButton.MIDDLE) {
                previous6Months();
            }
        });
        previousMonth.setCursor(Cursor.HAND);
        previousMonth.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 18px;");

        Button nextMonth = new Button(">");
        nextMonth.setOnMouseClicked(e -> {
                if(e.getButton() == MouseButton.PRIMARY) {
                    nextMonth();
                } else if(e.getButton() == MouseButton.SECONDARY) {
                    nextYear();
                } else if(e.getButton() == MouseButton.MIDDLE) {
                    next6Months();
                }
        });
        nextMonth.setCursor(Cursor.HAND);
        nextMonth.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 18px;");

        HBox titleBar = new HBox(previousMonth, calendarTitle, nextMonth);
        titleBar.setAlignment(Pos.BASELINE_CENTER);
        populateCalendar(yearMonth);
        view = new VBox(titleBar, dayLabels, calendar);
    }

    public void populateCalendar(YearMonth yearMonth) {
        LocalDate calendarDate = LocalDate.of(yearMonth.getYear(), yearMonth.getMonthValue(), 1);
        while (!calendarDate.getDayOfWeek().toString().equals("SUNDAY") ) {
            calendarDate = calendarDate.minusDays(1);
        }
        for (AnchorPaneNode ap : allCalendarDays) {
            if (ap.getChildren().size() != 0) {
                ap.getChildren().remove(0);
            }
            Text txt = new Text(String.valueOf(calendarDate.getDayOfMonth()));
            ap.setDate(calendarDate);
            AnchorPane.setTopAnchor(txt, 5.0);
            AnchorPane.setLeftAnchor(txt, 5.0);
            txt.setFill(currentYearMonth.getMonthValue() == calendarDate.getMonthValue() ? Color.WHITE : Color.DARKGRAY);
            ap.getChildren().add(txt);
            calendarDate = calendarDate.plusDays(1);
        }
        calendarTitle.setText(yearMonth.getMonth().toString().substring(0, 1).toUpperCase() + yearMonth.getMonth().toString().substring(1).toLowerCase() + " " + yearMonth.getYear() + "\n");
    }

    private void previousMonth() {
        currentYearMonth = currentYearMonth.minusMonths(1);
        populateCalendar(currentYearMonth);
    }

    private void nextMonth() {
        currentYearMonth = currentYearMonth.plusMonths(1);
        populateCalendar(currentYearMonth);
    }

    private void previous6Months() {
        currentYearMonth = currentYearMonth.minusMonths(6);
        populateCalendar(currentYearMonth);
    }

    private void next6Months() {
        currentYearMonth = currentYearMonth.plusMonths(6);
        populateCalendar(currentYearMonth);
    }

    public void previousYear() {
        currentYearMonth = currentYearMonth.minusYears(1);
        populateCalendar(currentYearMonth);
    }

    public void nextYear() {
        currentYearMonth = currentYearMonth.plusYears(1);
        populateCalendar(currentYearMonth);
    }

    public VBox getView() {
        return view;
    }
}