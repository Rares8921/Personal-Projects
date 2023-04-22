package com.company;

import javax.swing.*;
import java.awt.event.*;
import java.lang.*;
import java.awt.*;
import java.net.*;
import java.io.*;

public class Main extends JFrame {


    private Main() {

        setLayout(new FlowLayout(FlowLayout.LEFT));

        setSize(422,200);

        getContentPane().setBackground(Color.decode("#111111"));

        JLabel version;
        JLabel version_value;
        JLabel home;
        JLabel home_value;
        JLabel vendor;
        JLabel vendor_value;
        JLabel vendor_url;
        JLabel vendor_url_value;
        JLabel info;

        version = new JLabel("(!) Java Runtime Version -> ");
        version.setFont(version.getFont().deriveFont(14.0f));
        version.setForeground(Color.decode("#ffffff"));
        add(version);

        version_value = new JLabel("" + System.getProperty("java.runtime.version"));
        version_value.setFont(version_value.getFont().deriveFont(14.0f));
        version_value.setForeground(Color.decode("#13ccd6"));
        version_value.setCursor(new Cursor(Cursor.HAND_CURSOR));
        version_value.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI("https://www.java.com/en/download/"));
                } catch (URISyntaxException ex) {
                    ex.printStackTrace();
                } catch(IOException exe) {
                    exe.printStackTrace();
                }
            }
        });
        add(version_value);

        JLabel space = new JLabel("                               ");
        add(space);

        home = new JLabel("(!) Java Home -> ");
        home.setFont(home.getFont().deriveFont(14.0f));
        home.setForeground(Color.decode("#ffffff"));
        add(home);

        home_value = new JLabel("" + System.getProperty("java.home"));
        home_value.setFont(home_value.getFont().deriveFont(14.0f));
        home_value.setForeground(Color.decode("#13ccd6"));
        home_value.setCursor(new Cursor(Cursor.HAND_CURSOR)); //
        home_value.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    String a = System.getProperty("java.home");
                    Desktop.getDesktop().open(new File(a));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        add(home_value);

        JLabel space1 = new JLabel("     ");
        add(space1);

        vendor = new JLabel( "(!) Java Vendor ->  ");
        vendor.setFont(vendor.getFont().deriveFont(14.0f));
        vendor.setForeground(Color.decode("#ffffff"));
        add(vendor);

        vendor_value = new JLabel("" + System.getProperty("java.vendor"));
        vendor_value.setFont(vendor_value.getFont().deriveFont(14.0f));
        vendor_value.setForeground(Color.decode("#13ccd6"));
        vendor_value.setCursor(new Cursor(Cursor.HAND_CURSOR));
        vendor_value.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI("https://ro.wikipedia.org/wiki/Oracle"));
                } catch (URISyntaxException ex) {
                    //It looks like there's a problem
                    ex.printStackTrace();
                } catch(IOException exe) {
                    exe.printStackTrace();
                }
            }
        });
        add(vendor_value);

        JLabel space2 = new JLabel("                               ");
        add(space2);

        vendor_url = new JLabel("(!) Java Vendor URL ->  ");
        vendor_url.setFont(vendor_url.getFont().deriveFont(14.0f));
        vendor_url.setForeground(Color.decode("#ffffff"));
        add(vendor_url);

        vendor_url_value = new JLabel("" + System.getProperty("java.vendor.url"));
        vendor_url_value.setFont(vendor_url_value.getFont().deriveFont(14.0f));
        vendor_url_value.setForeground(Color.decode("#13ccd6"));
        vendor_url_value.setCursor(new Cursor(Cursor.HAND_CURSOR));
        vendor_url_value.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI(System.getProperty("java.vendor.url")));
                } catch (URISyntaxException ex) {
                    //It looks like there's a problem
                    ex.printStackTrace();
                } catch(IOException exe) {
                    exe.printStackTrace();
                }
            }
        });
        add(vendor_url_value);

        JLabel space3 = new JLabel("     ");
        add(space3);

        info = new JLabel("<html><br />(?) You can click on the colored text to visit urls</html>");
        info.setFont(info.getFont().deriveFont(14.0f));
        info.setForeground(Color.decode("#ffffff"));
        add(info);

    }

    public static void main(String[] args) {
        Main gui = new Main();
        gui.setVisible(true);
        gui.setResizable(false);
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gui.setTitle("Java Status");
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int height = screenSize.height;
        int width = screenSize.width;
        gui.setLocation(width/2-gui.getSize().width/2, height/2-gui.getSize().height/2);
    }
}
