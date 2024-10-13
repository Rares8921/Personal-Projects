package com.example.weatherapp;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;

import java.io.*;
import java.net.URL;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import java.net.URLConnection;

import java.util.*;

public class Controller extends Thread implements Initializable {

    @FXML
    private AnchorPane parent;

    @FXML
    private Label country, description, sunrise, sunset, temperature, feelsLike, maxAndMin, wind, humidity, visibility, pressure, dayAndTime, day1, day2, day3, day4, day5, temp1, temp2, temp3, temp4, temp5;

    @FXML
    private ImageView icon, icon1, icon2, icon3, icon4, icon5;

    @FXML
    private TextField city;

    @FXML
    private Button unit;

    private double xOffSet;
    private double yOffSet;

    private final String API_KEY = "Nope";
    private static String URL, cityName = "", units;
    private final String[] days = {"", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
    private Map content;

    private void setDraggable() {
        parent.setOnMousePressed((event) -> {
            xOffSet = event.getSceneX();
            yOffSet = event.getSceneY();
        });
        parent.setOnMouseDragged((event) -> {
            Main.mainStage.setX(event.getScreenX() - xOffSet);
            Main.mainStage.setY(event.getScreenY() - yOffSet);
        });
    }

    private void loadUrl(String u) {
        try {
            Scanner scan = new Scanner(new File("src/data.txt"));
            if(cityName.length() == 0) {
                cityName += scan.nextLine();
            } else {
                String useless = "";
                useless += scan.nextLine();
            }
            units = scan.next();
            scan.close();
            if(u.length() > 0) {
                units = u;
            }
            URL = "https://api.openweathermap.org/data/2.5/weather?q=" + cityName + "&appid=" + API_KEY + "&units=" + units;
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void connect() {
        try {
            StringBuilder result = new StringBuilder();
            URL url = new URL(URL);
            URLConnection urlC = url.openConnection();
            BufferedReader bf = new BufferedReader(new InputStreamReader(urlC.getInputStream()));
            String line;
            while((line = bf.readLine()) != null) {
                result.append(line);
            }
            ObjectMapper mapper = new ObjectMapper();
            content = mapper.readValue(result.toString().getBytes(), Map.class);
            connectNextDays();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    private void connectNextDays() {
        try {
            StringBuilder result = new StringBuilder();
            URL url = new URL("https://api.openweathermap.org/data/2.5/forecast?q=" + cityName + "&appid=" + API_KEY + "&units=" + units);
            URLConnection urlC = url.openConnection();
            BufferedReader bf = new BufferedReader(new InputStreamReader(urlC.getInputStream()));
            String line;
            while((line = bf.readLine()) != null) {
                result.append(line);
            }
            ObjectMapper mapper = new ObjectMapper();
            Map daysMap = mapper.readValue(result.toString().getBytes(), Map.class);
            String[] arr = daysMap.get("list").toString().split(" ");
            int t1 = -1000000000, t2 = -1000000000, t3 = -1000000000, t4 = -1000000000; // max temp
            int tt1 = 1000000000, tt2 = 1000000000, tt3 = 1000000000, tt4 = 1000000000; // min temp
            int currentTemp = 0, currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
            String ico1 = "", ico2 = "", ico3 = "", ico4 = "", ico5 = "";
            String currentIconString = "";
            for(String s : arr) {
                if(s.startsWith("main={")) {
                    currentTemp = (int) Math.round(Double.parseDouble(s.substring(s.lastIndexOf('=') + 1, s.length() - 1)));
                } else if(s.startsWith("dt_txt")) {
                    int day = Integer.parseInt(s.substring(s.lastIndexOf('-') + 1));
                    if(currentDay + 1 == day) {
                        if(currentTemp > t1) {
                            t1 = currentTemp;
                            ico1 = currentIconString;
                        }
                        tt1 = Math.min(tt1, currentTemp);
                    } else if(currentDay + 2 == day) {
                        if(currentTemp > t2) {
                            t2 = currentTemp;
                            ico2 = currentIconString;
                        }
                        tt2 = Math.min(tt2, currentTemp);

                    } else if(currentDay + 3 == day) {
                        if(currentTemp > t3) {
                            t3 = currentTemp;
                            ico3 = currentIconString;
                        }
                        tt3 = Math.min(tt3, currentTemp);
                    } else if(currentDay + 4 == day) {
                        if(currentTemp > t4) {
                            t4 = currentTemp;
                            ico4 = currentIconString;
                        }
                        tt4 = Math.min(tt4, currentTemp);
                    }
                } else if(s.startsWith("icon")) {
                    currentIconString = s.substring(s.indexOf('=') + 1, s.indexOf('=') + 4);
                }
            }
            temp1.setText("" + t1 + "°/" + tt1 + "°");
            temp2.setText("" + t2 + "°/" + tt2 + "°");
            temp3.setText("" + t3 + "°/" + tt3 + "°");
            temp4.setText("" + t4 + "°/" + tt4 + "°");
            temp5.setText("Unknown");
            icon1.setImage(new Image("http://openweathermap.org/img/wn/" + ico1 + "@2x.png"));
            icon2.setImage(new Image("http://openweathermap.org/img/wn/" + ico2 + "@2x.png"));
            icon3.setImage(new Image("http://openweathermap.org/img/wn/" + ico3 + "@2x.png"));
            icon4.setImage(new Image("http://openweathermap.org/img/wn/" + ico4 + "@2x.png"));
            icon5.setImage(new Image("http://openweathermap.org/img/wn/" + ico4 + "@2x.png"));
        } catch(Exception e) {
            e.printStackTrace();
        }
    }


    private void onUserInput() {
        city.setOnKeyPressed(keyEvent -> {
            if(keyEvent.getCode() == KeyCode.ENTER) {
                String temp = URL;
                URL = "https://api.openweathermap.org/data/2.5/weather?q=" + city.getText() + "&appid=" + API_KEY + "&units=" + units;
                try {
                    connect();
                    loadContent();
                    cityName = city.getText();
                    connectNextDays();
                } catch(Exception e) {
                    URL = temp;
                }
            }
        });
    }

    private void loadContent() {

        if(units.equals("metric")) {
            unit.setText("M");
        } else {
            unit.setText("I");
        }

        /*
            Left menu - temp
         */

        Object sys = content.get("sys"), weather = content.get("weather");
        String[] strSys = sys.toString().replace("{", "").replace("}", "").split(", ");
        String[] strW = weather.toString().replace("{", "").replace("}", "").replace("]", "").replace("[", "").split(", ");

        int sunriseIndex = 0, sunsetIndex = 0, countryIndex = -1;
        for(int i = 0; i < strSys.length; i++) {
            if(strSys[i].startsWith("sunrise")) {
                sunriseIndex = i;
            } else if(strSys[i].startsWith("sunset")) {
                sunsetIndex = i;
            } else if(strSys[i].startsWith("country")) {
                countryIndex = i;
            }
        }

        if(countryIndex == -1) {
            String c = content.get("name").toString().substring(0, 2).toUpperCase();
            country.setText(c + " -");
        } else {
            country.setText(strSys[countryIndex].substring(strSys[countryIndex].indexOf("=") + 1) + " -");
        }
        city.setText(content.get("name").toString());

        description.setText("Status: " + strW[1].substring(strW[1].indexOf("=") + 1));
        String imageUrl = "http://openweathermap.org/img/wn/" + strW[3].substring(strW[3].indexOf("=") + 1) + "@2x.png";
        icon.setImage(new Image(imageUrl));
        //Main.mainStage.getIcons().add(new Image("src/icon.png"));

        /*
            Sunrise and sunset ( strSys[3], strSys[4] )
            Converting from unix timestamp to date
         */

        long sunriseTimeStamp = Long.parseLong(strSys[sunriseIndex].substring(strSys[sunriseIndex].indexOf("=") + 1));
        long sunsetTimeStamp = Long.parseLong(strSys[sunsetIndex].substring(strSys[sunsetIndex].indexOf("=") + 1));
        Date sunriseDate = new Date(sunriseTimeStamp * 1000), sunsetDate = new Date(sunsetTimeStamp * 1000); // milliseconds
        String sunriseString = sunriseDate.toString().substring(11, 16), sunsetString = sunsetDate.toString().substring(11, 16);
        sunrise.setText(sunriseString);
        sunset.setText(sunsetString);

        /*
            Right menu + temp
         */

        Object obj = content.get("main");
        String[] str = obj.toString().replace("{", "").replace("}", "").split(", ");

        /*
            str will store the values in order:
            temp, feels_like, temp_min, temp_max, pressure, humidity
         */

        double temp = Double.parseDouble(str[0].substring(str[0].indexOf("=") + 1));
        temperature.setText((int) Math.round(temp) + (units.equals("metric") ? "°C" : "°F"));

        double tempFeelsLike = Double.parseDouble(str[1].substring(str[1].indexOf("=") + 1));
        feelsLike.setText((int) Math.round(tempFeelsLike) + (units.equals("metric") ? "°C" : "°F"));

        double maxTemp = Double.parseDouble(str[3].substring(str[3].indexOf("=") + 1));
        double minTemp = Double.parseDouble(str[2].substring(str[2].indexOf("=") + 1));
        maxAndMin.setText( (int) Math.round(maxTemp) + "°C / " + (int) Math.round(minTemp) + (units.equals("metric") ? "°C" : "°F") );

        Object windObj = content.get("wind");
        String[] strWi = windObj.toString().replace("{", "").replace("}", "").split(", ");
        int windIndex = 0;
        for(int i = 0; i < strWi.length; i++) {
            if(strWi[i].startsWith("speed")) {
                windIndex = i;
                break;
            }
        }

        if(units.equals("metric")) {
            double windValue = Double.parseDouble(strWi[windIndex].substring(strWi[windIndex].indexOf("=") + 1)) * 3.6;
            wind.setText((int) Math.round(windValue) + " km/h");
        } else {
            double windValue = Double.parseDouble(strWi[windIndex].substring(strWi[windIndex].indexOf("=") + 1));
            wind.setText((int) Math.round(windValue) + " mph");
        }

        double pressureValue = Double.parseDouble(str[4].substring(str[4].indexOf("=") + 1));
        pressure.setText((int) Math.round(pressureValue) + " mb");

        double humidityValue = Double.parseDouble(str[5].substring(str[5].indexOf("=") + 1));
        humidity.setText((int) Math.round(humidityValue) + "%");

        double visibilityValue = Double.parseDouble(content.get("visibility").toString());
        visibilityValue /= 1000;
        visibilityValue = Math.floor(10 * visibilityValue) / 10;
        if(units.equals("imperial")) {
            visibilityValue /= 1.6;
            visibility.setText(visibilityValue + " miles");
        } else {
            visibility.setText(visibilityValue + " km");
        }

        int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        for(int i = 1; i <= 5; i++) {
            if(day >= 8) {
                day = 1;
            }
            switch(i) {
                case 1:
                    day1.setText("" + days[day++]);
                    break;
                case 2:
                    day2.setText("" + days[day++]);
                    break;
                case 3:
                    day3.setText("" + days[day++]);
                    break;
                case 4:
                    day4.setText("" + days[day++]);
                    break;
                case 5:
                    day5.setText("" + days[day++]);
                    break;
                default:
                    break;
            }
        }
    }

    public void changeUnit() {
        if(unit.getText().equals("M")) {
            loadUrl("imperial");
        } else {
            loadUrl("metric");
        }
        connect();
        loadContent();
    }

    public void close() {
        Main.mainStage.fireEvent(new WindowEvent(Main.mainStage, WindowEvent.WINDOW_CLOSE_REQUEST));
        Main.mainStage.close();
    }

    public void minimize() {
        Main.mainStage.setIconified(true);
    }

    public void refresh() {
        connect();
        loadContent();
    }

    public String getCityName() {
        return cityName;
    }

    public String getUnits() {
        return units;
    }

    private void setTime() {
        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            Calendar c = Calendar.getInstance();

            int day = c.get(Calendar.DAY_OF_WEEK);

            String hour = String.valueOf(c.get(Calendar.HOUR_OF_DAY));
            hour = hour.length() < 2 ? "0" + hour : hour;

            String minute = String.valueOf(c.get(Calendar.MINUTE));
            minute = minute.length() < 2 ? "0" + minute : minute;

            String second = String.valueOf(c.get(Calendar.SECOND));
            second = second.length() < 2 ? "0" + second : second;

            dayAndTime.setText(days[day - 1] + ", " + hour + ":" + minute + ":" + second);
        }), new KeyFrame(Duration.seconds(1))
        );
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setDraggable();
        loadUrl("");
        connect();
        loadContent();
        onUserInput();
        setTime();
    }
}