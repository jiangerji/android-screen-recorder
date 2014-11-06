package com.jianglin.util;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.intel.inde.mp.IProgressListener;
import com.intel.inde.mp.android.graphics.FullFrameTexture;

public class Capturing {
    private final static String TAG = "capturing";

    private static FullFrameTexture texture;
    private VideoCapture videoCapture;

    private IProgressListener progressListener = new IProgressListener() {
        @Override
        public void onMediaStart() {
        }

        @Override
        public void onMediaProgress(float progress) {
        }

        @Override
        public void onMediaDone() {
        }

        @Override
        public void onMediaPause() {
        }

        @Override
        public void onMediaStop() {
        }

        @Override
        public void onError(Exception exception) {
        }
    };

    /**
     * 初始化录屏对象
     * 
     * @param context
     */
    public Capturing(Context context) {
        videoCapture = new VideoCapture(context, progressListener);

        //        texture = new FullFrameTexture();
    }

    public static String getDirectoryDCIM() {
        return Environment.getExternalStorageDirectory() + File.separator;
    }

    /**
     * 初始化录屏的参数
     * 
     * @param width
     *            录制屏幕的宽度
     * @param height
     *            录制屏幕的高度
     * @param frameRate
     *            录制的帧率
     * @param bitRate
     *            录制视频的码率
     */
    public void
            initCapturing(int width, int height, int frameRate, int bitRate) {
        Log.d(TAG, "init capturing:");
        Log.d(TAG, "  width=" + width);
        Log.d(TAG, "  height=" + height);
        Log.d(TAG, "  frameRate=" + frameRate);
        Log.d(TAG, "  bitRate=" + bitRate);
        VideoCapture.init(width, height, frameRate, bitRate);
    }

    /**
     * 开始录制
     * 
     * @param videoPath
     *            录制视频存放的绝对路径
     */
    public void startCapturing(String videoPath) {
        if (videoCapture == null) {
            return;
        }
        synchronized (videoCapture) {
            try {
                videoCapture.start(videoPath);
                Log.d(TAG, "start capturing, save to " + videoPath);
            } catch (IOException e) {
            }
        }
    }

    public void captureFrame(int textureID) {
        if (videoCapture == null) {
            return;
        }
        synchronized (videoCapture) {
            videoCapture.beginCaptureFrame();
            texture.draw(textureID);
            videoCapture.endCaptureFrame();
        }
    }

    /**
     * 开始抓取当前的桢
     */
    public void beginCaptureFrame() {
        if (videoCapture == null) {
            return;
        }

        videoCapture.beginCaptureFrame();
    }

    /**
     * 结束抓取当前桢
     */
    public void endCaptureFrame() {
        if (videoCapture == null) {
            return;
        }

        videoCapture.endCaptureFrame();
    }

    /**
     * 停止录制视频
     */
    public void stopCapturing() {
        if (videoCapture == null) {
            return;
        }
        synchronized (videoCapture) {
            if (videoCapture.isStarted()) {
                videoCapture.stop();
                Log.d(TAG, "stop capturing.");
            }
        }
    }

}
