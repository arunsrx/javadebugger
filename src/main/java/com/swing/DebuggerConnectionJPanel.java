package com.swing;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.*;

import com.debugger.AttachingDebugger;
import com.sun.jdi.VirtualMachine;

/**
 * Main class of the Debugger project.
 * This JPanel allows user the specify a host/server name on which the program to be debugged is running.
 * also allows user to specify port number fo the debugged program.
 *
 * @author arunkumar
 */
public class DebuggerConnectionJPanel extends JFrame implements ActionListener {

    private JLabel labelServername = new JLabel("Server/Host Name: ");
    private JLabel labelServerport = new JLabel("Server Port: ");
    private JTextField textServername = new JTextField(20);
    private JTextField textServerport = new JTextField(20);
    private JButton buttonConnect = new JButton("Connect");
    private JButton buttonCancel = new JButton("Cancel");

    public DebuggerConnectionJPanel() {
        super("Java Debugger Server");

        // create a new panel with GridBagLayout manager
        JPanel newPanel = new JPanel(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(10, 10, 10, 10);

        // add components to the panel
        constraints.gridx = 0;
        constraints.gridy = 0;
        newPanel.add(labelServername, constraints);

        constraints.gridx = 1;
        newPanel.add(textServername, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        newPanel.add(labelServerport, constraints);

        constraints.gridx = 1;
        newPanel.add(textServerport, constraints);

        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 2;
        constraints.anchor = GridBagConstraints.CENTER;
        newPanel.add(buttonConnect, constraints);

        constraints.gridx = 1;
        constraints.gridy = 2;
        constraints.gridwidth = 2;
        constraints.anchor = GridBagConstraints.LINE_END;
        newPanel.add(buttonCancel, constraints);
        // set border for the panel
        newPanel.setBorder(
                BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Server Connection Details"));

        // add the panel to this frame
        add(newPanel);
        setUpListeners();
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    /**
      public static void main(String[] args) { // set look and feel to the system
         SwingUtilities.invokeLater(new Runnable() {
              @Override
              public void run() {
                  new
                          DebuggerConnectionJPanel().setVisible(true);
              }
          });
      }
     */

    /**
     * Main entry class of debugger program.
     *
     * @param
     */
    public static void main(String[] args) {
        DebuggerConnectionJPanel demo = new DebuggerConnectionJPanel();
        demo.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Coding Part of LOGIN button
        if (e.getSource() == buttonConnect) {
            connect();
        }
        // Coding Part of cancel button
        if (e.getSource() == buttonCancel) {
            dispose();
        }
    }

    /**
     * Setup listeners on the buttons.
     */
    private void setUpListeners() {
        buttonConnect.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    connect();
                }
            }
        });

        textServerport.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    connect();
                }
            }
        });
        buttonConnect.addActionListener(this);
        buttonCancel.addActionListener(this);
    }

    /**
     * connect to the debuggee VM
     */
    private void connect() {
        String serverName;
        String serverPort;
        serverName = textServername.getText();
        serverPort = textServerport.getText();
        if (serverName != null && serverPort != null && !serverName.isEmpty() && !serverPort.isEmpty()) {
            try {
                // AttachingDebugger debugger = new AttachingDebugger();
                VirtualMachine vm = AttachingDebugger.attachToVM(serverName, serverPort);
                if (vm == null) {
                    JOptionPane.showMessageDialog(this,
                            "Invalid Server Name or Port, Cannot connect to remote server for debugging");
                    textServername.setText("");
                    textServerport.setText("");
                    textServername.requestFocus();
                } else {
                    this.dispose();
                    BreakPointJPanel breakPointJPanel = new BreakPointJPanel();
                    breakPointJPanel.setVisible(true);
                    Runnable runnable = () -> {
                        System.out.println("Spawning JDI event listener thread.");
                        AttachingDebugger.processEvents();
                        breakPointJPanel.dispose();
                    };
                    new Thread(runnable).start();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                textServername.setText("");
                textServerport.setText("");
                textServername.requestFocus();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Invalid Server Name or Port", "Error", JOptionPane.ERROR_MESSAGE);
            textServername.setText("");
            textServerport.setText("");
            textServername.requestFocus();
        }
    }
}