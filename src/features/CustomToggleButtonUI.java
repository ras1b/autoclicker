package features;

import javax.swing.*;
import javax.swing.plaf.basic.BasicToggleButtonUI;
import java.awt.*;

class CustomToggleButtonUI extends BasicToggleButtonUI {
    private Color offColor = new Color(0xCCCCCC);
    private Color onColor = new Color(0x4CAF50);
    private Color handleColor = Color.WHITE;

    @Override
    protected void paintButtonPressed(Graphics g, AbstractButton b) {
        if (b.isSelected()) {
            paintBackground(g, b, onColor);
        } else {
            paintBackground(g, b, offColor);
        }
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        AbstractButton b = (AbstractButton) c;
        if (b.isSelected()) {
            paintBackground(g, b, onColor);
        } else {
            paintBackground(g, b, offColor);
        }
        super.paint(g, c);
    }

    private void paintBackground(Graphics g, AbstractButton b, Color color) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(color);
        g2.fillRoundRect(0, 0, b.getWidth(), b.getHeight(), 30, 30);
        g2.setColor(handleColor);
        if (b.isSelected()) {
            g2.fillOval(b.getWidth() / 2, 1, b.getHeight() - 2, b.getHeight() - 2);
        } else {
            g2.fillOval(1, 1, b.getHeight() - 2, b.getHeight() - 2);
        }
    }
}
