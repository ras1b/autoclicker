package features;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;
import javax.swing.Timer;

public class Autoclicker {
    private Timer clickTimer;
    private Robot robot; // Robot to perform mouse clicks
    private String targetProgram;

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
                if (targetProgram == null || ApplicationFocusHelper.isMouseOverWindow(targetProgram)) {
//                    System.out.println("Autoclicking for program: " + targetProgram); // Debug output
                    simulateClick();
                } else {
//                    System.out.println("Mouse not over the specified program: " + targetProgram); // Debug output
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
