package com.intel.inde.mp.android;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.Matrix;

import com.intel.inde.mp.domain.Command;
import com.intel.inde.mp.domain.CommandQueue;
import com.intel.inde.mp.domain.Frame;
import com.intel.inde.mp.domain.IInputRaw;
import com.intel.inde.mp.domain.IOnFrameAvailableListener;
import com.intel.inde.mp.domain.IPreview;
import com.intel.inde.mp.domain.ISurface;
import com.intel.inde.mp.domain.Resolution;
import com.intel.inde.mp.domain.graphics.IEglUtil;
import com.intel.inde.mp.domain.graphics.Program;
import com.intel.inde.mp.domain.graphics.TextureType;
import com.intel.inde.mp.domain.pipeline.TriangleVerticesCalculator;

public class CameraSource extends com.intel.inde.mp.domain.CameraSource
{
    private final IEglUtil eglUtil;
    private Camera camera;
    private ISurface surface;
    private SurfaceTextureManager surfaceTextureManager;
    private Frame currentFrame = new Frame(null, 1, 0L, 0, 0, 0);
    private long startTimeStamp;
    private boolean firstFrame = true;
    private long timeStampOffset;
    private IPreview preview;
    private Resolution outputResolution;

    public CameraSource(IEglUtil eglUtil)
    {
        this.eglUtil = eglUtil;
    }

    public Resolution getOutputResolution()
    {
        return this.outputResolution;
    }

    private void releaseCamera()
    {
        if (this.camera != null)
        {
            if (this.preview == null) {
                this.camera.stopPreview();
            }
            this.preview = null;

            this.camera = null;
        }
    }

    private void prepareSurfaceTexture()
    {
        this.surfaceTextureManager = new SurfaceTextureManager(getOutputCommandQueue(),
                this.eglUtil);
        SurfaceTexture surfaceTexture = this.surfaceTextureManager.getSurfaceTexture();
        try {
            this.camera.setPreviewTexture(surfaceTexture);
        } catch (IOException e) {
            throw new RuntimeException("Failed to prepare EGL surface texture.",
                    e);
        }
    }

    public void setPreview(IPreview preview)
    {
        this.preview = preview;
    }

    private void releaseSurfaceTexture()
    {
        if (this.surfaceTextureManager != null) {
            this.surfaceTextureManager.release();
            this.surfaceTextureManager = null;
        }
    }

    public void setOutputSurface(ISurface surface)
    {
        this.surface = surface;
    }

    public void setCamera(Object camera)
    {
        this.camera = ((Camera) camera);
        updateOutputResolution();
    }

    private void updateOutputResolution() {
        Camera.Parameters parameters = this.camera.getParameters();
        Camera.Size previewSize = parameters.getPreviewSize();
        this.outputResolution = new Resolution(previewSize.width,
                previewSize.height);
    }

    public void configure()
    {
        getOutputCommandQueue().queue(Command.OutputFormatChanged,
                Integer.valueOf(0));
        if (this.preview != null) {
            this.preview.setListener(new IOnFrameAvailableListener()
            {
                public void onFrameAvailable() {
                    CameraSource.this.getOutputCommandQueue()
                            .queue(Command.HasData, Integer.valueOf(0));
                }
            });
        }
        else {
            this.surface.makeCurrent();
            prepareSurfaceTexture();
            this.camera.startPreview();
        }

        updateOutputResolution();
        this.camera.startPreview();

        getOutputCommandQueue().queue(Command.OutputFormatChanged,
                Integer.valueOf(0));
    }

    public Frame getFrame()
    {
        if (this.surfaceTextureManager != null) {
            SurfaceTexture st = this.surfaceTextureManager.getSurfaceTexture();

            this.surfaceTextureManager.prepareAvailableFrame();
            this.surfaceTextureManager.drawImage();

            if (this.firstFrame) {
                long fromStartToFirstFrame = (System.currentTimeMillis() - this.startTimeStamp) * 1000000L;
                this.timeStampOffset = (st.getTimestamp() - fromStartToFirstFrame);
                this.firstFrame = false;
            }
            this.surface.setPresentationTime(st.getTimestamp()
                    - this.timeStampOffset);
        }

        long sampleTime = (System.currentTimeMillis() - this.startTimeStamp) * 1000L;

        this.currentFrame.setSampleTime(sampleTime);

        return this.currentFrame;
    }

    public ISurface getSurface()
    {
        return this.surface;
    }

    public boolean canConnectFirst(IInputRaw connector)
    {
        return true;
    }

    public void fillCommandQueues()
    {
    }

    public void close()
    {
        releaseCamera();
        releaseSurfaceTexture();
    }

    public void start()
    {
        this.startTimeStamp = System.currentTimeMillis();
        super.start();
    }

