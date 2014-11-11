 package com.intel.inde.mp.android;
 
 import android.graphics.SurfaceTexture;
 import com.intel.inde.mp.domain.IEffectorSurface;
 import com.intel.inde.mp.domain.ISurfaceWrapper;
 import com.intel.inde.mp.domain.Resolution;
 import com.intel.inde.mp.domain.graphics.IEglUtil;
 import com.intel.inde.mp.domain.graphics.TextureRenderer;
 
 public class EffectorSurface
   implements IEffectorSurface
 {
   OutputSurface outputSurface;
   private int width;
   private int height;
   private SurfaceTexture surfaceTexture;
 
   public EffectorSurface(IEglUtil eglUtil)
   {
     this.outputSurface = new OutputSurface(eglUtil);
   }
 
   public void release()
   {
     this.outputSurface.release();
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
   }
 
   public void swapBuffers()
   {
   }
 
   public void makeCurrent()
   {
     this.outputSurface.makeCurrent();
   }
 
   public void setProjectionMatrix(float[] projectionMatrix)
   {
   }
 
   public void getTransformMatrix(float[] transformMatrix)
   {
     this.outputSurface.getTransformMatrix(transformMatrix);
   }
 
   public ISurfaceWrapper getCleanObject()
   {
     return AndroidMediaObjectFactory.Converter.convert(this.outputSurface.getSurface());
   }
 
   public int getSurfaceId()
   {
     return this.outputSurface.getTextureId();
   }
 
   public void drawImage(int textureIdx, float[] matrix)
   {
     this.outputSurface.getTextureRender().drawFrameOES(matrix, textureIdx, 0.0F, false);
   }
 
   public void drawImage2D(int textureIdx, float[] matrix)
   {
     this.outputSurface.getTextureRender().drawFrame2D(matrix, textureIdx, 0.0F, true);
   }
 
   public void setViewport()
   {
   }
 
   public void setInputSize(int width, int height)
   {
     this.width = width;
     this.height = height;
     this.outputSurface.getTextureRender().setInputSize(width, height);
   }
 
   public Resolution getInputSize()
   {
     return new Resolution(this.width, this.height);
   }
 }

/* Location:           E:\SouceCode\recordGame\gdxDemo\libs\android-1.2.2415.jar
 * Qualified Name:     com.intel.inde.mp.android.EffectorSurface
 * JD-Core Version:    0.6.1
 */