package com.jetbrains;

import java.lang.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import javax.swing.border.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.awt.event.*;


public class Main extends JFrame{


	private Main() {
		setLayout(new FlowLayout(FlowLayout.LEFT));

		UIManager.put("Menu.selectionBackground", Color.WHITE);
        	UIManager.put("Menu.selectionForeground", Color.BLACK);

		JLabel space = new JLabel("                     ");
		add(space);

		JLabel label = new JLabel("  Height Conversion Calculator  ");
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

		JLabel label2 = new JLabel("         Convert ft & in to cm:");
		label2.setFont(label2.getFont().deriveFont(16.0f));
		add(label2);

		JLabel space4 = new JLabel("                                                  ");
		add(space4);

		JLabel feets = new JLabel("         Feet:");
		feets.setFont(feets.getFont().deriveFont(16.0f));
		add(feets);

		JFormattedTextField feet = new JFormattedTextField();
		feet.setFont(feet.getFont().deriveFont(16.0f));
		feet.setColumns(6);
		feet.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		feet.setToolTipText("Enter \'feet\' value");
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

		JLabel in = new JLabel("        In:");
		in.setFont(in.getFont().deriveFont(16.0f));
		add(in);

		JFormattedTextField feet1 = new JFormattedTextField();
		feet1.setFont(feet1.getFont().deriveFont(16.0f));
		feet1.setColumns(6);
		feet1.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		feet1.setToolTipText("Enter \'in\' value");
		feet1.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				if(e.getKeyChar() == KeyEvent.VK_ENTER || e.getKeyChar() == '.' || e.getKeyChar() == KeyEvent.VK_0 || e.getKeyChar() == KeyEvent.VK_1 || e.getKeyChar() == KeyEvent.VK_2 || e.getKeyChar() == KeyEvent.VK_3 || e.getKeyChar() == KeyEvent.VK_4 || e.getKeyChar() == KeyEvent.VK_5 || e.getKeyChar() == KeyEvent.VK_6 || e.getKeyChar() == KeyEvent.VK_7 || e.getKeyChar() == KeyEvent.VK_NUM_LOCK || e.getKeyChar() == KeyEvent.VK_WINDOWS || e.getKeyChar() == KeyEvent.VK_ALT || e.getKeyChar() == KeyEvent.VK_8 || e.getKeyChar() == KeyEvent.VK_9  || e.getKeyChar() == KeyEvent.VK_ESCAPE || e.getKeyChar() == KeyEvent.VK_BACK_SPACE || e.getKeyChar() == KeyEvent.VK_TAB || e.getKeyChar() == KeyEvent.VK_CAPS_LOCK || e.getKeyChar() == KeyEvent.VK_CONTROL || e.getKeyChar() == KeyEvent.VK_F1 || e.getKeyChar() == KeyEvent.VK_F2 || e.getKeyChar() == KeyEvent.VK_F3 || e.getKeyChar() == KeyEvent.VK_F4 || e.getKeyChar() == KeyEvent.VK_F5 || e.getKeyChar() == KeyEvent.VK_F6 || e.getKeyChar() == KeyEvent.VK_F7 || e.getKeyChar() == KeyEvent.VK_F8 || e.getKeyChar() == KeyEvent.VK_F9 || e.getKeyChar() == KeyEvent.VK_F10 || e.getKeyChar() == KeyEvent.VK_F11 || e.getKeyChar() == KeyEvent.VK_F12 || e.getKeyChar() == KeyEvent.VK_INSERT || e.getKeyChar() == KeyEvent.VK_END || e.getKeyChar() == KeyEvent.VK_DELETE || e.getKeyChar() == KeyEvent.VK_HOME || e.getKeyChar() == KeyEvent.VK_PAGE_DOWN || e.getKeyChar() == KeyEvent.VK_PAGE_UP || e.getKeyChar() == KeyEvent.VK_PAUSE || e.getKeyChar() == KeyEvent.VK_SCROLL_LOCK || e.getKeyChar() == KeyEvent.VK_PRINTSCREEN) {
					feet1.setEditable(true);
				} else if(e.getKeyChar() == KeyEvent.VK_EXCLAMATION_MARK || e.getKeyChar() == KeyEvent.VK_DOLLAR  || e.getKeyChar() == KeyEvent.VK_ASTERISK  || e.getKeyChar() == KeyEvent.VK_LEFT_PARENTHESIS  || e.getKeyChar() == KeyEvent.VK_RIGHT_PARENTHESIS || e.getKeyChar() == KeyEvent.VK_DIVIDE  || e.getKeyChar() == KeyEvent.VK_COPY  || e.getKeyChar() == KeyEvent.VK_PASTE  || e.getKeyChar() == KeyEvent.VK_NUMBER_SIGN ){
					feet1.setEditable(false);
				} else {
					feet1.setEditable(false);
				}
			}
		});
		add(feet1);

		JLabel space5 = new JLabel("                                                                              ");
		add(space5);

		JLabel label4 = new JLabel("         Convert cm to in:");
		label4.setFont(label4.getFont().deriveFont(16.0f));
		add(label4);

		JLabel space6 = new JLabel("                                                  ");
		add(space6);

		JLabel cm = new JLabel("         Cm:");
		cm.setFont(cm.getFont().deriveFont(16.0f));
		add(cm);

		JFormattedTextField feet2 = new JFormattedTextField();
		feet2.setFont(feet2.getFont().deriveFont(16.0f));
		feet2.setColumns(6);
		feet2.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		feet2.setToolTipText("Enter \'cm\' value");
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
				String b = feet1.getText();
				String c = feet2.getText();
				if(a.length() > 0 && b.length() > 0 && c.length() > 0) {
					JOptionPane.showMessageDialog(null, "You can\'t calculate 2 values in the same time", "Error", JOptionPane.ERROR_MESSAGE);
					feet.setText("");
					feet1.setText("");
					feet2.setText("");
				} else if(c.length() > 0) {
					double a1 = Double.parseDouble(c) / 2.54;
					ans.setText("Answer: " + a1 + " in.");
					ans.setVisible(true);
				} else if(a.length() > 0 && b.length() > 0) {
					double a2 = (Double.parseDouble(a) * 12 + Double.parseDouble(b)) * 2.54;
					ans.setText("Answer: " + a2 + " cm.");
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
					feet1.setText("");
					feet2.setText("");
			}
		});
		add(clear);

		JLabel space11 = new JLabel("                                                                                                                                ");
		add(space11);

		JLabel space12 = new JLabel("                    ");
		add(space12);

		add(ans);

		JMenuBar bar = new JMenuBar();
		setJMenuBar(bar);

		JMenu change = new JMenu("Black");
		change.setFont(change.getFont().deriveFont(17.0f));
		change.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		change.addMenuListener(new MenuListener() {
            @Override
            public void menuSelected(MenuEvent e) {
                if(change.getText().equals("Black")) {
                    change.setText("White");
                    bar.setBackground(Color.BLACK);
                    getContentPane().setBackground(Color.BLACK);
                    change.setForeground(Color.WHITE);
                    change.setBackground(Color.BLACK);
                    clear.setForeground(Color.WHITE);
                    clear.setBackground(Color.BLACK);
                    calc.setForeground(Color.WHITE);
                    calc.setBackground(Color.BLACK);
                    ans.setForeground(Color.WHITE);
                    cm.setForeground(Color.WHITE);
                    feet2.setBackground(Color.BLACK);
                    feet2.setForeground(Color.WHITE);
                    feet1.setBackground(Color.BLACK);
                    feet1.setForeground(Color.WHITE);
                    feet.setBackground(Color.BLACK);
                    feet.setForeground(Color.WHITE);
                    in.setForeground(Color.WHITE);
                    feets.setForeground(Color.WHITE);
                    label2.setForeground(Color.WHITE);
                    label.setForeground(Color.WHITE);
                    label4.setForeground(Color.WHITE);
                    label1.setForeground(Color.WHITE);
                    LineBorder line1 = new LineBorder(Color.decode("#ffffff"), 1, true);
                    label.setBorder(line1);
                } else {
                    change.setText("Black");
                    bar.setBackground(Color.WHITE);
                    getContentPane().setBackground(Color.WHITE);
                    change.setForeground(Color.BLACK);
                    change.setBackground(Color.WHITE);
                    clear.setForeground(Color.BLACK);
                    clear.setBackground(Color.WHITE);
                    calc.setForeground(Color.BLACK);
                    calc.setBackground(Color.WHITE);
                    ans.setForeground(Color.BLACK);
                    cm.setForeground(Color.BLACK);
                    feet2.setBackground(Color.WHITE);
                    feet2.setForeground(Color.BLACK);
                    feet1.setBackground(Color.WHITE);
                    feet1.setForeground(Color.BLACK);
                    feet.setBackground(Color.WHITE);
                    feet.setForeground(Color.BLACK);
                    in.setForeground(Color.BLACK);
                    feets.setForeground(Color.BLACK);
                    label2.setForeground(Color.BLACK);
                    label.setForeground(Color.BLACK);
                    label4.setForeground(Color.BLACK);
                    label1.setForeground(Color.BLACK);
                    LineBorder line1 = new LineBorder(Color.decode("#264475"), 1, true);
                    label.setBorder(line1);
                }
            }

            @Override
            public void menuDeselected(MenuEvent e) {

            }

            @Override
            public void menuCanceled(MenuEvent e) {

            }
        });
		bar.add(change);
	}


    public static void main(String[] args) {
    	Main gui = new Main();
    	gui.setVisible(true);
    	gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	gui.setName("Converter");
    	gui.setTitle("Height converter");
    	gui.setResizable(false);
    	gui.setLocale(Locale.ENGLISH);
    	gui.setSize(400, 420);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int height = screenSize.height;
		int width = screenSize.width;
		gui.setLocation(width / 2 - gui.getSize().width / 2, height / 2 - gui.getSize().height / 2);
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel"); //com.sun.java.swing.plaf.windows.WindowsLookAndFeel
		}
		catch (Exception e) { e.printStackTrace(); }
	}

}
