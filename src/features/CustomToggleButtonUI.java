package features;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JToggleButton;
import javax.swing.Timer;
import javax.swing.plaf.basic.BasicToggleButtonUI;

public class CustomToggleButtonUI extends BasicToggleButtonUI {
    private final Color offColor = Color.decode("#72A0C1");
    private final Color onColor = Color.decode("#002D62");
    private final Color handleColor = Color.WHITE;
    private double animationPosition = 0;
    private Timer timer;

    @Override
    public void installUI(JComponent c) {
        super.installUI(c);
        JToggleButton toggleButton = (JToggleButton) c;
        toggleButton.setBorderPainted(false);
        toggleButton.setContentAreaFilled(false);
        toggleButton.setFocusPainted(false);

        timer = new Timer(10, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                double targetPosition = toggleButton.isSelected() ? toggleButton.getWidth() - toggleButton.getHeight() : 0;
                if (Math.abs(animationPosition - targetPosition) > 1) {
                    animationPosition += (targetPosition - animationPosition) * 0.2;
                    toggleButton.repaint();
                } else {
                    animationPosition = targetPosition;
                    timer.stop();
                }
            }
        });
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        AbstractButton b = (AbstractButton) c;
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Background
        int arc = b.getHeight() - 1;
        g2.setColor(b.isSelected() ? onColor : offColor);
        g2.fillRoundRect(1, 1, b.getWidth() - 2, b.getHeight() - 2, arc, arc);

        // Handle
        int handleRadius = b.getHeight() - 4;
        int handlePosition = (int) animationPosition + 1;
        g2.setColor(handleColor);
        g2.fillOval(handlePosition, 2, handleRadius, handleRadius);

        g2.dispose();
    }

    public void toggleButtonStateChanged(JToggleButton toggleButton) {
        if (!timer.isRunning()) {
            timer.start();
        }
    }
}
