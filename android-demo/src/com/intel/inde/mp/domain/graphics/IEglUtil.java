package com.intel.inde.mp.domain.graphics;

import java.nio.FloatBuffer;

import com.intel.inde.mp.domain.Resolution;

public abstract interface IEglUtil
{
    public static final String VERTEX_SHADER = "uniform mat4 uMVPMatrix;\nuniform mat4 uSTMatrix;\nattribute vec4 aPosition;\nattribute vec4 aTextureCoord;\nvarying vec2 vTextureCoord;\nvoid main() {\n  gl_Position = uMVPMatrix * aPosition;\n  vTextureCoord = (uSTMatrix * aTextureCoord).xy;\n}\n";
    public static final String FRAGMENT_SHADER_OES = "#extension GL_OES_EGL_image_external : require\nprecision mediump float;\nvarying vec2 vTextureCoord;\nuniform samplerExternalOES sTexture;\nvoid main() {\n  gl_FragColor = texture2D(sTexture, vTextureCoord);\n}\n";

    public abstract Resolution getCurrentSurfaceResolution();

    public abstract Program createProgram(
            String paramString1, String paramString2);

    public abstract int createTexture(int paramInt);

    public abstract void drawFrameStart(
            Program paramProgram, FloatBuffer paramFloatBuffer,
            float[] paramArrayOfFloat1, float[] paramArrayOfFloat2,
            float paramFloat, TextureType paramTextureType, int paramInt,
            Resolution paramResolution, boolean paramBoolean);

    public abstract void drawFrameFinish();

    public abstract void drawFrame(
            Program paramProgram, FloatBuffer paramFloatBuffer,
            float[] paramArrayOfFloat1, float[] paramArrayOfFloat2,
            float paramFloat, TextureType paramTextureType, int paramInt,
            Resolution paramResolution, boolean paramBoolean);

    public abstract void checkEglError(String paramString);

    public abstract void setIdentityMatrix(
            float[] paramArrayOfFloat, int paramInt);
}

/*
 * Location: E:\SouceCode\recordGame\gdxDemo\libs\domain-1.2.2415.jar
 * Qualified Name: com.intel.inde.mp.domain.graphics.IEglUtil
 * JD-Core Version: 0.6.1
 */
