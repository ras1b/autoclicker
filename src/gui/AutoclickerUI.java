package gui;

import features.Autoclicker;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class AutoclickerUI extends JFrame {
    private static final long serialVersionUID = 1L;
    private JComboBox<Integer> cpsChoice;
    private JToggleButton toggleButton, themeToggleButton;
    private Autoclicker autoClicker;
    private JLabel cpsLabel;
    private JPanel buttonPanel, cpsPanel, logoPanel;
    private boolean isClicking = false;
    private boolean darkMode = false;

    public AutoclickerUI() {
        setTitle("Autoclicker by ras1b");
        setSize(500, 300);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setIconImage(new ImageIcon("./img/translogo.png").getImage());

        cpsLabel = new JLabel("Select a CPS Level:");
        cpsChoice = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20});
        cpsChoice.setPreferredSize(new Dimension(100, 20));

        ImageIcon logoIconOriginal = new ImageIcon("./img/translogo.png");
        Image image = logoIconOriginal.getImage();
        Image newimg = image.getScaledInstance(120, 120, Image.SCALE_SMOOTH);
        ImageIcon logoIcon = new ImageIcon(newimg);
        JLabel logoLabel = new JLabel(logoIcon);
        logoPanel = new JPanel();
        logoPanel.add(logoLabel);

        // Toggle button for start/stop
        toggleButton = new JToggleButton("Start");
        toggleButton.addActionListener(e -> toggleClicking());

        // Toggle button for theme switching
        themeToggleButton = new JToggleButton("Toggle Theme: Light");
        themeToggleButton.addActionListener(e -> toggleTheme());

        buttonPanel = new JPanel();
        buttonPanel.add(toggleButton);
        buttonPanel.add(themeToggleButton);

        add(logoPanel, BorderLayout.NORTH);
        cpsPanel = new JPanel();
        cpsPanel.add(cpsLabel);
        cpsPanel.add(cpsChoice);
        add(cpsPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        autoClicker = new Autoclicker();
        setVisible(true);
    }

    private void toggleClicking() {
        isClicking = !isClicking; // Toggle state
        if (isClicking) {
            autoClicker.startClicking((Integer) cpsChoice.getSelectedItem());
            toggleButton.setText("Stop");
        } else {
            autoClicker.stopClicking();
            toggleButton.setText("Start");
        }
    }

    private void toggleTheme() {
        darkMode = !darkMode; // Toggle state
        Color backgroundColor = darkMode ? Color.decode("#141414") : Color.decode("#ffffff");
        Color textColor = darkMode ? Color.white : Color.black;

        cpsPanel.setBackground(backgroundColor);
        buttonPanel.setBackground(backgroundColor);
        logoPanel.setBackground(backgroundColor);
        getContentPane().setBackground(backgroundColor);
        cpsLabel.setForeground(textColor);
        cpsChoice.setBackground(backgroundColor);
        cpsChoice.setForeground(textColor);

        toggleButton.setForeground(textColor);
        themeToggleButton.setText(darkMode ? "Toggle Theme: Dark" : "Toggle Theme: Light");
        themeToggleButton.setForeground(textColor);
    }

    public static void main(String[] args) {
        new AutoclickerUI();
    }
}
