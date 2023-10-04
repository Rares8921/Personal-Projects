package com.jetbrains;

import javax.swing.*;
import java.lang.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;
import java.time.*;

public class Main extends JFrame {

	private static final String ANSI_BLUE = "\u001B[34m";
	private static final String ANSI_RESET = "\u001B[0m";

	private JTextField command;

	private Main() {
		setLayout(new FlowLayout(FlowLayout.LEFT));

		JLabel ver = new JLabel("Java Command Prompt                                       ");
		ver.setFont(ver.getFont().deriveFont(14.0f));
		ver.setForeground(Color.WHITE);
		add(ver);

		JLabel cpr = new JLabel("Copyright (c) 2019. All rights reserved");
		cpr.setFont(cpr.getFont().deriveFont(14.0f));
		cpr.setForeground(Color.WHITE);
		add(cpr);

		JLabel info = new JLabel("Here, you can see the prompt's commands: ");
		info.setFont(info.getFont().deriveFont(14.0f));
		info.setForeground(Color.WHITE);
		add(info);

		JLabel print = new JLabel(" => !print - Print a message                                         ");
		print.setFont(print.getFont().deriveFont(14.0f));
		print.setForeground(Color.CYAN);
		add(print);

		JLabel java = new JLabel(" => !java - Get java info                                                  ");
		java.setFont(java.getFont().deriveFont(14.0f));
		java.setForeground(Color.CYAN);
		add(java);

		JLabel url = new JLabel(" => !url - Go to specified url in browser                              ");
		url.setFont(url.getFont().deriveFont(14.0f));
		url.setForeground(Color.CYAN);
		add(url);

		JLabel prime = new JLabel(" => !prime - Get the prime numbers from 1 to 100");
		prime.setFont(prime.getFont().deriveFont(14.0f));
		prime.setForeground(Color.CYAN);
		add(prime);

		JLabel date = new JLabel(" => !date - Get the current date (d.m.y)                                                 ");
		date.setFont(date.getFont().deriveFont(14.0f));
		date.setForeground(Color.CYAN);
		add(date);

		JLabel exit = new JLabel(" => !exit - Quit the Java Prompt");
		exit.setFont(exit.getFont().deriveFont(14.0f));
		exit.setForeground(Color.CYAN);
		add(exit);

		command = new JTextField(40);
		command.setFont(command.getFont().deriveFont(16.0f));
		command.setBorder(null);
		command.setBackground(Color.BLACK);
		command.setForeground(Color.CYAN);
		command.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
				// -
			}

