package com.tcblauweiss.getraenkeabrechner;

import android.content.Context;
import android.content.Intent;

public class UncaughtExceptionHandler implements
        java.lang.Thread.UncaughtExceptionHandler {
    private final Context myContext;
    private final Class<?> myActivityClass;

    public UncaughtExceptionHandler(Context context, Class<?> c) {
        myContext = context;
        myActivityClass = c;
    }

    public void uncaughtException(Thread thread, Throwable exception) {
        Intent intent = new Intent(myContext.getApplicationContext(), myActivityClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_NEW_TASK);

        myContext.getApplicationContext().startActivity(intent);
        // Exit the application
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }}
