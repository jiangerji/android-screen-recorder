 package com.intel.inde.mp.android;
 
 import android.media.MediaCodec;
 import android.opengl.EGL14;
 import com.intel.inde.mp.domain.ISurface;
 import com.intel.inde.mp.domain.ISurfaceWrapper;
 import com.intel.inde.mp.domain.Resolution;
 import com.intel.inde.mp.domain.graphics.IEglUtil;
 
 public class Surface
   implements ISurface
 {
   private final OutputSurface outputSurface;
   private final InputSurface inputSurface;
   private int width;
   private int height;
 
   public Surface(MediaCodec mediaCodec, IEglUtil eglUtil)
   {
     this.inputSurface = new InputSurface(mediaCodec.createInputSurface(), EGL14.eglGetCurrentContext());
     this.inputSurface.makeCurrent();
     this.outputSurface = new OutputSurface(eglUtil);
   }
 
   public void awaitNewImage()
   {
     this.outputSurface.awaitNewImage();
   }
 
   public void drawImage()
   {
     this.outputSurface.drawImage();
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
     return AndroidMediaObjectFactory.Converter.convert(this.outputSurface.getSurface());
   }
 
   public void setProjectionMatrix(float[] projectionMatrix)
   {
     this.inputSurface.setProjectionMatrix(projectionMatrix);
   }
 
   public void setViewport()
   {
     this.inputSurface.setViewPort();
   }
 
   public void setInputSize(int width, int height)
   {
     this.width = width;
     this.height = height;
     this.outputSurface.setInputSize(width, height);
   }
 
   public Resolution getInputSize()
   {
     return new Resolution(this.width, this.height);
   }
 
   public OutputSurface getOutputSurface() {
     return this.outputSurface;
   }
 
   public InputSurface getInputSurface() {
     return this.inputSurface;
   }
 
   public void release()
   {
   }
 }

/* Location:           E:\SouceCode\recordGame\gdxDemo\libs\android-1.2.2415.jar
 * Qualified Name:     com.intel.inde.mp.android.Surface
 * JD-Core Version:    0.6.1
 */