 package com.intel.inde.mp.android;
 
 import android.media.MediaCodec;
 import android.opengl.EGLContext;
 import android.view.Surface;
 import com.intel.inde.mp.domain.ISurface;
 import com.intel.inde.mp.domain.ISurfaceWrapper;
 import com.intel.inde.mp.domain.Resolution;
 
 public class SimpleSurface
   implements ISurface
 {
   private InputSurface inputSurface;
   private Surface androidSurface;
   private int width;
   private int height;
 
   public SimpleSurface(MediaCodec mediaCodec, EGLContext eglSharedCtx)
   {
     this.androidSurface = mediaCodec.createInputSurface();
     this.inputSurface = new InputSurface(this.androidSurface, eglSharedCtx);
   }
 
   public void awaitNewImage()
   {
   }
 
   public void drawImage()
   {
   }
 
   public void setPresentationTime(long presentationTimeInNanoSeconds)
   {
     this.inputSurface.setPresentationTime(presentationTimeInNanoSeconds);
   }
 
   public void swapBuffers()
   {
     this.inputSurface.swapBuffers();
   }
 
   public void makeCurrent()
   {
     this.inputSurface.makeCurrent();
   }
 
   public ISurfaceWrapper getCleanObject()
   {
     return AndroidMediaObjectFactory.Converter.convert(this.inputSurface.getSurface());
   }
 
   public void setProjectionMatrix(float[] projectionMatrix)
   {
   }
 
   public void setViewport()
   {
   }
 
   public void setInputSize(int width, int height)
   {
     this.width = width;
     this.height = height;
   }
 
   public Resolution getInputSize()
   {
     return new Resolution(this.width, this.height);
   }
 
   public Surface getNativeSurface() {
     return this.androidSurface;
   }
 
   public void release()
   {
     this.inputSurface.release();
     this.androidSurface.release();
 
     this.inputSurface = null;
     this.androidSurface = null;
   }
 }

/* Location:           E:\SouceCode\recordGame\gdxDemo\libs\android-1.2.2415.jar
 * Qualified Name:     com.intel.inde.mp.android.SimpleSurface
 * JD-Core Version:    0.6.1
 */