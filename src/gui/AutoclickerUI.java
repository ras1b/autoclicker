package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.prefs.Preferences;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.plaf.FontUIResource;

import features.Autoclicker;
import features.CustomToggleButtonUI;

public class AutoclickerUI extends JFrame {
    private static final long serialVersionUID = 1L;
    private JComboBox<Integer> cpsChoice;
    private JToggleButton toggleButton, themeToggleButton;
    private JLabel cpsLabel, toggleButtonLabel, themeToggleButtonLabel;
    private JPanel buttonPanel, cpsPanel, logoPanel;
    private Autoclicker autoClicker;
    private boolean isClicking = false;
    private boolean darkMode = false;
    private Font customFont;
    private static final String PREFS_NAME = "AutoclickerPreferences";
    private static final String THEME_KEY = "theme";
    private Preferences preferences;
    private JComboBox<String> programChoice;
    private JLabel programLabel;
    private JButton refreshButton, cancelButton;
    private static final String REFRESH_ICON_PATH = "./img/refresh.png";
    private static final String CANCEL_ICON_PATH = "./img/cancel.png";

    public AutoclickerUI() {
        preferences = Preferences.userRoot().node(PREFS_NAME);
        darkMode = preferences.getBoolean(THEME_KEY, false); // Load the saved theme preference

        setTitle("Autoclicker by ras1b");
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setIconImage(new ImageIcon("./img/logo.png").getImage());

        // Load and set the custom font
        try {
            customFont = Font.createFont(Font.TRUETYPE_FONT, new File("./font/LoveDays.ttf")).deriveFont(12f);
            setFont(new FontUIResource(customFont));
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
            customFont = new JLabel().getFont(); // Use default font if custom font fails to load
        }

        cpsLabel = new JLabel("Select a CPS Level:");
        cpsChoice = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20});
        cpsChoice.setPreferredSize(new Dimension(100, 20));
        cpsLabel.setFont(customFont);
        cpsChoice.setFont(customFont);

        programLabel = new JLabel("Capture a program:");
        programChoice = new JComboBox<>();
        programChoice.setPreferredSize(new Dimension(200, 20));
        programLabel.setFont(customFont);
        programChoice.setFont(customFont);
        loadRunningPrograms();

        refreshButton = new JButton(new ImageIcon(new ImageIcon(REFRESH_ICON_PATH).getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
        refreshButton.setPreferredSize(new Dimension(25, 25));
        refreshButton.addActionListener(e -> loadRunningPrograms());

        cancelButton = new JButton(new ImageIcon(new ImageIcon(CANCEL_ICON_PATH).getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
        cancelButton.setPreferredSize(new Dimension(25, 25));
        cancelButton.addActionListener(e -> programChoice.setSelectedItem("Specify a program"));

        ImageIcon logoIconOriginal = new ImageIcon("./img/translogo.png");
        Image image = logoIconOriginal.getImage();
        Image newimg = image.getScaledInstance(120, 120, Image.SCALE_SMOOTH);
        ImageIcon logoIcon = new ImageIcon(newimg);
        JLabel logoLabel = new JLabel(logoIcon);
        logoPanel = new JPanel();
        logoPanel.add(logoLabel);

        toggleButton = new JToggleButton("Status: Inactive");
        themeToggleButton = new JToggleButton(darkMode ? "Theme: Dark" : "Theme: Light");

        toggleButton.setUI(new CustomToggleButtonUI());
        themeToggleButton.setUI(new CustomToggleButtonUI());

        toggleButtonLabel = new JLabel("Status: Inactive");
        themeToggleButtonLabel = new JLabel(darkMode ? "Theme: Dark" : "Theme: Light");

        toggleButton.addActionListener(e -> {
            toggleClicking();
            ((CustomToggleButtonUI)toggleButton.getUI()).toggleButtonStateChanged(toggleButton);
        });
        themeToggleButton.addActionListener(e -> {
            toggleTheme();
            ((CustomToggleButtonUI)themeToggleButton.getUI()).toggleButtonStateChanged(themeToggleButton);
        });

        toggleButtonLabel.setFont(customFont);
        themeToggleButtonLabel.setFont(customFont);
        toggleButton.setFont(customFont);
        themeToggleButton.setFont(customFont);

        buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        buttonPanel.add(toggleButtonLabel);
        buttonPanel.add(toggleButton);
        buttonPanel.add(themeToggleButtonLabel);
        buttonPanel.add(themeToggleButton);

        cpsPanel = new JPanel();
        cpsPanel.add(cpsLabel);
        cpsPanel.add(cpsChoice);
        cpsPanel.add(programLabel);
        cpsPanel.add(programChoice);
        cpsPanel.add(refreshButton);
        cpsPanel.add(cancelButton);

        add(logoPanel, BorderLayout.NORTH);
        add(cpsPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        autoClicker = new Autoclicker();
        updateUI(); // Apply the saved theme preference
        setVisible(true);
    }

    private void loadRunningPrograms() {
        programChoice.removeAllItems();
        programChoice.addItem("Specify a program");
        ProcessHandle.allProcesses()
            .filter(ProcessHandle::isAlive)
            .map(ProcessHandle::info)
            .map(info -> info.command().orElse("") + " " + info.arguments().map(args -> String.join(" ", args)).orElse(""))
            .filter(command -> !command.isEmpty() && !command.contains("Windows"))
            .forEach(programChoice::addItem);
    }

    private void toggleClicking() {
        isClicking = !isClicking;
        updateLabels();
        if (isClicking) {
            String selectedProgram = (String) programChoice.getSelectedItem();
            autoClicker.startClicking(cpsChoice.getItemAt(cpsChoice.getSelectedIndex()), selectedProgram);
        } else {
            autoClicker.stopClicking();
        }
    }

    private void toggleTheme() {
        darkMode = !darkMode;
        preferences.putBoolean(THEME_KEY, darkMode); // Save the theme preference
        updateLabels();
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
        toggleButtonLabel.setForeground(textColor);
        themeToggleButton.setForeground(textColor);
        themeToggleButtonLabel.setForeground(textColor);
        programLabel.setForeground(textColor);
        programChoice.setBackground(backgroundColor);
        programChoice.setForeground(textColor);
    }

    private void updateLabels() {
        toggleButton.setText(isClicking ? "Status: Active" : "Status: Inactive");
        themeToggleButton.setText(darkMode ? "Theme: Dark" : "Theme: Light");
        toggleButtonLabel.setText(isClicking ? "Status: Active" : "Status: Inactive");
        themeToggleButtonLabel.setText(darkMode ? "Theme: Dark" : "Theme: Light");
        updateUI();
    }

    public static void main(String[] args) {
        new AutoclickerUI();
    }
}
