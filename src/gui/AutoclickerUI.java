package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.plaf.FontUIResource;

import features.Autoclicker;
import features.ApplicationFocusHelper;
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
    private JButton hotkeyButton;
    private JLabel hotkeyLabel;
    private int assignedKey = KeyEvent.VK_UNDEFINED;

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
        cpsChoice.setPreferredSize(new Dimension(250, 25));
        cpsLabel.setFont(customFont);
        cpsChoice.setFont(customFont);

        programLabel = new JLabel("Capture a program: ");
        programChoice = new JComboBox<>();
        programChoice.setPreferredSize(new Dimension(250, 25));
        programLabel.setFont(customFont);
        programChoice.setFont(customFont);
        loadRunningPrograms();

        refreshButton = new JButton(new ImageIcon(new ImageIcon(REFRESH_ICON_PATH).getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
        refreshButton.setPreferredSize(new Dimension(25, 25));
        refreshButton.addActionListener(e -> loadRunningPrograms());

        cancelButton = new JButton(new ImageIcon(new ImageIcon(CANCEL_ICON_PATH).getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
        cancelButton.setPreferredSize(new Dimension(25, 25));
        cancelButton.addActionListener(e -> programChoice.setSelectedItem("Specify a program"));

        ImageIcon logoIconOriginal = new ImageIcon("./img/refinelogo.png");
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
            ((CustomToggleButtonUI) toggleButton.getUI()).toggleButtonStateChanged(toggleButton);
        });
        themeToggleButton.addActionListener(e -> {
            toggleTheme();
            ((CustomToggleButtonUI) themeToggleButton.getUI()).toggleButtonStateChanged(themeToggleButton);
        });

        toggleButtonLabel.setFont(customFont);
        themeToggleButtonLabel.setFont(customFont);
        toggleButton.setFont(customFont);
        themeToggleButton.setFont(customFont);

        hotkeyLabel = new JLabel("Assign Hotkey:");
        hotkeyLabel.setFont(customFont);

        hotkeyButton = new JButton("Unassigned");
        hotkeyButton.setFont(customFont);
        hotkeyButton.addActionListener(e -> assignHotkey());

        buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        buttonPanel.add(toggleButtonLabel);
        buttonPanel.add(toggleButton);
        buttonPanel.add(themeToggleButtonLabel);
        buttonPanel.add(themeToggleButton);

        cpsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        cpsPanel.add(cpsLabel, gbc);
        gbc.gridx = 1;
        cpsPanel.add(cpsChoice, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        cpsPanel.add(programLabel, gbc);
        gbc.gridx = 1;
        cpsPanel.add(programChoice, gbc);
        gbc.gridx = 2;
        cpsPanel.add(refreshButton, gbc);
        gbc.gridx = 3;
        cpsPanel.add(cancelButton, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        cpsPanel.add(hotkeyLabel, gbc);
        gbc.gridx = 1;
        cpsPanel.add(hotkeyButton, gbc);

        add(logoPanel, BorderLayout.NORTH);
        add(cpsPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        autoClicker = new Autoclicker();
        updateUI(); // Apply the saved theme preference
        setVisible(true);

        // Start continuous mouse position check
        startMousePositionCheck();

        // Start listening for key presses
        addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == assignedKey) {
                    toggleClicking();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {}
        });
        setFocusable(true);
    }

    private void loadRunningPrograms() {
        programChoice.removeAllItems();
        programChoice.addItem("Specify a program");

        List<String> titles = ApplicationFocusHelper.getTopLevelWindowTitles();
        titles.forEach(programChoice::addItem);
    }

    private void toggleClicking() {
        isClicking = !isClicking;
        updateLabels();
        if (isClicking) {
            String selectedProgram = (String) programChoice.getSelectedItem();
            int cps = cpsChoice.getItemAt(cpsChoice.getSelectedIndex());
            if (selectedProgram == null || selectedProgram.equals("Specify a program")) {
                autoClicker.startClicking(cps, null); // No specific program
            } else {
                autoClicker.startClicking(cps, selectedProgram);
            }
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
        toggleButtonLabel.setForeground(textColor);
        themeToggleButton.setForeground(textColor);
        themeToggleButtonLabel.setForeground(textColor);
        programLabel.setForeground(textColor);
        programChoice.setBackground(backgroundColor);
        programChoice.setForeground(textColor);
        hotkeyLabel.setForeground(textColor);
        hotkeyButton.setBackground(backgroundColor);
        hotkeyButton.setForeground(textColor);
    }

    private void updateLabels() {
        toggleButton.setText(isClicking ? "Status: Active" : "Status: Inactive");
        themeToggleButton.setText(darkMode ? "Theme: Dark" : "Theme: Light");
        toggleButtonLabel.setText(isClicking ? "Status: Active" : "Status: Inactive");
        themeToggleButtonLabel.setText(darkMode ? "Theme: Dark" : "Theme: Light");
        updateUI();
    }

    // Method to assign a hotkey
    private void assignHotkey() {
        hotkeyButton.setText("Press a key...");
        KeyListener keyListener = new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {
                assignedKey = e.getKeyCode();
                hotkeyButton.setText(KeyEvent.getKeyText(assignedKey));
                removeKeyListener(this);
            }

            @Override
            public void keyReleased(KeyEvent e) {}
        };
        addKeyListener(keyListener);
        setFocusable(true);
        requestFocusInWindow();
    }

    // New method to continuously check mouse position and print application name
    private void startMousePositionCheck() {
        Timer timer = new Timer(500, e -> {
            List<String> titles = ApplicationFocusHelper.getTopLevelWindowTitles();
            for (String title : titles) {
                if (ApplicationFocusHelper.isMouseOverAndFocusedWindow(title)) {
                    // System.out.println("Mouse is over: " + title);
                    break;
                }
            }
        });
        timer.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AutoclickerUI::new);
    }
}
