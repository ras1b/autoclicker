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

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseInputListener;

import features.ApplicationFocusHelper;
import features.Autoclicker;
import features.CustomToggleButtonUI;

public class AutoclickerUI extends JFrame {
    private static final long serialVersionUID = 1L;
    private JComboBox<Integer> cpsChoice;
    private JToggleButton toggleButton, themeToggleButton;
    private JLabel cpsLabel, toggleButtonLabel, themeToggleButtonLabel, hotkeyLabel, programLabel;
    private JPanel buttonPanel, cpsPanel, logoPanel, footerPanel;
    private JButton refreshButton, cancelButton, hotkeyButton, hotkeyCancelButton;
    private Autoclicker autoClicker;
    private boolean isClicking = false;
    private boolean darkMode = false;
    private Font customFont;
    private static final String PREFS_NAME = "AutoclickerPreferences";
    private static final String THEME_KEY = "theme";
    private Preferences preferences;
    private JComboBox<String> programChoice;
    private int assignedKey = KeyEvent.VK_UNDEFINED;
    private int assignedMouseButton = -1; // -1 indicates no mouse button is assigned
    private Timer mouseHoldTimer; // Timer to detect mouse button hold
    private JLabel copyrightLabel;

    public AutoclickerUI() {
        preferences = Preferences.userRoot().node(PREFS_NAME);
        darkMode = preferences.getBoolean(THEME_KEY, false); // Load the saved theme preference

        setTitle("Autoclicker by ras1b [v1.0]");
        setSize(500, 350); // Increased height by 50
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setIconImage(new ImageIcon(getClass().getResource("/img/logo.png")).getImage());

        // Load and set the custom font
        try {
            customFont = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/font/LoveDays.ttf")).deriveFont(12f);
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

        refreshButton = new JButton(new ImageIcon(new ImageIcon(getClass().getResource("/img/refresh.png")).getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
        refreshButton.setPreferredSize(new Dimension(25, 25));
        refreshButton.addActionListener(e -> loadRunningPrograms());

        cancelButton = new JButton(new ImageIcon(new ImageIcon(getClass().getResource("/img/cancel.png")).getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
        cancelButton.setPreferredSize(new Dimension(25, 25));
        cancelButton.addActionListener(e -> programChoice.setSelectedItem("Specify a program"));

        ImageIcon logoIconOriginal = new ImageIcon(getClass().getResource("/img/translogo.png"));
        Image image = logoIconOriginal.getImage();
        Image newimg = image.getScaledInstance(120, 120, Image.SCALE_SMOOTH);
        ImageIcon logoIcon = new ImageIcon(newimg);
        JLabel logoLabel = new JLabel(logoIcon);
        logoPanel = new JPanel();
        logoPanel.add(logoLabel);

        toggleButton = new JToggleButton("Inactive");
        themeToggleButton = new JToggleButton(darkMode ? "Dark Mode" : "Light Mode");

        toggleButton.setUI(new CustomToggleButtonUI());
        themeToggleButton.setUI(new CustomToggleButtonUI());

        toggleButtonLabel = new JLabel("Inactive");
        themeToggleButtonLabel = new JLabel(darkMode ? "Dark Mode" : "Light Mode");

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

        hotkeyLabel = new JLabel("Assign CPS Hotkey:");
        hotkeyLabel.setFont(customFont);

        hotkeyButton = new JButton("Unassigned");
        hotkeyButton.setFont(customFont);
        hotkeyButton.addActionListener(e -> assignHotkey());

        hotkeyCancelButton = new JButton(new ImageIcon(new ImageIcon(getClass().getResource("/img/cancel.png")).getImage().getScaledInstance(23, 23, Image.SCALE_SMOOTH)));
        hotkeyCancelButton.setPreferredSize(new Dimension(29, 29));
        hotkeyCancelButton.addActionListener(e -> {
            assignedKey = KeyEvent.VK_UNDEFINED;
            assignedMouseButton = -1;
            hotkeyButton.setText("Unassigned");
            if (isClicking) {
                toggleClicking();
            }
        });

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
        gbc.gridx = 2;
        cpsPanel.add(hotkeyCancelButton, gbc);

        // Adding button panel and footer panel in a vertical box layout
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(buttonPanel, BorderLayout.NORTH);

        // Footer panel with social media icons and copyright text
        footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        copyrightLabel = new JLabel("Copyright " + java.time.Year.now().getValue() + " All rights reserved - Developed by ras1b");
        copyrightLabel.setFont(customFont);

        JButton gitlabButton = new JButton(new ImageIcon(new ImageIcon(getClass().getResource("/img/gitlab.png")).getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
        gitlabButton.setPreferredSize(new Dimension(25, 25));
        gitlabButton.addActionListener(e -> openURL("https://gitlab.com/ras1b"));

        JButton githubButton = new JButton(new ImageIcon(new ImageIcon(getClass().getResource("/img/github.png")).getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
        githubButton.setPreferredSize(new Dimension(25, 25));
        githubButton.addActionListener(e -> openURL("https://github.com/ras1b/autoclicker"));

        JButton discordButton = new JButton(new ImageIcon(new ImageIcon(getClass().getResource("/img/discord.png")).getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
        discordButton.setPreferredSize(new Dimension(25, 25));
        discordButton.addActionListener(e -> {
            copyToClipboard("ras1b");
            showPopup("Copied ras1b's Discord username");
        });

        footerPanel.add(copyrightLabel);
        footerPanel.add(gitlabButton);
        footerPanel.add(githubButton);
        footerPanel.add(discordButton);

        southPanel.add(footerPanel, BorderLayout.SOUTH);

        add(logoPanel, BorderLayout.NORTH);
        add(cpsPanel, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);

        autoClicker = new Autoclicker();
        updateUI(); // Apply the saved theme preference
        setVisible(true);

        // Start continuous mouse position check
        startMousePositionCheck();

        // Register global key and mouse listeners
        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException ex) {
            ex.printStackTrace();
        }

        GlobalScreen.addNativeKeyListener(globalKeyListener);
        GlobalScreen.addNativeMouseListener(globalMouseListener);
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
        footerPanel.setBackground(backgroundColor);
        getContentPane().setBackground(backgroundColor);
        cpsLabel.setForeground(textColor);
        cpsChoice.setBackground(backgroundColor);
        cpsChoice.setForeground(textColor);
        themeToggleButton.setForeground(textColor);
        themeToggleButtonLabel.setForeground(textColor);
        programLabel.setForeground(textColor);
        programChoice.setBackground(backgroundColor);
        programChoice.setForeground(textColor);
        hotkeyLabel.setForeground(textColor);
        hotkeyButton.setBackground(backgroundColor);
        hotkeyButton.setForeground(textColor);
        toggleButton.setForeground(isClicking ? Color.GREEN : Color.RED);
        toggleButtonLabel.setForeground(isClicking ? Color.GREEN : Color.RED);

        themeToggleButtonLabel.setText(darkMode ? "Dark Mode" : "Light Mode");
        copyrightLabel.setForeground(textColor); // Set the color of the copyright label
    }

    private void updateLabels() {
        toggleButton.setText(isClicking ? "Active" : "Inactive");
        toggleButtonLabel.setText(isClicking ? "Active" : "Inactive");
        toggleButtonLabel.setForeground(isClicking ? Color.GREEN : Color.RED);
        themeToggleButtonLabel.setText(darkMode ? "Dark Mode" : "Light Mode");
        updateUI();
    }

    // Method to assign a hotkey
    private void assignHotkey() {
        hotkeyButton.setText("Press a key or mouse button...");

        // Clear previous listeners
        GlobalScreen.removeNativeKeyListener(globalKeyListener);
        GlobalScreen.removeNativeMouseListener(globalMouseListener);

        // Declare tempKeyListener so it can be referenced in tempMouseListener
        final NativeKeyListener[] tempKeyListener = new NativeKeyListener[1];

        // Define the tempMouseListener
        final NativeMouseInputListener tempMouseListener = new NativeMouseInputListener() {
            @Override
            public void nativeMousePressed(NativeMouseEvent e) {
                assignedKey = KeyEvent.VK_UNDEFINED;
                assignedMouseButton = e.getButton();
                String buttonText = switch (assignedMouseButton) {
                    case NativeMouseEvent.BUTTON1 -> "LMB";
                    case NativeMouseEvent.BUTTON2 -> "MMB";
                    case NativeMouseEvent.BUTTON3 -> "RMB";
                    default -> "Unassigned";
                };
                hotkeyButton.setText(buttonText);
                // Remove both temp listeners
                GlobalScreen.removeNativeKeyListener(tempKeyListener[0]);
                GlobalScreen.removeNativeMouseListener(this);
                // Re-add global listeners
                GlobalScreen.addNativeKeyListener(globalKeyListener);
                GlobalScreen.addNativeMouseListener(globalMouseListener);
            }

            @Override
            public void nativeMouseReleased(NativeMouseEvent e) {}

            @Override
            public void nativeMouseClicked(NativeMouseEvent e) {}

            @Override
            public void nativeMouseMoved(NativeMouseEvent e) {}

            @Override
            public void nativeMouseDragged(NativeMouseEvent e) {}
        };

        // Define tempKeyListener
        tempKeyListener[0] = new NativeKeyListener() {
            @Override
            public void nativeKeyPressed(NativeKeyEvent e) {
                assignedKey = e.getKeyCode();
                assignedMouseButton = -1;
                hotkeyButton.setText(NativeKeyEvent.getKeyText(assignedKey));
                // Remove both temp listeners
                GlobalScreen.removeNativeKeyListener(this);
                GlobalScreen.removeNativeMouseListener(tempMouseListener);
                // Re-add global listeners
                GlobalScreen.addNativeKeyListener(globalKeyListener);
                GlobalScreen.addNativeMouseListener(globalMouseListener);
            }

            @Override
            public void nativeKeyReleased(NativeKeyEvent e) {}

            @Override
            public void nativeKeyTyped(NativeKeyEvent e) {}
        };

        // Add temporary listeners to capture the next key or mouse button press
        GlobalScreen.addNativeKeyListener(tempKeyListener[0]);
        GlobalScreen.addNativeMouseListener(tempMouseListener);
    }

    private final NativeKeyListener globalKeyListener = new NativeKeyListener() {
        @Override
        public void nativeKeyPressed(NativeKeyEvent e) {
            if (e.getKeyCode() == assignedKey) {
                toggleClicking();
            }
        }

        @Override
        public void nativeKeyReleased(NativeKeyEvent e) {}

        @Override
        public void nativeKeyTyped(NativeKeyEvent e) {}
    };

    private final NativeMouseInputListener globalMouseListener = new NativeMouseInputListener() {
        @Override
        public void nativeMousePressed(NativeMouseEvent e) {
            if (assignedMouseButton != -1 && e.getButton() == assignedMouseButton) {
                if (mouseHoldTimer != null && mouseHoldTimer.isRunning()) {
                    mouseHoldTimer.stop();
                }
                mouseHoldTimer = new Timer(500, evt -> {
                    if (e.getButton() == assignedMouseButton && !autoClicker.isAutomatedClick()) {
                        startAutoClicking();
                    }
                });
                mouseHoldTimer.setRepeats(false);
                mouseHoldTimer.start();
            }
        }

        @Override
        public void nativeMouseReleased(NativeMouseEvent e) {
            if (e.getButton() == assignedMouseButton && mouseHoldTimer != null && mouseHoldTimer.isRunning()) {
                mouseHoldTimer.stop();
            }
            if (e.getButton() == assignedMouseButton && !autoClicker.isAutomatedClick()) {
                stopAutoClicking();
            }
        }

        @Override
        public void nativeMouseMoved(NativeMouseEvent e) {}

        @Override
        public void nativeMouseDragged(NativeMouseEvent e) {}
    };

    private void startAutoClicking() {
        if (!isClicking) {
            String selectedProgram = (String) programChoice.getSelectedItem();
            int cps = cpsChoice.getItemAt(cpsChoice.getSelectedIndex());
            if (selectedProgram == null || selectedProgram.equals("Specify a program")) {
                autoClicker.startClicking(cps, null); // No specific program
            } else {
                autoClicker.startClicking(cps, selectedProgram);
            }
            isClicking = true;
            updateLabels();
        }
    }

    private void stopAutoClicking() {
        if (isClicking) {
            autoClicker.stopClicking();
            isClicking = false;
            updateLabels();
        }
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

    // Method to open a URL in the default web browser
    private void openURL(String url) {
        try {
            java.awt.Desktop.getDesktop().browse(java.net.URI.create(url));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to copy text to the clipboard
    private void copyToClipboard(String text) {
        java.awt.datatransfer.StringSelection stringSelection = new java.awt.datatransfer.StringSelection(text);
        java.awt.Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
    }

    // Method to show a popup message
    private void showPopup(String message) {
        final JFrame popup = new JFrame();
        popup.setUndecorated(true);
        popup.setSize(200, 50);
        popup.setLayout(new FlowLayout());
        popup.add(new JLabel(message));
        popup.setLocationRelativeTo(this);
        popup.setVisible(true);
        Timer timer = new Timer(1000, e -> popup.setVisible(false)); // 1 second popup
        timer.setRepeats(false);
        timer.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AutoclickerUI::new);
    }
}
