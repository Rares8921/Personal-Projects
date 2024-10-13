package com.company;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.metal.MetalButtonUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Main extends JFrame {

    private int playerPoints = 0, opponentPoints = 0;

    static boolean playerTurn = true;
    private static boolean playerWin = false, opponentWin = false;
    private static int[][] table = new int[3][3];

    private JButton btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9, play;
    private JLabel you, opponent;

    private void wonGame(int[][] v) {
        if( (v[0][0] == 1 && v[0][1] == 1 && v[0][2] == 1) || (v[1][0] == 1 && v[1][1] == 1 && v[1][2] == 1) || (v[2][0] == 1 && v[2][1] == 1 && v[2][2] == 1) ||
                (v[0][0] == 1 && v[1][0] == 1 && v[2][0] == 1) || (v[0][1] == 1 && v[1][1] == 1 && v[2][1] == 1) || (v[0][2] == 1 && v[1][2] == 1 && v[2][2] == 1) ||
                (v[0][0] == 1 && v[1][1] == 1 && v[2][2] == 1) || (v[0][2] == 1 && v[1][1] == 1 && v[2][0] == 1) ) {
            playerWin = true;
        } else if( (v[0][0] == 2 && v[0][1] == 2 && v[0][2] == 2) || (v[1][0] == 2 && v[1][1] == 2 && v[1][2] == 2) || (v[2][0] == 2 && v[2][1] == 2 && v[2][2] == 2) ||
                (v[0][0] == 2 && v[1][0] == 2 && v[2][0] == 2) || (v[0][1] == 2 && v[1][1] == 2 && v[2][1] == 2) || (v[0][2] == 2 && v[1][2] == 2 && v[2][2] == 2) ||
                (v[0][0] == 2 && v[1][1] == 2 && v[2][2] == 2) || (v[0][2] == 2 && v[1][1] == 2 && v[2][0] == 2) ) {
            opponentWin = true;
        }
    }

    private boolean finishedGame() {
        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 3; j++) {
                if(table[i][j] == 0) {
                    return false;
                }
            }
        }
        return !playerWin && !opponentWin;
    }

    private void resetMatrix() {
        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 3; j++) {
                table[i][j] = 0;
            }
        }

        playerWin = false;
        opponentWin = false;
    }

    private void reset() {
        btn1.setEnabled(true);
        btn2.setEnabled(true);
        btn3.setEnabled(true);
        btn4.setEnabled(true);
        btn5.setEnabled(true);
        btn6.setEnabled(true);
        btn7.setEnabled(true);
        btn8.setEnabled(true);
        btn9.setEnabled(true);

        btn1.setText(" ");
        btn2.setText(" ");
        btn3.setText(" ");
        btn4.setText(" ");
        btn5.setText(" ");
        btn6.setText(" ");
        btn7.setText(" ");
        btn8.setText(" ");
        btn9.setText(" ");

    }


    private Main() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

        UIManager.put("TextField.inactiveForeground", Color.white);
        UIManager.put("OptionPane.background",new ColorUIResource(0, 0, 0));
        UIManager.put("Panel.background",new ColorUIResource(0, 0, 0));
        UIManager.put("OptionPane.messageForeground",new ColorUIResource(255, 255, 255));
        UIManager.put("Button.background", Color.BLACK);
        UIManager.put("Button.disabledText", Color.WHITE);
        UIManager.put("Menu.selectionBackground", Color.WHITE);
        UIManager.put("Menu.selectionForeground", Color.BLACK);
        UIManager.put("MenuItem.selectionBackground", Color.WHITE);
        UIManager.put("MenuItem.selectionForeground", Color.BLACK);


        Border whiteLine =  BorderFactory.createLineBorder(Color.WHITE);

        btn1 = new JButton(" ");
        btn1.setPreferredSize(new Dimension(80, 80));
        btn1.setEnabled(false);
        btn1.setBackground(Color.BLACK);
        btn1.setBorder(whiteLine);
        btn1.setForeground(Color.WHITE);
        btn1.setFont(btn1.getFont().deriveFont(69.0f));
        add(btn1);

        btn2 = new JButton(" ");
        btn2.setPreferredSize(new Dimension(80, 80));
        btn2.setBorder(whiteLine);
        btn2.setEnabled(false);
        btn2.setBackground(Color.BLACK);
        btn2.setForeground(Color.WHITE);
        btn2.setFont(btn2.getFont().deriveFont(69.0f));
        add(btn2);

        btn3 = new JButton(" ");
        btn3.setPreferredSize(new Dimension(80, 80));
        btn3.setBorder(whiteLine);
        btn3.setEnabled(false);
        btn3.setBackground(Color.BLACK);
        btn3.setForeground(Color.WHITE);
        btn3.setFont(btn3.getFont().deriveFont(69.0f));
        add(btn3);

        btn4 = new JButton(" ");
        btn4.setPreferredSize(new Dimension(80, 80));
        btn4.setBorder(whiteLine);
        btn4.setEnabled(false);
        btn4.setBackground(Color.BLACK);
        btn4.setForeground(Color.WHITE);
        btn4.setFont(btn4.getFont().deriveFont(69.0f));
        add(btn4);

        btn5 = new JButton(" ");
        btn5.setPreferredSize(new Dimension(80, 80));
        btn5.setBorder(whiteLine);
        btn5.setEnabled(false);
        btn5.setBackground(Color.BLACK);
        btn5.setForeground(Color.WHITE);
        btn5.setFont(btn5.getFont().deriveFont(69.0f));
        add(btn5);

        btn6 = new JButton(" ");
        btn6.setPreferredSize(new Dimension(80, 80));
        btn6.setBorder(whiteLine);
        btn6.setEnabled(false);
        btn6.setBackground(Color.BLACK);
        btn6.setForeground(Color.WHITE);
        btn6.setFont(btn6.getFont().deriveFont(69.0f));
        add(btn6);

        btn7 = new JButton(" ");
        btn7.setPreferredSize(new Dimension(80, 80));
        btn7.setBorder(whiteLine);
        btn7.setEnabled(false);
        btn7.setBackground(Color.BLACK);
        btn7.setForeground(Color.WHITE);
        btn7.setFont(btn7.getFont().deriveFont(69.0f));
        add(btn7);

        btn8 = new JButton(" ");
        btn8.setPreferredSize(new Dimension(80, 80));
        btn8.setBorder(whiteLine);
        btn8.setEnabled(false);
        btn8.setBackground(Color.BLACK);
        btn8.setForeground(Color.WHITE);
        btn8.setFont(btn8.getFont().deriveFont(69.0f));
        add(btn8);

        btn9 = new JButton(" ");
        btn9.setPreferredSize(new Dimension(80, 80));
        btn9.setBorder(whiteLine);
        btn9.setEnabled(false);
        btn9.setBackground(Color.BLACK);
        btn9.setForeground(Color.WHITE);
        btn9.setFont(btn9.getFont().deriveFont(69.0f));
        add(btn9);

        JLabel space1 = new JLabel("                                                                                                                                                                                                                                                    ");
        add(space1);

        JLabel space2 = new JLabel("                             ");
        add(space2);

        play = new JButton("Play");
        play.setFont(play.getFont().deriveFont(17.0f));
        play.setBackground(Color.BLACK);
        play.setUI(new MetalButtonUI() {
            protected Color getDisabledTextColor() {
                return Color.WHITE;
            }
        });
        play.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                play.setBackground(Color.decode("#171717"));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                play.setBackground(Color.BLACK);
            }
        });
        play.setForeground(Color.WHITE);
        //play.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        play.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        play.addActionListener(e -> {

            if(play.getText().equals("Play")) {
                play.setText("Reset");
                btn1.setEnabled(true);
                btn1.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                btn2.setEnabled(true);
                btn2.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                btn3.setEnabled(true);
                btn3.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                btn4.setEnabled(true);
                btn4.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                btn5.setEnabled(true);
                btn5.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                btn6.setEnabled(true);
                btn6.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                btn7.setEnabled(true);
                btn7.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                btn8.setEnabled(true);
                btn8.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                btn9.setEnabled(true);
                btn9.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                btn1.addActionListener(ef -> {
                    if (playerTurn) {
                        btn1.setText("X");
                        table[0][0] = 1;
                    } else {
                        btn1.setText("0");
                        table[0][0] = 2;
                    }
                    playerTurn = !playerTurn;
                    btn1.setEnabled(false);
                    wonGame(table);
                    if(playerWin) {
                        JLabel txt = new JLabel("Player 1 wins!");
                        txt.setFont(txt.getFont().deriveFont(16.0f));
                        txt.setForeground(Color.WHITE);
                        JOptionPane.showOptionDialog(null, txt, "Won", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, new Object[]{}, null);
                        playerPoints++;
                        you.setText("    You: " + playerPoints + " points ");
                        resetMatrix(); reset();
                    } else if(opponentWin) {
                        JLabel txt = new JLabel("Player 2 wins!");
                        txt.setFont(txt.getFont().deriveFont(16.0f));
                        txt.setForeground(Color.WHITE);
                        JOptionPane.showOptionDialog(null, txt, "Won", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, new Object[]{}, null);
                        opponentPoints++;
                        opponent.setText("    Opponent: " + opponentPoints + " points");
                        resetMatrix(); reset();
                    } else if(finishedGame()) {
                        JLabel txt = new JLabel("Equal! No one wins.");
                        txt.setFont(txt.getFont().deriveFont(16.0f));
                        txt.setForeground(Color.WHITE);
                        JOptionPane.showOptionDialog(null, txt, "Equal", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, new Object[]{}, null);
                        resetMatrix(); reset();
                    }
                });
                btn2.addActionListener(ef -> {
                    if (playerTurn) {
                        btn2.setText("X");
                        table[0][1] = 1;
                    } else {
                        btn2.setText("0");
                        table[0][1] = 2;
                    }
                    playerTurn = !playerTurn;
                    btn2.setEnabled(false);
                    wonGame(table);
                    if(playerWin) {
                        JLabel txt = new JLabel("Player 1 wins!");
                        txt.setFont(txt.getFont().deriveFont(16.0f));
                        txt.setForeground(Color.WHITE);
                        JOptionPane.showOptionDialog(null, txt, "Won", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, new Object[]{}, null);
                        playerPoints++;
                        you.setText("    You: " + playerPoints + " points ");
                        resetMatrix(); reset();
                    } else if(opponentWin) {
                        JLabel txt = new JLabel("Player 2 wins!");
                        txt.setFont(txt.getFont().deriveFont(16.0f));
                        txt.setForeground(Color.WHITE);
                        JOptionPane.showOptionDialog(null, txt, "Won", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, new Object[]{}, null);
                        resetMatrix(); reset();
                        opponentPoints++;
                        opponent.setText("    Opponent: " + opponentPoints + " points");
                        resetMatrix(); reset();
                    } else if(finishedGame()) {
                        JLabel txt = new JLabel("Equal! No one wins.");
                        txt.setFont(txt.getFont().deriveFont(16.0f));
                        txt.setForeground(Color.WHITE);
                        JOptionPane.showOptionDialog(null, txt, "Equal", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, new Object[]{}, null);
                        resetMatrix(); reset();
                    }
                });
                btn3.addActionListener(ef -> {
                    if (playerTurn) {
                        btn3.setText("X");
                        table[0][2] = 1;
                    } else {
                        btn3.setText("0");
                        table[0][2] = 2;
                    }
                    playerTurn = !playerTurn;
                    btn3.setEnabled(false);
                    wonGame(table);
                    if(playerWin) {
                        JLabel txt = new JLabel("Player 1 wins!");
                        txt.setFont(txt.getFont().deriveFont(16.0f));
                        txt.setForeground(Color.WHITE);
                        JOptionPane.showOptionDialog(null, txt, "Won", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, new Object[]{}, null);
                        playerPoints++;
                        you.setText("    You: " + playerPoints + " points ");
                        resetMatrix(); reset();
                    } else if(opponentWin) {
                        JLabel txt = new JLabel("Player 2 wins!");
                        txt.setFont(txt.getFont().deriveFont(16.0f));
                        txt.setForeground(Color.WHITE);
                        JOptionPane.showOptionDialog(null, txt, "Won", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, new Object[]{}, null);
                        opponentPoints++;
                        opponent.setText("    Opponent: " + opponentPoints + " points");
                        resetMatrix(); reset();
                    } else if(finishedGame()) {
                        JLabel txt = new JLabel("Equal! No one wins.");
                        txt.setFont(txt.getFont().deriveFont(16.0f));
                        txt.setForeground(Color.WHITE);
                        JOptionPane.showOptionDialog(null, txt, "Equal", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, new Object[]{}, null);
                        resetMatrix(); reset();
                    }
                });
                btn4.addActionListener(ef -> {
                    if (playerTurn) {
                        btn4.setText("X");
                        table[1][0] = 1;
                    } else {
                        btn4.setText("0");
                        table[1][0] = 2;
                    }
                    playerTurn = !playerTurn;
                    btn4.setEnabled(false);
                    wonGame(table);
                    if(playerWin) {
                        JLabel txt = new JLabel("Player 1 wins!");
                        txt.setFont(txt.getFont().deriveFont(16.0f));
                        txt.setForeground(Color.WHITE);
                        JOptionPane.showOptionDialog(null, txt, "Won", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, new Object[]{}, null);
                        playerPoints++;
                        you.setText("    You: " + playerPoints + " points ");
                        resetMatrix(); reset();
                    } else if(opponentWin) {
                        JLabel txt = new JLabel("Player 2 wins!");
                        txt.setFont(txt.getFont().deriveFont(16.0f));
                        txt.setForeground(Color.WHITE);
                        JOptionPane.showOptionDialog(null, txt, "Won", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, new Object[]{}, null);
                        opponentPoints++;
                        opponent.setText("    Opponent: " + opponentPoints + " points");
                        resetMatrix(); reset();
                    } else if(finishedGame()) {
                        JLabel txt = new JLabel("Equal! No one wins.");
                        txt.setFont(txt.getFont().deriveFont(16.0f));
                        txt.setForeground(Color.WHITE);
                        JOptionPane.showOptionDialog(null, txt, "Equal", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, new Object[]{}, null);
                        resetMatrix(); reset();
                    }
                });
                btn5.addActionListener(ef -> {
                    if (playerTurn) {
                        btn5.setText("X");
                        table[1][1] = 1;
                    } else {
                        btn5.setText("0");
                        table[1][1] = 2;
                    }
                    playerTurn = !playerTurn;
                    btn5.setEnabled(false);
                    wonGame(table);
                    if(playerWin) {
                        JLabel txt = new JLabel("Player 1 wins!");
                        txt.setFont(txt.getFont().deriveFont(16.0f));
                        txt.setForeground(Color.WHITE);
                        JOptionPane.showOptionDialog(null, txt, "Won", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, new Object[]{}, null);
                        playerPoints++;
                        you.setText("    You: " + playerPoints + " points ");
                        resetMatrix(); reset();
                    } else if(opponentWin) {
                        JLabel txt = new JLabel("Player 2 wins!");
                        txt.setFont(txt.getFont().deriveFont(16.0f));
                        txt.setForeground(Color.WHITE);
                        JOptionPane.showOptionDialog(null, txt, "Won", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, new Object[]{}, null);
                        opponentPoints++;
                        opponent.setText("    Opponent: " + opponentPoints + " points");
                        resetMatrix(); reset();
                    } else if(finishedGame()) {
                        JLabel txt = new JLabel("Equal! No one wins.");
                        txt.setFont(txt.getFont().deriveFont(16.0f));
                        txt.setForeground(Color.WHITE);
                        JOptionPane.showOptionDialog(null, txt, "Equal", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, new Object[]{}, null);
                        resetMatrix(); reset();
                    }
                });
                btn6.addActionListener(ef -> {
                    if (playerTurn) {
                        btn6.setText("X");
                        table[1][2] = 1;
                    } else {
                        btn6.setText("0");
                        table[1][2] = 2;
                    }
                    playerTurn = !playerTurn;
                    btn6.setEnabled(false);
                    wonGame(table);
                    if(playerWin) {
                        JLabel txt = new JLabel("Player 1 wins!");
                        txt.setFont(txt.getFont().deriveFont(16.0f));
                        txt.setForeground(Color.WHITE);
                        JOptionPane.showOptionDialog(null, txt, "Won", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, new Object[]{}, null);
                        playerPoints++;
                        you.setText("    You: " + playerPoints + " points ");
                        resetMatrix(); reset();
                    } else if(opponentWin) {
                        JLabel txt = new JLabel("Player 2 wins!");
                        txt.setFont(txt.getFont().deriveFont(16.0f));
                        txt.setForeground(Color.WHITE);
                        JOptionPane.showOptionDialog(null, txt, "Won", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, new Object[]{}, null);
                        opponentPoints++;
                        opponent.setText("    Opponent: " + opponentPoints + " points");
                        resetMatrix(); reset();
                    } else if(finishedGame()) {
                        JLabel txt = new JLabel("Equal! No one wins.");
                        txt.setFont(txt.getFont().deriveFont(16.0f));
                        txt.setForeground(Color.WHITE);
                        JOptionPane.showOptionDialog(null, txt, "Equal", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, new Object[]{}, null);
                        resetMatrix(); reset();
                    }
                });
                btn7.addActionListener(ef -> {
                    if (playerTurn) {
                        btn7.setText("X");
                        table[2][0] = 1;
                    } else {
                        btn7.setText("0");
                        table[2][0] = 2;
                    }
                    playerTurn = !playerTurn;
                    btn7.setEnabled(false);
                    wonGame(table);
                    if(playerWin) {
                        JLabel txt = new JLabel("Player 1 wins!");
                        txt.setFont(txt.getFont().deriveFont(16.0f));
                        txt.setForeground(Color.WHITE);
                        JOptionPane.showOptionDialog(null, txt, "Won", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, new Object[]{}, null);
                        playerPoints++;
                        you.setText("    You: " + playerPoints + " points ");
                        resetMatrix(); reset();
                    } else if(opponentWin) {
                        JLabel txt = new JLabel("Player 2 wins!");
                        txt.setFont(txt.getFont().deriveFont(16.0f));
                        txt.setForeground(Color.WHITE);
                        JOptionPane.showOptionDialog(null, txt, "Won", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, new Object[]{}, null);
                        opponentPoints++;
                        opponent.setText("    Opponent: " + opponentPoints + " points");
                        resetMatrix(); reset();
                    } else if(finishedGame()) {
                        JLabel txt = new JLabel("Equal! No one wins.");
                        txt.setFont(txt.getFont().deriveFont(16.0f));
                        txt.setForeground(Color.WHITE);
                        JOptionPane.showOptionDialog(null, txt, "Equal", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, new Object[]{}, null);
                        resetMatrix(); reset();
                    }
                });
                btn8.addActionListener(ef -> {
                    if (playerTurn) {
                        btn8.setText("X");
                        table[2][1] = 1;
                    } else {
                        btn8.setText("0");
                        table[2][1] = 2;
                    }
                    playerTurn = !playerTurn;
                    btn8.setEnabled(false);
                    wonGame(table);
                    if(playerWin) {
                        JLabel txt = new JLabel("Player 1 wins!");
                        txt.setFont(txt.getFont().deriveFont(16.0f));
                        txt.setForeground(Color.WHITE);
                        JOptionPane.showOptionDialog(null, txt, "Won", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, new Object[]{}, null);
                        playerPoints++;
                        you.setText("    You: " + playerPoints + " points ");
                        resetMatrix(); reset();
                    } else if(opponentWin) {
                        JLabel txt = new JLabel("Player 2 wins!");
                        txt.setFont(txt.getFont().deriveFont(16.0f));
                        txt.setForeground(Color.WHITE);
                        JOptionPane.showOptionDialog(null, txt, "Won", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, new Object[]{}, null);
                        opponentPoints++;
                        opponent.setText("    Opponent: " + opponentPoints + " points");
                        resetMatrix(); reset();
                    } else if(finishedGame()) {
                        JLabel txt = new JLabel("Equal! No one wins.");
                        txt.setFont(txt.getFont().deriveFont(16.0f));
                        txt.setForeground(Color.WHITE);
                        JOptionPane.showOptionDialog(null, txt, "Equal", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, new Object[]{}, null);
                        resetMatrix(); reset();
                    }
                });
                btn9.addActionListener(ef -> {
                    if (playerTurn) {
                        btn9.setText("X");
                        table[2][2] = 1;
                    } else {
                        btn9.setText("0");
                        table[2][2] = 2;
                    }
                    playerTurn = !playerTurn;
                    btn9.setEnabled(false);
                    wonGame(table);
                    if(playerWin) {
                        JLabel txt = new JLabel("Player 1 wins!");
                        txt.setFont(txt.getFont().deriveFont(16.0f));
                        txt.setForeground(Color.WHITE);
                        JOptionPane.showOptionDialog(null, txt, "Won", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, new Object[]{}, null);
                        playerPoints++;
                        you.setText("    You: " + playerPoints + " points ");
                        resetMatrix(); reset();
                    } else if(opponentWin) {
                        JLabel txt = new JLabel("Player 2 wins!");
                        txt.setFont(txt.getFont().deriveFont(16.0f));
                        txt.setForeground(Color.WHITE);
                        JOptionPane.showOptionDialog(null, txt, "Won", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, new Object[]{}, null);
                        opponentPoints++;
                        opponent.setText("    Opponent: " + opponentPoints + " points");
                        resetMatrix(); reset();
                    } else if(finishedGame()) {
                        JLabel txt = new JLabel("Equal! No one wins.");
                        txt.setFont(txt.getFont().deriveFont(16.0f));
                        txt.setForeground(Color.WHITE);
                        JOptionPane.showOptionDialog(null, txt, "Equal", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, new Object[]{}, null);
                        resetMatrix(); reset();
                    }
                });
            } else if (play.getText().equals("Reset")) {
                btn1.setText(" ");
                btn2.setText(" ");
                btn3.setText(" ");
                btn4.setText(" ");
                btn5.setText(" ");
                btn6.setText(" ");
                btn7.setText(" ");
                btn8.setText(" ");
                btn9.setText(" ");
                btn1.setEnabled(true);
                btn1.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                btn2.setEnabled(true);
                btn2.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                btn3.setEnabled(true);
                btn3.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                btn4.setEnabled(true);
                btn4.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                btn5.setEnabled(true);
                btn5.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                btn6.setEnabled(true);
                btn6.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                btn7.setEnabled(true);
                btn7.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                btn8.setEnabled(true);
                btn8.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                btn9.setEnabled(true);
                btn9.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                for(int i = 0; i < 3; i++) {
                    for(int j = 0; j < 3; j++) {
                        table[i][j] = 0;
                    }
                }
            }
        });
        add(play);

        JLabel space3 = new JLabel("                                                                                                                                                                                                                                                    ");
        add(space3);

        you = new JLabel("    You: " + playerPoints + " points ");
        you.setForeground(Color.WHITE);
        you.setBackground(Color.BLACK);
        you.setFont(you.getFont().deriveFont(16.0f));
        add(you);

        opponent = new JLabel("    Opponent: " + opponentPoints + " points");
        opponent.setForeground(Color.WHITE);
        opponent.setBackground(Color.BLACK);
        opponent.setFont(opponent.getFont().deriveFont(16.0f));
        add(opponent);

    }

    public static void main(String[] args) {
        Main gui = new Main();
        gui.setVisible(true);
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gui.setResizable(false);
        gui.setSize(257, 400);
        gui.getContentPane().setBackground(Color.BLACK);
        gui.setTitle("Tic Tac Toe");
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int height = screenSize.height;
        int width = screenSize.width;
        gui.setLocation(width / 2 - gui.getSize().width / 2, height / 2 - gui.getSize().height / 2);
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}