			@Override
			public void keyPressed(KeyEvent e) {
                		int key = e.getKeyCode();
                		if(key == KeyEvent.VK_ENTER) {
                			String cmd = command.getText();
                			if(cmd.equals("!prime")) {
                				Queue<Integer> prime = new LinkedList<>();
						for (int i = 1; i <= 100; i++) {
							int counter = 0;
							for(int num = i; num >= 1; num--) {
								if(i % num == 0) {
									counter++;
								}
							}
							if (counter == 2) {
								//Adding numbers.
								prime.add(i);
							}
						}
                				JOptionPane.showMessageDialog(null, "The prime numbers from 1 to 100 are:\n" + prime, "Prime numbers", JOptionPane.INFORMATION_MESSAGE);

						InetAddress ip;
						try {
							ip = InetAddress.getLocalHost();
							System.out.println("(" + ANSI_BLUE + "!" + ANSI_RESET + ") Command '" + ANSI_BLUE + "!prime" + ANSI_RESET + "' was executed by " + ANSI_BLUE + ip.getHostAddress() + ANSI_RESET);
						} catch (UnknownHostException m) {
							m.printStackTrace();
						}
					} else if(cmd.equals("!java")) {

                				HashMap<String, String> java = new HashMap<>();

                				String version = System.getProperty("java.version");
                				String runtime_version = System.getProperty("java.runtime.version");
                				String home = System.getProperty("java.home");
		                		String vendor = System.getProperty("java.vendor");
		                		String vendor_url = System.getProperty("java.vendor.url");

                				java.put("version", version);
						java.put("runtime_version", runtime_version);
						java.put("home", home);
						java.put("vendor", vendor);
						java.put("vendor_url", vendor_url);

						JOptionPane.showMessageDialog(null, "Java version: " + java.get("version") + "\nJava runtime version: " + java.get("runtime_version") + "\nJava home: " + java.get("home") + "\nJava vendor: " + java.get("vendor") + "\nJava vendor url: " + java.get("vendor_url"), "Java Information", JOptionPane.INFORMATION_MESSAGE);
						InetAddress ip;
						try {
							ip = InetAddress.getLocalHost();
							System.out.println("(" + ANSI_BLUE + "!" + ANSI_RESET + ") Command '" + ANSI_BLUE + "!java" + ANSI_RESET + "' was executed by " + ANSI_BLUE + ip.getHostAddress() + ANSI_RESET);
						} catch (UnknownHostException m) {
							m.printStackTrace();
						}
                			} else if(cmd.equals("!print")) {


						  LinkedList<String> print = new LinkedList<>();

						  String msg = JOptionPane.showInputDialog(null, "Type a message: ", "Custom Message", JOptionPane.INFORMATION_MESSAGE);
						  print.add(" " + msg + " ");

						if((msg != null)) {
							if (msg.length() == 0) {
								JOptionPane.showMessageDialog(null, "You must enter a message.", "Error - No message", JOptionPane.ERROR_MESSAGE);
								InetAddress ip;
								try {

									ip = InetAddress.getLocalHost();
									System.out.println("(" + ANSI_BLUE + "!" + ANSI_RESET + ") Command '" + ANSI_BLUE + "!print" + ANSI_RESET + "' was executed by " + ANSI_BLUE + ip.getHostAddress() + ANSI_RESET + ". No message.");

								} catch (UnknownHostException m) {

									m.printStackTrace();

								}
							} else {
								JOptionPane.showMessageDialog(null, "Your message:\n " + print, "Custom message", JOptionPane.INFORMATION_MESSAGE);
								InetAddress ip;
								try {

									ip = InetAddress.getLocalHost();
									System.out.println("(" + ANSI_BLUE + "!" + ANSI_RESET + ") Command '" + ANSI_BLUE + "!print" + ANSI_RESET + "' was executed by " + ANSI_BLUE + ip.getHostAddress() + ANSI_RESET + ". The message is: " + ANSI_BLUE + print + ANSI_RESET);

								} catch (UnknownHostException m) {

									m.printStackTrace();

								}
							}
						} else {
							InetAddress ip;
							try {

								ip = InetAddress.getLocalHost();
								System.out.println("(" + ANSI_BLUE + "!" + ANSI_RESET + ") Command '" + ANSI_BLUE + "!print" + ANSI_RESET + "' was executed by " + ANSI_BLUE + ip.getHostAddress() + ANSI_RESET + ". No message.");

							} catch (UnknownHostException m) {

								m.printStackTrace();

							}
						}

					} else if(cmd.equals("!url")) {
                         			ArrayList<String> link = new ArrayList<>();

                         			String url = JOptionPane.showInputDialog(null, "Enter an url: (https://`url`) ", "Link to browser", JOptionPane.INFORMATION_MESSAGE);

                         			if((url != null) && (url.length() > 0)) {
							 try {
								 Desktop.getDesktop().browse(new URI(url));
							 } catch (URISyntaxException | IOException ex) {
								 JOptionPane.showMessageDialog(null, "The link cannot be accessed", "Error - Link", JOptionPane.ERROR_MESSAGE);
							 }

							 link.add(url);

							 InetAddress ip;
							 try {

								 ip = InetAddress.getLocalHost();
								 System.out.println("(" + ANSI_BLUE + "!" + ANSI_RESET + ") Command '" + ANSI_BLUE + "!url" + ANSI_RESET + "' was executed by " + ANSI_BLUE + ip.getHostAddress() + ANSI_RESET + ". Link used: " + link);

							 } catch (UnknownHostException m) {

								 m.printStackTrace();

							 }
						 } else {
							 link.add(url);

							 InetAddress ip;
							 try {

								 ip = InetAddress.getLocalHost();
								 System.out.println("(" + ANSI_BLUE + "!" + ANSI_RESET + ") Command '" + ANSI_BLUE + "!url" + ANSI_RESET + "' was executed by " + ANSI_BLUE + ip.getHostAddress() + ANSI_RESET + ". No link ");

							 } catch (UnknownHostException m) {

								 m.printStackTrace();

							 }
						 }


					} else if(cmd.equals("!date")) {
                				Stack<Integer> date = new Stack<>();
						LocalDate data = LocalDate.now();
                				int year = data.getYear();
                				int month = data.getMonthValue();
                				int day = data.getDayOfMonth();

						date.push(day);
						date.push(month);
                				date.push(year);

                				JOptionPane.showMessageDialog(null, "The current date is: \n" + date.get(0) + "." + date.get(1) + "." + date.get(2), "Date", JOptionPane.INFORMATION_MESSAGE);

						InetAddress ip;
						try {

							ip = InetAddress.getLocalHost();
							System.out.println("(" + ANSI_BLUE + "!" + ANSI_RESET + ") Command '" + ANSI_BLUE + "!date" + ANSI_RESET + "' was executed by " + ANSI_BLUE + ip.getHostAddress() + ANSI_RESET + ". Date: " + date.get(0) + "." +  date.get(1) + "." + date.get(2));

						} catch (UnknownHostException m) {

							m.printStackTrace();

						}

					} else if(cmd.equals("!exit")) {

						InetAddress ip;
						try {

							ip = InetAddress.getLocalHost();
							System.out.println("(" + ANSI_BLUE + "!" + ANSI_RESET + ") Command '" + ANSI_BLUE + "!exit" + ANSI_RESET + "' was executed by " + ANSI_BLUE + ip.getHostAddress() + ANSI_RESET);

						} catch (UnknownHostException m) {

							m.printStackTrace();

						}

                		System.exit(0);
				} else {
	                		JOptionPane.showMessageDialog(null, "You've entered a wrong command", "Wrong command", JOptionPane.ERROR_MESSAGE);
					InetAddress ip;
					try {
						ip = InetAddress.getLocalHost();
						System.out.println("(" + ANSI_BLUE + "!" + ANSI_RESET + ") Unknown command '" + ANSI_BLUE + command.getText() + ANSI_RESET + "' was executed by " + ANSI_BLUE + ip.getHostAddress() + ANSI_RESET + ". Link used: ");
	
					} catch (UnknownHostException m) {
	
						m.printStackTrace();
	
					}
	
				}
			}
		}

			@Override
			public void keyReleased(KeyEvent e) {
				// -
			}
		});
		add(command);
	}

    public static void main(String[] args) {
	Main gui = new Main();
	gui.setVisible(true);
	gui.setResizable(false);
	gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	gui.setTitle("Java Prompt");
	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	int height = screenSize.height;
	int width = screenSize.width;
	gui.setLocation(width / 2 - gui.getSize().width / 2, height / 2 - gui.getSize().height / 2);
	gui.setSize(560, 290);
	gui.getContentPane().setBackground(Color.BLACK);
    }
}
