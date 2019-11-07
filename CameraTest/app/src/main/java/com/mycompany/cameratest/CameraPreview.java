package com.mycompany.cameratest;

import android.content.Context;
import android.hardware.Camera;
import android.opengl.Visibility;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceView;
import android.view.SurfaceHolder;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.io.IOException;
import java.util.List;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private String TAG = "CameraPreview";
    private Camera mCamera;
    private SurfaceHolder mHolder;

    public CameraPreview(Context context) {
        super(context);
        init();
    }

    public CameraPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CameraPreview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mHolder = getHolder();
        mHolder.addCallback(this);
    }

    public void setCamera(Camera camera) {
        if (mHolder.getSurface() == null) return;
        try {
            if (mCamera != null) mCamera.stopPreview();
        } catch (Exception ex) {
            Log.d(TAG, "Couldn't stop camera preview" + ex.getMessage());
        }

        mCamera = camera;
        mCamera.setDisplayOrientation(90);
    }

    public void refreshCamera() {
        if (mCamera == null) return;
        Camera.Parameters params = mCamera.getParameters();
        // Get preview sizes to avoid getting a stretched preview
        List<Camera.Size> supportedSizes = params.getSupportedPreviewSizes();
        List<int[]> fpsRange = params.getSupportedPreviewFpsRange();
        LinearLayout parent = (LinearLayout) getParent();
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        int width = parent.getWidth();
        int height = parent.getHeight();
        Camera.Size mPreviewSize = getOptimalPreviewSize(supportedSizes, width, height);
        params.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
        // Preview size is flipped here for some reason, switch the height and width
        //noinspection SuspiciousNameCombination
        layoutParams.width = mPreviewSize.height;
        //noinspection SuspiciousNameCombination
        layoutParams.height = mPreviewSize.width;
        setLayoutParams(layoutParams);
        //setMeasuredDimension(mPreviewSize.width, mPreviewSize.height);

        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        } catch (IOException ex) {
            Log.d(TAG, "Couldn't set camera display" + ex.getMessage());
        }
    }

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio=(double)h / w;

        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - h) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - h);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - h) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - h);
                }
            }
        }
        return optimalSize;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "Surface created");
        refreshCamera();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(TAG, "Surface changed");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "Surface destroyed");
    }
}
