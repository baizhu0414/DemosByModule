package com.example.modulestepcounter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.library.DateUtil;
import com.example.modulestepcounter.constant.XiaomiConstant;
import com.example.modulestepcounter.utils.XiaomiRootUtil;
import com.example.modulestepcounter.utils.XiaomiSpUtil;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * 参考：https://github.com/LinYuLuo/XMStepManage/
 */
public class XiaomiCounterActivity extends AppCompatActivity {

    private final String[] QUERY_FILED = {XiaomiConstant.ID,
            XiaomiConstant.BEGIN_TIME, XiaomiConstant.END_TIME,
            XiaomiConstant.MODE, XiaomiConstant.STEPS};
    private TextView tvTodaySteps;
    private EditText edtAddSteps;
    private AlertDialog.Builder dialogAppTip;

    private int todayStepCount;
    private long clickTime = 0L;
    private XiaomiSpUtil shared;
    private int currentWorkMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xiaomi_counter);

        initView();
        initParam();
        initListener();
    }

    private void initView() {
        tvTodaySteps = findViewById(R.id.tvTodaySteps);
        edtAddSteps = findViewById(R.id.edtAddSteps);
        dialogAppTip = new AlertDialog.Builder(this)
                .setNegativeButton(R.string.btn_ok, null);
    }

    private void initParam() {
        shared = new XiaomiSpUtil(this);
        currentWorkMode = shared.getInt(XiaomiSpUtil.KEY_WORK_MODE);
        getTodayStep();
    }

    private void initListener() {
    }

    /**
     * 此处可用，获取Xiaomi系统健康步数
     */
    private void getTodayStep() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Cursor cursor = getContentResolver().query(XiaomiConstant.STEP_URI, QUERY_FILED,
                            null, null, null);
                    long todayBeginTime = DateUtil.parseXiaomiTodayTime(true);
                    long todayEndTime = DateUtil.parseXiaomiTodayTime(false);
                    if (cursor != null) {
                        todayStepCount = 0;
                        while (cursor.moveToNext()) {
                            if (cursor.getLong(1) > todayBeginTime
                                    && cursor.getLong(2) < todayEndTime
                                    && cursor.getInt(3) == 2) {
                                todayStepCount += cursor.getInt(4);
                            }
                        }
                        cursor.close();
                    }
                    XiaomiCounterActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvTodaySteps.setText(String.format("今日步数：%d", todayStepCount));
                        }
                    });
                } catch (Exception ignored) {
                }
            }
        }).start();
    }

//    private ContentValues getAddStepValues() throws NumberFormatException {
//        ContentValues values = new ContentValues();
//        values.put(XiaomiConstant.BEGIN_TIME, (System.currentTimeMillis() - 600000L));
//        values.put(XiaomiConstant.END_TIME, System.currentTimeMillis());
//        values.put(XiaomiConstant.MODE, 2);
//        values.put(XiaomiConstant.STEPS, Integer.parseInt(edtAddSteps.getText().toString()));
//        return values;
//    }

    public void startStepAdd(View view) {
//        try {
//            switch (currentWorkMode) {
//                case XiaomiConstant.WORK_MODE_CORE:
//                case XiaomiConstant.WORK_MODE_SYSTEM: {
//                    if (currentWorkMode == XiaomiConstant.WORK_MODE_SYSTEM) {
//                        if (!XiaomiRootUtil.isSystemApp(this)) {
//                            if (!shared.getBoolean(XiaomiSpUtil.KEY_TRY_CONVERT_SYSTEM_APP)) {
//                                shared.getEdit().putBoolean(XiaomiSpUtil.KEY_TRY_CONVERT_SYSTEM_APP, true).editApply();
//                                if (!XiaomiRootUtil.convertSystemApp(this)) {
//                                    throw new NullPointerException();
//                                }
//                            }
//                            Toast.makeText(this, R.string.convert_system_app_fail, Toast.LENGTH_LONG).show();
//                            return;
//                        }
//                    }
//                    getContentResolver().insert(XiaomiConstant.STEP_URI, getAddStepValues());
//                    break;
//                }
//                case XiaomiConstant.WORK_MODE_ROOT: {
//                    if (!shared.getBoolean(XiaomiSpUtil.KEY_UNZIP_SQLITE_FILE)) {
//                        InputStream stream = getAssets().open(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? "sqlite3_21" : "sqlite3");
//                        byte[] fileBytes = new byte[stream.available()];
//                        stream.read(fileBytes);
//                        stream.close();
//                        OutputStream outputStream = new FileOutputStream(String.format("%s/sqlite3", getExternalCacheDir()));
//                        outputStream.write(fileBytes);
//                        outputStream.flush();
//                        outputStream.close();
//                        XiaomiRootUtil.copySqliteFileToSystem(this);
//                        shared.getEdit().putBoolean(XiaomiSpUtil.KEY_UNZIP_SQLITE_FILE, true).editApply();
//                    }
//                    XiaomiRootUtil.addStepsByRootMode(getAddStepValues());
//                    break;
//                }
//                default: {
//                    return;
//                }
//            }
//            Toast.makeText(this, R.string.toast_add_steps_success, Toast.LENGTH_SHORT).show();
//            getTodayStep();
//            return;
//        } catch (SecurityException ignored) {
//            dialogAppTip.setMessage(
//                    currentWorkMode == XiaomiConstant.WORK_MODE_CORE ?
//                            R.string.add_step_error_core :
//                            currentWorkMode == XiaomiConstant.WORK_MODE_ROOT ?
//                                    R.string.add_step_error_root :
//                                    R.string.add_step_error_system
//            );
//        } catch (NumberFormatException e) {
//            dialogAppTip.setMessage(R.string.dialog_message_int_parser_error);
//        } catch (IOException ignored) {
//            dialogAppTip.setMessage(R.string.unzip_sqlite_file_fail);
//        } catch (NullPointerException ignored) {
//            if (currentWorkMode == XiaomiConstant.WORK_MODE_ROOT) {
//                dialogAppTip.setMessage(R.string.copy_sqlite_file_fail);
//            } else if (currentWorkMode == XiaomiConstant.WORK_MODE_SYSTEM) {
//                dialogAppTip.setMessage(R.string.convert_system_app_fail);
//            }
//        }
//        dialogAppTip.show();
    }

    /**
     * 返回两次才退出程序
     * @see #onKeyUp
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - clickTime) > 2000L) {
                clickTime = System.currentTimeMillis();
                Toast.makeText(this, R.string.toast_exit_app, Toast.LENGTH_SHORT).show();
                return true;
            }
            android.os.Process.killProcess(android.os.Process.myPid());
        }
        return super.onKeyDown(keyCode, event);
    }
}
