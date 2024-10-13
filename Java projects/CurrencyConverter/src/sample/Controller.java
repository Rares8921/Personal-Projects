package sample;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;

import java.beans.EventHandler;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.YearMonth;
import java.util.*;

public class Controller implements Initializable {

    // 153 currencies

    @FXML
    private AnchorPane parent;

    @FXML
    private ComboBox<String> convertFrom, convertTo;

    @FXML
    private TextField convertFromField, convertToField;

    @FXML
    private Button days7, months1, months3, months6, years1, years5, years10, years20;

    @FXML
    private AreaChart graph;

    private final Map<String, Integer> months = new HashMap<String, Integer>() {{
        put("jan", 1);
        put("feb", 2);
        put("mar", 3);
        put("apr", 4);
        put("may", 5);
        put("jun", 6);
        put("jul", 7);
        put("aug", 8);
        put("sep", 9);
        put("oct", 10);
        put("nov", 11);
        put("dec", 12);
    }};

    private double xOffSet;
    private double yOffSet;

    private static Map<String, Object> map;
    private static double oldNumber;

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

    // steag pt fiecare moneda
    // grafic de comparatie intre currencyuri
    // puncte pe grafic

    public static Map<String, Object> toMap(JsonObject jsonobj)  throws Exception {
        Map<String, Object> map = new HashMap<>();
        for (String key : jsonobj.keySet()) {
            Object value = jsonobj.get(key);
            if (value instanceof JsonArray) {
                value = toList((JsonArray) value);
            } else if (value instanceof JsonObject) {
                value = toMap((JsonObject) value);
            }
            map.put(key, value);
        }
        return map;
    }

    public static List<Object> toList(JsonArray array) throws Exception {
        List<Object> list = new ArrayList<>();
        for(int i = 0; i < array.size(); i++) {
            Object value = array.get(i);
            if (value instanceof JsonArray) {
                value = toList((JsonArray) value);
            }
            else if (value instanceof JsonObject) {
                value = toMap((JsonObject) value);
            }
            list.add(value);
        }   return list;
    }

    private void loadData() throws Exception {
        File file = new File("src/sample/currencies.txt");
        Scanner scan = new Scanner(file);
        while(scan.hasNext()) {
            String s = scan.next();
            convertFrom.getItems().add(s);
            convertTo.getItems().add(s);
        }
        scan.close();
    }

