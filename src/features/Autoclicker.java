package features;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;
import javax.swing.Timer;

public class Autoclicker {
    private int clicksPerSecond;
    private Timer clickTimer;
    private Robot robot; // Robot to perform mouse clicks
    private String targetProgram;

    public Autoclicker() {
        this.clicksPerSecond = 0; // Default CPS
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    public void startClicking(int cps, String program) {
        this.clicksPerSecond = cps;
        this.targetProgram = program;
        if (clickTimer != null) {
            clickTimer.stop();
        }
        if (cps > 0) {
            int delay = 1000 / cps; // Calculate delay in milliseconds
            clickTimer = new Timer(delay, e -> {
                if (ApplicationFocusHelper.isApplicationFocused(targetProgram)) {
                    simulateClick();
                }
            });
            clickTimer.start();
        }
    }

    public void stopClicking() {
        if (clickTimer != null) {
            clickTimer.stop();
        }
    }

    private void simulateClick() {
        // This method triggers an actual mouse click
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }
}
