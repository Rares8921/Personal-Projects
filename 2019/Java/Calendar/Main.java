package com.company;

import java.lang.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.awt.event.*;

public class Main extends JFrame {

    private DefaultTableModel model;
    private Calendar cal = new GregorianCalendar();
    private JLabel label;

    private Main() {
        setLayout(new FlowLayout(FlowLayout.LEFT));

		// Set the icon of the app
        ImageIcon img = new ImageIcon("C:\\Users\\user\\IdeaProjects\\calculatordate\\img\\img.png");
        setIconImage(img.getImage());

		// Month and year label (MM.YYYY)
        label = new JLabel();
        label.setHorizontalAlignment(SwingConstants.CENTER);
        add(label);

        JLabel space = new JLabel("                                                    ");
        add(space);

		// Button for prev month
        JButton b1 = new JButton("←");
        b1.setFont(b1.getFont().deriveFont(20.0f));
        b1.setToolTipText("Previous month");
        b1.setBorder(null);
        b1.setBackground(Color.WHITE);
        b1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                cal.add(Calendar.MONTH, -1);
                updateMonth();
            }
        });

		// Button for next month
        JButton b2 = new JButton("→");
        b2.setFont(b2.getFont().deriveFont(20.0f));
        b2.setBackground(Color.WHITE);
        b2.setToolTipText("Next month");
        b2.setBorder(null);
        b2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                cal.add(Calendar.MONTH, +1);
                updateMonth();
            }
        });

		// Adding the content
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(b1,BorderLayout.WEST);
        panel.add(label,BorderLayout.CENTER);
        panel.add(b2,BorderLayout.EAST);

		// Creating the month table
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        String [] columns = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        model = new DefaultTableModel(null,columns);
        JTable table = new JTable(model) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // all cells false
                return false;
            }

        };
		// make all the text centered
        for(int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        table.getTableHeader().setReorderingAllowed(false);
        JScrollPane pane = new JScrollPane(table);

        this.add(panel, BorderLayout.NORTH);
        this.add(pane, BorderLayout.CENTER);
		// create the table
        this.updateMonth();

    }

    private void updateMonth() {
        cal.set(Calendar.DAY_OF_MONTH, 1);

		// Get the month and the year
        String month = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US);
        int year = cal.get(Calendar.YEAR);
		// Special case for month "May"
        if(month.length() == 3) {
            label.setText("   " + month + " " + year + "   "  );
        } else {
            label.setText("   " + month.substring(0, 3) + ". " + year + "   "  );
        }

        int startDay = cal.get(Calendar.DAY_OF_WEEK);
        int numberOfDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        int weeks = cal.getActualMaximum(Calendar.WEEK_OF_MONTH);

        model.setRowCount(0);
        model.setRowCount(weeks);

        int i = startDay - 1;
        for(int day = 1; day <= numberOfDays; day++) {
            // Value, row, column
            model.setValueAt(day, i / 7 , i % 7);
            i++;
        }

    }


    public static void main(String[] args) {
        Main gui = new Main();
        gui.setVisible(true);
        gui.setSize(465, 190);
        gui.setName("Calendar");
        gui.setFont(gui.getFont().deriveFont(16.0f));
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gui.setResizable(false);
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
		// Setting the current date on the title
        Date date = new Date();
        String a = formatter.format(date);
        gui.setTitle("Calendar - " + a);
		// Change the default design
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (Exception e) { e.printStackTrace(); }
		// Set the application in the middle of the screen
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int height = screenSize.height;
        int width = screenSize.width;
        gui.setLocation(width / 2 - gui.getSize().width / 2, height / 2 - gui.getSize().height / 2);
    }
}