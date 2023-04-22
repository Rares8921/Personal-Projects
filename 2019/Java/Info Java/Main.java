package com.jetbrains;

import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.border.Border;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URI;
import java.awt.Desktop;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.Cursor;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.Component;
import java.awt.Color;
import javax.swing.ImageIcon;
import java.awt.LayoutManager;
import java.awt.FlowLayout;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JFrame;

public class Main extends JFrame
{
    private JLabel text;
    private JLabel copyright;
    private JTextField command;
    private JLabel texts;
    
    private Main() {
        this.setLayout(new FlowLayout(0));
        final ImageIcon icon = new ImageIcon("img/download.png");
        this.setIconImage(icon.getImage());
        (this.copyright = new JLabel("Copyright (c) 2019 Java Prompt. All rights reserved                                                                     ")).setForeground(Color.WHITE);
        this.add(this.copyright);
        (this.text = new JLabel("Type '!info' to get some info about java or '!exit' to quit:                                                                                                           ")).setForeground(Color.WHITE);
        this.add(this.text);
        (this.command = new JTextField(100)).addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(final KeyEvent e) {
            }
            
            @Override
            public void keyPressed(final KeyEvent e) {
                final int key = e.getKeyCode();
                if (key == 10) {
                    final String b = Main.this.command.getText();
                    if (b.equals("!info")) {
                        Main.this.setSize(650, 370);
                        Main.this.command.setEditable(false);
                        Main.this.command.setVisible(false);
                        Main.this.command.setText("");
                        Main.this.copyright.setFont(Main.this.copyright.getFont().deriveFont(14.0f));
                        Main.this.text.setFont(Main.this.text.getFont().deriveFont(15.0f));
                        Main.this.texts.setFont(Main.this.texts.getFont().deriveFont(15.0f));
                        Main.this.copyright.setText("<html>James Gosling, Mike Sheridan, and Patrick Naughton initiated the Java <br />language project in June 1991. <br /><br />Java was originally designed for interactive television, but it was too advanced <br />for the digital cable television industry at the time. <br /><br />The language was initially called Oak after an oak tree that stood <br />outside Gosling's office. <br /><br />Later the project went by the name Green and was finally <br />renamed Java, from Java coffee. <br /><br />Gosling designed Java with a C/C++-style syntax that system and <br />application programmers would find familiar.<br /><hr /></html> ");
                        Main.this.text.setText("To get more info, visit: ");
                        final JLabel text2 = new JLabel("https://en.wikipedia.org/wiki/Java_(programming_language)");
                        text2.setFont(text2.getFont().deriveFont(15.0f));
                        text2.setForeground(Color.CYAN);
                        text2.setCursor(Cursor.getPredefinedCursor(12));
                        text2.addMouseListener(new MouseAdapter() {
                            @Override
                            public void mouseClicked(final MouseEvent e) {
                                try {
                                    Desktop.getDesktop().browse(new URI("https://en.wikipedia.org/wiki/Java_(programming_language)"));
                                }
                                catch (URISyntaxException ex) {}
                                catch (IOException ex2) {}
                            }
                        });
                        Main.this.getContentPane().add(text2);
                        final JLabel text3 = new JLabel();
                        text3.setText("<html>Back to main page \u21ba</html>");
                        text3.setForeground(Color.decode("#7093cc"));
                        text3.setFont(text3.getFont().deriveFont(15.0f));
                        text3.setCursor(Cursor.getPredefinedCursor(12));
                        text3.addMouseListener(new MouseAdapter() {
                            @Override
                            public void mouseClicked(final MouseEvent e) {
                                Main.this.setSize(600, 200);
                                Main.this.copyright.setText("Copyright (c) 2019 Java Prompt. All rights reserved                                                                     ");
                                Main.this.copyright.setFont(Main.this.copyright.getFont().deriveFont(12.0f));
                                Main.this.copyright.setForeground(Color.WHITE);
                                Main.this.text.setText("Type '!info' to get some info about java or '!exit' to quit:                                                                                                           ");
                                Main.this.text.setForeground(Color.WHITE);
                                Main.this.text.setFont(Main.this.text.getFont().deriveFont(12.0f));
                                text2.setText("");
                                text3.setText("");
                                Main.this.command.setColumns(100);
                                Main.this.command.setVisible(true);
                                Main.this.command.setEditable(true);
                                Main.this.command.setBackground(Color.black);
                                Main.this.command.setBorder(null);
                                Main.this.command.setForeground(Color.WHITE);
                            }
                        });
                        Main.this.getContentPane().add(text3);
                    }
                    else if (b.equals("!exit")) {
                        System.exit(0);
                    }
                }
            }
            
            @Override
            public void keyReleased(final KeyEvent e) {
            }
        });
        this.command.setBackground(Color.black);
        this.command.setBorder(null);
        this.command.setForeground(Color.WHITE);
        this.add(this.command);
        (this.texts = new JLabel("https://en.wikipedia.org/wiki/Java_(programming_language)")).setForeground(Color.CYAN);
        this.texts.setVisible(false);
        this.texts.setCursor(Cursor.getPredefinedCursor(12));
        this.add(this.texts);
    }
    
    public static void main(final String[] args) {
        final Main gui = new Main();
        gui.setVisible(true);
        gui.setResizable(false);
        gui.setDefaultCloseOperation(3);
        gui.getContentPane().setBackground(Color.decode("#000000"));
        gui.setTitle("Info Java");
        gui.setSize(600, 200);
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        final int height = screenSize.height;
        final int width = screenSize.width;
        gui.setLocation(width / 2 - gui.getSize().width / 2, height / 2 - gui.getSize().height / 2);
    }
}