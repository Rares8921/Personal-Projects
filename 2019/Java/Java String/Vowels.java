package com.jetbrains;

import java.lang.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.regex.*;
import java.math.*;

public class Vowels extends JFrame {

    private JLabel text;
    private JTextField enter;
    private JButton run;
    private JLabel info;
    private JLabel info2;
    private JLabel vowels;
    private JMenuBar menubar;
    private JMenu File;
    private JMenuItem refresh;
    private JMenuItem exit;
    private JMenu infos;
    private JMenuItem chars;
    private JMenuItem howto;
    private JMenu history;
    private JMenuItem words;
    private JMenuItem delete;

    private static final String SPECIAL_CHARS_REGEX_PATERN = "[^A-Za-z0-9]";


    public static boolean vowels(String ac) {
        String word = ac.toLowerCase();
        char[] words = word.toCharArray();
        for(int i = 0; i < words.length; i++ ) {
            char z = words[i];
            if ( z=='a'|| z=='e'|| z=='i'|| z=='o'|| z=='u' || z=='A' || z=='E' || z=='I' || z=='O' || z=='U') {
                return true;
            }
        }
        return false;
    }

    public Vowels() {
        setLayout(new FlowLayout(FlowLayout.LEFT));

        text = new JLabel("Enter text:");
        text.setFont(text.getFont().deriveFont(14.0f));
        add(text);

        enter = new JTextField(10);
        enter.setFont(enter.getFont().deriveFont(14.0f));
        add(enter);

        run = new JButton("Enter");
        run.setFont(run.getFont().deriveFont(14.0f));
        run.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String abc = enter.getText();
                String ac = abc.replaceAll("\\s", "");

                boolean numeric = true;

                try {
                    Double num = Double.parseDouble(ac);
                } catch (NumberFormatException ee) {
                    numeric = false;
                }

                if (numeric) {
                    String acc = ac.replaceAll("-", "");
                    info.setText("Your number has");
                    if (acc.length() == 1 ) {
                        info2.setText(acc.length() + " digit\n");
                        info2.setForeground(Color.green);
                        vowels.setText("");
                    }
                    else {
                        info2.setText(acc.length() + " digits\n");
                        info2.setForeground(Color.green);
                        vowels.setText("");
                    }
                } else if (specialChars(ac)) {
                    info.setText("You are not allowed to use special chars.");
                    info2.setText("");
                    vowels.setText("");

                }

                else {

                    if (ac.length() > 0) {
                        info.setText("Your text has the number of");
                        info2.setText(ac.length() + " characters");
                        info2.setForeground(Color.GREEN);
                        if (vowels(ac)) {
                            if ( count_Vowels(ac) == 1) {
                                vowels.setText("Your text has " + count_Vowels(ac) + " vowel.");
                            } else {
                                vowels.setText("Your text has " + count_Vowels(ac) + " vowels.");
                            }
                        } else {
                            vowels.setText("Your text doesn't have any vowel.");
                        }
                    } else if(numeric) {
                        info.setText("You are not allowed to enter numbers instead of text.");
                        info2.setText("");
                        vowels.setText("");
                    }

                    else {
                        info.setText("You must enter text first.");
                        info2.setText("");
                        vowels.setText("");
                    }
                }
            }
        });
        add(run);

        info = new JLabel("");
        info.setFont(info.getFont().deriveFont(14.0f));
        add(info);

        info2 = new JLabel("");
        info2.setFont(info2.getFont().deriveFont(14.0f));
        add(info2);

        vowels = new JLabel("");
        vowels.setFont(vowels.getFont().deriveFont(14.0f));
        add(vowels);

        menubar = new JMenuBar();
        setJMenuBar(menubar);

        File = new JMenu("File");
        File.setFont(File.getFont().deriveFont(14.0f));
        menubar.add(File);

        refresh = new JMenuItem("Refresh");
        refresh.setFont(refresh.getFont().deriveFont(14.0f));
        refresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               info.setText("");
               info2.setText("");
               vowels.setText("");
               enter.setText("");
            }
        });
        File.add(refresh);

        exit = new JMenuItem("Exit");
        exit.setFont(exit.getFont().deriveFont(14.0f));
        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        File.add(exit);

        infos = new JMenu("Info");
        infos.setFont(infos.getFont().deriveFont(14.0f));
        menubar.add(infos);

        howto = new JMenuItem("Info & how to use?");
        howto.setFont(infos.getFont().deriveFont(14.0f));
        howto.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "Hey, user.\n You can use this app. to store text ( your text will be stored in history menu), but if only the app runs. When it close, " +
                        "the data will disappear.\n In the file menu, you can exit and refresh your main page ( the history will refresh also ).\n You are not allowed to use some special chars ( open special chars section in the info menu ).\n" +
                        " When u enter text, you'll get some additional info about it.\n You are allowed to use numbers, but only when are just numbers, not words.\n If you found a bug or have an idea, contact us: javacompany24@yahoo.com ", "Info & how to use.", JOptionPane.QUESTION_MESSAGE);
            }
        });
        infos.add(howto);

        chars = new JMenuItem("Special chars.");
        chars.setFont(infos.getFont().deriveFont(14.0f));
        chars.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, " Special chars. can't be used in this app. .\n The special chars are: \n ! @ # $ ^ & * ( ) - + = _ [ ] { } ; : ,  ' . / | ? ~ ` „ ”  \nAnd chars like: ✪", "Special chars.", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        infos.add(chars);

        history = new JMenu("History");
        history.setFont(infos.getFont().deriveFont(14.0f));
        menubar.add(history);

        words = new JMenuItem("Words");
        words.setFont(infos.getFont().deriveFont(14.0f));
        words.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String value = enter.getText();

                String abc = enter.getText();
                String ac = abc.replaceAll("\\s", "");

                boolean numeric = true;

                try {
                    Double num = Double.parseDouble(ac);
                } catch (NumberFormatException ee) {
                    numeric = false;
                }

                if (ac.length() == 0) {
                    JOptionPane.showMessageDialog(null,"You haven't entered a text already.");
                } else {

                    if (numeric) {
                        JOptionPane.showMessageDialog(null, "Your last number: " + ac, "Last number", JOptionPane.INFORMATION_MESSAGE);
                    } else if (specialChars(ac)) {
                        JOptionPane.showMessageDialog(null, "Your last text ( with special chars. ): " + ac, "Last text with special chars", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, "Your last text is: " + ac, "Last text", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
        });
        history.add(words);

        delete = new JMenuItem("Delete");
        delete.setFont(infos.getFont().deriveFont(14.0f));
        delete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String abc = enter.getText();
                String ac = abc.replaceAll("\\s", "");

                int n = (int) (Math.random() * 1001);
                Integer n1;

                if (ac.length() == 0 ) {
                    JOptionPane.showMessageDialog(null, "First, you must have a text stored in history.", "Error: No existing text", JOptionPane.ERROR_MESSAGE);
                } else {

                    n1 = (Integer.parseInt(JOptionPane.showInputDialog(null, "To delete history,\nType the number: " + n)));
                    if (n == n1) {
                        JOptionPane.showMessageDialog(null, "The history was deleted successfully.", "History deleted!", JOptionPane.INFORMATION_MESSAGE);
                        info.setText("");
                        info2.setText("");
                        vowels.setText("");
                        enter.setText("");

                    } else if ( n != n1 ) {
                        JOptionPane.showMessageDialog(null, "The numbers doesn't match, so the history won't be deleted.", "Error: Numbers", JOptionPane.WARNING_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, "You must enter a number.", "Error: Number undefined", JOptionPane.ERROR_MESSAGE);
                    }

                }
            }
        });
        history.add(delete);
    }

    public static void main(String[] args) {
        Vowels gui = new Vowels();
        gui.setVisible(true);
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gui.setResizable(false);
        gui.setSize(400,300);
        gui.setBackground(Color.LIGHT_GRAY);
        gui.setLocationRelativeTo(null);

        gui.setTitle("Java - String");
    }

    public static int count_Vowels(String ac) {
        int count = 0;
        for (int i = 0; i < ac.length(); i++ ) {
            if(ac.charAt(i) == 'a' || ac.charAt(i) == 'e' || ac.charAt(i) == 'i' || ac.charAt(i) == 'o' || ac.charAt(i) == 'u' || ac.charAt(i) == 'A' || ac.charAt(i) == 'E' || ac.charAt(i) == 'I' || ac.charAt(i) == 'O' || ac.charAt(i) == 'U') {
                count++;
            }
        }
        return count;
    }

    public static boolean specialChars(String ac) {

        Pattern p = Pattern.compile(SPECIAL_CHARS_REGEX_PATERN);
        Matcher m = p.matcher(ac);

        boolean charFound = m.find();

        if(charFound) {
            return true;
        } else {
            return false;
        }
    }
}
