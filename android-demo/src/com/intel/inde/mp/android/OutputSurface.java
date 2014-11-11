 package com.intel.inde.mp.android;
 
 import android.graphics.SurfaceTexture;
 import android.graphics.SurfaceTexture.OnFrameAvailableListener;
 import android.view.Surface;
 import com.intel.inde.mp.domain.graphics.IEglUtil;
 import com.intel.inde.mp.domain.graphics.TextureRenderer;
 import javax.microedition.khronos.egl.EGL10;
 import javax.microedition.khronos.egl.EGLContext;
 import javax.microedition.khronos.egl.EGLDisplay;
 import javax.microedition.khronos.egl.EGLSurface;
 
 public class OutputSurface
   implements SurfaceTexture.OnFrameAvailableListener
 {
   private EGL10 egl;
   private EGLDisplay eglDisplay;
   private EGLContext eglContext;
   private EGLSurface eglSurface;
   private int textureId;
   private SurfaceTexture surfaceTexture;
   private Surface surface;
   private final Object isFrameAvailableSyncGuard = new Object();
   private boolean isFrameAvailable;
   private TextureRenderer textureRender;
   private IEglUtil eglUtil;
 
   public OutputSurface(IEglUtil eglUtil)
   {
     this.eglUtil = eglUtil;
     this.textureRender = new TextureRenderer(this.eglUtil);
     this.textureRender.surfaceCreated();
 
     this.textureId = this.eglUtil.createTexture(36197);
     this.surfaceTexture = new SurfaceTexture(this.textureId);
 
     this.surfaceTexture.setOnFrameAvailableListener(this);
     this.surface = new Surface(this.surfaceTexture);
   }
 
   public void setInputSize(int width, int height) {
     this.textureRender.setInputSize(width, height);
   }
 
   public void release()
   {
     if (this.egl != null) {
       if (this.egl.eglGetCurrentContext().equals(this.eglContext))
       {
         this.egl.eglMakeCurrent(this.eglDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
       }
 
       this.egl.eglDestroySurface(this.eglDisplay, this.eglSurface);
       this.egl.eglDestroyContext(this.eglDisplay, this.eglContext);
     }
 
     this.surface.release();
 
     this.surfaceTexture.release();
     this.eglUtil = null;
 
     this.eglDisplay = null;
     this.eglContext = null;
     this.eglSurface = null;
     this.egl = null;
 
     this.textureRender = null;
     this.surface = null;
     this.surfaceTexture = null;
   }
 
   public void makeCurrent()
   {
     String message = "Failed to set up EGL context and surface.";
 
     if (this.egl == null) {
       throw new RuntimeException(message);
     }
     this.eglUtil.checkEglError("before makeCurrent");
     if (!this.egl.eglMakeCurrent(this.eglDisplay, this.eglSurface, this.eglSurface, this.eglContext))
       throw new RuntimeException(message);
   }
 
   public Surface getSurface()
   {
     return this.surface;
   }
 
   public int getTextureId() {
     return this.textureId;
   }
 
   public void getTransformMatrix(float[] transformMatrix) {
     this.surfaceTexture.getTransformMatrix(transformMatrix);
   }
 
   public void awaitNewImage()
   {
     int TIMEOUT_MS = 500;
     int timeout = 0;
 
     synchronized (this.isFrameAvailableSyncGuard) {
       while (!this.isFrameAvailable)
       {
         try
         {
           this.isFrameAvailableSyncGuard.wait(500L);
           if (!this.isFrameAvailable)
           {
             timeout++;
 
             if (timeout > 20)
               throw new RuntimeException("Frame wait timed out.");
           }
         }
         catch (InterruptedException ie)
         {
           throw new RuntimeException(ie);
         }
       }
       this.isFrameAvailable = false;
     }
 
     this.eglUtil.checkEglError("before updateTexImage");
     this.surfaceTexture.updateTexImage();
   }
 
   public void drawImage()
   {
     this.textureRender.drawFrameOES(new SurfaceTextureWrapper(this.surfaceTexture), this.textureId, 0.0F, true);
   }
 
   public void onFrameAvailable(SurfaceTexture st)
   {
     synchronized (this.isFrameAvailableSyncGuard) {
       if (this.isFrameAvailable) {
         throw new RuntimeException("Failed to notify on a new frame available.");
       }
       this.isFrameAvailable = true;
       this.isFrameAvailableSyncGuard.notifyAll();
     }
   }
 
   public SurfaceTexture getSurfaceTexture() { return this.surfaceTexture; } 
   public TextureRenderer getTextureRender() {
     return this.textureRender;
   }
 }

/* Location:           E:\SouceCode\recordGame\gdxDemo\libs\android-1.2.2415.jar
 * Qualified Name:     com.intel.inde.mp.android.OutputSurface
 * JD-Core Version:    0.6.1
 */