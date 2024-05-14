package features;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;
import javax.swing.Timer;

public class Autoclicker {
    private Timer clickTimer;
    private Robot robot; // Robot to perform mouse clicks
    private String targetProgram;
    private boolean isAutomatedClick = false;

    public Autoclicker() {
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    public void startClicking(int cps, String program) {
        this.targetProgram = program;
        if (clickTimer != null) {
            clickTimer.stop();
        }
        if (cps > 0) {
            int delay = 1000 / cps; // Calculate delay in milliseconds
            clickTimer = new Timer(delay, e -> {
                long startTime = System.nanoTime();
                if (targetProgram == null || ApplicationFocusHelper.isMouseOverAndFocusedWindow(targetProgram)) {
                    isAutomatedClick = true;
                    simulateClick();
                    isAutomatedClick = false;
                }
                long elapsedTime = System.nanoTime() - startTime;
                int adjustment = (int) (elapsedTime / 1000000); // Convert to milliseconds
                clickTimer.setInitialDelay(delay - adjustment); // Adjust delay for next click
                clickTimer.setDelay(delay - adjustment);
            });
            clickTimer.setRepeats(true);
            clickTimer.start();
        }
    }

    public void stopClicking() {
        if (clickTimer != null) {
            clickTimer.stop();
        }
    }

    public boolean isAutomatedClick() {
        return isAutomatedClick;
    }

    private void simulateClick() {
        // This method triggers an actual mouse click
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }
}
