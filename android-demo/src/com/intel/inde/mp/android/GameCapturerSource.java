 package com.intel.inde.mp.android;
 
 import com.intel.inde.mp.domain.CaptureSource;
 import com.intel.inde.mp.domain.Command;
 import com.intel.inde.mp.domain.CommandQueue;
 import com.intel.inde.mp.domain.ISurface;
 import com.intel.inde.mp.domain.ISurfaceListener;
 
 public class GameCapturerSource extends CaptureSource
 {
   ISurface renderingSurface = null;
   private boolean swapBuffers = true;
   private EglContextSwitcher contextSwitcher;
   private ISurfaceListener listener;
 
   public GameCapturerSource()
   {
     this.contextSwitcher = new EglContextSwitcher();
   }
 
   public void addSetSurfaceListener(ISurfaceListener listenMe)
   {
     this.listener = listenMe;
   }
 
   public ISurface getSurface()
   {
     return this.renderingSurface;
   }
 
   public void setSurfaceSize(int width, int height)
   {
     this.contextSwitcher.init(width, height);
     this.contextSwitcher.saveEglState();
 
     if (this.listener != null) {
       this.listener.onSurfaceAvailable(null);
     }
 
     this.renderingSurface.makeCurrent();
     this.contextSwitcher.restoreEglState();
   }
 
   public void beginCaptureFrame()
   {
     if (this.renderingSurface == null) {
       return;
     }
 
     this.contextSwitcher.saveEglState();
 
     this.renderingSurface.makeCurrent();
     this.renderingSurface.setProjectionMatrix(this.contextSwitcher.getProjectionMatrix());
     this.renderingSurface.setViewport();
   }
 
   public void endCaptureFrame()
   {
     super.endCaptureFrame();
 
     long presentationTimeUs = System.nanoTime() - this.startTime;
     this.renderingSurface.setPresentationTime(presentationTimeUs);
 
     if (this.swapBuffers) {
       this.renderingSurface.swapBuffers();
     }
 
     this.contextSwitcher.restoreEglState();
     this.commandQueue.queue(Command.HasData, Integer.valueOf(0));
   }
 
   public void setOutputSurface(ISurface surface)
   {
     this.renderingSurface = surface;
   }
 }

/* Location:           E:\SouceCode\recordGame\gdxDemo\libs\android-1.2.2415.jar
 * Qualified Name:     com.intel.inde.mp.android.GameCapturerSource
 * JD-Core Version:    0.6.1
 */