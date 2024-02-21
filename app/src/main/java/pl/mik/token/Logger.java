package pl.mik.token;

import android.util.Log;

public class Logger {
    private String className;

    public Logger(String className) {
        this.className = className + "-----> ";
        i("Logger class created");
    }

    public void i(String msg) {
        Log.i(className, msg);
    }
}
