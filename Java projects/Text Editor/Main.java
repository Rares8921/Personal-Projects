package com.kickstart;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.text.*;
import javax.swing.undo.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.*;
import java.io.*;
import java.util.*;

public class Main extends JFrame {

    private JMenuItem theme;
    private JMenu file;
    private JMenuItem New;
    private JMenuItem open;
    private JMenuItem save;
    private JMenuItem exit;
    private JMenu settings;
    private JMenu help;
    private JMenuItem Default;
    private JMenuItem font;
    private static JTextArea text;
    private JMenuBar bar;
    private static Highlighter.HighlightPainter myHighlightPainter;
    private static JFileChooser jfc;
    private static TextLineNumber tln;
    private static Main gui;
    private static UndoManager undo;

    public static String nameOfTheme = "";
    public static Integer fontSize = 16;

    private String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();


    private Main() {
        setLayout(new FlowLayout(FlowLayout.LEFT));
        setUIFont (new javax.swing.plaf.FontUIResource("Sans Serif", Font.BOLD,13));

        ImageIcon image = new ImageIcon("C:\\Users\\user\\Downloads\\icon4.png");
        setIconImage(image.getImage());

        myHighlightPainter = new MyHighlightPainter(Color.decode("#c2c2c2"));

        UIManager.put("FileChooser.saveButtonText", "OK");
        UIManager.put("FileChooser.saveButtonToolTipText", "Do action with selected file");
        UIManager.put("Menu.selectionBackground", Color.WHITE);
        UIManager.put("Menu.selectionForeground", Color.BLACK);
        UIManager.put("Menu.background", Color.WHITE);
        UIManager.put("Menu.foreground", Color.BLACK);
        UIManager.put("Menu.opaque", false);
        UIManager.put("MenuItem.selectionBackground", Color.WHITE);
        UIManager.put("MenuItem.selectionForeground", Color.BLACK);
        UIManager.put("MenuItem.background", Color.WHITE);
        UIManager.put("MenuItem.foreground", Color.BLACK);
        UIManager.put("MenuItem.opaque", true);
        UIManager.put("Menu.border", null);
        UIManager.put("MenuItem.border", null);
        UIManager.put("TextArea.selectionBackground", new Color(0,0,0,0));
        UIManager.put("TextArea.selectionForeground", Color.decode("#999999"));

        getContentPane().setBackground(Color.WHITE);

        bar = new JMenuBar();
        bar.setBorder(BorderFactory.createRaisedBevelBorder());
        bar.setBackground(Color.WHITE);
        bar.setForeground(Color.WHITE);
        setJMenuBar(bar);

        file = new JMenu("File") {
            @Override
            public JToolTip createToolTip() {
                return (new CustomJToolTip(this));
            }
        };
        file.setPreferredSize(new Dimension(40, 30));
        file.setFont(file.getFont().deriveFont(16.0f));
        file.setToolTipText("   File menu   ");
        file.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        bar.add(file);

        New = new JMenuItem("New") {
            @Override
            public JToolTip createToolTip() {
                return (new CustomJToolTip(this));
            }
        };
        New.setFont(New.getFont().deriveFont(15.0f));
        New.setToolTipText("   New file   ");
        New.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        New.addActionListener(e -> {
            JCTextField create = new JCTextField();
            create.setPlaceholder("Example: file.txt");

            Object[] menu = {
                    "Enter a new file name ( with extension ): ", create
            };

            String[] options = new String[2];
            options[0] = "Create";
            options[1] = "Cancel";

            int value = JOptionPane.showOptionDialog(gui.getContentPane(), menu, "New file", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, null);
            if(value == 0) {
                jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
                jfc.setDialogTitle("New file");
                File file = new File(create.getText());
                jfc.setSelectedFile(file);
                jfc.setDialogType(0);
                int returnValue = jfc.showSaveDialog(null);
                setTitle(create.getText() + " - Text editor");
                try {
                    if(returnValue == JFileChooser.APPROVE_OPTION) {
                        File new_file = jfc.getSelectedFile();
                        if(!new_file.createNewFile()) {
                            JOptionPane.showMessageDialog(gui.getContentPane(), "File already exists!", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        System.out.println("Canceled");
                    }
                } catch(Exception efg) {
                    efg.printStackTrace();
                    JOptionPane.showMessageDialog(gui.getContentPane(), "Couldn't create a new file!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                System.out.println("Canceled");
            }
        });
        file.add(New);

        open = new JMenuItem("Open") {
            @Override
            public JToolTip createToolTip() {
                return (new CustomJToolTip(this));
            }
        };
        open.setFont(open.getFont().deriveFont(15.0f));
        open.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        open.setToolTipText("   Open file   ");
        open.addActionListener(e -> {
            jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
            jfc.setDialogTitle("Open file");
            int returnValue = jfc.showSaveDialog(null);

            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = jfc.getSelectedFile();
                try {
                    Scanner sc = new Scanner(selectedFile);
                    String value = "";
                    while(sc.hasNextLine()) {
                        String a = sc.nextLine();
                        value = value.concat(a + '\n');
                    }
                    text.setText(value);
                    String name = jfc.getSelectedFile().getName();
                    setTitle(name + " - Text editor");
                } catch(FileNotFoundException ef) {
                    ef.printStackTrace();
                }
            }
        });
        file.add(open);

        save = new JMenuItem("Save") {
            @Override
            public JToolTip createToolTip() {
                return (new CustomJToolTip(this));
            }
        };
        save.setFont(save.getFont().deriveFont(15.0f));
        save.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        save.setToolTipText("   Save file   ");
        save.addActionListener(e -> {
            try {
                String path = jfc.getSelectedFile().getPath();
                try {
                    File file = new File(path);
                    BufferedWriter out = new BufferedWriter(new FileWriter(file));
                    out.write(text.getText());
                    out.close();
                } catch(Exception efg) {
                    efg.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Couldn't save!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch(Exception ef) {
                ef.printStackTrace();
                JOptionPane.showMessageDialog(null, "Couldn't save!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        file.add(save);

        exit = new JMenuItem("Exit") {
            @Override
            public JToolTip createToolTip() {
                return (new CustomJToolTip(this));
            }
        };
        exit.setFont(exit.getFont().deriveFont(15.0f));
        exit.setToolTipText("   Exit editor   ");
        exit.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        exit.addActionListener(e -> {
            System.exit(0); // Quit program
        });
        file.add(exit);

        settings = new JMenu("Settings") {
            @Override
            public JToolTip createToolTip() {
                return (new CustomJToolTip(this));
            }
        };
        settings.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        settings.setFont(settings.getFont().deriveFont(16.0f));
        settings.setToolTipText("   Change styles   ");
        bar.add(settings);

        theme = new JMenuItem("Change theme") {
            @Override
            public JToolTip createToolTip() {
                return (new CustomJToolTip(this));
            }
        };
        theme.setFont(theme.getFont().deriveFont(15.0f));
        theme.setToolTipText("   Change the theme   ");
        theme.addActionListener(e -> {
            final JPanel panel = new JPanel();
            final JRadioButton light_theme = new JRadioButton("Light theme\n");
            final JRadioButton dark_theme = new JRadioButton("Dark theme\n");
            final JRadioButton hacker_theme = new JRadioButton("Hacker theme\n");
            final JRadioButton monokai_theme = new JRadioButton("Kimbie Monokai theme\n");
            final JRadioButton night_blue_theme = new JRadioButton("Night blue theme\n");
            final ButtonGroup group = new ButtonGroup();

            light_theme.setSelected(true);

            panel.add(light_theme);
            panel.add(new JLabel("\n"));
            panel.add(dark_theme);
            panel.add(hacker_theme);
            panel.add(monokai_theme);
            panel.add(night_blue_theme);

            String[] themes = {"Light theme", "Dark theme", "Hacker theme", "Kimbie Monokai theme", "Night blue theme"};
            JComboBox list = new JComboBox(themes);

            group.add(light_theme);
            group.add(dark_theme);
            group.add(hacker_theme);
            group.add(monokai_theme);
            group.add(night_blue_theme);

            int result = JOptionPane.showOptionDialog(null, list, "Change theme", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
            String themeName = (String)list.getSelectedItem();
            if (result == JOptionPane.OK_OPTION && themeName != null) {
                switch (themeName) {
                    case "Light theme":
                        nameOfTheme = "Light";
                        text.setSelectedTextColor(Color.decode("#999999"));
                        removeHighlights(text);
                        myHighlightPainter = new MyHighlightPainter(Color.decode("#c2c2c2"));
                        panel.setBackground(Color.WHITE);
                        getContentPane().setBackground(Color.WHITE);
                        getContentPane().setForeground(Color.BLACK);
                        text.setBackground(Color.WHITE);
                        text.setForeground(Color.BLACK);
                        bar.setBackground(Color.WHITE);
                        bar.setForeground(Color.WHITE);
                        file.setForeground(Color.BLACK);
                        settings.setForeground(Color.BLACK);
                        help.setForeground(Color.BLACK);
                        New.setForeground(Color.BLACK);
                        New.setBackground(Color.WHITE);
                        open.setForeground(Color.BLACK);
                        open.setBackground(Color.WHITE);
                        save.setForeground(Color.BLACK);
                        save.setBackground(Color.WHITE);
                        exit.setForeground(Color.BLACK);
                        exit.setBackground(Color.WHITE);
                        Default.setForeground(Color.BLACK);
                        Default.setBackground(Color.WHITE);
                        font.setForeground(Color.BLACK);
                        font.setBackground(Color.WHITE);
                        theme.setForeground(Color.BLACK);
                        theme.setBackground(Color.WHITE);
                        break;
                    case "Dark theme":
                        nameOfTheme = "Dark";
                        text.setSelectedTextColor(Color.decode("#8c8b8b"));
                        removeHighlights(text);
                        list.getModel().setSelectedItem("Dark theme");
                        myHighlightPainter = new MyHighlightPainter(Color.decode("#6e6e6e"));
                        panel.setBackground(Color.decode("#1E1E1E"));
                        getContentPane().setBackground(Color.decode("#1E1E1E"));
                        getContentPane().setForeground(Color.decode("#ededed"));
                        text.setBackground(Color.decode("#1E1E1E"));
                        text.setForeground(Color.decode("#ededed"));
                        bar.setBackground(Color.decode("#323232"));
                        bar.setForeground(Color.decode("#ededed"));
                        file.setForeground(Color.decode("#ededed"));
                        settings.setForeground(Color.decode("#ededed"));
                        help.setForeground(Color.decode("#ededed"));
                        New.setForeground(Color.decode("#ededed"));
                        New.setBackground(Color.decode("#323232"));
                        open.setForeground(Color.decode("#ededed"));
                        open.setBackground(Color.decode("#323232"));
                        //save, exit, Default, font
                        save.setForeground(Color.decode("#ededed"));
                        save.setBackground(Color.decode("#323232"));
                        exit.setForeground(Color.decode("#ededed"));
                        exit.setBackground(Color.decode("#323232"));
                        Default.setForeground(Color.decode("#ededed"));
                        Default.setBackground(Color.decode("#323232"));
                        font.setForeground(Color.decode("#ededed"));
                        font.setBackground(Color.decode("#323232"));
                        theme.setBackground(Color.decode("#323232"));
                        theme.setForeground(Color.decode("#ededed"));
                        break;
                    case "Hacker theme":
                        nameOfTheme = "Hacker";
                        text.setSelectedTextColor(Color.decode("#3cba2f"));
                        removeHighlights(text);
                        list.getModel().setSelectedItem("Hacker theme");
                        myHighlightPainter = new MyHighlightPainter(Color.decode("#0e2e0b"));
                        panel.setBackground(Color.decode("#000000"));
                        getContentPane().setBackground(Color.decode("#000000"));
                        getContentPane().setForeground(Color.decode("#1D5F16"));
                        text.setBackground(Color.decode("#000000"));
                        text.setForeground(Color.decode("#1D5F16"));
                        bar.setBackground(Color.decode("#0f0f0f"));
                        bar.setForeground(Color.decode("#1D5F16"));
                        file.setForeground(Color.decode("#1D5F16"));
                        settings.setForeground(Color.decode("#1D5F16"));
                        help.setForeground(Color.decode("#1D5F16"));
                        New.setForeground(Color.decode("#1D5F16"));
                        New.setBackground(Color.decode("#1E1E1E"));
                        open.setForeground(Color.decode("#1D5F16"));
                        open.setBackground(Color.decode("#1E1E1E"));
                        //save, exit, Default, font
                        save.setForeground(Color.decode("#1D5F16"));
                        save.setBackground(Color.decode("#1E1E1E"));
                        exit.setForeground(Color.decode("#1D5F16"));
                        exit.setBackground(Color.decode("#1E1E1E"));
                        Default.setForeground(Color.decode("#1D5F16"));
                        Default.setBackground(Color.decode("#1E1E1E"));
                        font.setForeground(Color.decode("#1D5F16"));
                        font.setBackground(Color.decode("#1E1E1E"));
                        theme.setBackground(Color.decode("#1E1E1E"));
                        theme.setForeground(Color.decode("#1D5F16"));
                        break;
                    case "Kimbie Monokai theme":
                        nameOfTheme = "Kimbie";
                        text.setSelectedTextColor(Color.decode("#fcb068"));
                        removeHighlights(text);
                        list.getModel().setSelectedItem("Kimbie Monokai theme");
                        myHighlightPainter = new MyHighlightPainter(Color.decode("#4f3c23"));
                        panel.setBackground(Color.decode("#221A0F"));
                        getContentPane().setBackground(Color.decode("#221A0F"));
                        text.setBackground(Color.decode("#221A0F"));
                        text.setForeground(Color.decode("#b37439"));
                        bar.setBackground(Color.decode("#221A0F"));
                        bar.setForeground(Color.decode("#b37439"));
                        file.setForeground(Color.decode("#b37439"));
                        settings.setForeground(Color.decode("#b37439"));
                        help.setForeground(Color.decode("#b37439"));
                        New.setForeground(Color.decode("#b37439"));
                        New.setBackground(Color.decode("#221A0F"));
                        open.setForeground(Color.decode("#b37439"));
                        open.setBackground(Color.decode("#221A0F"));
                        //save, exit, Default, font
                        save.setForeground(Color.decode("#b37439"));
                        save.setBackground(Color.decode("#221A0F"));
                        exit.setForeground(Color.decode("#b37439"));
                        exit.setBackground(Color.decode("#221A0F"));
                        Default.setForeground(Color.decode("#b37439"));
                        Default.setBackground(Color.decode("#221A0F"));
                        font.setForeground(Color.decode("#b37439"));
                        font.setBackground(Color.decode("#221A0F"));
                        theme.setBackground(Color.decode("#221A0F"));
                        theme.setForeground(Color.decode("#b37439"));
                        break;
                    case "Night blue theme":
                        nameOfTheme = "Night";
                        text.setSelectedTextColor(Color.decode("#0298cf"));
                        removeHighlights(text);
                        myHighlightPainter = new MyHighlightPainter(Color.decode("#6e6e6e"));
                        list.getModel().setSelectedItem("Night blue theme");
                        panel.setBackground(Color.decode("#000C18"));
                        getContentPane().setBackground(Color.decode("#000C18"));
                        text.setBackground(Color.decode("#000C18"));
                        text.setForeground(Color.decode("#ededed"));
                        bar.setBackground(Color.decode("#10182B"));
                        bar.setForeground(Color.decode("#ededed"));
                        file.setForeground(Color.decode("#ededed"));
                        settings.setForeground(Color.decode("#ededed"));
                        help.setForeground(Color.decode("#ededed"));
                        New.setForeground(Color.decode("#ededed"));
                        New.setBackground(Color.decode("#10182B"));
                        open.setForeground(Color.decode("#ededed"));
                        open.setBackground(Color.decode("#10182B"));
                        //save, exit, Default, font
                        save.setForeground(Color.decode("#ededed"));
                        save.setBackground(Color.decode("#10182B"));
                        exit.setForeground(Color.decode("#ededed"));
                        exit.setBackground(Color.decode("#10182B"));
                        Default.setForeground(Color.decode("#ededed"));
                        Default.setBackground(Color.decode("#10182B"));
                        font.setForeground(Color.decode("#ededed"));
                        font.setBackground(Color.decode("#10182B"));
                        theme.setBackground(Color.decode("#10182B"));
                        theme.setForeground(Color.decode("#ededed"));
                        break;
                }
            } else {
                System.out.print("The user canceled the action.");
            }
        });
        settings.add(theme);

        font = new JMenuItem("Change font") {
            @Override
            public JToolTip createToolTip() {
                return (new CustomJToolTip(this));
            }
        };
        font.setFont(font.getFont().deriveFont(15.0f));
        font.setToolTipText("   Change the font of the text   ");
        font.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        font.addActionListener(e -> {
            JComboBox list = new JComboBox(fonts);
            int result = JOptionPane.showOptionDialog(null, list , "Change font", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
            if (result == JOptionPane.OK_OPTION) {
                String fontName = (String)list.getSelectedItem();
                if(fontName != null) {
                    for(int i = 0; i < 256; ++i) {
                        if(fontName.equals(fonts[i])) {
                            setJavaFont(fonts[i]);
                        setUIFont(new javax.swing.plaf.FontUIResource(fonts[i], Font.BOLD, 16));
                        break;
                        }
                    }
                }
            } else {
                System.out.println("The action was canceled.");
            }
        });
        settings.add(font);

        Default = new JMenuItem("Default") {
            @Override
            public JToolTip createToolTip() {
                return (new CustomJToolTip(this));
            }
        };
        Default.setFont(Default.getFont().deriveFont(15.0f));
        Default.setToolTipText("   Reset the font and the theme   ");
        Default.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        Default.addActionListener(e -> {
            int result = JOptionPane.showOptionDialog(null, "Change settings.\nAre you sure?", "Change theme", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, null, null);
            if(result == JOptionPane.YES_OPTION) {
                nameOfTheme = "Light";
                text.setSelectedTextColor(Color.decode("#999999"));
                getContentPane().setBackground(Color.WHITE);
                getContentPane().setForeground(Color.BLACK);
                text.setBackground(Color.WHITE);
                text.setForeground(Color.BLACK);
                bar.setBackground(Color.WHITE);
                bar.setForeground(Color.WHITE);
                file.setForeground(Color.BLACK);
                settings.setForeground(Color.BLACK);
                help.setForeground(Color.BLACK);
                New.setForeground(Color.BLACK);
                New.setBackground(Color.WHITE);
                open.setForeground(Color.BLACK);
                open.setBackground(Color.WHITE);
                save.setForeground(Color.BLACK);
                save.setBackground(Color.WHITE);
                exit.setForeground(Color.BLACK);
                exit.setBackground(Color.WHITE);
                Default.setForeground(Color.BLACK);
                Default.setBackground(Color.WHITE);
                font.setForeground(Color.BLACK);
                font.setBackground(Color.WHITE);
                theme.setForeground(Color.BLACK);
                theme.setBackground(Color.WHITE);
                text.setFont(new Font("Sans Serif",Font.BOLD,15));
                tln.setFont(new Font("Sans Serif",Font.BOLD,16));
                help.setFont(new Font("Sans Serif",Font.BOLD,16));
                Default.setFont(new Font("Sans Serif",Font.BOLD,15));
                font.setFont(new Font("Sans Serif",Font.BOLD,15));
                theme.setFont(new Font("Sans Serif",Font.BOLD,15));
                settings.setFont(new Font("Sans Serif",Font.BOLD,16));
                exit.setFont(new Font("Sans Serif",Font.BOLD,15));
                save.setFont(new Font("Sans Serif",Font.BOLD,15));
                open.setFont(new Font("Sans Serif",Font.BOLD,15));
                New.setFont(new Font("Sans Serif",Font.BOLD,15));
                file.setFont(new Font("Sans Serif",Font.BOLD,16));
                setUIFont (new javax.swing.plaf.FontUIResource("Sans Serif", Font.BOLD,13));
            } else {
                System.out.print("The action was canceled.");
            }
        });
        settings.add(Default);

        help = new JMenu("Help") {
            @Override
            public JToolTip createToolTip() {
                return (new CustomJToolTip(this));
            }
        };
        help.setFont(help.getFont().deriveFont(16.0f));
        help.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        help.addMenuListener(new MenuListener() {
            @Override
            public void menuSelected(MenuEvent e) {
                JOptionPane.showOptionDialog(null, "What is this program? \nThis text editor is a basic text-editing program and it's most commonly used to view or edit text files.\nA text file is a file type typically identified by the .txt file name extension.\n\nHow do I change the font and theme?\n1. Go to 'Settings' menu\n2. Click on 'Change font' or 'Change settings' category and choose.\n(!) If u want to reset the settings, press the default button.\n\nWhat are the shortcuts for paste, delete, etc.?\n* To copy, do CTRL + C.\n* To paste, do CTRL + V.\n* To undo, do CTRL + Z.\n* To redo, do CTRL + R.\n* To replace, do CTRL + H.\n* To delete, do CTRL + X.\n* To select all text, do CTRL + A.\n\nHow can I work with the editor?\n* If you want to create a file, go to 'File' menu and select 'New' or, do CTRL + N.\n* If you want to open a file, go to 'File' menu and select 'Open' or, do CTRL + O.\n* If you want to save a file, go to 'File' menu and select 'Save' or, do CTRL + S.", "Help", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
            }
            @Override
            public void menuDeselected(MenuEvent e) {
            }

            @Override
            public void menuCanceled(MenuEvent e) {
            }

        });
        help.setToolTipText("   View info   ");
        bar.add(help);


        undo = new UndoManager();

        text = new JTextArea(46, 850);
        text.getDocument().addUndoableEditListener(undo);
        text.setBorder(null);
        text.setWrapStyleWord(true);
        text.setLineWrap(true);
        text.setTabSize(2);
        text.setFont(new Font("Sans Serif", Font.BOLD, fontSize));
        text.addKeyListener(new MyKeyListener());
        JScrollPane scrollPane = new JScrollPane(text);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setComponentZOrder(scrollPane.getVerticalScrollBar(), 0);
        scrollPane.setComponentZOrder(scrollPane.getViewport(), 0);
        scrollPane.getVerticalScrollBar().setOpaque(false);
        scrollPane.setWheelScrollingEnabled(true);
        scrollPane.addMouseWheelListener(new MyMouseListener());
        scrollPane.setBorder(null);
        tln = new TextLineNumber(text);
        tln.setFont(text.getFont());
        tln.setBackground(text.getBackground());
        scrollPane.setRowHeaderView(tln);
        scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            private final Dimension d = new Dimension();
            @Override protected JButton createDecreaseButton(int orientation) {
                return new JButton() {
                    @Override public Dimension getPreferredSize() {
                        return d;
                    }
                };
            }
            @Override protected JButton createIncreaseButton(int orientation) {
                return new JButton() {
                    @Override public Dimension getPreferredSize() {
                        return d;
                    }
                };
            }
            @Override
            protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
                Graphics2D g2 = (Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                Color color;
                JScrollBar sb = (JScrollBar)c;
                if(!sb.isEnabled() || r.width > r.height) {
                    return;
                } else if(isDragging) {
                    color = new Color(0,0,0,0);
                } else if(isThumbRollover()) {
                    color = new Color(0,0,0,0);
                } else {
                    color = new Color(0,0,0,0);
                }
                g2.setPaint(color);
                g2.fillRoundRect(r.x, r.y, r.width, r.height, 10, 10);
                g2.drawRoundRect(r.x, r.y, r.width, r.height, 10, 10);
                g2.dispose();
            }
        });
        add(scrollPane);
        setVisible(true);
    }

    public static void main(String[] args) {
        gui = new Main();
        gui.setVisible(true);
        gui.setSize(1200, 800);
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gui.setResizable(true);
        gui.setTitle("Text editor");
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int height = screenSize.height;
        int width = screenSize.width;
        gui.setLocation(width / 2 - gui.getSize().width / 2, height / 2 - gui.getSize().height / 2 - 10);
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private static void setUIFont (javax.swing.plaf.FontUIResource f) {
        java.util.Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get (key);
            if (value instanceof javax.swing.plaf.FontUIResource)
                UIManager.put (key, f);
        }
    }

    private void setJavaFont(String fontName) {
        text.setFont(new Font(fontName, Font.BOLD,15));
    }

    static class MyKeyListener extends KeyAdapter {
        @Override
        public void keyTyped(KeyEvent e) {}

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_S && e.isControlDown()) {
                try {
                    String path = jfc.getSelectedFile().getPath();
                    try {
                        File file = new File(path);
                        BufferedWriter out = new BufferedWriter(new FileWriter(file));
                        out.write(text.getText());
                        out.close();
                    } catch(Exception efg) {
                        efg.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Couldn't save!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch(Exception ef) {
                    ef.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Couldn't save!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else if(e.getKeyCode() == KeyEvent.VK_O && e.isControlDown()) {
                jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
                jfc.setDialogTitle("Open file");
                int returnValue = jfc.showSaveDialog(null);

                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = jfc.getSelectedFile();
                    try {
                        Scanner sc = new Scanner(selectedFile);
                        String value = "";
                        while(sc.hasNextLine()) {
                            String a = sc.nextLine();
                            value = value.concat(a + '\n');
                        }
                        text.setText(value);
                        String name = jfc.getSelectedFile().getName();
                        gui.setTitle(name + " - Text editor");
                    } catch(FileNotFoundException ef) {
                        ef.printStackTrace();
                    }
                }
            } else if(e.getKeyCode() == KeyEvent.VK_H && e.isControlDown()) {
                JTextField replace1 = new JTextField();
                JTextField replace2 = new JTextField();
                Object[] menu = {
                        "Search for: ", replace1,
                        "Replace with: ", replace2
                };
                String[] options = new String[2];
                options[0] = "Replace";
                options[1] = "Cancel";

                int option = JOptionPane.showOptionDialog(gui.getContentPane(), menu, "Replace", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, null);
                if(option == 0) {
                    try {
                        if(replace1.getText().length() > 0 && replace2.getText().length() > 0) {
                            String value = text.getText().replaceAll(replace1.getText(), replace2.getText());
                            text.setText(value + value.charAt(value.length() - 1));
                        } else {
                            JOptionPane.showMessageDialog(gui.getContentPane(), "The fields must be completed!", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch(Exception er) {
                        er.printStackTrace();
                        JOptionPane.showMessageDialog(gui.getContentPane(), "Couldn't replace!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    System.out.println("Canceled");
                }
            } else if(e.getKeyCode() == KeyEvent.VK_Z && e.isControlDown()) {
                try {
                    undo.undo();
                } catch(Exception efg) {
                    efg.printStackTrace();
                    System.out.println("Error");
                }
            } else if(e.getKeyCode() == KeyEvent.VK_R && e.isControlDown()) {
                try {
                    undo.redo();
                } catch(Exception efg) {
                    efg.printStackTrace();
                    System.out.println("Error");
                }
            } else if(e.getKeyCode() == KeyEvent.VK_F && e.isControlDown()) {
                JTextField find = new JTextField();

                Object[] menu = {
                        "Search for: ", find
                };

                String[] option = new String[1];
                option[0] = "Find";
                int option1 = JOptionPane.showOptionDialog(gui.getContentPane(), menu, "Find text", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, option, null);
                if(option1 == 0) {
                    try {
                        if(find.getText().length() > 0) {
                            highlight(text, find.getText());
                        } else {
                            JOptionPane.showMessageDialog(gui.getContentPane(), "The field must be completed!", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception er) {
                        er.printStackTrace();
                        JOptionPane.showMessageDialog(gui.getContentPane(), "Couldn't find text!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    System.out.println("Canceled");
                }
            } else if(e.getKeyCode() == KeyEvent.VK_N && e.isControlDown()) {
                JCTextField create = new JCTextField();
                create.setPlaceholder("Example: file.txt");
                Object[] menu = {
                        "Enter a new file name: ", create
                };

                String[] options = new String[2];
                options[0] = "Create";
                options[1] = "Cancel";

                int value = JOptionPane.showOptionDialog(gui.getContentPane(), menu, "New file", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, null);
                if(value == 0) {
                    jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
                    jfc.setDialogTitle("New file");
                    File file = new File(create.getText());
                    jfc.setSelectedFile(file);
                    jfc.setDialogType(1);
                    int returnValue = jfc.showSaveDialog(null);
                    gui.setTitle(create.getText() + " - Text editor");
                    try {
                        if(returnValue == JFileChooser.APPROVE_OPTION) {
                            File new_file = jfc.getSelectedFile();
                            if(!new_file.createNewFile()) {
                                JOptionPane.showMessageDialog(gui.getContentPane(), "File already exists!", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        } else {
                            System.out.println("Canceled");
                        }
                    } catch(Exception efg) {
                        efg.printStackTrace();
                        JOptionPane.showMessageDialog(gui.getContentPane(), "Couldn't create a new file!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    System.out.println("Canceled");
                }
            }
        }


        @Override
        public void keyReleased(KeyEvent e) {}
    }

    static class MyMouseListener extends JPanel implements MouseWheelListener {
        MyMouseListener() {
            addMouseWheelListener(this);
        }
        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            if(e.isControlDown()) {
                if (e.getWheelRotation() < 0) { // wheel up
                    fontSize++;
                } else { // wheel down
                    fontSize--;
                }
                text.setFont(text.getFont().deriveFont(fontSize * 1f));
                tln.setFont(text.getFont());
            }
        }

    }

    private static class CustomJToolTip extends JToolTip {
        public CustomJToolTip(JComponent component) {
            super();
            setComponent(component);
            setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
            switch(nameOfTheme) {
                case "Dark":
                    setBackground(Color.decode("#1E1E1E"));
                    setForeground(Color.decode("#ededed"));
                    setBorder(BorderFactory.createLineBorder(Color.decode("#ededed")));
                    break;
                case "Hacker":
                    setBackground(Color.decode("#000000"));
                    setForeground(Color.decode("#1D5F16"));
                    setBorder(BorderFactory.createLineBorder(Color.decode("#1D5F16")));
                    break;
                case "Kimbie":
                    setBackground(Color.decode("#221A0F"));
                    setForeground(Color.decode("#b37439"));
                    setBorder(BorderFactory.createLineBorder(Color.decode("#b37439")));
                    break;
                case "Night":
                    setBackground(Color.decode("#000C18"));
                    setForeground(Color.decode("#ededed"));
                    setBorder(BorderFactory.createLineBorder(Color.decode("#ededed")));
                    break;
                default:
                    setBackground(Color.WHITE);
                    setForeground(Color.BLACK);
                    setBorder(BorderFactory.createLineBorder(Color.BLACK));
                    break;
            }
            //setBackground(Color.decode("#2d2e2e"));
            //setBorder(BorderFactory.createLineBorder(Color.decode("#1b1c1c")));
            //setForeground(Color.WHITE);
        }
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

        for (Highlighter.Highlight highlight : hilites) {
            if (highlight.getPainter() instanceof MyHighlightPainter) {
                hilite.removeHighlight(highlight);
            }
        }
    }

    private static class MyHighlightPainter extends DefaultHighlighter.DefaultHighlightPainter {

        private MyHighlightPainter(Color color) {
            super(color);
        }
    }

    public class TextLineNumber extends JPanel
            implements CaretListener, DocumentListener, PropertyChangeListener {

        public void main(String[] args) {
            getContentPane().setBackground(text.getBackground());
        }

        private final static float RIGHT = 0.2f;

        private final  Border OUTER = new MatteBorder(0, 0, 0, 0, text.getBackground());

        private final static int HEIGHT = Integer.MAX_VALUE - 1000000;

        private JTextArea component;

        private boolean updateFont;
        private float digitAlignment;
        private int minimumDisplayDigits;

        private int lastDigits;
        private int lastHeight;
        private int lastLine;

        private HashMap<String, FontMetrics> fonts;
        private TextLineNumber(JTextArea component)
        {
            this(component, 3);
        }

        private TextLineNumber(JTextArea component, int minimumDisplayDigits)
        {
            this.component = component;

            setFont( component.getFont() );

            setBorderGap();
            setDigitAlignment();
            setMinimumDisplayDigits( minimumDisplayDigits );

            component.getDocument().addDocumentListener(this);
            component.addCaretListener( this );
            component.addPropertyChangeListener("font", this);
        }

        private void setBorderGap()
        {
            Border inner = new EmptyBorder(0, 4, 0, 4);
            setBorder( new CompoundBorder(OUTER, inner) );
            lastDigits = 0;
            setPreferredWidth();
        }

        private void setDigitAlignment()
        {
            this.digitAlignment = TextLineNumber.RIGHT;
        }

        private void setMinimumDisplayDigits(int minimumDisplayDigits)
        {
            this.minimumDisplayDigits = minimumDisplayDigits;
            setPreferredWidth();
        }

        private void setPreferredWidth()
        {
            Element root = component.getDocument().getDefaultRootElement();
            int lines = root.getElementCount();
            int digits = Math.max(String.valueOf(lines).length(), minimumDisplayDigits);

            if (lastDigits != digits)
            {
                lastDigits = digits;
                FontMetrics fontMetrics = getFontMetrics( getFont() );
                int width = fontMetrics.charWidth( '0' ) * digits;
                Insets insets = getInsets();
                int preferredWidth = insets.left + insets.right + width;

                Dimension d = getPreferredSize();
                d.setSize(preferredWidth, HEIGHT);
                setPreferredSize( d );
                setSize( d );
            }
        }

        @Override
        public void paintComponent(Graphics g)
        {
            super.paintComponent(g);

            FontMetrics fontMetrics = component.getFontMetrics( component.getFont() );
            Insets insets = getInsets();
            int availableWidth = getSize().width - insets.left - insets.right;

            Rectangle clip = g.getClipBounds();
            int rowStartOffset = component.viewToModel2D(new Point(0, clip.y));
            int endOffset = component.viewToModel2D(new Point(0, clip.y + clip.height));

            while (rowStartOffset <= endOffset)
            {
                try
                {
                    setBackground(text.getBackground());
                    g.setColor(text.getForeground());

                    String lineNumber = getTextLineNumber(rowStartOffset) + ". ";
                    int stringWidth = fontMetrics.stringWidth( lineNumber );
                    int x = getOffsetX(availableWidth, stringWidth) + insets.left;
                    int y = getOffsetY(rowStartOffset, fontMetrics);
                    g.drawString(lineNumber, x, y);

                    rowStartOffset = Utilities.getRowEnd(component, rowStartOffset) + 1;
                }
                catch(Exception e) {break;}
            }
        }

        private String getTextLineNumber(int rowStartOffset)
        {
            Element root = component.getDocument().getDefaultRootElement();
            int index = root.getElementIndex( rowStartOffset );
            Element line = root.getElement( index );

            if (line.getStartOffset() == rowStartOffset)
                return String.valueOf(index + 1);
            else
                return " ";
        }

        private int getOffsetX(int availableWidth, int stringWidth)
        {
            return (int)((availableWidth - stringWidth) * digitAlignment);
        }

        private int getOffsetY(int rowStartOffset, FontMetrics fontMetrics)
                throws BadLocationException
        {

            Rectangle r = component.modelToView( rowStartOffset );
            int lineHeight = fontMetrics.getHeight();
            int y = r.y + r.height;
            int descent = 0;

            if (r.height == lineHeight)
            {
                descent = fontMetrics.getDescent();
            }
            else
            {
                if (fonts == null)
                    fonts = new HashMap<>();

                Element root = component.getDocument().getDefaultRootElement();
                int index = root.getElementIndex( rowStartOffset );
                Element line = root.getElement( index );

                for (int i = 0; i < line.getElementCount(); i++)
                {
                    Element child = line.getElement(i);
                    AttributeSet as = child.getAttributes();
                    String fontFamily = (String)as.getAttribute(StyleConstants.FontFamily);
                    Integer fontSize = (Integer)as.getAttribute(StyleConstants.FontSize);
                    String key = fontFamily + fontSize;

                    FontMetrics fm = fonts.get( key );

                    if (fm == null)
                    {
                        Font font = new Font(fontFamily, Font.PLAIN, fontSize);
                        fm = component.getFontMetrics( font );
                        fonts.put(key, fm);
                    }

                    descent = Math.max(descent, fm.getDescent());
                }
            }

            return y - descent;
        }

        @Override
        public void caretUpdate(CaretEvent e)
        {

            int caretPosition = component.getCaretPosition();
            Element root = component.getDocument().getDefaultRootElement();
            int currentLine = root.getElementIndex( caretPosition );

            if (lastLine != currentLine)
            {
                repaint();
                lastLine = currentLine;
            }
        }

        @Override
        public void changedUpdate(DocumentEvent e)
        {
            documentChanged();
        }

        @Override
        public void insertUpdate(DocumentEvent e)
        {
            documentChanged();
        }

        @Override
        public void removeUpdate(DocumentEvent e)
        {
            documentChanged();
        }

        private void documentChanged()
        {

            SwingUtilities.invokeLater(() -> {
                try
                {
                    int endPos = component.getDocument().getLength();
                    Rectangle rect = component.modelToView(endPos);

                    if (rect != null && rect.y != lastHeight)
                    {
                        setPreferredWidth();
                        repaint();
                        lastHeight = rect.y;
                    }
                }
                catch (BadLocationException ex) { /* nothing to do */ }
            });
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt)
        {
            if (evt.getNewValue() instanceof Font)
            {
                if (updateFont)
                {
                    Font newFont = (Font) evt.getNewValue();
                    setFont(newFont);
                    lastDigits = 0;
                    setPreferredWidth();
                }
                else
                {
                    repaint();
                }
            }
        }
    }
    public static class JCTextField extends JTextField {

        private String placeholder = "";
        private Color phColor= new Color(0,0,0);
        private boolean band = true;

        public JCTextField()  {
            super();
            Dimension d = new Dimension(200, 32);
            setSize(d);
            setPreferredSize(d);
            setVisible(true);
            setMargin( new Insets(3,6,3,6));
            getDocument().addDocumentListener(new DocumentListener() {

                @Override
                public void removeUpdate(DocumentEvent e) {
                    band = getText().length() <= 0;
                }

                @Override
                public void insertUpdate(DocumentEvent e) {
                    band = false;
                }

                @Override
                public void changedUpdate(DocumentEvent de) {}

            });
        }

        public void setPlaceholder(String placeholder)
        {
            this.placeholder=placeholder;
        }

        /*public String getPlaceholder()
        {
            return placeholder;
        }

        public Color getPhColor() {
            return phColor;
        }

        public void setPhColor(Color phColor) {
            this.phColor = phColor;
        }
        */

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor( new Color(phColor.getRed(), phColor.getGreen(), phColor.getBlue(),90));
            g.drawString( (band) ? placeholder:"", getMargin().left, (getSize().height) / 2 + getFont().getSize() / 2 );
        }

    }

}