 package com.intel.inde.mp.android;
 
 import android.opengl.EGL14;
 import android.opengl.EGLContext;
 import android.opengl.EGLDisplay;
 import android.opengl.EGLSurface;
 import android.opengl.Matrix;
 import com.intel.inde.mp.domain.IEgl;
 
 public class EglContextSwitcher
   implements IEgl
 {
   private EGLDisplay display;
   private EGLSurface drawSurface;
   private EGLSurface readSurface;
   private EGLContext context;
   static final float[] projectionMatrix = new float[16];
   private final float[] savedMatrix = new float[16];
 
   private int width = 0; private int height = 0;
 
   public void init(int width, int height)
   {
     this.width = width;
     this.height = height;
 
     Matrix.orthoM(projectionMatrix, 0, 0.0F, this.width, 0.0F, this.height, -1.0F, 1.0F);
   }
 
   public void saveEglState()
   {
     if ((this.width == 0) || (this.height == 0)) {
       return;
     }
 
     System.arraycopy(projectionMatrix, 0, this.savedMatrix, 0, projectionMatrix.length);
     this.display = EGL14.eglGetCurrentDisplay();
     this.drawSurface = EGL14.eglGetCurrentSurface(12377);
     this.readSurface = EGL14.eglGetCurrentSurface(12378);
     this.context = EGL14.eglGetCurrentContext();
   }
 
   public void restoreEglState()
   {
     if ((this.width == 0) || (this.height == 0)) {
       return;
     }
 
     if (!EGL14.eglMakeCurrent(this.display, this.drawSurface, this.readSurface, this.context)) {
       throw new RuntimeException("Failed to restore EGL state.");
     }
     System.arraycopy(this.savedMatrix, 0, projectionMatrix, 0, projectionMatrix.length);
   }
 
   public float[] getProjectionMatrix()
   {
     return projectionMatrix;
   }
 }

/* Location:           E:\SouceCode\recordGame\gdxDemo\libs\android-1.2.2415.jar
 * Qualified Name:     com.intel.inde.mp.android.EglContextSwitcher
 * JD-Core Version:    0.6.1
 */