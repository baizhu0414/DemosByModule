package com.example.smallfeatureuse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.smallfeatureuse.ui.CameraPreview;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Logger;

import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

/**
 * 注：Android >=11 等级此程序不适配，因为存储权限需要更麻烦一点的申请方式。
 */
public class CameraActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "CameraActivity";
    // 组件
    Button btnTakePhoto;
    // 权限
    private final int PERMISSION_REQ = 111;
    String[] pers = new String[]{Manifest.permission.CAMERA
            , Manifest.permission.WRITE_EXTERNAL_STORAGE};
    // 成员变量
    Camera mCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        btnTakePhoto = findViewById(R.id.btn_take_photo);
        btnTakePhoto.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestPermissions(pers);
        startCameraPreview();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_take_photo) {
            if (checkPermission(pers[1])) {
                mCamera.takePicture(null, null, mPictureCallback);
            }
        }
    }

    void requestPermissions(String[] pers) {
        if (!checkPermission(pers[0])) {
            ActivityCompat.requestPermissions(this, pers, PERMISSION_REQ);
        }
    }

    boolean checkPermission(String per) {
        return PackageManager.PERMISSION_GRANTED
                == ContextCompat.checkSelfPermission(this, per);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQ
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startCameraPreview();
        }
    }

    void startCameraPreview() {
        mCamera = Camera.open();    //初始化 Camera对象
        CameraPreview mPreview = new CameraPreview(this, mCamera);
        LinearLayout camera_preview = findViewById(R.id.parent_layout);
        camera_preview.addView(mPreview);

        //得到照相机的参数
        Camera.Parameters parameters = mCamera.getParameters();
        //图片的格式
        parameters.setPictureFormat(ImageFormat.JPEG);
//        //预览的大小是多少
//        parameters.setPreviewSize(camera_preview.getMeasuredWidth(), camera_preview.getMeasuredHeight());
        //设置对焦模式，自动对焦
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        mCamera.setParameters(parameters);
    }

    //获取照片中的接口回调
    Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            //这里就是拍照成功后返回照片的地方，注意，返回的是原图，可能几兆十几兆大，需要压缩处理
            FileOutputStream fos = null;
            String path = Environment.getExternalStorageDirectory().getPath();
            String mFilePath = path + File.separator + "tt001.png";
            Log.e(TAG, "Picture Taken: path---\n"+mFilePath);
            //文件
            Long currentTimeMillis = System.currentTimeMillis();

            try {
                File jpgFile = new File(mFilePath);
                FileOutputStream outputStream = new FileOutputStream(jpgFile); // 文件输出流
                outputStream.write(data); // 写入sd卡中
                outputStream.close(); // 关闭输出流

//                fos.write(data);
                long l1 = System.currentTimeMillis() - currentTimeMillis;
                Long d = System.currentTimeMillis();
                Log.e(TAG, "图片存储时间--"+ l1);
                Luban.with(CameraActivity.this)
                        .load(jpgFile)                                   // 传人要压缩的图片列表
                        .ignoreBy(200)                                  // 忽略不压缩图片的大小
                        .setTargetDir(path)                        // 设置压缩后文件存储位置
                        .setCompressListener(new OnCompressListener() { //设置回调
                            @Override
                            public void onStart() {
                            }

                            @Override
                            public void onSuccess(File file) {
                                long l2 = System.currentTimeMillis() - d;
                                Log.e(TAG, "图片压缩时间--"+ l2);
//                                EventBus.getDefault().post(new EventMessage<String>(EventBusTag.PIC_SUCCESS,file.getPath()));
//                                finish();
                            }

                            @Override
                            public void onError(Throwable e) {
                            }
                        }).launch();    //启动压缩

            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, e.toString());
            } finally {
                //实现连续拍多张的效果
                mCamera.startPreview();
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }
}