    private class SurfaceTextureManager
            implements SurfaceTexture.OnFrameAvailableListener
    {
        private SurfaceTexture surfaceTexture;
        private CameraSource.STextureRender textureRender;
        private CommandQueue commandQueue;
        private final IEglUtil eglUtil;
        private Object syncObject = new Object();
        private int numberOfUnprocessedFrames = 0;
        private float[] matrix = new float[16];

        public SurfaceTextureManager(CommandQueue commandQueue, IEglUtil eglUtil)
        {
            this.commandQueue = commandQueue;
            this.eglUtil = eglUtil;
            this.textureRender = new CameraSource.STextureRender(eglUtil);
            this.textureRender.surfaceCreated();

            this.surfaceTexture = new SurfaceTexture(this.textureRender.getTextureId());

            this.surfaceTexture.setOnFrameAvailableListener(this);
        }

        public void release()
        {
            this.textureRender = null;
            this.surfaceTexture = null;
        }

        public SurfaceTexture getSurfaceTexture()
        {
            return this.surfaceTexture;
        }

        public void prepareAvailableFrame()
        {
            synchronized (this.syncObject)
            {
                if (this.numberOfUnprocessedFrames > 0) {
                    this.numberOfUnprocessedFrames -= 1;
                }

            }

            this.eglUtil.checkEglError("before updateTexImage");
            this.surfaceTexture.updateTexImage();
        }

        public void drawImage()
        {
            this.surfaceTexture.getTransformMatrix(this.matrix);

            this.eglUtil.drawFrameStart(this.textureRender.program,
                    this.textureRender.triangleVertices,
                    this.textureRender.mvpMatrix,
                    this.textureRender.stMatrix,
                    0.0F,
                    TextureType.GL_TEXTURE_EXTERNAL_OES,
                    this.textureRender.getTextureId(),
                    CameraSource.this.outputResolution,
                    true);
        }

        public void onFrameAvailable(SurfaceTexture st)
        {
            synchronized (this.syncObject)
            {
                this.commandQueue.queue(Command.HasData, Integer.valueOf(0));
                this.numberOfUnprocessedFrames += 1;
                this.syncObject.notifyAll();
            }
        }
    }

    private static class STextureRender
    {
        private static final int FLOAT_SIZE_BYTES = 4;
        private final float[] triangleVerticesData = TriangleVerticesCalculator.getDefaultTriangleVerticesData();
        private final IEglUtil eglUtil;
        private FloatBuffer triangleVertices;
        private static final String VERTEX_SHADER = "uniform mat4 uMVPMatrix;\nuniform mat4 uSTMatrix;\nattribute vec4 aPosition;\nattribute vec4 aTextureCoord;\nvarying vec2 vTextureCoord;\nvoid main() {\n    gl_Position = uMVPMatrix * aPosition;\n    vTextureCoord = (uSTMatrix * aTextureCoord).xy;\n}\n";
        private static final String FRAGMENT_SHADER = "#extension GL_OES_EGL_image_external : require\nprecision mediump float;\nvarying vec2 vTextureCoord;\nuniform samplerExternalOES sTexture;\nvoid main() {\n    gl_FragColor = texture2D(sTexture, vTextureCoord);\n}\n";
        private float[] mvpMatrix = new float[16];
        private float[] stMatrix = new float[16];

        private Program program = new Program();
        private int textureId;

        public STextureRender(IEglUtil eglUtil)
        {
            this.eglUtil = eglUtil;
            this.triangleVertices = ByteBuffer.allocateDirect(this.triangleVerticesData.length * 4)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer();
            this.triangleVertices.put(this.triangleVerticesData).position(0);
            Matrix.setIdentityM(this.stMatrix, 0);
        }

        public int getTextureId() {
            return this.textureId;
        }

        public void surfaceCreated()
        {
            this.program = this.eglUtil.createProgram("uniform mat4 uMVPMatrix;\nuniform mat4 uSTMatrix;\nattribute vec4 aPosition;\nattribute vec4 aTextureCoord;\nvarying vec2 vTextureCoord;\nvoid main() {\n    gl_Position = uMVPMatrix * aPosition;\n    vTextureCoord = (uSTMatrix * aTextureCoord).xy;\n}\n",
                    "#extension GL_OES_EGL_image_external : require\nprecision mediump float;\nvarying vec2 vTextureCoord;\nuniform samplerExternalOES sTexture;\nvoid main() {\n    gl_FragColor = texture2D(sTexture, vTextureCoord);\n}\n");
            this.textureId = this.eglUtil.createTexture(36197);
        }
    }
}

/*
 * Location: E:\SouceCode\recordGame\gdxDemo\libs\android-1.2.2415.jar
 * Qualified Name: com.intel.inde.mp.android.CameraSource
 * JD-Core Version: 0.6.1
 */
