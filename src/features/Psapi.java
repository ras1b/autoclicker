package features;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinNT.HANDLE;

public interface Psapi extends Library {
    Psapi INSTANCE = Native.load("psapi", Psapi.class);

    int GetModuleFileNameExW(HANDLE hProcess, HANDLE hModule, char[] lpFilename, int nSize);
}
