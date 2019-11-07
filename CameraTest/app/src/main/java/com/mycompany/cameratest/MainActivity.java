package com.mycompany.cameratest;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {
    private String TAG = "CameraMain";
    private Camera mCamera;
    private CameraPreview mPreview;
    private int currentCamera;
    private int FRONT_CAMERA = -1;
    private int BACK_CAMERA = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPreview = findViewById(R.id.cameraPreview);

        setFullscreen();
        // Check number of cameras and activate the switch camera button
        getCameras();
        if (FRONT_CAMERA != -1 && BACK_CAMERA != -1) {
            ImageButton switchCameraButton = findViewById(R.id.switchCamera);
            switchCameraButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    releaseCamera();
                    switchCamera();
                }
            });
            switchCameraButton.setVisibility(View.VISIBLE);
        }

        // Set back facing camera as current camera, unless it's not found
        currentCamera = BACK_CAMERA != -1 ? BACK_CAMERA : FRONT_CAMERA;

        try {
            mCamera = Camera.open(currentCamera);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mPreview.setCamera(mCamera);

    }

    private void getCameras() {
        Log.d(TAG, "Getting number of cameras");
        int numberOfCameras = Camera.getNumberOfCameras();
        Log.d(TAG, String.format("Found %d cameras", numberOfCameras));
        for (int i = 0; i < numberOfCameras; i++){
            // Check the facing of all cameras
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) FRONT_CAMERA = i;
            else if(info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) BACK_CAMERA = i;
            // If both cameras are set, end the function
            if (FRONT_CAMERA != -1 && BACK_CAMERA != -1) return;
        }
    }

    private void releaseCamera() {
        if (mCamera == null) return;
        Log.d(TAG, String.format("Releasing camera #%d", currentCamera));
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
    }

    private void restartCamera() {
        if (mCamera != null) return;
        Log.d(TAG, String.format("Restarting camera #%d", currentCamera));
        mCamera.open(currentCamera);
        mCamera.startPreview();
    }

    private void switchCamera() {
        // Change the current camera
        currentCamera = currentCamera == BACK_CAMERA ? FRONT_CAMERA : BACK_CAMERA;
        mCamera = Camera.open(currentCamera);
        mPreview.setVisibility(View.INVISIBLE);
        mPreview.setCamera(mCamera);
        mPreview.setVisibility(View.VISIBLE);
        //mPreview.refreshCamera();
    }

    private void setFullscreen() {
        // hide the app title bar and the Android status bar
        ActionBar supportBar;
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        supportBar = getSupportActionBar();
        if (supportBar != null) supportBar.hide();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
