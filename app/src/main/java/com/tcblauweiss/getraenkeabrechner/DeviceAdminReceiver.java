package com.tcblauweiss.getraenkeabrechner;

import android.content.ComponentName;
import android.content.Context;

public class DeviceAdminReceiver extends android.app.admin.DeviceAdminReceiver {
    private static final String TAG = "DeviceAdminReceiver";

    public static ComponentName getComponentName(Context context) {

        return new ComponentName(context.getApplicationContext(), DeviceAdminReceiver.class);

    }}
