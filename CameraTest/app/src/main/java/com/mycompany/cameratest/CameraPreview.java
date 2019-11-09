package com.mycompany.cameratest;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback,
        Camera.PictureCallback {
    private String TAG = "CameraPreview";
    private Camera mCamera;
    private int focusAreaSize;
    private double targetRatio;
    private SurfaceHolder mHolder;
    private Camera.Parameters mParameters;
    private Matrix matrix;


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
        // Add an focusOnTouch to the surface view
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mCamera != null && event.getAction() == MotionEvent.ACTION_UP) {
                    Log.d(TAG, "Focus camera");
                    mCamera.cancelAutoFocus();
                    Rect focusRect = calculateTapArea(event.getX(), event.getY(), 1f);
                    if (mParameters.getFocusMode().equals(Camera.Parameters.FOCUS_MODE_AUTO))
                        mParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);

                    if (mParameters.getMaxNumFocusAreas() > 0) {
                        List<Camera.Area> myList = new ArrayList<>();
                        myList.add(new Camera.Area(focusRect, 1000));
                        mParameters.setFocusAreas(myList);
                    }

                    try {
                        mCamera.cancelAutoFocus();
                        mCamera.setParameters(mParameters);
                        mCamera.startPreview();
                        mCamera.autoFocus(new Camera.AutoFocusCallback() {
                            @Override
                            public void onAutoFocus(boolean success, Camera camera) {
                                if (!camera.getParameters().getFocusMode().equals(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                                    mParameters = camera.getParameters();
                                    mParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                                    if (mParameters.getMaxNumFocusAreas() > 0) {
                                        mParameters.setFocusAreas(null);
                                    }
                                    camera.setParameters(mParameters);
                                    camera.startPreview();
                                }
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return true;
            }
        });
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
        /* Called whenever the camera is started/changed/restarted
         *
         * Sets a preview size for the surface view, and the picture resolution
         */

        if (mCamera == null) return;
        mParameters = mCamera.getParameters();

        LinearLayout parent = (LinearLayout) getParent();
        int width = parent.getWidth();
        int height = parent.getHeight();

        // Prevent the view from being stretched sets target ratio for the picture
        setPreviewDisplaySize(width, height);
        // Set picture resolution size
        setPictureSize();
        // Enable auto focus until camera is focused

        mCamera.setParameters(mParameters);

        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        } catch (IOException ex) {
            Log.d(TAG, "Couldn't set camera display" + ex.getMessage());
        }
    }

    private void setPreviewDisplaySize(int width, int height) {
        // Get preview sizes to avoid getting a stretched preview
        List<Camera.Size> supportedPreviewSizes = mParameters.getSupportedPreviewSizes();
        Camera.Size mPreviewSize = getOptimalPreviewSize(supportedPreviewSizes, width, height);
        mParameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);

        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        // Preview size is flipped here for some reason, switch the height and width
        //noinspection SuspiciousNameCombination
        layoutParams.width = mPreviewSize.height;
        //noinspection SuspiciousNameCombination
        layoutParams.height = mPreviewSize.width;
        Log.d(TAG, String.format("Set preview size to w:%d, h:%d", mPreviewSize.width,
                mPreviewSize.height));
        setLayoutParams(layoutParams);
    }

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        if (sizes == null) return null;

        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) h / w;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        for (Camera.Size size : sizes) {
            // Camera size and height are flipped so it fits on the display
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
        if (optimalSize != null) this.targetRatio = (double) optimalSize.width / optimalSize.height;
        return optimalSize;
    }

    private void setPictureSize() {
        List<Camera.Size> supportedPictureSizes = mParameters.getSupportedPictureSizes();
        Camera.Size mPictureSize = getOptimalPictureSize(supportedPictureSizes);
        Log.d(TAG, String.format("Set picture size to w:%d, h:%d", mPictureSize.width,
                mPictureSize.height));
        mParameters.setPictureSize(mPictureSize.width, mPictureSize.height);
    }

    private Camera.Size getOptimalPictureSize(List<Camera.Size> sizes) {
        if (sizes == null) return null;

        Log.d(TAG, "targetRatio is: " + targetRatio);
        double minRatioDiff = Double.MAX_VALUE;
        Camera.Size optimalSize = null;

        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            Log.d(TAG, String.format("Picture w: %d, h: %d, r: ", size.width, size.height) + ratio);
            // If the ratio fits perfectly return it
            if (ratio == targetRatio) {
                optimalSize = size;
                break;
            } else if (Math.abs(ratio - targetRatio) < minRatioDiff) optimalSize = size;
            // Else if the ratio fits the screen better then set it as the new optimalSize
        }

        // Return the optimal size if no perfect ratio was found
        return optimalSize;
    }

    private Rect calculateTapArea(float x, float y, float coefficient) {
        int areaSize = Float.valueOf(focusAreaSize * coefficient).intValue();

        int left = clamp((int) x - areaSize / 2, 0, getWidth() - areaSize);
        int top = clamp((int) y - areaSize / 2, 0, getHeight() - areaSize);

        RectF rectF = new RectF(left, top, left + areaSize, top + areaSize);
        matrix.mapRect(rectF);

        return new Rect(Math.round(rectF.left), Math.round(rectF.top), Math.round(rectF.right), Math.round(rectF.bottom));
    }

    private int clamp(int x, int min, int max) {
        if (x > max) {
            return max;
        }
        if (x < min) {
            return min;
        }
        return x;
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "Surface created");
        refreshCamera();
        focusAreaSize = getResources().getDimensionPixelSize(R.dimen.camera_focus_area_size);
        matrix = new Matrix();
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