    private void setComponents() {
        convertFrom.setValue("EUR");
        convertTo.setValue("USD");
        convertFromField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.matches("^[0-9.]+$")) {
                    convertFromField.setText(newValue.replaceAll("[^\\d]", ""));
                }
                if(newValue.contains(".")) {
                    int index = newValue.indexOf("."), index1 = newValue.lastIndexOf(".");
                    if(index == index1) {
                        boolean valid = true;
                        if(index >= 17 || newValue.length() - index > 11) {
                            valid = false;
                        }
                        if(!valid) {
                            convertFromField.setText(oldValue);
                        }
                    } else {
                        convertFromField.setText(oldValue);
                    }

                } else {
                    if (newValue.length() > 17) {
                        String copy = newValue.substring(0, 17);
                        convertFromField.setText(copy);
                    }
                }
                if(!newValue.equals(oldValue)) {
                    convertToField.setText("");
                }
        });
        convertTo.setOnAction(e -> {
            if(convertFromField.getText().length() > 0 && oldNumber == Double.parseDouble(convertFromField.getText())) {
                convertToField.setText(map.get(convertTo.getValue()).toString());
            } else {
                convertToField.setText("");
            }
        });
        convertFrom.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> p) {
                return new ListCell<String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(item);
                        if (item == null || empty) {
                            setGraphic(null);
                        } else {
                            Image icon;
                            try {
                                String iconPath = "sample/flags/" + this.getText().toLowerCase() + ".png";
                                icon = new Image(iconPath);
                                ImageView iconImageView = new ImageView(icon);
                                iconImageView.setFitHeight(30);
                                iconImageView.setPreserveRatio(true);
                                setGraphic(iconImageView);
                            } catch(NullPointerException ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                };
            }
        });
        convertTo.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> p) {
                return new ListCell<String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(item);
                        if (item == null || empty) {
                            setGraphic(null);
                        } else {
                            Image icon;
                            try {
                                String iconPath = "sample/flags/" + this.getText().toLowerCase() + ".png";
                                icon = new Image(iconPath);
                                ImageView iconImageView = new ImageView(icon);
                                iconImageView.setFitHeight(30);
                                iconImageView.setPreserveRatio(true);
                                setGraphic(iconImageView);
                            } catch(NullPointerException ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                };
            }
        });
        graph.setLegendVisible(false);
        graph.setStyle("-fx-text-fill: white;");
        days7.setOnMouseClicked(e -> {
            Calendar cal1 = Calendar.getInstance(), cal2 = Calendar.getInstance(), cal3 = Calendar.getInstance(), cal4 = Calendar.getInstance(), cal5 = Calendar.getInstance();
            cal1.add(Calendar.DAY_OF_YEAR, -7);
            cal2.add(Calendar.DAY_OF_YEAR, -4);
            cal3.add(Calendar.DAY_OF_YEAR, -3);
            cal4.add(Calendar.DAY_OF_YEAR, -2);
            try {
                updateGraph(cal1.getTime(), cal2.getTime(), cal3.getTime(), cal4.getTime(), cal5.getTime());
                months1.setStyle("-fx-background-color: transparent;"); months3.setStyle("-fx-background-color: transparent;"); months6.setStyle("-fx-background-color: transparent;");
                years1.setStyle("-fx-background-color: transparent;"); years5.setStyle("-fx-background-color: transparent;"); years10.setStyle("-fx-background-color: transparent;"); years20.setStyle("-fx-background-color: transparent;");
                days7.setStyle("-fx-background-color: #01185b;");
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        });
        months1.setOnMouseClicked(e -> {
            Calendar cal1 = Calendar.getInstance(), cal2 = Calendar.getInstance(), cal3 = Calendar.getInstance(), cal4 = Calendar.getInstance(), cal5 = Calendar.getInstance();
            cal1.add(Calendar.MONTH, -1);
            cal2.add(Calendar.DAY_OF_YEAR, -21);
            cal3.add(Calendar.DAY_OF_YEAR, -14);
            cal4.add(Calendar.DAY_OF_YEAR, -7);
            try {
                updateGraph(cal1.getTime(), cal2.getTime(), cal3.getTime(), cal4.getTime(), cal5.getTime());
                days7.setStyle("-fx-background-color: transparent;");
                months3.setStyle("-fx-background-color: transparent;"); months6.setStyle("-fx-background-color: transparent;");
                years1.setStyle("-fx-background-color: transparent;"); years5.setStyle("-fx-background-color: transparent;"); years10.setStyle("-fx-background-color: transparent;"); years20.setStyle("-fx-background-color: transparent;");
                months1.setStyle("-fx-background-color: #01185b;");
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        });
        months3.setOnMouseClicked(e -> {
            Calendar cal1 = Calendar.getInstance(), cal2 = Calendar.getInstance(), cal3 = Calendar.getInstance(), cal4 = Calendar.getInstance(), cal5 = Calendar.getInstance();
            cal1.add(Calendar.MONTH, -3);
            cal2.add(Calendar.MONTH, -2);
            cal3.add(Calendar.MONTH, -1);
            cal4.add(Calendar.DAY_OF_YEAR, -15);
            try {
                updateGraph(cal1.getTime(), cal2.getTime(), cal3.getTime(), cal4.getTime(), cal5.getTime());
                days7.setStyle("-fx-background-color: transparent;");
                months1.setStyle("-fx-background-color: transparent;"); months6.setStyle("-fx-background-color: transparent;");
                years1.setStyle("-fx-background-color: transparent;"); years5.setStyle("-fx-background-color: transparent;"); years10.setStyle("-fx-background-color: transparent;"); years20.setStyle("-fx-background-color: transparent;");
                months3.setStyle("-fx-background-color: #01185b;");
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        });
        months6.setOnMouseClicked(e -> {
            Calendar cal1 = Calendar.getInstance(), cal2 = Calendar.getInstance(), cal3 = Calendar.getInstance(), cal4 = Calendar.getInstance(), cal5 = Calendar.getInstance();
            cal1.add(Calendar.MONTH, -6);
            cal2.add(Calendar.MONTH, -4);
            cal3.add(Calendar.MONTH, -3);
            cal4.add(Calendar.MONTH, -2);
            try {
                updateGraph(cal1.getTime(), cal2.getTime(), cal3.getTime(), cal4.getTime(), cal5.getTime());
                days7.setStyle("-fx-background-color: transparent;");
                months1.setStyle("-fx-background-color: transparent;"); months3.setStyle("-fx-background-color: transparent;");
                years1.setStyle("-fx-background-color: transparent;"); years5.setStyle("-fx-background-color: transparent;"); years10.setStyle("-fx-background-color: transparent;"); years20.setStyle("-fx-background-color: transparent;");
                months6.setStyle("-fx-background-color: #01185b;");
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        });
        years1.setOnMouseClicked(e -> {
            Calendar cal1 = Calendar.getInstance(), cal2 = Calendar.getInstance(), cal3 = Calendar.getInstance(), cal4 = Calendar.getInstance(), cal5 = Calendar.getInstance();
            cal1.add(Calendar.YEAR, -1);
            cal2.add(Calendar.MONTH, -9);
            cal3.add(Calendar.MONTH, -6);
            cal4.add(Calendar.MONTH, -3);
            try {
                updateGraph(cal1.getTime(), cal2.getTime(), cal3.getTime(), cal4.getTime(), cal5.getTime());
                days7.setStyle("-fx-background-color: transparent;");
                months1.setStyle("-fx-background-color: transparent;"); months3.setStyle("-fx-background-color: transparent;"); months6.setStyle("-fx-background-color: transparent;");
                years5.setStyle("-fx-background-color: transparent;"); years10.setStyle("-fx-background-color: transparent;"); years20.setStyle("-fx-background-color: transparent;");
                years1.setStyle("-fx-background-color: #01185b;");
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        });
        years5.setOnMouseClicked(e -> {
            Calendar cal1 = Calendar.getInstance(), cal2 = Calendar.getInstance(), cal3 = Calendar.getInstance(), cal4 = Calendar.getInstance(), cal5 = Calendar.getInstance();
            cal1.add(Calendar.YEAR, -5);
            cal2.add(Calendar.YEAR, -4);
            cal3.add(Calendar.YEAR, -2);
            cal4.add(Calendar.YEAR, -1);
            try {
                updateGraph(cal1.getTime(), cal2.getTime(), cal3.getTime(), cal4.getTime(), cal5.getTime());
                days7.setStyle("-fx-background-color: transparent;");
                months1.setStyle("-fx-background-color: transparent;"); months3.setStyle("-fx-background-color: transparent;"); months6.setStyle("-fx-background-color: transparent;");
                years1.setStyle("-fx-background-color: transparent;"); years10.setStyle("-fx-background-color: transparent;"); years20.setStyle("-fx-background-color: transparent;");
                years5.setStyle("-fx-background-color: #01185b;");
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        });
        years10.setOnMouseClicked(e -> {
            Calendar cal1 = Calendar.getInstance(), cal2 = Calendar.getInstance(), cal3 = Calendar.getInstance(), cal4 = Calendar.getInstance(), cal5 = Calendar.getInstance();
            cal1.add(Calendar.YEAR, -10);
            cal2.add(Calendar.YEAR, -8);
            cal3.add(Calendar.YEAR, -5);
            cal4.add(Calendar.YEAR, -2);
            try {
                updateGraph(cal1.getTime(), cal2.getTime(), cal3.getTime(), cal4.getTime(), cal5.getTime());
                days7.setStyle("-fx-background-color: transparent;");
                months1.setStyle("-fx-background-color: transparent;"); months3.setStyle("-fx-background-color: transparent;"); months6.setStyle("-fx-background-color: transparent;");
                years1.setStyle("-fx-background-color: transparent;"); years5.setStyle("-fx-background-color: transparent;"); years20.setStyle("-fx-background-color: transparent;");
                years10.setStyle("-fx-background-color: #01185b;");
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        });
        years20.setOnMouseClicked(e -> {
            Calendar cal1 = Calendar.getInstance(), cal2 = Calendar.getInstance(), cal3 = Calendar.getInstance(), cal4 = Calendar.getInstance(), cal5 = Calendar.getInstance();
            cal1.add(Calendar.YEAR, -20);
            cal2.add(Calendar.YEAR, -15);
            cal3.add(Calendar.YEAR, -10);
            cal4.add(Calendar.YEAR, -5);
            try {
                updateGraph(cal1.getTime(), cal2.getTime(), cal3.getTime(), cal4.getTime(), cal5.getTime());
                days7.setStyle("-fx-background-color: transparent;");
                months1.setStyle("-fx-background-color: transparent;"); months3.setStyle("-fx-background-color: transparent;"); months6.setStyle("-fx-background-color: transparent;");
                years1.setStyle("-fx-background-color: transparent;"); years5.setStyle("-fx-background-color: transparent;"); years10.setStyle("-fx-background-color: transparent;");
                years20.setStyle("-fx-background-color: #01185b;");
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    private Map<String, Object> getMap(String year, String month, String day) throws Exception {
        final String url_str = "https://openexchangerates.org/api/historical/" + year + "-" + month + "-" + day + ".json?app_id=6730fccff7634c31bace58bddf455ca0";
        URL url = new URL(url_str);
        HttpURLConnection request = (HttpURLConnection) url.openConnection();
        request.connect();

        // Convert to JSON
        JsonParser jp = new JsonParser();
        JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
        JsonObject jsonobj = root.getAsJsonObject();

        // Accessing object
        JsonObject result = jsonobj.get("rates").getAsJsonObject();
        return toMap(result);
    }

    private void updateGraph(Date... date) throws Exception {
        graph.getData().clear();
        graph.setTitle(convertFrom.getValue() + " to " + convertTo.getValue() + " chart");
        XYChart.Series series = new XYChart.Series();
        for(int i = 0; i < date.length; i++) {
            String[] str = date[i].toString().split(" ");
            String month = str[1], day = str[2], year = str[5];
            String monthNumber = months.get(month.toLowerCase()) + "";
            if (monthNumber.length() == 1) {
                monthNumber = "0" + monthNumber;
            }
            if (day.length() == 1) {
                day = "0" + day;
            }

            // past values
            Map<String, Object> mp = getMap(year, monthNumber, day);
            double val1 = Double.parseDouble(mp.get(convertFrom.getValue()).toString());
            double val2 = Double.parseDouble(mp.get(convertTo.getValue()).toString());

            double ratio = val1 / val2;
            double newValue = ratio * Double.parseDouble(convertToField.getText());

            series.getData().add(new XYChart.Data(day + "." + monthNumber + "." + year, newValue));
        }
        graph.getData().add(series);
    }

    public void loadApi() throws Exception {
        String url_str = "https://v6.exchangerate-api.com/v6/af5e92eece628b43a2340cc3/latest/" + convertFrom.getValue();

        // Making Request
        URL url = new URL(url_str);
                HttpURLConnection request = (HttpURLConnection) url.openConnection();
                request.connect();

        // Convert to JSON
                JsonParser jp = new JsonParser();
                JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
                JsonObject jsonobj = root.getAsJsonObject();

        // Accessing object
        JsonObject result = jsonobj.get("conversion_rates").getAsJsonObject();
        map = toMap(result);
        oldNumber = Double.parseDouble(convertFromField.getText());
        convertToField.setText("" + (Double.parseDouble(map.get(convertTo.getValue()).toString()) * Double.parseDouble(convertFromField.getText())));
        //System.out.println(req_result.toString());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setDraggable();
        try {
            loadData();
        } catch (Exception e) {
            e.printStackTrace();
        }
        setComponents();
    }
}
