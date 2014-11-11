 package com.intel.inde.mp.android;
 
 import android.opengl.EGL14;
 import android.opengl.EGLConfig;
 import android.opengl.EGLContext;
 import android.opengl.EGLDisplay;
 import android.opengl.EGLExt;
 import android.opengl.EGLSurface;
 import android.view.Surface;
 
 class InputSurface
 {
   private static final int EGL_RECORDABLE_ANDROID = 12610;
   private EGLDisplay mEGLDisplay;
   private EGLContext mEGLContext;
   private EGLSurface mEGLSurface;
   private Surface surface;
   private final float[] projectionMatrix = new float[16];
 
   public InputSurface(Surface surface, EGLContext sharedEglContext)
   {
     if (surface == null) {
       throw new NullPointerException();
     }
     this.surface = surface;
 
     eglSetup(sharedEglContext);
   }
 
   private void eglSetup(EGLContext eglContext)
   {
     this.mEGLDisplay = EGL14.eglGetDisplay(0);
     if (this.mEGLDisplay == EGL14.EGL_NO_DISPLAY) {
       throw new RuntimeException("Failed to get EGL display.");
     }
     int[] version = new int[2];
     if (!EGL14.eglInitialize(this.mEGLDisplay, version, 0, version, 1)) {
       this.mEGLDisplay = null;
       throw new RuntimeException("Failed to initialize EGL.");
     }
 
     int[] attribList = { 12324, 8, 12323, 8, 12322, 8, 12352, 4, 12610, 1, 12344 };
 
     EGLConfig[] configs = new EGLConfig[1];
     int[] numConfigs = new int[1];
     if (!EGL14.eglChooseConfig(this.mEGLDisplay, attribList, 0, configs, 0, configs.length, numConfigs, 0)) {
       throw new RuntimeException("Failed to find RGB888 recordable ES2 EGL config.");
     }
 
     int[] attrib_list = { 12440, 2, 12344 };
 
     this.mEGLContext = EGL14.eglCreateContext(this.mEGLDisplay, configs[0], eglContext, attrib_list, 0);
 
     checkEglError("eglCreateContext");
     if (this.mEGLContext == null) {
       throw new RuntimeException("Null EGL context.");
     }
 
     int[] surfaceAttribs = { 12344 };
 
     this.mEGLSurface = EGL14.eglCreateWindowSurface(this.mEGLDisplay, configs[0], this.surface, surfaceAttribs, 0);
 
     checkEglError("eglCreateWindowSurface");
     if (this.mEGLSurface == null)
       throw new RuntimeException("Null EGL surface.");
   }
 
   public void release()
   {
     if (EGL14.eglGetCurrentContext().equals(this.mEGLContext))
     {
       EGL14.eglMakeCurrent(this.mEGLDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT);
     }
 
     EGL14.eglDestroySurface(this.mEGLDisplay, this.mEGLSurface);
     EGL14.eglDestroyContext(this.mEGLDisplay, this.mEGLContext);
 
     this.surface.release();
 
     this.mEGLDisplay = null;
     this.mEGLContext = null;
     this.mEGLSurface = null;
 
     this.surface = null;
   }
 
   public void makeCurrent()
   {
     if (!EGL14.eglMakeCurrent(this.mEGLDisplay, this.mEGLSurface, this.mEGLSurface, this.mEGLContext))
       throw new RuntimeException("Failed to make EGL context and surface current.");
   }
 
   public boolean swapBuffers()
   {
     return EGL14.eglSwapBuffers(this.mEGLDisplay, this.mEGLSurface);
   }
 
   public Surface getSurface()
   {
     return this.surface;
   }
 
   public void setPresentationTime(long nsecs)
   {
     EGLExt.eglPresentationTimeANDROID(this.mEGLDisplay, this.mEGLSurface, nsecs);
   }
 
   private void checkEglError(String msg)
   {
     boolean failed = false;
     int error;
     while ((error = EGL14.eglGetError()) != 12288)
     {
       failed = true;
     }
     if (failed)
       throw new RuntimeException("EGL error encountered.");
   }
 
   public void setProjectionMatrix(float[] projectionMatrix)
   {
     System.arraycopy(projectionMatrix, 0, this.projectionMatrix, 0, this.projectionMatrix.length);
   }
 
   public void setViewPort()
   {
   }
 }

/* Location:           E:\SouceCode\recordGame\gdxDemo\libs\android-1.2.2415.jar
 * Qualified Name:     com.intel.inde.mp.android.InputSurface
 * JD-Core Version:    0.6.1
 */