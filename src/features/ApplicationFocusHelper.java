package features;

import java.awt.MouseInfo;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.Psapi;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.RECT;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.ptr.IntByReference;

public class ApplicationFocusHelper {

    public interface WndEnumProc extends User32.WNDENUMPROC {
        boolean callback(HWND hWnd, Pointer arg);
    }

    /**
     * Retrieves the titles of all top-level windows.
     * @return A list of window titles.
     */
    public static List<String> getTopLevelWindowTitles() {
        final List<String> windowTitles = new ArrayList<>();

        User32.INSTANCE.EnumWindows((hWnd, data) -> {
            if (User32.INSTANCE.IsWindowVisible(hWnd)) {
                char[] windowText = new char[512];
                User32.INSTANCE.GetWindowText(hWnd, windowText, 512);
                String wText = Native.toString(windowText).trim();
                if (!wText.isEmpty()) {
                    windowTitles.add(wText);
                }
            }
            return true;
        }, null);

        return windowTitles;
    }

    /**
     * Checks if the specified application window is currently focused.
     * @param processName The name of the application process to check.
     * @return true if the specified application is focused, false otherwise.
     */
    public static boolean isApplicationFocused(String processName) {
        HWND hwnd = User32.INSTANCE.GetForegroundWindow();
        if (hwnd == null) {
            return false;
        }

        IntByReference pid = new IntByReference();
        User32.INSTANCE.GetWindowThreadProcessId(hwnd, pid);

        HANDLE process = Kernel32.INSTANCE.OpenProcess(Kernel32.PROCESS_QUERY_INFORMATION | Kernel32.PROCESS_VM_READ, false, pid.getValue());
        if (process == null) {
            return false;
        }

        char[] buffer = new char[512];
        Psapi.INSTANCE.GetModuleFileNameExW(process, null, buffer, buffer.length);
        Kernel32.INSTANCE.CloseHandle(process);

        String currentProcess = Native.toString(buffer).trim();

        return currentProcess.equalsIgnoreCase(processName);
    }

    /**
     * Checks if the mouse is over the specified window and the window is focused.
     * @param windowTitle The title of the window to check.
     * @return true if the mouse is over the specified window and it is focused, false otherwise.
     */
    public static boolean isMouseOverAndFocusedWindow(String windowTitle) {
        HWND hwnd = User32.INSTANCE.FindWindow(null, windowTitle);
        if (hwnd == null) {
            return false;
        }

        RECT rect = new RECT();
        User32.INSTANCE.GetWindowRect(hwnd, rect);
        Point mousePoint = MouseInfo.getPointerInfo().getLocation();

        HWND foregroundWindow = User32.INSTANCE.GetForegroundWindow();
        if (foregroundWindow == null || !foregroundWindow.equals(hwnd)) {
            return false;
        }

        return rect.left <= mousePoint.x && mousePoint.x <= rect.right && rect.top <= mousePoint.y && mousePoint.y <= rect.bottom;
    }
}
