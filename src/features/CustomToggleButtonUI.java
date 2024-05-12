package features;

import javax.swing.plaf.basic.BasicToggleButtonUI;
import java.awt.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CustomToggleButtonUI extends BasicToggleButtonUI {
    private final Color offColor = new Color(0xFF6347); // Red for "off"
    private final Color onColor = new Color(0x4CAF50);  // Green for "on"
    private final Color handleColor = Color.WHITE;
    private double animationPosition = 0;
    private Timer timer;

    @Override
    public void installUI(JComponent c) {
        super.installUI(c);
        ((JToggleButton) c).setBorderPainted(false);
        ((JToggleButton) c).setContentAreaFilled(false);
        ((JToggleButton) c).setFocusPainted(false);

        timer = new Timer(10, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JToggleButton toggleButton = (JToggleButton) c;
                double targetPosition = toggleButton.isSelected() ? toggleButton.getWidth() - toggleButton.getHeight() : 0;
                double increment = (toggleButton.getWidth() - toggleButton.getHeight()) * 0.05; // 5% of the distance per frame

                if (toggleButton.isSelected() && animationPosition < targetPosition) {
                    animationPosition = Math.min(animationPosition + increment, targetPosition);
                } else if (!toggleButton.isSelected() && animationPosition > 0) {
                    animationPosition = Math.max(animationPosition - increment, 0);
                } else {
                    timer.stop(); // Stop the timer when the final position is reached
                }
                toggleButton.repaint();
            }
        });
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        AbstractButton b = (AbstractButton) c;
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Background
        g2.setColor(b.isSelected() ? onColor : offColor);
        g2.fillRoundRect(0, 0, b.getWidth() - 1, b.getHeight() - 1, 30, 30);

        // Toggle Handle
        g2.setColor(handleColor);
        g2.fillOval((int) animationPosition, 1, b.getHeight() - 2, b.getHeight() - 2);

        // Text Rendering
        drawText(g2, b, b.isSelected());

        g2.dispose();
    }

    private void drawText(Graphics2D g2, AbstractButton b, boolean selected) {
        Font serifBold = new Font("Serif", Font.BOLD, 14);
        g2.setFont(serifBold);
        g2.setColor(Color.BLACK);
        String text = b.getActionCommand();
        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getAscent();
        // Ensure the text does not overlap with the handle by placing it appropriately
        int xText = selected ? (int) (animationPosition + b.getHeight()) : 5;
        int yText = (b.getHeight() / 2) + (textHeight / 4);
        g2.drawString(text, xText, yText);
    }

    public void toggleButtonStateChanged(JToggleButton toggleButton) {
        if (!timer.isRunning()) {
            timer.start();
        }
    }
}
