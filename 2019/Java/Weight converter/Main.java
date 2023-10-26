package com.jetbrains;

import java.lang.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import javax.swing.border.*;
import java.awt.event.*;


public class Main extends JFrame{

    private Main() {
        setLayout(new FlowLayout(FlowLayout.LEFT));

        JLabel space = new JLabel("                     ");
        add(space);

        JLabel label = new JLabel("  Weight Conversion Calculator  ");
        LineBorder line = new LineBorder(Color.decode("#264475"), 1, true); // color, thickness, rounded
        label.setBorder(line);
        label.setFont(label.getFont().deriveFont(16.0f));
        add(label, BorderLayout.CENTER);

        JLabel asgfa = new JLabel("                      ");
        asgfa.setFont(asgfa.getFont().deriveFont(16.0f));
        add(asgfa);

        JLabel label1 = new JLabel("( Enter one value to convert )");
        label1.setFont(new Font("Serif", Font.ITALIC, 16));
        add(label1);

        JLabel space2 = new JLabel("                                                                              ");
        add(space2);

        JLabel space3 = new JLabel("                                                                              ");
        add(space3);

        JLabel label2 = new JLabel("         Convert lbs to kg:");
        label2.setFont(label2.getFont().deriveFont(16.0f));
        add(label2);

        JLabel space4 = new JLabel("                                                  ");
        add(space4);

        JLabel feets = new JLabel("         Lbs:");
        feets.setFont(feets.getFont().deriveFont(16.0f));
        add(feets);

        JFormattedTextField feet = new JFormattedTextField();
        feet.setFont(feet.getFont().deriveFont(16.0f));
        feet.setColumns(6);
        feet.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        feet.setToolTipText("Enter \'lbs\' value");
        feet.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if(e.getKeyChar() == KeyEvent.VK_ENTER || e.getKeyChar() == '.' || e.getKeyChar() == KeyEvent.VK_0 || e.getKeyChar() == KeyEvent.VK_1 || e.getKeyChar() == KeyEvent.VK_2 || e.getKeyChar() == KeyEvent.VK_3 || e.getKeyChar() == KeyEvent.VK_4 || e.getKeyChar() == KeyEvent.VK_5 || e.getKeyChar() == KeyEvent.VK_6 || e.getKeyChar() == KeyEvent.VK_7 || e.getKeyChar() == KeyEvent.VK_NUM_LOCK || e.getKeyChar() == KeyEvent.VK_WINDOWS || e.getKeyChar() == KeyEvent.VK_ALT || e.getKeyChar() == KeyEvent.VK_8 || e.getKeyChar() == KeyEvent.VK_9  || e.getKeyChar() == KeyEvent.VK_ESCAPE || e.getKeyChar() == KeyEvent.VK_BACK_SPACE || e.getKeyChar() == KeyEvent.VK_TAB || e.getKeyChar() == KeyEvent.VK_CAPS_LOCK || e.getKeyChar() == KeyEvent.VK_CONTROL || e.getKeyChar() == KeyEvent.VK_F1 || e.getKeyChar() == KeyEvent.VK_F2 || e.getKeyChar() == KeyEvent.VK_F3 || e.getKeyChar() == KeyEvent.VK_F4 || e.getKeyChar() == KeyEvent.VK_F5 || e.getKeyChar() == KeyEvent.VK_F6 || e.getKeyChar() == KeyEvent.VK_F7 || e.getKeyChar() == KeyEvent.VK_F8 || e.getKeyChar() == KeyEvent.VK_F9 || e.getKeyChar() == KeyEvent.VK_F10 || e.getKeyChar() == KeyEvent.VK_F11 || e.getKeyChar() == KeyEvent.VK_F12 || e.getKeyChar() == KeyEvent.VK_INSERT || e.getKeyChar() == KeyEvent.VK_END || e.getKeyChar() == KeyEvent.VK_DELETE || e.getKeyChar() == KeyEvent.VK_HOME || e.getKeyChar() == KeyEvent.VK_PAGE_DOWN || e.getKeyChar() == KeyEvent.VK_PAGE_UP || e.getKeyChar() == KeyEvent.VK_PAUSE || e.getKeyChar() == KeyEvent.VK_SCROLL_LOCK || e.getKeyChar() == KeyEvent.VK_PRINTSCREEN) {
                    feet.setEditable(true);
                } else if(e.getKeyChar() == KeyEvent.VK_EXCLAMATION_MARK || e.getKeyChar() == KeyEvent.VK_DOLLAR  || e.getKeyChar() == KeyEvent.VK_ASTERISK  || e.getKeyChar() == KeyEvent.VK_LEFT_PARENTHESIS  || e.getKeyChar() == KeyEvent.VK_RIGHT_PARENTHESIS || e.getKeyChar() == KeyEvent.VK_DIVIDE  || e.getKeyChar() == KeyEvent.VK_COPY  || e.getKeyChar() == KeyEvent.VK_PASTE  || e.getKeyChar() == KeyEvent.VK_NUMBER_SIGN ){
                    feet.setEditable(false);
                } else {
                    feet.setEditable(false);
                }
            }
        });
        add(feet);


        JLabel space5 = new JLabel("                                                                              ");
        add(space5);

        JLabel label4 = new JLabel("         Convert kg to lbs:");
        label4.setFont(label4.getFont().deriveFont(16.0f));
        add(label4);

        JLabel space6 = new JLabel("                                                  ");
        add(space6);

        JLabel cm = new JLabel("         Kg:");
        cm.setFont(cm.getFont().deriveFont(16.0f));
        add(cm);

        JFormattedTextField feet2 = new JFormattedTextField();
        feet2.setFont(feet2.getFont().deriveFont(16.0f));
        feet2.setColumns(6);
        feet2.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        feet2.setToolTipText("Enter \'kg\' value");
        feet2.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if(e.getKeyChar() == KeyEvent.VK_ENTER || e.getKeyChar() == '.' || e.getKeyChar() == KeyEvent.VK_0 || e.getKeyChar() == KeyEvent.VK_1 || e.getKeyChar() == KeyEvent.VK_2 || e.getKeyChar() == KeyEvent.VK_3 || e.getKeyChar() == KeyEvent.VK_4 || e.getKeyChar() == KeyEvent.VK_5 || e.getKeyChar() == KeyEvent.VK_6 || e.getKeyChar() == KeyEvent.VK_7 || e.getKeyChar() == KeyEvent.VK_NUM_LOCK || e.getKeyChar() == KeyEvent.VK_WINDOWS || e.getKeyChar() == KeyEvent.VK_ALT || e.getKeyChar() == KeyEvent.VK_8 || e.getKeyChar() == KeyEvent.VK_9  || e.getKeyChar() == KeyEvent.VK_ESCAPE || e.getKeyChar() == KeyEvent.VK_BACK_SPACE || e.getKeyChar() == KeyEvent.VK_TAB || e.getKeyChar() == KeyEvent.VK_CAPS_LOCK || e.getKeyChar() == KeyEvent.VK_CONTROL || e.getKeyChar() == KeyEvent.VK_F1 || e.getKeyChar() == KeyEvent.VK_F2 || e.getKeyChar() == KeyEvent.VK_F3 || e.getKeyChar() == KeyEvent.VK_F4 || e.getKeyChar() == KeyEvent.VK_F5 || e.getKeyChar() == KeyEvent.VK_F6 || e.getKeyChar() == KeyEvent.VK_F7 || e.getKeyChar() == KeyEvent.VK_F8 || e.getKeyChar() == KeyEvent.VK_F9 || e.getKeyChar() == KeyEvent.VK_F10 || e.getKeyChar() == KeyEvent.VK_F11 || e.getKeyChar() == KeyEvent.VK_F12 || e.getKeyChar() == KeyEvent.VK_INSERT || e.getKeyChar() == KeyEvent.VK_END || e.getKeyChar() == KeyEvent.VK_DELETE || e.getKeyChar() == KeyEvent.VK_HOME || e.getKeyChar() == KeyEvent.VK_PAGE_DOWN || e.getKeyChar() == KeyEvent.VK_PAGE_UP || e.getKeyChar() == KeyEvent.VK_PAUSE || e.getKeyChar() == KeyEvent.VK_SCROLL_LOCK || e.getKeyChar() == KeyEvent.VK_PRINTSCREEN) {
                    feet2.setEditable(true);
                } else if(e.getKeyChar() ==  KeyEvent.VK_AT|| e.getKeyChar() == KeyEvent.VK_EXCLAMATION_MARK || e.getKeyChar() == KeyEvent.VK_DOLLAR  || e.getKeyChar() == KeyEvent.VK_ASTERISK  || e.getKeyChar() == KeyEvent.VK_LEFT_PARENTHESIS  || e.getKeyChar() == KeyEvent.VK_RIGHT_PARENTHESIS || e.getKeyChar() == KeyEvent.VK_DIVIDE  || e.getKeyChar() == KeyEvent.VK_COPY  || e.getKeyChar() == KeyEvent.VK_PASTE  || e.getKeyChar() == KeyEvent.VK_NUMBER_SIGN ){
                    feet2.setEditable(false);
                } else {
                    feet2.setEditable(false);
                }
            }
        });
        add(feet2);

        JLabel space7 = new JLabel("                                                                                                                                                    ");
        add(space7);

        JLabel space8 = new JLabel("                                                                                                                                                    ");
        add(space8);

        JLabel space9 = new JLabel("                     ");
        add(space9);

        JLabel ans = new JLabel("Answer: ");
        ans.setFont(ans.getFont().deriveFont(16.0f));
        ans.setVisible(false);

        JButton calc = new JButton("Calculate");
        calc.setFont(calc.getFont().deriveFont(16.0f));
        calc.setToolTipText("Transform units");
        calc.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        calc.setBorder(null);
        calc.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String a = feet.getText();
                String c = feet2.getText();
                if(a.length() > 0 && c.length() > 0) {
                    JOptionPane.showMessageDialog(null, "You can\'t calculate 2 values in the same time", "Error", JOptionPane.ERROR_MESSAGE);
                    feet.setText("");
                    feet2.setText("");
                } else if(c.length() > 0) {
                    double a1 = Double.parseDouble(c) * 2.2046;
                    ans.setText("Answer: " + a1 + " lbs.");
                    ans.setVisible(true);
                } else if(a.length() > 0) {
                    double a2 = (Double.parseDouble(a) / 2.2046);
                    ans.setText("Answer: " + a2 + " kg.");
                    ans.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(null, "You don\'t have enough elements to calculate", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        add(calc);


        JLabel space10 = new JLabel("                           ");
        add(space10);

        JButton clear = new JButton("Clear");
        clear.setFont(clear.getFont().deriveFont(16.0f));
        clear.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        clear.setToolTipText("Clear the values and answer");
        clear.setBorder(null);
        clear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ans.setText("");
                feet.setText("");
                feet2.setText("");
            }
        });
        add(clear);

        JLabel space11 = new JLabel("                                                                                                                                ");
        add(space11);

        JLabel space12 = new JLabel("                    ");
        add(space12);

        add(ans);
    }


    public static void main(String[] args) {
        Main gui = new Main();
        gui.setVisible(true);
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gui.setName("Converter");
        gui.setTitle("Weight converter");
        gui.setResizable(false);
        gui.setLocale(Locale.ENGLISH);
        gui.setSize(400,400);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int height = screenSize.height;
        int width = screenSize.width;
        gui.setLocation(width/2-gui.getSize().width/2, height/2-gui.getSize().height/2);
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel"); //com.sun.java.swing.plaf.windows.WindowsLookAndFeel
        }
        catch (UnsupportedLookAndFeelException e) {/* handle exception */ } catch (ClassNotFoundException e) {/* handle exception */} catch (InstantiationException e) {/* handle exception */} catch (IllegalAccessException e) {/* handle exception */}
    }

}

