package features;

import javax.swing.Timer;

public class Autoclicker {
    private int clicksPerSecond;
    private Timer clickTimer;

    public Autoclicker() {
        this.clicksPerSecond = 0;  // Default CPS
    }

    public void startClicking(int cps) {
        this.clicksPerSecond = cps;
        if (clickTimer != null) {
            clickTimer.stop();
        }
        clickTimer = new Timer(1000 / clicksPerSecond, e -> simulateClick());
        clickTimer.start();
    }

    public void stopClicking() {
        if (clickTimer != null) {
            clickTimer.stop();
        }
    }

    private void simulateClick() {
    	// This method would trigger a click. Here we're just simulating the action.
//    	System.out.println("Click performed"); // For debugging
    	}
    }
