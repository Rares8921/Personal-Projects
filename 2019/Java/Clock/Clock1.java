package com.company;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.util.Timer;


public class Clock1 extends JFrame {

    private static int n = 1;
    private ClockDial cd;
    private int hour;
    private int min;
    private int sec;
    private Toolkit toolkit;
    private static Date date = new Date();
    private static SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");

    private Clock1() {
        Timer timer;
        UIManager.put("Menu.selectionBackground", Color.BLACK);
        UIManager.put("Menu.selectionForeground", Color.WHITE);
        ImageIcon icon = new ImageIcon("C:\\Users\\user\\Downloads\\alarm.png");
        setIconImage(icon.getImage());
        String time= date.toString();
        hour=Integer.parseInt(time.substring(11,13));
        min=Integer.parseInt(time.substring(14,16));
        sec=Integer.parseInt(time.substring(17,19));
        cd = new ClockDial(this);
        add(cd);
        Thread clockEngine = new Thread() {
            int new_sec, new_min;
            public void run() {
                while(isAlive()) {
                    new_sec = (sec + 1) % 60;
                    new_min = (min + (sec + 1) / 60) % 60;
                    hour = (hour + (min + (sec + 1) / 60) / 60) % 12;
                    sec = new_sec;
                    min = new_min;

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    cd.repaint();
                }
            }
        };
        clockEngine.setPriority(clockEngine.getPriority()+3);
        clockEngine.start();
        toolkit = Toolkit.getDefaultToolkit();
        timer = new Timer();
        timer.schedule(new RemindTask(), 0,1000);
        JMenuBar bar = new JMenuBar();
        bar.setForeground(Color.WHITE);
        bar.setBackground(Color.BLACK);
        setJMenuBar(bar);
        final JMenu ch = new JMenu("Turn off the sound");
        ch.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        ch.setFont(ch.getFont().deriveFont(16.0f));
        ch.setBackground(Color.BLACK);
        ch.setForeground(Color.WHITE);
        ch.addMenuListener(new MenuListener() {
            @Override
            public void menuSelected(MenuEvent e) {
                if(n == 1) {
                    n = 0;
                    ch.setText("Turn on the sound");
                    setTitle("Clock - Sound OFF │" + formatter.format(date));
                } else {
                    n = 1;
                    ch.setText("Turn off the sound");
                    setTitle("Clock - Sound ON │" + formatter.format(date));
                }
            }
            @Override
            public void menuDeselected(MenuEvent e) {}
            @Override
            public void menuCanceled(MenuEvent e) {}
        });
        bar.add(ch);
    }

    public static void main(String args[]) {
        Clock1 gui = new Clock1();
        gui.setVisible(true);
        gui.getContentPane().setBackground(Color.WHITE);
        gui.setDefaultCloseOperation(EXIT_ON_CLOSE);
        gui.setSize(510, 555);
        gui.setResizable(false);
        gui.setLocale(Locale.ENGLISH);
        gui.setTitle("Clock - Sound ON │" + formatter.format(date));
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int height = screenSize.height;
        int width = screenSize.width;
        gui.setLocation(width/2-gui.getSize().width/2, height/2-gui.getSize().height/2);
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (UnsupportedLookAndFeelException e) { e.printStackTrace(); } catch (ClassNotFoundException e) { e.printStackTrace(); } catch (InstantiationException e) { e.printStackTrace(); } catch (IllegalAccessException e) { e.printStackTrace(); }
    }

    class RemindTask extends TimerTask {
        public void run() {
            while(n == 1) {
                try {
                    Thread.sleep(1000);
                    toolkit.beep();
                } catch(InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


class ClockDial extends JPanel{

    private Clock1 parent;
    private ClockDial(Clock1 pt){
        setSize(520,530);
        parent=pt;
    }


    @Override
    public void paintComponent(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(Color.WHITE);
        g.fillOval(5, 5,480,480);
        g.setColor(Color.BLACK);
        g.fillOval(10, 10,470,470);
        g.setColor(Color.WHITE);
        g.fillOval(237,237,15,15);
        g.setFont(g.getFont().deriveFont(Font.BOLD,32));

        for(int i=1;i<=12;i++)
            g.drawString(Integer.toString(i),240-(i/12)*11+(int)(210*Math.sin(i*Math.PI/6)),253-(int)(210*Math.cos(i*Math.PI/6)));

        double minsecdeg=Math.PI/30;
        double hrdeg=Math.PI/6;
        int tx,ty;
        int xpoints[]=new int[3];
        int ypoints[]=new int[3];

        //second hand
        tx=245+(int)(210*Math.sin(parent.sec*minsecdeg));
        ty=245-(int)(210*Math.cos(parent.sec*minsecdeg));
        g.drawLine(245,245,tx,ty);

        //minute hand
        tx=245+(int)(190*Math.sin(parent.min*minsecdeg));
        ty=245-(int)(190*Math.cos(parent.min*minsecdeg));
        xpoints[0]=245;
        xpoints[1]=tx+2;
        xpoints[2]=tx-2;
        ypoints[0]=245;
        ypoints[1]=ty+2;
        ypoints[2]=ty-2;
        g.fillPolygon(xpoints, ypoints,3);

        //hour hand
        tx=245+(int)(160*Math.sin(parent.hour*hrdeg+parent.min*Math.PI/360));
        ty=245-(int)(160*Math.cos(parent.hour*hrdeg+parent.min*Math.PI/360));
        xpoints[1]=tx+4;
        xpoints[2]=tx-4;
        ypoints[1]=ty-4;
        ypoints[2]=ty+4;
        g.fillPolygon(xpoints, ypoints, 3);

    }
}
}
