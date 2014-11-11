package com.badlogic.invaders.android;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.invaders.Invaders;
import com.jianglin.util.Capturing;

public class AndroidLauncher extends AndroidApplication {

    private Capturing mScreenCapture;
    private static final int width = 1280;
    private static final int height = 720;
    private static final int frameRate = 30;
    private static final int bitRate = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mScreenCapture = new Capturing(getApplicationContext());
        mScreenCapture.initCapturing(width, height, frameRate, bitRate);
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        initialize(new Invaders(mScreenCapture), config);
    }
}
