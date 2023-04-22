package com.jetbrains;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.lang.*;
import java.net.*;
import java.io.*;
import javax.swing.*;
import java.awt.*;
import javax.swing.border.Border;
import javax.swing.filechooser.FileSystemView;
import javax.swing.plaf.ColorUIResource;
import javax.swing.text.*;
import javax.swing.undo.UndoManager;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.io.File;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Main extends JFrame {

    public static Main gui;
    public static JLabel text1, text2;
    private static Highlighter.HighlightPainter myHighlightPainter;
    public static JTextArea field1, field2;
    public static JButton lang, send, folders, switch_lang;
    public static JMenu menu;
    public static JButton first_lang, second_lang;
    public static AtomicInteger l1 = new AtomicInteger();
    public static UndoManager undo = new UndoManager();
    private static JFileChooser jfc;

    private Main() {
        setLayout(new FlowLayout(FlowLayout.LEFT));

        UIManager.put("Menu.selectionBackground", Color.decode("#1c1c1c"));
        UIManager.put("Menu.selectionForeground", Color.decode("#fefefe"));
        UIManager.put("MenuItem.selectionBackground", Color.decode("#1c1c1c"));
        UIManager.put("MenuItem.selectionForeground", Color.decode("#fefefe"));
        UIManager.put("Menu.disabledBackground", Color.decode("#1c1c1c"));
        UIManager.put("Menu.disabledForeground", Color.decode("#fefefe"));
        UIManager.put("TextArea.selectionBackground", Color.decode("#3b3a3a"));
        UIManager.put("TextArea.selectionForeground", Color.decode("#fefefe"));
        UIManager.put("OptionPane.background", Color.decode("#1c1c1c"));
        UIManager.put("OptionPane.foreground", Color.decode("#fefefe"));
        UIManager.put("OptionPane.messageForeground", Color.decode("#fefefe"));
        UIManager.put("Panel.background", Color.decode("#1c1c1c"));
        UIManager.put("Button.opaque", true);
        UIManager.put("ScrollBar.thumb", new ColorUIResource(Color.decode("#e6e6e8")));
        Border white_line = BorderFactory.createLineBorder(Color.decode("#fefefe"));

        myHighlightPainter = new MyKeyListener.MyHighlightPainter(Color.decode("#3b3a3a"));

        JMenuBar bar =  new JMenuBar();
        bar.setBorder(white_line);
        bar.setBackground(Color.decode("#1c1c1c"));
        bar.setForeground(Color.decode("#fefefe"));
        setJMenuBar(bar);

        lang = new JButton("<html><u>Translate</u></html>") {
            @Override
            public JToolTip createToolTip() {
                return (new CustomJToolTip(this));
            }
        };
        lang.setFont(lang.getFont().deriveFont(15.0f));
        lang.addActionListener(e -> {
            if(l1.get() == 0) {
                JOptionPane.showOptionDialog(null, "Those are the key binds which can be used:\n" +
                                "1) Ctrl C - Copy text\n" +
                                "2) Ctrl V - Paste text\n" +
                                "3) Ctrl X - Delete text\n" +
                                "4) Ctrl A - Select all text\n" +
                                "5) Ctrl Z - Undo action\n" +
                                "6) Ctrl R - Redo action\n" +
                                "7) Ctrl H - Replace text\n" +
                                "8) Ctrl F - Search text and highlight it\n" +
                                "9) Ctrl F1 - Remove highlight of text\n" +
                                "10) Ctrl F2 - Swap translate languages\n" +
                                "11) Ctrl F3 - Translate text\n" +
                                "12) Ctrl F4 - Select a file to translate\n" +
                                "13) Ctrl F5 - Change language\n",
                        "Key Binds", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, new Object[]{}, null);
            } else {
                JOptionPane.showOptionDialog(null, "Acestea sunt bindurile care pot fi folosite:\n" +
                                "1) Ctrl C - Copiază textul\n" +
                                "2) Ctrl V - Inserează textul\n" +
                                "3) Ctrl X - Șterge textul\n" +
                                "4) Ctrl A - Selectează textul\n" +
                                "5) Ctrl Z - Anulează acțiunea\n" +
                                "6) Ctrl R - Restabilește ultima acțiune\n" +
                                "7) Ctrl H - Inlocuiește textul\n" +
                                "8) Ctrl F - Caută text și evidențiază-l\n" +
                                "9) Ctrl F1 - Elimină evidențierea textului\n" +
                                "10) Ctrl F2 - Schimbă limba de tradus\n" +
                                "11) Ctrl F3 - Tradu textul\n" +
                                "12) Ctrl F4 - Selecteză o filă pt a o traduce\n" +
                                "13) Ctrl F5 - Schimbă limba\n",
                        "Binduri", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, new Object[]{}, null);
            }
        });
        lang.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lang.setForeground(Color.BLACK);
        lang.setBackground(Color.decode("#1c1c1c"));
        lang.setForeground(Color.decode("#fefefe"));
        bar.add(lang);

        JMenu sp1 = new JMenu("|");
        sp1.setFont(sp1.getFont().deriveFont(14.0f));
        sp1.setEnabled(false);
        sp1.setBackground(Color.decode("#1c1c1c"));
        sp1.setForeground(Color.decode("#fefefe"));
        bar.add(sp1);

        first_lang =  new JButton("EN.") {
            @Override
            public JToolTip createToolTip() {
                return (new CustomJToolTip(this));
            }
        };
        first_lang.setFont(first_lang.getFont().deriveFont(14.0f));
        first_lang.setToolTipText("        Translate from");
        first_lang.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
        first_lang.setBackground(Color.decode("#1c1c1c"));
        first_lang.setForeground(Color.decode("#fefefe"));
        bar.add(first_lang);

        switch_lang = new JButton("↔") {
            @Override
            public JToolTip createToolTip() {
                return (new CustomJToolTip(this));
            }
        };
        switch_lang.setFont(switch_lang.getFont().deriveFont(14.0f));
        switch_lang.setToolTipText("     Switch languages");
        switch_lang.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        switch_lang.setBackground(Color.decode("#1c1c1c"));
        switch_lang.setForeground(Color.decode("#fefefe"));
        switch_lang.addActionListener(e -> {
            if(first_lang.getText().equals("EN.")) {
                field2.setText("");
                field1.setText("");
                first_lang.setText("RO.");
                second_lang.setText("EN.");
                String s1 = text1.getText();
                String s2 = text2.getText();
                text1.setText(s2);
                text2.setText(s1);
            } else {
                field2.setText("");
                field1.setText("");
                first_lang.setText("EN.");
                second_lang.setText("RO.");
                String s1 = text1.getText();
                String s2 = text2.getText();
                text1.setText(s2);
                text2.setText(s1);
            }
        });
        bar.add(switch_lang);

        second_lang = new JButton("RO.") {
            @Override
            public JToolTip createToolTip() {
                return (new CustomJToolTip(this));
            }
        };
        second_lang.setFont(second_lang.getFont().deriveFont(14.0f));
        second_lang.setToolTipText("           Translate to");
        second_lang.setBackground(Color.decode("#1c1c1c"));
        second_lang.setForeground(Color.decode("#fefefe"));
        second_lang.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
        bar.add(second_lang);

        JMenu sp2 = new JMenu("|");
        sp2.setFont(sp2.getFont().deriveFont(14.0f));
        sp2.setBackground(Color.decode("#1c1c1c"));
        sp2.setForeground(Color.decode("#fefefe"));
        sp2.setEnabled(false);
        bar.add(sp2);

        send = new JButton("➤") {
            @Override
            public JToolTip createToolTip() {
                return (new CustomJToolTip(this));
            }
        };
        send.setFont(send.getFont().deriveFont(14.0f));
        send.setToolTipText("  Send text to translate");
        send.setBackground(Color.decode("#1c1c1c"));
        send.setForeground(Color.decode("#fefefe"));
        send.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        send.addActionListener(e -> {
            if(!field1.getText().isEmpty()) {
                if (first_lang.getText().equals("EN.")) {
                    try {
                        String val = field1.getText();
                        String[] arr1 = val.split(" ");
                        String ans = "";
                        for(String s : arr1) {
                            if(s.equals("%")) {
                                ans = ans.concat("%");
                            } else {
                                String s1 = GoogleTranslate.translate("en", "ro", s);
                                ans = ans.concat(s1 + " ");
                            }
                        }
                        ans = ans.replace("\\u0026", "&");
                        ans = ans.replace("\\u003d", "=");
                        ans = ans.replace("\\u003c", "<");
                        ans = ans.replace("\\u003e", ">");
                        field2.setText(ans);
                    } catch (IOException er) {
                        er.printStackTrace();
                    }
                } else {
                    try {
                        String val = field1.getText();
                        String[] arr1 = val.split(" ");
                        String ans = "";
                        for(String s : arr1) {
                            if(s.equals("%")) {
                                ans = ans.concat("%");
                            } else {
                                String s1 = GoogleTranslate.translate("ro", "en", s);
                                ans = ans.concat(s1 + " ");
                            }
                        }
                        ans = ans.replace("\\u0026", "&");
                        ans = ans.replace("\\u003d", "=");
                        ans = ans.replace("\\u003c", "<");
                        ans = ans.replace("\\u003e", ">");
                        field2.setText(ans);
                    } catch (IOException er) {
                        er.printStackTrace();
                    }
                }
            }
        });
        bar.add(send);

        folders = new JButton("\uD83D\uDCC1") {
            @Override
            public JToolTip createToolTip() {
                return (new CustomJToolTip(this));
            }
        };
        folders.setFont(folders.getFont().deriveFont(14.0f));
        folders.setToolTipText("            Select a file");
        folders.setBackground(Color.decode("#1c1c1c"));
        folders.setForeground(Color.decode("#fefefe"));
        folders.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        folders.addActionListener(e -> {
            UIManager.put("Panel.background", new ColorUIResource(238, 238, 238));
            jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
            if(getTitle().equals("Translate")) {
                jfc.setDialogTitle("Select a file");
            } else {
                jfc.setDialogTitle("Selectează o filă");
            }
            int returnValue = jfc.showSaveDialog(null);
            if(returnValue == JFileChooser.APPROVE_OPTION) {
                File file = jfc.getSelectedFile();
                try {
                    Scanner scan = new Scanner(file);
                    String value = "";
                    while(scan.hasNext()) {
                        String a = scan.nextLine();
                        value = value.concat(a + '\n');
                    }
                    field1.setText(value);
                } catch(FileNotFoundException ex) {
                    ex.printStackTrace();
                }
            }
            UIManager.put("Panel.background", Color.decode("#1c1c1c"));
        });
        bar.add(folders);

        menu = new JMenu("   ☰") {
            @Override
            public JToolTip createToolTip() {
                return (new CustomJToolTip(this));
            }
        };
        menu.setFont(new Font("SERIF", Font.BOLD, 14));
        menu.setToolTipText("      Set the language");
        menu.setPreferredSize(new Dimension(47, 10));
        menu.setBackground(Color.decode("#1c1c1c"));
        menu.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        menu.setForeground(Color.decode("#fefefe"));
        bar.add(menu);

        JLabel space1 = new JLabel("                                                                                                                                                  ");
        add(space1);

        text1 = new JLabel(" \uD83C\uDF0F English: ");
        text1.setFont(text1.getFont().deriveFont(14.0f));
        text1.setBackground(Color.decode("#1c1c1c"));
        text1.setForeground(Color.decode("#fefefe"));
        add(text1);

        field1 =  new JTextArea(7, 38);
        field1.addKeyListener(new MyKeyListener());
        field1.setFont(field1.getFont().deriveFont(14.0f));
        field1.setBorder(white_line);
        field1.getDocument().addUndoableEditListener(undo);
        field1.setBackground(Color.decode("#1c1c1c"));
        field1.setForeground(Color.decode("#fefefe"));
        field1.setLineWrap(true);
        field1.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(field1, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane);

        JLabel ab = new JLabel("                                                                                                                                                          ");
        add(ab);

        text2 = new JLabel(" \uD83C\uDF0F Romanian: ");
        text2.setFont(text2.getFont().deriveFont(14.0f));
        text2.setBackground(Color.decode("#1c1c1c"));
        text2.setForeground(Color.decode("#fefefe"));
        add(text2);

        field2 = new JTextArea(7, 38) {
            @Override
            public JToolTip createToolTip() {
                return (new CustomJToolTip(this));
            }
        };
        field2.setFont(field2.getFont().deriveFont(14.0f));
        field2.setBorder(white_line);
        field2.setEditable(false);
        field2.setWrapStyleWord(true);
        field2.setLineWrap(true);
        field2.setBackground(Color.decode("#1c1c1c"));
        field2.setForeground(Color.decode("#fefefe"));
        field2.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        field2.setToolTipText(" Copy text to clipboard ");
        field2.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
            }

            @Override
            public void focusLost(FocusEvent e) {
                StringSelection stringSelection = new StringSelection(field2.getText());
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(stringSelection, null);
            }
        });
        JScrollPane pane2 = new JScrollPane(field2, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(pane2);

        JMenuItem ro = new JMenuItem("Ro.");
        ro.setFont(ro.getFont().deriveFont(14.0f));
        ro.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        ro.addActionListener(e -> {
            setTitle("Traducător");
            lang.setText("<html><u>Traducător</u></html>");
            first_lang.setToolTipText("            Tradu din");
            switch_lang.setToolTipText("     Inversează limbile");
            second_lang.setToolTipText("            Tradu în");
            send.setToolTipText(" Scrie text pt traducere");
            field2.setToolTipText("        Copiază textul ");
            folders.setToolTipText("        Selectează o filă");
            l1.set(1);
            menu.setToolTipText("         Setează limba");
            if(first_lang.getText().equals("EN.")) {
                text1.setText(" \uD83C\uDF0F Engleză: ");
                text2.setText(" \uD83C\uDF0F Română: ");
            } else {
                text2.setText(" \uD83C\uDF0F Engleză: ");
                text1.setText(" \uD83C\uDF0F Română: ");
            }
        });
        ro.setBackground(Color.decode("#1c1c1c"));
        ro.setForeground(Color.decode("#fefefe"));
        menu.add(ro);

        JMenuItem en = new JMenuItem("En.");
        en.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        en.setFont(en.getFont().deriveFont(14.0f));
        en.addActionListener(e -> {
            setTitle("Translate");
            lang.setText("<html><u>Translate</u></html>");
            send.setToolTipText("  Send text to translate");
            folders.setToolTipText("            Select a file");
            menu.setToolTipText("      Set the language");
            second_lang.setToolTipText("           Translate to");
            first_lang.setToolTipText("        Translate from");
            field2.setToolTipText(" Copy text to clipboard ");
            l1.set(0);
            switch_lang.setToolTipText("     Switch languages");
            if(first_lang.getText().equals("EN.")) {
                text1.setText(" \uD83C\uDF0F English: ");
                text2.setText(" \uD83C\uDF0F Romanian: ");
            } else {
                text2.setText(" \uD83C\uDF0F English: ");
                text1.setText(" \uD83C\uDF0F Romanian: ");
            }
        });
        en.setBackground(Color.decode("#1c1c1c"));
        en.setForeground(Color.decode("#fefefe"));
        menu.add(en);
    }

    public static void main(String[] args) {
        gui = new Main();
        gui.setVisible(true);
        gui.setResizable(false);
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gui.getContentPane().setBackground(Color.decode("#1c1c1c"));
        gui.setName("Translate");
        gui.setTitle("Translate");
        JPanel panel = new JPanel();
        panel.setBackground(gui.getContentPane().getBackground());
        panel.setFocusable(true);
        panel.requestFocusInWindow();
        panel.addKeyListener(new MyKeyListener());
        gui.getContentPane().add(panel);
        gui.setSize(465, 465);
        gui.setLocale(Locale.ENGLISH);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int height = screenSize.height;
        int width = screenSize.width;
        gui.setLocation(width / 2 - gui.getSize().width / 2, height / 2 - gui.getSize().height / 2);
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); //com.sun.java.swing.plaf.windows.WindowsLookAndFeel
        }  catch(Exception e) {
            e.printStackTrace();
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

    static class MyKeyListener extends KeyAdapter {
        @Override
        public void keyTyped(KeyEvent e) {
            // nothing
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if(e.getKeyCode() == KeyEvent.VK_Z && e.isControlDown()) {
                try {
                    undo.undo();
                } catch(Exception efg) {
                    efg.printStackTrace();
                }
            } else if(e.getKeyCode() == KeyEvent.VK_R && e.isControlDown()) {
                try {
                    undo.redo();
                } catch(Exception efg) {
                    efg.printStackTrace();
                }
            } else if(e.getKeyCode() == KeyEvent.VK_F && e.isControlDown()) {

                JTextField find = new JTextField();
                find.setBackground(Color.decode("#1c1c1c"));
                find.setForeground(Color.WHITE);

                Object[] menu = {
                        "Search for: ", find
                };

                Button btn = new Button("Find");
                btn.setBackground(Color.decode("#1c1c1c"));
                btn.setForeground(Color.WHITE);
                btn.addActionListener(e12 -> {
                    try {
                        highlight(field1, find.getText());
                        highlight(field2, find.getText());
                    } catch (Exception er) {
                        er.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Couldn't find text!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    JOptionPane.getRootFrame().dispose();
                });

                Button btn1 = new Button("Cancel");
                btn1.setBackground(Color.decode("#1c1c1c"));
                btn1.setForeground(Color.WHITE);
                btn1.addActionListener(e1 -> JOptionPane.getRootFrame().dispose());

                Object[] options = {
                    btn, btn1
                };

                JOptionPane.showOptionDialog(null, menu, "Find text", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, null);


            } else if(e.getKeyCode() == KeyEvent.VK_H && e.isControlDown()) {
                JTextField replace1 = new JTextField();
                JTextField replace2 = new JTextField();
                replace1.setBackground(Color.decode("#1c1c1c"));
                replace1.setForeground(Color.WHITE);
                replace2.setBackground(Color.decode("#1c1c1c"));
                replace2.setForeground(Color.WHITE);

                Object[] menu = {
                        "Search for: ", replace1,
                        "Replace with: ", replace2
                };

                String s = field1.getText();

                Button btn = new Button("Replace");
                btn.setBackground(Color.decode("#1c1c1c"));
                btn.setForeground(Color.WHITE);
                btn.addActionListener(e12 -> {
                    try {
                        String str = s.replaceAll(replace1.getText(), replace2.getText());
                        field1.setText(str + "" + str.charAt(str.length() - 1));
                    } catch(Exception er) {
                        er.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Couldn't replace!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    JOptionPane.getRootFrame().dispose();
                });

                Button btn1 = new Button("Cancel");
                btn1.setBackground(Color.decode("#1c1c1c"));
                btn1.setForeground(Color.WHITE);
                btn1.addActionListener(e1 -> {
                    field1.setText(s + s.charAt(s.length() - 1));
                    JOptionPane.getRootFrame().dispose();
                });

                Object[] options = {
                        btn, btn1
                };
                JOptionPane.showOptionDialog(null, menu, "Replace", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, null);

            } else if(e.getKeyCode() == KeyEvent.VK_F1 && e.isControlDown()) {
                removeHighlights(field1);
                removeHighlights(field2);
            } else if(e.getKeyCode() == KeyEvent.VK_F2 && e.isControlDown()) {
                if(first_lang.getText().equals("EN.")) {
                    first_lang.setText("RO.");
                    second_lang.setText("EN.");
                    String s1 = text1.getText();
                    String s2 = text2.getText();
                    text1.setText(s2);
                    text2.setText(s1);
                } else {
                    first_lang.setText("EN.");
                    second_lang.setText("RO.");
                    String s1 = text1.getText();
                    String s2 = text2.getText();
                    text1.setText(s2);
                    text2.setText(s1);
                }
            } else if(e.getKeyCode() == KeyEvent.VK_F3 && e.isControlDown()) {
                if(!field1.getText().isEmpty()) {
                    if (first_lang.getText().equals("EN.")) {
                        try {
                            String val = field1.getText();
                            String[] arr1 = val.split(" ");
                            String ans = "";
                            for(String s : arr1) {
                                if(s.equals("%")) {
                                    ans = ans.concat("%");
                                } else {
                                    String s1 = GoogleTranslate.translate("en", "ro", s);
                                    ans = ans.concat(s1 + " ");
                                }
                            }
                            ans = ans.replace("\\u0026", "&");
                            ans = ans.replace("\\u003d", "=");
                            ans = ans.replace("\\u003c", "<");
                            ans = ans.replace("\\u003e", ">");
                            field2.setText(ans);
                        } catch (IOException er) {
                            er.printStackTrace();
                        }
                    } else {
                        try {
                            String val = field1.getText();
                            String[] arr1 = val.split(" ");
                            String ans = "";
                            for(String s : arr1) {
                                if(s.equals("%")) {
                                    ans = ans.concat("%");
                                } else {
                                    String s1 = GoogleTranslate.translate("ro", "en", s);
                                    ans = ans.concat(s1 + " ");
                                }
                            }
                            ans = ans.replace("\\u0026", "&");
                            ans = ans.replace("\\u003d", "=");
                            ans = ans.replace("\\u003c", "<");
                            ans = ans.replace("\\u003e", ">");
                            field2.setText(ans);
                        } catch (IOException er) {
                            er.printStackTrace();
                        }
                    }
                }
            } else if(e.getKeyCode() == KeyEvent.VK_F4 && e.isControlDown()) {
                UIManager.put("Panel.background", new ColorUIResource(238, 238, 238));
                jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
                if(gui.getTitle().equals("Translate")) {
                    jfc.setDialogTitle("Select a file");
                } else {
                    jfc.setDialogTitle("Selectează o filă");
                }
                int returnValue = jfc.showSaveDialog(null);
                if(returnValue == JFileChooser.APPROVE_OPTION) {
                    File file = jfc.getSelectedFile();
                    try {
                        Scanner scan = new Scanner(file);
                        String value = "";
                        while(scan.hasNext()) {
                            String a = scan.nextLine();
                            value = value.concat(a + '\n');
                        }
                        field1.setText(value);
                    } catch(FileNotFoundException ex) {
                        ex.printStackTrace();
                    }
                }
                UIManager.put("Panel.background", Color.decode("#1c1c1c"));
            } else if(e.getKeyCode() == KeyEvent.VK_F5 && e.isControlDown()) {
                if(gui.getTitle().equals("Translate")) {
                    gui.setTitle("Traducător");
                    lang.setText("<html><u>Traducător</u></html>");
                    first_lang.setToolTipText("            Tradu din");
                    switch_lang.setToolTipText("     Inversează limbile");
                    second_lang.setToolTipText("            Tradu în");
                    send.setToolTipText(" Scrie text pt traducere");
                    folders.setToolTipText("        Selectează o filă");
                    l1.set(1);
                    menu.setToolTipText("         Setează limba");
                    if(first_lang.getText().equals("EN.")) {
                        text1.setText(" \uD83C\uDF0F Engleză: ");
                        text2.setText(" \uD83C\uDF0F Română: ");
                    } else {
                        text2.setText(" \uD83C\uDF0F Engleză: ");
                        text1.setText(" \uD83C\uDF0F Română: ");
                    }
                } else {
                    gui.setTitle("Translate");
                    lang.setText("<html><u>Translate</u></html>");
                    send.setToolTipText("  Send text to translate");
                    folders.setToolTipText("            Select a file");
                    menu.setToolTipText("      Set the language");
                    second_lang.setToolTipText("           Translate to");
                    first_lang.setToolTipText("        Translate from");
                    l1.set(0);
                    switch_lang.setToolTipText("     Switch languages");
                    if(first_lang.getText().equals("EN.")) {
                        text1.setText(" \uD83C\uDF0F English: ");
                        text2.setText(" \uD83C\uDF0F Romanian: ");
                    } else {
                        text2.setText(" \uD83C\uDF0F English: ");
                        text1.setText(" \uD83C\uDF0F Romanian: ");
                    }
                }
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            // nothing
        }

        private static void highlight(JTextComponent textComp, String pattern) {
            removeHighlights(textComp);

            try {
                Highlighter hilite = textComp.getHighlighter();
                Document doc = textComp.getDocument();
                String text = doc.getText(0, doc.getLength());

                int pos = 0;
                while ((pos = text.indexOf(pattern, pos)) >= 0) {
                    hilite.addHighlight(pos, pos + pattern.length(), myHighlightPainter);
                    pos += pattern.length();
                }

            } catch (BadLocationException e) {
                System.out.println("Error");
            }
        }

        private static void removeHighlights(JTextComponent textComp) {
            Highlighter hilite = textComp.getHighlighter();
            Highlighter.Highlight[] hilites = hilite.getHighlights();

            for(Highlighter.Highlight highlight : hilites) {
                if (highlight.getPainter() instanceof MyHighlightPainter) {
                    hilite.removeHighlight(highlight);
                }
            }
        }


        public static class MyHighlightPainter extends DefaultHighlighter.DefaultHighlightPainter {

            private MyHighlightPainter(Color color) {
                super(color);
            }
        }
    }

    public static final class GoogleTranslate { // Class marked as final since all methods are static

        private final static String GOOGLE_TRANSLATE_URL = "http://translate.google.com/translate_a/single";

        private GoogleTranslate(){}


        /** Completes the complicated process of generating the URL
         * @param sourceLanguage The source language
         * @param targetLanguage The target language
         * @param text The text that you wish to generate
         * @return The generated URL as a string.
         */

        private static String generateURL(String sourceLanguage, String targetLanguage, String text)
                throws UnsupportedEncodingException{
            String encoded = URLEncoder.encode(text, "UTF-8"); //Encode
            return GOOGLE_TRANSLATE_URL +
                    "?client=webapp" + //The client parameter
                    "&hl=en" + //The language of the UI?
                    "&sl=" + //Source language
                    sourceLanguage +
                    "&tl=" + //Target language
                    targetLanguage +
                    "&q=" +
                    encoded +
                    "&multires=1" +//Necessary but unknown parameters
                    "&otf=0" +
                    "&pc=0" +
                    "&trs=1" +
                    "&ssel=0" +
                    "&tsel=0" +
                    "&kc=1" +
                    "&dt=t" +//This parameters requests the translated text back.
                    //Other dt parameters request additional information such as pronunciation, and so on.
                    "&ie=UTF-8" + //Input encoding
                    "&oe=UTF-8" + //Output encoding
                    "&tk=" + //Token authentication parameter
                    generateToken(text);
        }



        /**
         * Translate text from sourceLanguage to targetLanguage
         * Specifying the sourceLanguage greatly improves accuracy over short Strings
         * @param sourceLanguage The language you want to translate from in ISO-639 format
         * @param targetLanguage The language you want to translate into in ISO-639 format
         * @param text The text you actually want to translate
         * @return the translated text.
         * @throws IOException if it cannot complete the request
         */

        public static String translate(String sourceLanguage, String targetLanguage, String text) throws IOException{
            String urlText = generateURL(sourceLanguage, targetLanguage, text);
            URL url = new URL(urlText);
            String rawData = urlToText(url);//Gets text from Google
            String[] raw =  rawData.split("\"");//Parses the JSON
            if(raw.length<2){
                return null;
            }
            return raw[1];//Returns the translation
        }

        /**
         * Converts a URL to Text
         * @param url that you want to generate a String from
         * @return The generated String
         * @throws IOException if it cannot complete the request
         */

        private static String urlToText(URL url) throws IOException{
            URLConnection urlConn = url.openConnection();
            urlConn.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:2.0) Gecko/20100101 Firefox/4.0");
            Reader r = new java.io.InputStreamReader(urlConn.getInputStream(), StandardCharsets.UTF_8);
            StringBuilder buf = new StringBuilder();
            while (true) {
                int ch = r.read();
                if (ch < 0)
                    break;
                buf.append((char) ch);
            }
            return buf.toString();
        }


        /**
         * This function generates the int array for translation acting as the seed for the hashing algorithm.
         */
        private static int[] TKK() {
            return new int[]{ 0x6337E, 0x217A58DC + 0x5AF91132};
        }

        /**
         * An implementation of an unsigned right shift.
         * Necessary since Java does not have unsigned ints.
         * @param x The number you wish to shift.
         * @param bits The number of bytes you wish to shift.
         * @return The shifted number, unsigned.
         */

        private static int shr32(int x, int bits) {
            if (x < 0) {
                long x_l = 0xffffffffL + x + 1;
                return (int) (x_l >> bits);
            }
            return x >> bits;
        }

        private static int RL(int a, String b) { // I am not entirely sure what this magic does.
            for (int c = 0; c < b.length() - 2; c += 3) {
                int d = b.charAt(c + 2);
                d = d >= 65 ? d - 87 : d - 48;
                d = b.charAt(c + 1) == '+' ? shr32(a, d) : (a << d);
                a = b.charAt(c) == '+' ? (a + (d)) : a ^ d;
            }
            return a;
        }

        /**
         * Generates the token needed for translation.
         * @param text The text you want to generate the token for.
         * @return The generated token as a string.
         */

        private static String generateToken(String text) {
            int[] tkk = TKK();
            int b = tkk[0];
            int e = 0;
            int f = 0;
            List<Integer> d = new ArrayList<>();
            for (; f < text.length(); f++) {
                int g = text.charAt(f);
                if (0x80 > g) {
                    d.add(e++, g);
                } else {
                    if (0x800 > g) {
                        d.add(e++, g >> 6 | 0xC0);
                    } else {
                        if (0xD800 == (g & 0xFC00) && f + 1 < text.length() &&
                                0xDC00 == (text.charAt(f + 1) & 0xFC00)) {
                            g = 0x10000 + ((g & 0x3FF) << 10) + (text.charAt(++f) & 0x3FF);
                            d.add(e++, g >> 18 | 0xF0);
                            d.add(e++, g >> 12 & 0x3F | 0x80);
                        } else {
                            d.add(e++, g >> 12 | 0xE0);
                            d.add(e++, g >> 6 & 0x3F | 0x80);
                        }
                    }
                    d.add(e++, g & 63 | 128);
                }
            }

            int a_i = b;
            for (e = 0; e < d.size(); e++) {
                a_i += d.get(e);
                a_i = RL(a_i, "+-a^+6");
            }
            a_i = RL(a_i, "+-3^+b+-f");
            a_i ^= tkk[1];
            long a_l;
            if (0 > a_i) {
                a_l = 0x80000000L + (a_i & 0x7FFFFFFF);
            } else {
                a_l = a_i;
            }
            a_l %= Math.pow(10, 6);
            return String.format(Locale.US, "%d.%d", a_l, a_l ^ b);
        }
    }
}
