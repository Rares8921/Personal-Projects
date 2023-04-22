package com.company;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class Main extends JFrame {

    private static JLabel number;

    private Main() {
        FlowLayout flow = new FlowLayout(FlowLayout.LEFT, 11, 2);
        setLayout(flow);

        getContentPane().setBackground(Color.decode("#1b1c1c"));

        Border line = BorderFactory.createLineBorder(Color.decode("#1b1c1c"));
        UIManager.put("RadioButton.border", line);
        UIManager.put("OptionPane.background", Color.decode("#1b1c1c"));
        UIManager.put("OptionPane.messageBackground", Color.decode("#1b1c1c"));
        UIManager.put("OptionPane.foreground", Color.WHITE);
        UIManager.put("OptionPane.messageForeground", Color.WHITE);
        UIManager.put("Panel.background", Color.decode("#1b1c1c"));
        UIManager.put("TextArea.selectionBackground ", new Color(0, 0, 0, 0));
        UIManager.put("TextArea.selectionForeground", Color.WHITE);

        //JLabel space = new JLabel("");
        //add(space);

        JPanel txt = new JPanel();
        txt.setBackground(Color.decode("#1b1c1c"));
        txt.setForeground(Color.WHITE);
        txt.setLayout(new FlowLayout(FlowLayout.CENTER, 11, 8));

        JLabel label = new JLabel("<HTML><U>Create strong passwords with Password Generator </U></HTML>");
        label.setFont(label.getFont().deriveFont(16.0f));
        label.setForeground(Color.WHITE);
        label.setBackground(Color.decode("#1b1c1c"));
        txt.add(label);
        add(txt);

        JLabel space1 = new JLabel("                                                                                                ");
        add(space1);

        JLabel length = new JLabel("Pass length ( 8 - 32 ): ");
        length.setFont(length.getFont().deriveFont(15.0f));
        length.setForeground(Color.WHITE);
        length.setBackground(Color.decode("#1b1c1c"));
        add(length);

        JSlider slider = new JSlider(JSlider.HORIZONTAL, 8, 32, 12);
        slider.setMajorTickSpacing(8);
        slider.setPaintTicks(true);
        slider.setMinorTickSpacing(1);
        slider.setPaintLabels(true);
        slider.setForeground(Color.WHITE);
        slider.setBackground(Color.decode("#1b1c1c"));
        slider.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        slider.addChangeListener(e -> number.setText("( " + slider.getValue() + " )"));
        add(slider);

        number = new JLabel("( 12 )");
        number.setFont(number.getFont().deriveFont(15.0f));
        number.setForeground(Color.WHITE);
        number.setBackground(Color.decode("#1b1c1c"));
        add(number);

        MyRadioButton upperLetters = new MyRadioButton("Uppercase letters");
        upperLetters.setFont(upperLetters.getFont().deriveFont(15.0f));
        upperLetters.setBorder(null);
        upperLetters.setForeground(Color.WHITE);
        upperLetters.setBackground(Color.decode("#1b1c1c"));
        add(upperLetters);

        JLabel s1 = new JLabel("                                                        ");
        add(s1);

        MyRadioButton lowerLetters = new MyRadioButton("Lowercase letters");
        lowerLetters.setFont(lowerLetters.getFont().deriveFont(15.0f));
        lowerLetters.setForeground(Color.WHITE);
        lowerLetters.setBorder(null);
        lowerLetters.setBackground(Color.decode("#1b1c1c"));
        add(lowerLetters);

        JLabel s2 = new JLabel("                                                              ");
        add(s2);

        MyRadioButton numbers = new MyRadioButton("Numbers");
        numbers.setFont(numbers.getFont().deriveFont(15.0f));
        numbers.setForeground(Color.WHITE);
        numbers.setBorder(null);
        numbers.setBackground(Color.decode("#1b1c1c"));
        add(numbers);

        JLabel s3 = new JLabel("                                                                                     ");
        add(s3);

        MyRadioButton symbols = new MyRadioButton("Symbols");
        symbols.setFont(symbols.getFont().deriveFont(15.0f));
        symbols.setForeground(Color.WHITE);
        symbols.setBackground(Color.decode("#1b1c1c"));
        symbols.setBorder(null);
        add(symbols);

        JLabel s4 = new JLabel("                                                                              ");
        add(s4);

        JLabel s5 = new JLabel("                                           ");
        add(s5);

        MyButton generate = new MyButton("Generate") {
            @Override
            public JToolTip createToolTip() {
                return (new CustomJToolTip(this));
            }
        };
        generate.setForeground(Color.WHITE);
        generate.setBackground(Color.decode("#1b1c1c"));
        generate.setFont(generate.getFont().deriveFont(16.0f));
        generate.setToolTipText("  Generate password");
        generate.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        generate.setHoverBackgroundColor(Color.decode("#252626"));
        generate.setPressedBackgroundColor(Color.decode("#2d2e2e"));
        generate.setOpaque(false);
        generate.setFocusPainted(false);
        generate.setBorderPainted(false);
        generate.setContentAreaFilled(false);
        generate.addActionListener(e -> {
            if(lowerLetters.isSelected() || upperLetters.isSelected() || numbers.isSelected() || symbols.isSelected()) {
                String lower = "abcdefghijklmnopqrstuvwxyz";
                String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
                String numbers1 = "1234567890";
                String symbols1 = "!@#$%^&*()_][{}";
                String pass = "";
                if (lowerLetters.isSelected()) {
                    pass += lower;
                }
                if (upperLetters.isSelected()) {
                    pass += upper;
                }
                if (numbers.isSelected()) {
                    pass += numbers1;
                }
                if (symbols.isSelected()) {
                    pass += symbols1;
                }
                int max = pass.length();
                int min = 1;
                int range = max - min + 1;
                String finalPass = "";
                for (int i = 1; i <= slider.getValue(); i++) {
                    int index = (int) (Math.random() * range) + min;
                    finalPass = finalPass.concat("" + pass.charAt(index - 1));
                }
                JTextArea text = new JTextArea(finalPass) {
                    @Override
                    public JToolTip createToolTip() {
                        return (new CustomJToolTip(this));
                    }
                };
                text.setFont(text.getFont().deriveFont(16.0f));
                text.setForeground(Color.WHITE);
                text.setEditable(false);
                text.setSelectionColor(Color.decode("#2d2e2e"));
                text.setBorder(null);
                text.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                text.setToolTipText(" Copy text to clipboard ");
                text.addFocusListener(new FocusListener() {
                    @Override
                    public void focusGained(FocusEvent e) {
                    }

                    @Override
                    public void focusLost(FocusEvent e) {
                        StringSelection stringSelection = new StringSelection(text.getText());
                        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                        clipboard.setContents(stringSelection, null);
                    }
                });
                text.setBackground(Color.decode("#1b1c1c"));

                JPanel panel = new JPanel();
                panel.setBackground(Color.decode("#1b1c1c"));
                panel.setForeground(Color.WHITE);
                FlowLayout layout = new FlowLayout(FlowLayout.CENTER, 1, 15);
                panel.setLayout(layout);
                panel.add(text);

                JOptionPane.showOptionDialog(null, panel, "Your password", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, new Object[]{}, null);
            }
        });
        add(generate);

    }

    public static void main(String[] args) {
        Main gui = new Main();
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gui.setVisible(true);
        gui.setResizable(false);
        gui.setTitle("Password Generator");
        gui.setSize(450, 270);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int height = screenSize.height;
        int width = screenSize.width;
        gui.setLocation(width / 2 - gui.getSize().width / 2, height / 2 - gui.getSize().height / 2);
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            //UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

     static class MyButton extends JButton {

        private Color hoverBackgroundColor;
        private Color pressedBackgroundColor;

        public MyButton(String text) {
            super(text);
            super.setContentAreaFilled(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (getModel().isPressed()) {
                g.setColor(pressedBackgroundColor);
            } else if (getModel().isRollover()) {
                g.setColor(hoverBackgroundColor);
            } else {
                g.setColor(getBackground());
            }
            g.fillRect(0, 0, getWidth(), getHeight());
            super.paintComponent(g);
        }

        @Override
        public void setContentAreaFilled(boolean b) {
        }

        public void setHoverBackgroundColor(Color hoverBackgroundColor) {
            this.hoverBackgroundColor = hoverBackgroundColor;
        }


        public void setPressedBackgroundColor(Color pressedBackgroundColor) {
            this.pressedBackgroundColor = pressedBackgroundColor;
        }
    }

    private static class CustomJToolTip extends JToolTip {
        public CustomJToolTip(JComponent component) {
            super();
            setComponent(component);
            setBackground(Color.decode("#2d2e2e"));
            setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
            setPreferredSize(new Dimension(130,30));
            setBorder(BorderFactory.createLineBorder(Color.decode("#1b1c1c")));
            setForeground(Color.WHITE);
        }
    }

    private static class MyRadioButton extends JRadioButton {

        public MyRadioButton(String text) {
            super(text);
            super.setContentAreaFilled(false);
        }

        @Override
        protected void paintBorder(Graphics g) {
            if(getModel().isPressed()) {
               setBorder(null);
               setBorderPainted(false);
            }
        }
    }

}
