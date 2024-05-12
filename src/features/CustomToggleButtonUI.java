package features;

import javax.swing.plaf.basic.BasicToggleButtonUI;
import java.awt.*;
import javax.swing.*;

public class CustomToggleButtonUI extends BasicToggleButtonUI {
    private final Color offColor = new Color(0xFF6347); // Red color for "off" state
    private final Color onColor = new Color(0x4CAF50);  // Green color for "on" state
    private final Color handleColor = Color.WHITE;
    private int animationPosition = 0;  // To handle animation of the toggle

    @Override
    public void installUI(JComponent c) {
        super.installUI(c);
        ((JToggleButton) c).setBorderPainted(false);
        ((JToggleButton) c).setContentAreaFilled(false);
        ((JToggleButton) c).setFocusPainted(false);
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        AbstractButton b = (AbstractButton) c;
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Determine the position of the toggle based on its selection state
        if (b.isSelected()) {
            animationPosition = Math.min(animationPosition + 10, b.getWidth() - b.getHeight());
        } else {
            animationPosition = Math.max(animationPosition - 10, 0);
        }

        // Draw the background
        g2.setColor(b.isSelected() ? onColor : offColor);
        g2.fillRoundRect(0, 0, b.getWidth() - 1, b.getHeight() - 1, 30, 30);

        // Draw the toggle
        g2.setColor(handleColor);
        g2.fillOval(animationPosition, 1, b.getHeight() - 2, b.getHeight() - 2);

        g2.dispose();

        // Repaint periodically to animate
        SwingUtilities.invokeLater(() -> {
            if ((b.isSelected() && animationPosition < b.getWidth() - b.getHeight()) ||
                (!b.isSelected() && animationPosition > 0)) {
                b.repaint();
            }
        });
    }
}
