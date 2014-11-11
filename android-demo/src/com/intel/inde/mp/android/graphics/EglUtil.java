package com.intel.inde.mp.android.graphics;

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.intel.inde.mp.domain.Resolution;
import com.intel.inde.mp.domain.graphics.IEglUtil;
import com.intel.inde.mp.domain.graphics.Program;
import com.intel.inde.mp.domain.graphics.TextureType;
import com.intel.inde.mp.domain.pipeline.TriangleVerticesCalculator;

public class EglUtil
        implements IEglUtil
{
    private static final int FLOAT_SIZE_BYTES = 4;
    private static final int TRIANGLE_VERTICES_DATA_STRIDE_BYTES = 20;
    private static final int TRIANGLE_VERTICES_DATA_POS_OFFSET = 0;
    private static final int TRIANGLE_VERTICES_DATA_UV_OFFSET = 3;
    private static final TriangleVerticesCalculator scaleCalculator = new TriangleVerticesCalculator();

    public static EglUtil getInstance()
    {
        return EglUtilSingletonHolder.INSTANCE;
    }

    public Resolution getCurrentSurfaceResolution()
    {
        EGL10 egl = (EGL10) EGLContext.getEGL();
        EGLDisplay display = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
        EGLSurface readSurface = egl.eglGetCurrentSurface(12378);

        int[] width = { 0 };
        egl.eglQuerySurface(display, readSurface, 12375, width);

        int[] height = { 0 };
        egl.eglQuerySurface(display, readSurface, 12374, height);

        return new Resolution(width[0], height[0]);
    }

    public Program createProgram(String vertexShader, String fragmentShader)
    {
        ShaderProgram shaderProgram = createShaderProgram(vertexShader,
                fragmentShader);

        if (shaderProgram.getProgramHandle() == 0) {
            throw new RuntimeException("Failed to create shader program.");
        }

        Program program = new Program();
        program.programHandle = shaderProgram.getProgramHandle();
        program.positionHandle = shaderProgram.getAttributeLocation("aPosition");
        program.textureHandle = shaderProgram.getAttributeLocation("aTextureCoord");
        program.mvpMatrixHandle = shaderProgram.getAttributeLocation("uMVPMatrix");
        program.stMatrixHandle = shaderProgram.getAttributeLocation("uSTMatrix");
        return program;
    }

    public int createTexture(int textureType)
    {
        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);

        int textureId = textures[0];
        GLES20.glBindTexture(textureType, textureId);
        checkEglError("glBindTexture mTextureID");

        GLES20.glTexParameterf(textureType, 10241, 9728.0F);
        GLES20.glTexParameterf(textureType, 10240, 9729.0F);
        GLES20.glTexParameteri(textureType, 10242, 33071);
        GLES20.glTexParameteri(textureType, 10243, 33071);
        checkEglError("glTexParameter");
        return textureId;
    }

    public void drawFrameStart(
            Program program, FloatBuffer triangleVertices, float[] mvpMatrix,
            float[] stMatrix, float angle, TextureType textureType,
            int textureId, Resolution inputResolution, boolean fitToSurface)
    {
        checkEglError("onDrawFrame start");
        Resolution out;
        if (fitToSurface)
            out = getCurrentSurfaceResolution();
        else {
            out = inputResolution;
        }
        GLES20.glViewport(0, 0, out.width(), out.height());

        GLES20.glClearColor(0.0F, 0.0F, 0.0F, 1.0F);
        GLES20.glClear(16640);

        GLES20.glUseProgram(program.programHandle);
        checkEglError("glUseProgram");

        GLES20.glActiveTexture(33984);
        checkEglError("glActiveTexture");
        int textureTypeId;
        if (textureType == TextureType.GL_TEXTURE_2D)
            textureTypeId = 3553;
        else {
            textureTypeId = 36197;
        }

        GLES20.glBindTexture(textureTypeId, textureId);
        checkEglError("glBindTexture");

        triangleVertices.position(0);
        GLES20.glVertexAttribPointer(program.positionHandle,
                3,
                5126,
                false,
                20,
                triangleVertices);
        checkEglError("glVertexAttribPointer maPosition");
        GLES20.glEnableVertexAttribArray(program.positionHandle);
        checkEglError("glEnableVertexAttribArray maPositionHandle");

        triangleVertices.position(3);
        GLES20.glVertexAttribPointer(program.textureHandle,
                3,
                5126,
                false,
                20,
                triangleVertices);
        checkEglError("glVertexAttribPointer maTextureHandle");
        GLES20.glEnableVertexAttribArray(program.textureHandle);
        checkEglError("glEnableVertexAttribArray maTextureHandle");

        Matrix.setIdentityM(mvpMatrix, 0);

        if (fitToSurface) {
            float[] scale = scaleCalculator.getScale((int) angle,
                    inputResolution.width(),
                    inputResolution.height(),
                    out.width(),
                    out.height());
            Matrix.scaleM(mvpMatrix, 0, scale[0], scale[1], 1.0F);
        }
        Matrix.rotateM(mvpMatrix, 0, -angle, 0.0F, 0.0F, 1.0F);

        GLES20.glUniformMatrix4fv(program.mvpMatrixHandle,
                1,
                false,
                mvpMatrix,
                0);
        GLES20.glUniformMatrix4fv(program.stMatrixHandle, 1, false, stMatrix, 0);
    }

    public void drawFrameFinish()
    {
        GLES20.glDrawArrays(5, 0, 4);
        checkEglError("glDrawArrays");
        GLES20.glFinish();
    }

    public void drawFrame(
            Program program, FloatBuffer triangleVertices, float[] mvpMatrix,
            float[] stMatrix, float angle, TextureType textureType,
            int textureId, Resolution resolution, boolean fitToSurface)
    {
        drawFrameStart(program,
                triangleVertices,
                mvpMatrix,
                stMatrix,
                angle,
                textureType,
                textureId,
                resolution,
                fitToSurface);
        drawFrameFinish();
    }

    private ShaderProgram createShaderProgram(
            String vertexSource, String fragmentSource) {
        ShaderProgram shaderProgram = new ShaderProgram(this);
        shaderProgram.create(vertexSource, fragmentSource);

        int[] linkStatus = new int[1];
        GLES20.glGetProgramiv(shaderProgram.getProgramHandle(),
                35714,
                linkStatus,
                0);
        if (linkStatus[0] != 1) {
            GLES20.glDeleteProgram(shaderProgram.getProgramHandle());
        }
        return shaderProgram;
    }

    public void checkEglError(String operation)
    {
        int error;
        if ((error = GLES20.glGetError()) != 0)
            throw new RuntimeException(operation + ": glError " + error);
    }

    public void setIdentityMatrix(float[] stMatrix, int smOffset)
    {
        Matrix.setIdentityM(stMatrix, 0);
    }

    private static class EglUtilSingletonHolder
    {
        private static final EglUtil INSTANCE = new EglUtil();
    }
}

/*
 * Location: E:\SouceCode\recordGame\gdxDemo\libs\android-1.2.2415.jar
 * Qualified Name: com.intel.inde.mp.android.graphics.EglUtil
 * JD-Core Version: 0.6.1
 */
