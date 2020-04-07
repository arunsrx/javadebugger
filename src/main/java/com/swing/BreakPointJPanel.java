package com.swing;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.debugger.AttachingDebugger;

/**
 * This program demonstrates how to use JPanel in Swing.
 * 
 * @author arunkumar
 */
public class BreakPointJPanel extends JFrame implements ActionListener {

    private JLabel labelClassname = new JLabel("Class Name: ");
    private JLabel labelClassLineNo = new JLabel("Line Number: ");
    private JTextField textClassname = new JTextField(20);
    private JTextField textClasslineno = new JTextField(20);
    private JButton buttonSetBreakPoint = new JButton("Set BreakPoint");
    private JButton buttonRemoveBreakPoint = new JButton("Remove BreakPoint");
    private JButton buttonResume = new JButton("Resume");

    private JTextArea textArea = new JTextArea(5, 70);
    private JScrollPane scrollPane = new JScrollPane(textArea);

    public BreakPointJPanel() {
        super("Java Debugger Server");

        // create a new panel with GridBagLayout manager
        JPanel newPanel = new JPanel(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.insets = new Insets(10, 10, 10, 10);

        // add components to the panel
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 0.1;
        constraints.weighty = 0.1;
        newPanel.add(labelClassname, constraints);

        constraints.gridx = 1;
        newPanel.add(textClassname, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        newPanel.add(labelClassLineNo, constraints);

        constraints.gridx = 1;
        newPanel.add(textClasslineno, constraints);

        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 2;
        constraints.anchor = GridBagConstraints.LINE_START;
        newPanel.add(buttonSetBreakPoint, constraints);

        constraints.gridx = 1;
        constraints.gridy = 2;
        constraints.gridwidth = 2;
        constraints.anchor = GridBagConstraints.LINE_END;
        newPanel.add(buttonRemoveBreakPoint, constraints);

        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.gridwidth = 2;
        constraints.anchor = GridBagConstraints.CENTER;
        newPanel.add(buttonResume, constraints);
        if (textArea.getText().isEmpty()) {
            buttonResume.setEnabled(false);
        }
        constraints.gridx = 0;
        constraints.gridy = 4;
        constraints.anchor = GridBagConstraints.LINE_START;
        textArea.setBackground(Color.LIGHT_GRAY);

        textArea.setEditable(false);

        scrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Status"));
        newPanel.add(scrollPane, constraints);
        // set border for the panel
        newPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "BreakPoint Details"));

        // add the panel to this frame
        add(newPanel);
        setUpListeners();
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        BreakPointJPanel demo = new BreakPointJPanel();
        demo.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Coding Part of LOGIN button
        if (e.getSource() == buttonSetBreakPoint) {
            setBreakPoint();
        }
        // Coding Part of cancel button
        if (e.getSource() == buttonRemoveBreakPoint) {
            removeBreakPoint();
            // dispose();
        }
    }

    private void setUpListeners() {

        buttonSetBreakPoint.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    setBreakPoint();
                }
            }
        });

        buttonSetBreakPoint.addActionListener(this);

        buttonRemoveBreakPoint.addActionListener(this);
    }

    private void setBreakPoint() {
        String className;
        String lineNo;
        className = textClassname.getText();
        lineNo = textClasslineno.getText();
        if (className != null && lineNo != null && !className.isEmpty() && !lineNo.isEmpty()) {
            try {
                // AttachingDebugger debugger = new AttachingDebugger();

                AttachingDebugger.addBreakPoint(className, Integer.parseInt(lineNo));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                textClassname.setText("");
                textClasslineno.setText("");
                textClassname.requestFocus();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Invalid Class name or line no for setting breakpoint", "Error",
                    JOptionPane.ERROR_MESSAGE);
            textClassname.setText("");
            textClasslineno.setText("");
            textClassname.requestFocus();
        }
    }

    private void removeBreakPoint() {
        String className;
        String lineNo;
        className = textClassname.getText();
        lineNo = textClasslineno.getText();
        if (className != null && lineNo != null && !className.isEmpty() && !lineNo.isEmpty()) {
            try {
                // AttachingDebugger debugger = new AttachingDebugger();

                AttachingDebugger.removeBreakPoint(className, Integer.parseInt(lineNo));
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                textClassname.setText("");
                textClasslineno.setText("");
                textClassname.requestFocus();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Invalid Class name or line no for setting breakpoint", "Error",
                    JOptionPane.ERROR_MESSAGE);
            textClassname.setText("");
            textClasslineno.setText("");
            textClassname.requestFocus();
        }
    }
}