package features;

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinNT.HANDLE;

public class ApplicationFocusHelper {

    /**
     * Checks if the specified application window is currently focused.
     * @param applicationName The name of the application window to check.
     * @return true if the specified application is focused, false otherwise.
     */
    public static boolean isApplicationFocused(String applicationName) {
        HWND hwnd = User32.INSTANCE.GetForegroundWindow();
        if (hwnd == null) {
            return false;
        }
        
        char[] windowText = new char[512];
        User32.INSTANCE.GetWindowText(hwnd, windowText, 512);
        String currentWindow = new String(windowText).trim();
        
        return currentWindow.contains(applicationName);
    }
}
