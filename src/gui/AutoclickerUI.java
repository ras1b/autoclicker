package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import features.Autoclicker;
import features.CustomToggleButtonUI;

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
        toggleButton = new JToggleButton("Start CPS");
        toggleButton.setUI(new CustomToggleButtonUI());
        toggleButton.addActionListener(e -> {
            toggleClicking();
            ((CustomToggleButtonUI) toggleButton.getUI()).toggleButtonStateChanged(toggleButton);
        });

        // Toggle button for theme switching
        themeToggleButton = new JToggleButton("Toggle Theme: Light");
        themeToggleButton.setUI(new CustomToggleButtonUI());
        themeToggleButton.addActionListener(e -> {
            toggleTheme();
            ((CustomToggleButtonUI) themeToggleButton.getUI()).toggleButtonStateChanged(themeToggleButton);
        });

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
        isClicking = !isClicking;
        toggleButton.setText(isClicking ? "Stop CPS" : "Start CPS");
        toggleButton.setActionCommand(isClicking ? "Stop CPS" : "Start CPS");
        if (isClicking) {
            autoClicker.startClicking(cpsChoice.getItemAt(cpsChoice.getSelectedIndex()));
        } else {
            autoClicker.stopClicking();
        }
    }

    private void toggleTheme() {
        darkMode = !darkMode;
        themeToggleButton.setText(darkMode ? "Toggle Theme: Dark" : "Toggle Theme: Light");
        themeToggleButton.setActionCommand(darkMode ? "Toggle Theme: Dark" : "Toggle Theme: Light");
        updateUI();
    }

    private void updateUI() {
        Color backgroundColor = darkMode ? Color.decode("#141414") : Color.decode("#FFFFFF");
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
