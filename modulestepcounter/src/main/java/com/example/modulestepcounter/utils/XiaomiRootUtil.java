package com.example.modulestepcounter.utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Build;

import com.example.modulestepcounter.constant.XiaomiConstant;

import java.io.DataOutputStream;
import java.io.IOException;

public class XiaomiRootUtil {
    private static final String SYS_APP_DIR = Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1 ? "priv-app" : "app";

    private static Process getRootProcess() throws IOException {
        return Runtime.getRuntime().exec("su");
    }

    private static int getExecCommandResult(String... commands) {
        try {
            StringBuilder builder = new StringBuilder();
            for (String command : commands) {
                builder.append(command);
                builder.append("\n");
            }
            Process process = getRootProcess();
            DataOutputStream dataOutputStream = new DataOutputStream(process.getOutputStream());
            dataOutputStream.writeBytes(builder.toString());
            dataOutputStream.flush();
            dataOutputStream.close();
            return process.waitFor();
        } catch (IOException ignored) {
        } catch (InterruptedException ignored) {
        }
        return -1;
    }

    public static boolean convertSystemApp(Context context) {
        return getExecCommandResult(
                "mount -o rw,remount -t auto /system",
                String.format("mkdir /system/%s/", SYS_APP_DIR),
                String.format(
                        "cat %s > /system/%s/StepManage.apk",
                        context.getApplicationInfo().sourceDir,
                        SYS_APP_DIR
                ),
                String.format("cd /system/%s/", SYS_APP_DIR),
                "chmod 644 StepManage.apk",
                "reboot",
                "exit") == XiaomiConstant.INTEGER_NULL;
    }

    public static void copySqliteFileToSystem(Context context) {
        if (getExecCommandResult(
                "mount -o rw,remount -t auto /system",
                String.format(
                        "cat %s > /system/xbin/sqlite3",
                        String.format(
                                "%s/sqlite3",
                                context.getExternalCacheDir().getPath()
                        )
                ),
                "cd /system/xbin/",
                "chmod 4755 sqlite3",
                "exit") != XiaomiConstant.INTEGER_NULL) {
            throw new NullPointerException();
        }
    }

    public static void addStepsByRootMode(ContentValues values) {
        if (getExecCommandResult(
                "sqlite3 /data/data/com.xiaomi.joyose/databases/Steps.db",
                String.format(
                        "insert into StepsTable values(null,%d,%d,%d,%d);",
                        values.getAsLong(XiaomiConstant.BEGIN_TIME),
                        values.getAsLong(XiaomiConstant.END_TIME),
                        values.getAsInteger(XiaomiConstant.MODE),
                        values.getAsInteger(XiaomiConstant.STEPS)
                ),
                ".quit",
                "exit") != XiaomiConstant.INTEGER_NULL) {
            throw new SecurityException();
        }
    }

    public static boolean isSystemApp(Context context) {
        return (context.getApplicationInfo().flags & ApplicationInfo.FLAG_SYSTEM) != 0;
    }

    public static void uninstallAppByRoot() {
        if (getExecCommandResult(
                String.format("rm /system/%s/StepManage.apk", SYS_APP_DIR),
                "reboot",
                "exit") != XiaomiConstant.INTEGER_NULL) {
            throw new SecurityException();
        }
    }
}
