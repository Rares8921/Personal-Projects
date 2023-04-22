package com.company;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.LinkedList;
import java.util.Locale;

public class Main extends JFrame{

    private Main() {
        setLayout(new FlowLayout(FlowLayout.LEFT));
        UIManager.put("TextField.inactiveForeground", Color.white);
        UIManager.put("OptionPane.background",new ColorUIResource(0, 0, 0));
        UIManager.put("Panel.background",new ColorUIResource(0, 0, 0));
        UIManager.put("OptionPane.messageForeground",new ColorUIResource(255, 255, 255));
        ImageIcon icon = new ImageIcon("C:\\Users\\user\\Downloads\\calculator.png");
        setIconImage(icon.getImage());
        final JTextField txt = new JTextField("");
        txt.setColumns(12);
        txt.setForeground(Color.WHITE);
        txt.setBackground(Color.BLACK);
        txt.setHorizontalAlignment(SwingConstants.RIGHT);
        txt.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent ke) {
                if ((ke.getKeyChar() >= '0' && ke.getKeyChar() <= '9') || ke.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                    txt.setEditable(true);
                } else {
                    txt.setEditable(false);
                    java.awt.Toolkit.getDefaultToolkit().beep();

                }
            }
        });
        txt.setFont(txt.getFont().deriveFont(22.0f));
        add(txt);

        JLabel space = new JLabel("                                                                                                 ");
        add(space);

        JLabel space1 = new JLabel("                                                                                                 ");
        add(space1);

        JButton btn1 = new JButton("1");
        btn1.setPreferredSize(new Dimension(70, 50));
        btn1.setBackground(Color.BLACK);
        btn1.setForeground(Color.WHITE);
        btn1.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txt.setText(txt.getText() + "1");
            }
        });
        btn1.setFont(btn1.getFont().deriveFont(17.0f));
        add(btn1);

        JButton btn2 = new JButton("2");
        btn2.setPreferredSize(new Dimension(70, 50));
        btn2.setBackground(Color.BLACK);
        btn2.setForeground(Color.WHITE);
        btn2.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txt.setText(txt.getText() + "2");
            }
        });
        btn2.setFont(btn2.getFont().deriveFont(17.0f));
        add(btn2);

        JButton btn3 = new JButton("3");
        btn3.setPreferredSize(new Dimension(70, 50));
        btn3.setBackground(Color.BLACK);
        btn3.setForeground(Color.WHITE);
        btn3.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txt.setText(txt.getText() + "3");
            }
        });
        btn3.setFont(btn3.getFont().deriveFont(17.0f));
        add(btn3);

        JButton btn4 = new JButton("4");
        btn4.setPreferredSize(new Dimension(70, 50));
        btn4.setBackground(Color.BLACK);
        btn4.setForeground(Color.WHITE);
        btn4.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txt.setText(txt.getText() + "4");
            }
        });
        btn4.setFont(btn4.getFont().deriveFont(17.0f));
        add(btn4);

        JButton btn5 = new JButton("5");
        btn5.setPreferredSize(new Dimension(70, 50));
        btn5.setBackground(Color.BLACK);
        btn5.setForeground(Color.WHITE);
        btn5.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn5.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txt.setText(txt.getText() + "5");
            }
        });
        btn5.setFont(btn5.getFont().deriveFont(17.0f));
        add(btn5);

        JButton btn6 = new JButton("6");
        btn6.setPreferredSize(new Dimension(70, 50));
        btn6.setBackground(Color.BLACK);
        btn6.setForeground(Color.WHITE);
        btn6.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn6.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txt.setText(txt.getText() + "6");
            }
        });
        btn6.setFont(btn6.getFont().deriveFont(17.0f));
        add(btn6);

        JButton btn7 = new JButton("7");
        btn7.setPreferredSize(new Dimension(70, 50));
        btn7.setBackground(Color.BLACK);
        btn7.setForeground(Color.WHITE);
        btn7.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn7.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txt.setText(txt.getText() + "7");
            }
        });
        btn7.setFont(btn7.getFont().deriveFont(17.0f));
        add(btn7);

        JButton btn8 = new JButton("8");
        btn8.setPreferredSize(new Dimension(70, 50));
        btn8.setBackground(Color.BLACK);
        btn8.setForeground(Color.WHITE);
        btn8.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn8.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txt.setText(txt.getText() + "8");
            }
        });
        btn8.setFont(btn8.getFont().deriveFont(17.0f));
        add(btn8);

        JButton btn9 = new JButton("9");
        btn9.setPreferredSize(new Dimension(70, 50));
        btn9.setBackground(Color.BLACK);
        btn9.setForeground(Color.WHITE);
        btn9.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn9.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txt.setText(txt.getText() + "9");
            }
        });
        btn9.setFont(btn9.getFont().deriveFont(17.0f));
        add(btn9);

        JButton btn0 = new JButton("0");
        btn0.setPreferredSize(new Dimension(70, 50));
        btn0.setBackground(Color.BLACK);
        btn0.setForeground(Color.WHITE);
        btn0.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn0.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txt.setText(txt.getText() + "0");
            }
        });
        btn0.setFont(btn0.getFont().deriveFont(17.0f));
        add(btn0);

        JButton clear_btn = new JButton("C");
        clear_btn.setPreferredSize(new Dimension(70, 50));
        clear_btn.setBackground(Color.BLACK);
        clear_btn.setForeground(Color.WHITE);
        clear_btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        clear_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txt.setText("");
            }
        });
        clear_btn.setFont(clear_btn.getFont().deriveFont(17.0f));
        add(clear_btn);

        JButton delete_btn = new JButton("←");
        delete_btn.setPreferredSize(new Dimension(70, 50));
        delete_btn.setBackground(Color.BLACK);
        delete_btn.setForeground(Color.WHITE);
        delete_btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        delete_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(txt.getText().length() > 0)
                    txt.setText(txt.getText().substring(0, txt.getText().length() - 1));
            }
        });
        delete_btn.setFont(delete_btn.getFont().deriveFont(17.0f));
        add(delete_btn);

        JButton bin_btn = new JButton("Bin.");
        bin_btn.setPreferredSize(new Dimension(70, 50));
        bin_btn.setBackground(Color.BLACK);
        bin_btn.setForeground(Color.WHITE);
        bin_btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        bin_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String value = txt.getText();
                StringBuilder str = new StringBuilder();
                if(value.equals("")) {
                    java.awt.Toolkit.getDefaultToolkit().beep();
                } else {
                    long x = Long.parseLong(value);
                    int nr = 0;
                    LinkedList<Integer> list = new LinkedList<Integer>();
                    while(x != 0) {
                        long r = x % 2;
                        list.add((int) r);
                        nr++;
                        x /= 2;
                    }
                    for(int i = nr - 1; i >= 0; i--) {
                        str.append(list.get(i));
                    }
                    JLabel label = new JLabel(str.toString());
                    label.setForeground(Color.WHITE);
                    label.setFont(new Font("Arial", Font.BOLD, 18));
                    JOptionPane.showOptionDialog(null, label,txt.getText() + " to bin.", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, new Object[]{}, null);
                }
            }
        });
        bin_btn.setFont(bin_btn.getFont().deriveFont(17.0f));
        add(bin_btn);

        JButton oct_btn = new JButton("Oct.");
        oct_btn.setPreferredSize(new Dimension(70, 50));
        oct_btn.setBackground(Color.BLACK);
        oct_btn.setForeground(Color.WHITE);
        oct_btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        oct_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String value = txt.getText();
                StringBuilder str = new StringBuilder();
                if(value.equals("")) {
                    java.awt.Toolkit.getDefaultToolkit().beep();
                } else {
                    long x = Long.parseLong(value);
                    int nr = 0;
                    LinkedList<Integer> list = new LinkedList<Integer>();
                    while (x != 0) {
                        long r = x % 8;
                        list.add((int) r);
                        nr++;
                        x /= 8;
                    }
                    for(int i = nr - 1; i >= 0; i--) {
                        str.append(list.get(i));
                    }
                    JLabel label = new JLabel(str.toString());
                    label.setForeground(Color.WHITE);
                    label.setFont(new Font("Arial", Font.BOLD, 18));
                    JOptionPane.showOptionDialog(null, label,txt.getText() + " to hex.", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, new Object[]{}, null);
                }
            }
        });
        oct_btn.setFont(oct_btn.getFont().deriveFont(17.0f));
        add(oct_btn);

        JButton hex_btn = new JButton("Hex.");
        hex_btn.setPreferredSize(new Dimension(70, 50));
        hex_btn.setBackground(Color.BLACK);
        hex_btn.setForeground(Color.WHITE);
        hex_btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        hex_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String value = txt.getText();
                StringBuilder str = new StringBuilder();
                if(value.equals("")) {
                    java.awt.Toolkit.getDefaultToolkit().beep();
                } else {
                    long x = Long.parseLong(value);
                    int nr = 0;
                    LinkedList<Integer> list = new LinkedList<Integer>();
                    while (x != 0) {
                        long r = x % 16;
                        list.add((int) r);
                        nr++;
                        x /= 16;
                    }
                    for(int i = nr - 1; i >= 0; i--) {
                        int a = list.get(i);
                        if(a <= 9) {
                            str.append(a);
                        } else if(a == 10) {
                            str.append("A");
                        } else if(a == 11) {
                            str.append("B");
                        } else if(a == 12) {
                            str.append("C");
                        } else if(a == 13) {
                            str.append("D");
                        } else if(a == 14) {
                            str.append("E");
                        } else if(a == 15) {
                            str.append("F");
                        }
                    }
                    JLabel label = new JLabel(str.toString());
                    label.setForeground(Color.WHITE);
                    label.setFont(new Font("Arial", Font.BOLD, 18));
                    JOptionPane.showOptionDialog(null, label,txt.getText() + " to hex.", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, new Object[]{}, null);
                }
            }
        });
        hex_btn.setFont(hex_btn.getFont().deriveFont(17.0f));
        add(hex_btn);
    }

    public static void main(String[] args) {
	    Main gui = new Main();
	    gui.setVisible(true);
	    gui.setResizable(false);
	    gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    gui.setLocale(Locale.ENGLISH);
	    gui.getContentPane().setBackground(Color.BLACK);
	    gui.setTitle("Dec. conv. calc.");
	    gui.setName("dec");
	    gui.setSize(247, 405);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int height = screenSize.height;
        int width = screenSize.width;
        gui.setLocation(width/2-gui.getSize().width/2, height/2-gui.getSize().height/2);
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (UnsupportedLookAndFeelException e) { e.printStackTrace(); } catch (ClassNotFoundException e) { e.printStackTrace(); } catch (InstantiationException e) { e.printStackTrace(); } catch (IllegalAccessException e) { e.printStackTrace(); }
    }
}
