package com.jianglin.util;

import java.io.IOException;

import android.content.Context;
import android.util.Log;

import com.intel.inde.mp.AudioFormat;
import com.intel.inde.mp.GLCapture;
import com.intel.inde.mp.IProgressListener;
import com.intel.inde.mp.VideoFormat;
import com.intel.inde.mp.android.AndroidMediaObjectFactory;
import com.intel.inde.mp.android.AudioFormatAndroid;
import com.intel.inde.mp.android.VideoFormatAndroid;

public class VideoCapture
{
    private static final String TAG = "capturing";

    private static final String Codec = "video/avc";
    private static int IFrameInterval = 1;

    private static final Object syncObject = new Object();
    private static volatile VideoCapture videoCapture;

    private static VideoFormat videoFormat;
    private static int videoWidth;
    private static int videoHeight;
    private GLCapture capturer;

    private boolean isConfigured;
    private boolean isStarted;
    private long framesCaptured;
    private Context context;
    private IProgressListener progressListener;

    public VideoCapture(Context context, IProgressListener progressListener) {
        this.context = context;
        this.progressListener = progressListener;
    }

    public static void init(int width, int height, int frameRate, int bitRate) {
        videoWidth = width;
        videoHeight = height;

        videoFormat = new VideoFormatAndroid(Codec, videoWidth, videoHeight);
        videoFormat.setVideoFrameRate(frameRate);
        videoFormat.setVideoBitRateInKBytes(bitRate);
        videoFormat.setVideoIFrameInterval(IFrameInterval);
    }

    public void start(String videoPath) throws IOException {
        if (isStarted())
            throw new IllegalStateException(TAG + " already started!");

        capturer = new GLCapture(new AndroidMediaObjectFactory(context),
                progressListener);
        capturer.setTargetFile(videoPath);
        capturer.setTargetVideoFormat(videoFormat);

        AudioFormat audioFormat = new AudioFormatAndroid("audio/mp4a-latm",
                44100,
                2);
        capturer.setTargetAudioFormat(audioFormat);

        capturer.start();

        isStarted = true;
        isConfigured = false;
        framesCaptured = 0;
    }

    public void stop() {
        if (!isStarted())
            throw new IllegalStateException(TAG
                    + " not started or already stopped!");

        try {
            capturer.stop();
            isStarted = false;
        } catch (Exception ex) {
        }

        capturer = null;
        isConfigured = false;
    }

    private void configure() {
        if (isConfigured())
            return;

        try {
            capturer.setSurfaceSize(videoWidth, videoHeight);
            isConfigured = true;
            Log.d(TAG, "configure capturing:");
            Log.d(TAG, "  video width=" + videoWidth);
            Log.d(TAG, "  video height=" + videoHeight);
        } catch (Exception ex) {
        }
    }

    public void beginCaptureFrame() {
        if (!isStarted())
            return;

        configure();
        if (!isConfigured())
            return;

        capturer.beginCaptureFrame();
    }

    public void endCaptureFrame() {
        if (!isStarted() || !isConfigured())
            return;

        capturer.endCaptureFrame();
        framesCaptured++;
    }

    public boolean isStarted() {
        return isStarted;
    }

    public boolean isConfigured() {
        return isConfigured;
    }

}
