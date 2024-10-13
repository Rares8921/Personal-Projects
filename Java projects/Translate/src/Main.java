import org.json.JSONObject;

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
                    field2.setText(translate(field1.getText(), "en", "ro"));
                } else {
                    field2.setText(translate(field1.getText(), "ro", "en"));
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
                    ex.printStackTrace(System.err);
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
            e.printStackTrace(System.err);
        }
    }

    public static String translate(String query, String sourceLang, String targetLang) {
        try {
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
            String langPair = sourceLang+"|"+targetLang;
            langPair = URLEncoder.encode(langPair, StandardCharsets.UTF_8);
            String apiUrl = "https://api.mymemory.translated.net/get?q=" + encodedQuery + "&langpair=" + langPair;

            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                JSONObject responseData = getJsonObject(connection);
                String translatedText = responseData.getString("translatedText");
                connection.disconnect();
                return translatedText;
            } else {
                connection.disconnect();
                return "Request failed with response code: " +responseCode;
            }

        } catch (IOException e) {
            e.printStackTrace(System.err);
            return e.getMessage();
        }
    }

    private static JSONObject getJsonObject(HttpURLConnection connection) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        String jsonResponse = response.toString();
        // Parse the JSON response
        JSONObject object = new JSONObject(jsonResponse);
        return object.getJSONObject("responseData");
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
                    efg.printStackTrace(System.err);
                }
            } else if(e.getKeyCode() == KeyEvent.VK_R && e.isControlDown()) {
                try {
                    undo.redo();
                } catch(Exception efg) {
                    efg.printStackTrace(System.err);
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
                        er.printStackTrace(System.err);
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
                        field1.setText(str + str.charAt(str.length() - 1));
                    } catch(Exception er) {
                        er.printStackTrace(System.err);
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
                        field2.setText(translate(field1.getText(), "en", "ro"));
                    } else {
                        field2.setText(translate(field1.getText(), "ro", "en"));
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
                        ex.printStackTrace(System.err);
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


}