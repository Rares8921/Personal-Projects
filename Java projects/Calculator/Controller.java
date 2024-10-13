package sample;

import javafx.event.EventHandler;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private TextField inputField;

    @FXML
    private Label history;

    @FXML
    private Button closeButton;

    @FXML
    private Button minimizeButton;

    @FXML
    private AnchorPane parent;

    private String calc = "", sign = "";
    private int oldCaretPosition;

    private double xOffSet;
    private double yOffSet;

    public void validate() {
        inputField.textProperty().addListener((observable, oldValue, newValue) -> {
            //^[-0-9]+
            if (!newValue.matches("^[-0-9,]+$")) {
                inputField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }

    public void lastPosition() {
        inputField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                oldCaretPosition = inputField.getCaretPosition();
            }
        });
    }

    public void changeSign() {
        StringBuilder newText = new StringBuilder(inputField.getText());
        if(newText.length() > 0 && newText.toString().charAt(0) == '-') {
            newText.deleteCharAt(0);
            oldCaretPosition--;
        } else {
            newText.insert(0, "-");
            oldCaretPosition++;
        }
        inputField.setText(newText.toString());
    }

    public void zero() {
        StringBuilder newText = new StringBuilder(inputField.getText());
        newText.insert(oldCaretPosition, "0");
        inputField.setText(newText.toString());
        oldCaretPosition++;
    }

    public void one() {
        StringBuilder newText = new StringBuilder(inputField.getText());
        newText.insert(oldCaretPosition, "1");
        inputField.setText(newText.toString());
        oldCaretPosition++;
    }
    public void two() {
        StringBuilder newText = new StringBuilder(inputField.getText());
        newText.insert(oldCaretPosition, "2");
        inputField.setText(newText.toString());
        oldCaretPosition++;
    }

    public void three() {
        StringBuilder newText = new StringBuilder(inputField.getText());
        newText.insert(oldCaretPosition, "3");
        inputField.setText(newText.toString());
        oldCaretPosition++;
    }
    public void four() {
        StringBuilder newText = new StringBuilder(inputField.getText());
        newText.insert(oldCaretPosition, "4");
        inputField.setText(newText.toString());
        oldCaretPosition++;
    }

    public void five() {
        StringBuilder newText = new StringBuilder(inputField.getText());
        newText.insert(oldCaretPosition, "5");
        inputField.setText(newText.toString());
        oldCaretPosition++;
    }
    public void six() {
        StringBuilder newText = new StringBuilder(inputField.getText());
        newText.insert(oldCaretPosition, "6");
        inputField.setText(newText.toString());
        oldCaretPosition++;
    }

    public void seven() {
        StringBuilder newText = new StringBuilder(inputField.getText());
        newText.insert(oldCaretPosition, "7");
        inputField.setText(newText.toString());
        oldCaretPosition++;
    }

    public void eight() {
        StringBuilder newText = new StringBuilder(inputField.getText());
        newText.insert(oldCaretPosition, "8");
        inputField.setText(newText.toString());
        oldCaretPosition++;
    }
    public void nine() {
        StringBuilder newText = new StringBuilder(inputField.getText());
        newText.insert(oldCaretPosition, "9");
        inputField.setText(newText.toString());
        oldCaretPosition++;
    }

    public void deleteLastCharacter() {
        StringBuilder newText = new StringBuilder(inputField.getText());
        if(newText.toString().length() > 0 && oldCaretPosition != 0) {
            newText.deleteCharAt(oldCaretPosition - 1);
            inputField.setText(newText.toString());
            oldCaretPosition--;
        }
    }

    public void clearEntry() {
        inputField.setText("");
        oldCaretPosition = 0;
    }

    public void clear() {
        clearEntry();
        history.setText("");
        sign = "";
        calc = "";
    }

    public void makeRational() {
        StringBuilder newText = new StringBuilder(inputField.getText());
        if(newText.toString().contains(",")) {
            inputField.setText(newText.toString().replace(",", ""));
            oldCaretPosition--;
        } else {
            newText.insert(oldCaretPosition, ",");
            inputField.setText(newText.toString());
            oldCaretPosition++;
        }
    }

    public void powerOf2() {
        switch (sign) {
            case "+":
                addition();
                break;
            case "-":
                subtraction();
                break;
            case "*":
                multiplication();
                break;
            case "/":
                division();
                break;
            case "%":
                percentage();
                break;
            default:
                System.out.print("");
                break;
        }
        try {
            String value = inputField.getText();
            double ans = Math.pow(Double.parseDouble(value.replace(",", ".")), 2);
            history.setText(value + "^2");
            if ((int) ans == ans) {
                inputField.setText(Integer.toString((int) ans).replace(".", ","));
            } else {
                inputField.setText(Double.toString(ans).replace(".", ","));
            }
        } catch(NumberFormatException e) {
            history.setText("0^2");
            inputField.setText("0");
        }
        oldCaretPosition = inputField.getText().length() - 1;
    }

    public void powerOfX() throws IOException {
        switch (sign) {
            case "+":
                addition();
                break;
            case "-":
                subtraction();
                break;
            case "*":
                multiplication();
                break;
            case "/":
                division();
                break;
            case "%":
                percentage();
                break;
            default:
                System.out.print("");
                break;
        }
        InputBox box = new InputBox();
        Main.title = "Power Input";
        box.display();
        double Power = InputBox.getNumber();
        try {
            String value = inputField.getText();
            double ans = Math.pow(Double.parseDouble(value.replace(",", ".")), Power);
            if((int) Power == Power) {
                int pow = (int) Power;
                history.setText(value + "^" + pow);
            } else {
                history.setText(value + "^" + Power);
            }
            if ((int) ans == ans) {
                inputField.setText(Integer.toString((int) ans).replace(".", ","));
            } else {
                inputField.setText(Double.toString(ans).replace(".", ","));
            }
        } catch(NumberFormatException e) {
            if((int) Power == Power) {
                int pow = (int) Power;
                history.setText("0^" + pow);
            } else {
                history.setText("0^" + Power);
            }
            inputField.setText("0");
        }
        oldCaretPosition = inputField.getText().length() - 1;
    }

    public void squareRoot() {
        switch (sign) {
            case "+":
                addition();
                break;
            case "-":
                subtraction();
                break;
            case "*":
                multiplication();
                break;
            case "/":
                division();
                break;
            case "%":
                percentage();
                break;
            default:
                System.out.print("");
                break;
        }
        try {
            String value = inputField.getText();
            history.setText("√(" + value + ")");
            double ans = Math.sqrt(Double.parseDouble(value.replace(",", ".")));
            if ((int) ans == ans) {
                inputField.setText(Integer.toString((int) ans).replace(".", ","));
            } else {
                inputField.setText(Double.toString(ans).replace(".", ","));
            }
        }  catch(NumberFormatException e) {
            history.setText("√(0)");
            inputField.setText("0");
        }
        oldCaretPosition = inputField.getText().length() - 1;
    }

    static double ythRoot(double A, int N)  {
        double pre = Math.random() % 10;
        double eps = 0.001;
        double delX = 2147483647; // INT_MAX
        double xK = 0.0;

        while(delX > eps) {
            // newton's method
            xK = ((N - 1.0) * pre + A / Math.pow(pre, N - 1)) / (double) N;
            delX = Math.abs(xK - pre);
            pre = xK;
        }
        return xK;
    }

    public void squareRootYthBase() throws IOException {
        switch (sign) {
            case "+":
                addition();
                break;
            case "-":
                subtraction();
                break;
            case "*":
                multiplication();
                break;
            case "/":
                division();
                break;
            case "%":
                percentage();
                break;
            default:
                System.out.print("");
                break;
        }
        InputBox box = new InputBox();
        Main.title = "Base Input";
        box.display();
        int base = (int) InputBox.getNumber();
        try {
            String value = inputField.getText();
            history.setText(base + "√(" + value + ")");
            double ans = ythRoot(Double.parseDouble(value.replace(",", ".")), base);
            ans = Math.round(ans * 1000.0) / 1000.0;
            if ((int) ans == ans) {
                inputField.setText(Integer.toString((int) ans).replace(".", ","));
            } else {
                inputField.setText(Double.toString(ans).replace(".", ","));
            }
        } catch(NumberFormatException e) {
            history.setText(base + "√(0)");
            inputField.setText("0");
        }
        oldCaretPosition = inputField.getText().length() - 1;
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

    public void close() {
        Main.stage.close();
    }

    public void minimize() {
        Main.stage.setIconified(true);
    }

    public void addition() {
        if(calc.length() > 0 && sign.equals("+")) {
            try {
                oldCaretPosition = inputField.getText().length() - 1;
                double a = Double.parseDouble(calc.replace(",", "."));
                double b = Double.parseDouble(inputField.getText().replace(",", "."));
                calc = inputField.getText();
                double sum = a + b;
                String hist = "";
                if ((int) a == a) {
                    hist = hist.concat((int) a + " + ");
                } else {
                    hist = hist.concat(a + " + ");
                }
                if ((int) b == b) {
                    hist = hist.concat("" + (int) b);
                } else {
                    hist = hist.concat("" + b);
                }
                history.setText(hist.replace(".", ","));
                if ((int) sum == sum) {
                    inputField.setText(Integer.toString((int) sum));
                } else {
                    inputField.setText(Double.toString(sum).replace(".", ","));
                }
            } catch(NumberFormatException e) {
                // invalid input
            }
        } else {
            switch (sign) {
                case "/":
                    division();
                    break;
                case "-":
                    subtraction();
                    break;
                case "*":
                    multiplication();
                    break;
                case "%":
                    percentage();
                    break;
                default:
                    System.out.print("");
                    break;
            }
            oldCaretPosition = 0;
            calc = inputField.getText();
            history.setText(calc + " + ");
            inputField.setText("");
        }
        sign = "+";
    }

    public void subtraction() {
        if(calc.length() > 0 && sign.equals("-")) {
            try {
                oldCaretPosition = inputField.getText().length() - 1;
                double a = Double.parseDouble(calc.replace(",", "."));
                double b = Double.parseDouble(inputField.getText().replace(",", "."));
                calc = inputField.getText();
                double subtract = a - b;
                String hist = "";
                if ((int) a == a) {
                    hist = hist.concat((int) a + " - ");
                } else {
                    hist = hist.concat(a + " - ");
                }
                if ((int) b == b) {
                    hist = hist.concat("" + (int) b);
                } else {
                    hist = hist.concat("" + b);
                }
                history.setText(hist.replace(".", ","));
                if ((int) subtract == subtract) {
                    inputField.setText(Integer.toString((int) subtract));
                } else {
                    inputField.setText(Double.toString(subtract).replace(".", ","));
                }
            } catch(NumberFormatException e) {
                // invalid input
            }
        } else {
            switch (sign) {
                case "+":
                    addition();
                    break;
                case "/":
                    division();
                    break;
                case "*":
                    multiplication();
                    break;
                case "%":
                    percentage();
                    break;
                default:
                    System.out.print("");
                    break;
            }
            oldCaretPosition = 0;
            calc = inputField.getText();
            history.setText(calc + " - ");
            inputField.setText("");
        }
        sign = "-";
    }

    public void multiplication() {
        if(calc.length() > 0 && sign.equals("*")) {
            try {
                oldCaretPosition = inputField.getText().length() - 1;
                double a = Double.parseDouble(calc.replace(",", "."));
                double b = Double.parseDouble(inputField.getText().replace(",", "."));
                calc = inputField.getText();
                double multiply = a * b;
                String hist = "";
                if ((int) a == a) {
                    hist = hist.concat((int) a + " x ");
                } else {
                    hist = hist.concat(a + " x ");
                }
                if ((int) b == b) {
                    hist = hist.concat("" + (int) b);
                } else {
                    hist = hist.concat("" + b);
                }
                history.setText(hist.replace(".", ","));
                if ((int) multiply == multiply) {
                    inputField.setText(Integer.toString((int) multiply));
                } else {
                    inputField.setText(Double.toString(multiply).replace(".", ","));
                }
            } catch(NumberFormatException e) {
                // invalid input
            }
        } else {
            switch (sign) {
                case "+":
                    addition();
                    break;
                case "-":
                    subtraction();
                    break;
                case "/":
                    division();
                    break;
                case "%":
                    percentage();
                    break;
                default:
                    System.out.print("");
                    break;
            }
            oldCaretPosition = 0;
            calc = inputField.getText();
            history.setText(calc + " x ");
            inputField.setText("");
        }
        sign = "*";
    }

    public void division() {
        if(calc.length() > 0 && sign.equals("/")) {
            try {
                oldCaretPosition = inputField.getText().length() - 1;
                double a = Double.parseDouble(calc.replace(",", "."));
                double b = Double.parseDouble(inputField.getText().replace(",", "."));
                double divide = a / b;
                calc = inputField.getText();
                String hist = "";
                if ((int) a == a) {
                    hist = hist.concat((int) a + " / ");
                } else {
                    hist = hist.concat(a + " / ");
                }
                if ((int) b == b) {
                    hist = hist.concat("" + (int) b);
                } else {
                    hist = hist.concat("" + b);
                }
                history.setText(hist.replace(".", ","));
                if ((int) divide == divide) {
                    inputField.setText(Integer.toString((int) divide));
                } else {
                    inputField.setText(Double.toString(divide).replace(".", ","));
                }
            } catch(Exception e) {
                // invalid input && division by 0
            }
        } else {
            switch (sign) {
                case "+":
                    addition();
                    break;
                case "-":
                    subtraction();
                    break;
                case "*":
                    multiplication();
                    break;
                case "%":
                    percentage();
                    break;
                default:
                    System.out.print("");
                    break;
            }
            oldCaretPosition = 0;
            calc = inputField.getText();
            history.setText(calc + " / ");
            inputField.setText("");
        }
        sign = "/";
    }

    public void percentage() {
        if(calc.length() > 0 && sign.equals("%")) {
            try {
                oldCaretPosition = inputField.getText().length() - 1;
                double a = Double.parseDouble(calc.replace(",", "."));
                double b = Double.parseDouble(inputField.getText().replace(",", "."));
                double percent = (100 * a) / b;
                calc = inputField.getText();
                String hist = "";
                if ((int) a == a) {
                    hist = hist.concat((int) a + " % ");
                } else {
                    hist = hist.concat(a + " % ");
                }
                if ((int) b == b) {
                    hist = hist.concat("" + (int) b);
                } else {
                    hist = hist.concat("" + b);
                }
                history.setText(hist.replace(".", ","));
                if ((int) percent == percent) {
                    inputField.setText(Integer.toString((int) percent));
                } else {
                    inputField.setText(Double.toString(percent).replace(".", ","));
                }
            } catch(Exception e) {
                // invalid input && division by 0
            }
        } else {
            switch (sign) {
                case "+":
                    addition();
                    break;
                case "-":
                    subtraction();
                    break;
                case "*":
                    multiplication();
                    break;
                case "/":
                    division();
                    break;
                default:
                    System.out.print("");
                    break;
            }
            oldCaretPosition = 0;
            calc = inputField.getText();
            history.setText(calc + " % ");
            inputField.setText("");
        }
        sign = "%";
    }

    public void answer() {
        switch(sign) {
            case "+":
                try {
                    oldCaretPosition = inputField.getText().length() - 1;
                    double a = Double.parseDouble(calc.replace(",", "."));
                    double b = Double.parseDouble(inputField.getText().replace(",", "."));
                    calc = inputField.getText();
                    double sum = a + b;
                    String hist = "";
                    if ((int) a == a) {
                        hist = hist.concat((int) a + " + ");
                    } else {
                        hist = hist.concat(a + " + ");
                    }
                    if ((int) b == b) {
                        hist = hist.concat("" + (int) b);
                    } else {
                        hist = hist.concat("" + b);
                    }
                    history.setText(hist.replace(".", ","));
                    if ((int) sum == sum) {
                        inputField.setText(Integer.toString((int) sum));
                    } else {
                        inputField.setText(Double.toString(sum).replace(".", ","));
                    }
                } catch(NumberFormatException e) {
                    // invalid input
                }
                break;
            case "-":
                try {
                    oldCaretPosition = inputField.getText().length() - 1;
                    double a = Double.parseDouble(calc.replace(",", "."));
                    double b = Double.parseDouble(inputField.getText().replace(",", "."));
                    calc = inputField.getText();
                    double subtract = a - b;
                    String hist = "";
                    if ((int) a == a) {
                        hist = hist.concat((int) a + " - ");
                    } else {
                        hist = hist.concat(a + " - ");
                    }
                    if ((int) b == b) {
                        hist = hist.concat("" + (int) b);
                    } else {
                        hist = hist.concat("" + b);
                    }
                    history.setText(hist.replace(".", ","));
                    if ((int) subtract == subtract) {
                        inputField.setText(Integer.toString((int) subtract));
                    } else {
                        inputField.setText(Double.toString(subtract).replace(".", ","));
                    }
                } catch(NumberFormatException e) {
                    // invalid input
                }
                break;
            case "*":
                try {
                    oldCaretPosition = inputField.getText().length() - 1;
                    double a = Double.parseDouble(calc.replace(",", "."));
                    double b = Double.parseDouble(inputField.getText().replace(",", "."));
                    calc = inputField.getText();
                    double multiply = a * b;
                    String hist = "";
                    if ((int) a == a) {
                        hist = hist.concat((int) a + " x ");
                    } else {
                        hist = hist.concat(a + " x ");
                    }
                    if ((int) b == b) {
                        hist = hist.concat("" + (int) b);
                    } else {
                        hist = hist.concat("" + b);
                    }
                    history.setText(hist.replace(".", ","));
                    if ((int) multiply == multiply) {
                        inputField.setText(Integer.toString((int) multiply));
                    } else {
                        inputField.setText(Double.toString(multiply).replace(".", ","));
                    }
                } catch(NumberFormatException e) {
                    // invalid input
                }
                break;
            case "/":
                try {
                    oldCaretPosition = inputField.getText().length() - 1;
                    double a = Double.parseDouble(calc.replace(",", "."));
                    double b = Double.parseDouble(inputField.getText().replace(",", "."));
                    double divide = a / b;
                    calc = inputField.getText();
                    String hist = "";
                    if ((int) a == a) {
                        hist = hist.concat((int) a + " / ");
                    } else {
                        hist = hist.concat(a + " / ");
                    }
                    if ((int) b == b) {
                        hist = hist.concat("" + (int) b);
                    } else {
                        hist = hist.concat("" + b);
                    }
                    history.setText(hist.replace(".", ","));
                    if ((int) divide == divide) {
                        inputField.setText(Integer.toString((int) divide));
                    } else {
                        inputField.setText(Double.toString(divide).replace(".", ","));
                    }
                } catch(Exception e) {
                    // invalid input && division by 0
                }
                break;
            case "%":
                try {
                    oldCaretPosition = inputField.getText().length() - 1;
                    double a = Double.parseDouble(calc.replace(",", "."));
                    double b = Double.parseDouble(inputField.getText().replace(",", "."));
                    double percent = (100 * a) / b;
                    calc = inputField.getText();
                    String hist = "";
                    if ((int) a == a) {
                        hist = hist.concat((int) a + " % ");
                    } else {
                        hist = hist.concat(a + " % ");
                    }
                    if ((int) b == b) {
                        hist = hist.concat("" + (int) b);
                    } else {
                        hist = hist.concat("" + b);
                    }
                    history.setText(hist.replace(".", ","));
                    if ((int) percent == percent) {
                        inputField.setText(Integer.toString((int) percent));
                    } else {
                        inputField.setText(Double.toString(percent).replace(".", ","));
                    }
                } catch(Exception e) {
                    // invalid input && division by 0
                }
                break;
            default:
                break;
        }
        sign = "";
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // ********* BUTTONS' TOOLTIPS *********

        Tooltip minimize = new Tooltip("Minimize");
        minimize.setStyle("-fx-background-color: #464646; -fx-font-size: 14px");
        minimizeButton.setTooltip(minimize);

        Tooltip close = new Tooltip("Close");
        close.setStyle("-fx-background-color: #464646; -fx-font-size: 14px");
        closeButton.setTooltip(close);

        // ********* DRAGGABLE FUNCTIONALITY *********
        setDraggable();

        // ********* KEY SHORTCUTS *********

        parent.setOnKeyPressed( event -> {
            if(event.getCode() == KeyCode.EQUALS && event.isShiftDown()) {
                addition();
            }  else if(event.getCode() == KeyCode.MINUS && event.isShiftDown()) {
                subtraction();
            } else if(event.getCode() == KeyCode.MINUS) {
                changeSign();
            } else if(event.getCode() == KeyCode.X) {
                multiplication();
            } else if(event.getCode() == KeyCode.SLASH) {
                division();
            } else if(event.getCode() == KeyCode.DIGIT5 && event.isShiftDown()) {
                percentage();
            } else if(event.isShiftDown() && event.getCode() == KeyCode.C) {
                clearEntry();
            } else if(event.getCode() == KeyCode.C) {
                clear();
            } else if(event.isShiftDown() && event.getCode() == KeyCode.P) {
                try {
                    powerOfX();
                } catch(IOException e) {
                    // nothing
                }
            } else if(event.getCode() == KeyCode.P) {
                powerOf2();
            } else if(event.isShiftDown() && event.getCode() == KeyCode.S) {
                try {
                    squareRootYthBase();
                } catch(IOException e) {
                    // nothing
                }
            } else if(event.getCode() == KeyCode.S) {
                squareRoot();
            } else if(event.getCode() == KeyCode.EQUALS) {
                answer();
            } else if(event.getCode() == KeyCode.DIGIT0) {
                zero();
            } else if(event.getCode() == KeyCode.DIGIT1) {
                one();
            } else if(event.getCode() == KeyCode.DIGIT2) {
                two();
            } else if(event.getCode() == KeyCode.DIGIT3) {
                three();
            } else if(event.getCode() == KeyCode.DIGIT4) {
                four();
            } else if(event.getCode() == KeyCode.DIGIT5) {
                five();
            } else if(event.getCode() == KeyCode.DIGIT6) {
                six();
            } else if(event.getCode() == KeyCode.DIGIT7) {
                seven();
            } else if(event.getCode() == KeyCode.DIGIT8) {
                eight();
            } else if(event.getCode() == KeyCode.DIGIT9) {
                nine();
            } else if(event.getCode() == KeyCode.COMMA) {
                makeRational();
            } else if(event.getCode() == KeyCode.BACK_SPACE) {
                deleteLastCharacter();
            }
        });

        EventHandler<KeyEvent> handler = new EventHandler<KeyEvent>() {

            private boolean willConsume = false;

            @Override
            public void handle(KeyEvent event) {

                if (willConsume) {
                    event.consume();
                }

                if (event.getCode() == KeyCode.MINUS || event.getCode() == KeyCode.COMMA) {
                    if (event.getEventType() == KeyEvent.KEY_PRESSED) {
                        willConsume = true;
                    } else if (event.getEventType() == KeyEvent.KEY_RELEASED) {
                        willConsume = false;
                    }
                }
            }

        };

        inputField.addEventFilter(KeyEvent.ANY, handler);

        inputField.setOnKeyPressed( event -> {
            if(event.getCode() == KeyCode.EQUALS && event.isShiftDown()) {
                addition();
            }  else if(event.getCode() == KeyCode.MINUS && event.isShiftDown()) {
                subtraction();
            } else if(event.getCode() == KeyCode.MINUS) {
                changeSign();
            }  else if(event.getCode() == KeyCode.X) {
                multiplication();
            } else if(event.getCode() == KeyCode.SLASH) {
                division();
            } else if(event.getCode() == KeyCode.DIGIT5 && event.isShiftDown()) {
                percentage();
            } else if(event.isShiftDown() && event.getCode() == KeyCode.C) {
                clearEntry();
            } else if(event.getCode() == KeyCode.C) {
                clear();
            } else if(event.isShiftDown() && event.getCode() == KeyCode.P) {
                try {
                    powerOfX();
                } catch(IOException e) {
                    // nothing
                }
            } else if(event.getCode() == KeyCode.P) {
                powerOf2();
            } else if(event.isShiftDown() && event.getCode() == KeyCode.S) {
                try {
                    squareRootYthBase();
                } catch(IOException e) {
                    // nothing
                }
            } else if(event.getCode() == KeyCode.S) {
                squareRoot();
            } else if(event.getCode() == KeyCode.EQUALS) {
                answer();
            } else if(event.getCode() == KeyCode.COMMA) {
                oldCaretPosition = inputField.getCaretPosition();
                makeRational();
                inputField.positionCaret(oldCaretPosition);
            }
        });
    }
}
