 package com.intel.inde.mp.android;
 
 import android.graphics.SurfaceTexture;
 import android.graphics.SurfaceTexture.OnFrameAvailableListener;
 import android.hardware.Camera;
 import android.hardware.Camera.Parameters;
 import android.hardware.Camera.Size;
 import android.opengl.EGL14;
 import android.opengl.EGLContext;
 import android.opengl.GLSurfaceView;
 import android.opengl.GLSurfaceView.Renderer;
 import android.os.Handler;
 import android.os.Message;
 import android.view.Surface;
 import com.intel.inde.mp.IVideoEffect;
 import com.intel.inde.mp.domain.IOnFrameAvailableListener;
 import com.intel.inde.mp.domain.IPreview;
 import com.intel.inde.mp.domain.PreviewContext;
 import com.intel.inde.mp.domain.Resolution;
 import com.intel.inde.mp.domain.graphics.IEglUtil;
 import com.intel.inde.mp.domain.graphics.TextureRenderer;
 import java.io.IOException;
 import javax.microedition.khronos.egl.EGLConfig;
 import javax.microedition.khronos.opengles.GL10;
 
 public class PreviewRender extends Handler
   implements IPreview, GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener
 {
   private IOnFrameAvailableListener listener;
   public static final int MSG_SET_SURFACE_TEXTURE = 0;
   private Camera camera;
   static final String TAG = "PreviewRender";
   private GLSurfaceView glView;
   private IEglUtil eglUtil;
   private TextureRenderer textureRender;
   private final float[] stMatrix = new float[16];
   private SurfaceTexture surfaceTexture;
   private final Object activeEffectGuard = new Object();
   private IVideoEffect activeEffect;
   private EGLContext eglContext;
   private Camera.Size inputRes;
   private int frameBufferTextureId = -1;
   private int textureId;
   private boolean previewTextureSet;
   private boolean requestRendering = true;
   private boolean skipFrame;
   private Surface surface;
   private final float[] ffCameraMatrix_0 = { 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, -1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F };
 
   private final float[] ffCameraMatrix_1 = { 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F };
 
   public PreviewRender(GLSurfaceView glView, IEglUtil eglUtil, Camera camera)
   {
     this.glView = glView;
     this.eglUtil = eglUtil;
     this.textureRender = new TextureRenderer(eglUtil);
     this.camera = camera;
     updateCameraParameters();
 
     glView.setEGLContextClientVersion(2);
     glView.setEGLConfigChooser(true);
     glView.setRenderer(this);
     glView.setRenderMode(0);
   }
 
   public void onSurfaceCreated(GL10 unused, EGLConfig config)
   {
     this.textureRender.surfaceCreated();
 
     this.textureId = this.eglUtil.createTexture(36197);
     this.surfaceTexture = new SurfaceTexture(this.textureId);
     this.surface = new Surface(this.surfaceTexture);
 
     sendMessage(obtainMessage(0));
   }
 
   public void onSurfaceChanged(GL10 unused, int width, int height)
   {
   }
 
   public void onDrawFrame(GL10 unused)
   {
     this.surfaceTexture.updateTexImage();
     this.eglContext = EGL14.eglGetCurrentContext();
 
     if (this.inputRes == null)
     {
       return;
     }
 
     if (this.skipFrame) {
       this.skipFrame = false;
       return;
     }
 
     synchronized (this.activeEffectGuard) {
       if (this.frameBufferTextureId != -1)
       {
         this.textureRender.drawFrame2D(this.ffCameraMatrix_0, this.frameBufferTextureId, 0.0F, true);
 
         this.frameBufferTextureId = -1;
       } else if (this.activeEffect != null) {
         this.surfaceTexture.getTransformMatrix(this.stMatrix);
         this.activeEffect.applyEffect(this.textureId, 0L, this.stMatrix);
       } else {
         this.textureRender.drawFrameOES(new SurfaceTextureWrapper(this.surfaceTexture), this.textureId, 0.0F, true);
       }
     }
   }
 
   public void requestRendering()
   {
     this.glView.requestRender();
   }
 
   public void setActiveEffect(IVideoEffect effectApplied)
   {
     synchronized (this.activeEffectGuard) {
       this.activeEffect = effectApplied;
       updateEffectResolution();
       this.frameBufferTextureId = -1;
     }
   }
 
   public void renderSurfaceFromFrameBuffer(int id)
   {
     synchronized (this.activeEffectGuard) {
       this.frameBufferTextureId = id;
       requestRendering();
     }
   }
 
   public void updateCameraParameters()
   {
     synchronized (this.activeEffectGuard) {
       this.inputRes = this.camera.getParameters().getPreviewSize();
       updateEffectResolution();
       this.textureRender.setInputSize(this.inputRes.width, this.inputRes.height);
     }
   }
 
   private void updateEffectResolution() {
     if ((this.activeEffect != null) && (this.inputRes != null))
       this.activeEffect.setInputResolution(new Resolution(this.inputRes.width, this.inputRes.height));
   }
 
   public void start()
   {
     this.requestRendering = true;
     if (this.previewTextureSet) {
       this.camera.startPreview();
 
       skip1Frame();
     }
   }
 
   private void skip1Frame() {
     this.skipFrame = true;
   }
 
   public void stop()
   {
     this.requestRendering = false;
     if (this.previewTextureSet)
       this.camera.stopPreview();
   }
 
   public void setListener(IOnFrameAvailableListener listener)
   {
     synchronized (this.activeEffectGuard) {
       this.listener = listener;
     }
   }
 
   public PreviewContext getSharedContext() {
     return new PreviewContext(new SurfaceTextureWrapper(this.surfaceTexture), this.textureId, new EGLContextWrapper(this.eglContext));
   }
 
   public void handleMessage(Message inputMessage)
   {
     int what = inputMessage.what;
 
     switch (what) {
     case 0:
       try {
         this.camera.setPreviewTexture(this.surfaceTexture);
         this.previewTextureSet = true;
       } catch (IOException e) {
         e.printStackTrace();
         throw new RuntimeException("camera.setPreviewTexture(surfaceTexture)");
       }
       this.surfaceTexture.setOnFrameAvailableListener(this);
 
       this.camera.startPreview();
       break;
     default:
       throw new RuntimeException("unknown msg " + what);
     }
   }
 
   public void onFrameAvailable(SurfaceTexture surfaceTexture)
   {
     if (!this.requestRendering) return;
 
     synchronized (this.activeEffectGuard) {
       if (this.listener == null) {
         requestRendering();
         return;
       }
       this.listener.onFrameAvailable();
     }
   }
 }

/* Location:           E:\SouceCode\recordGame\gdxDemo\libs\android-1.2.2415.jar
 * Qualified Name:     com.intel.inde.mp.android.PreviewRender
 * JD-Core Version:    0.6.1
 */