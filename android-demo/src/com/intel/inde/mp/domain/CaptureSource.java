 package com.intel.inde.mp.domain;
 
 import java.io.IOException;
 
 public class CaptureSource
   implements ICaptureSource
 {
   protected CommandQueue commandQueue = new CommandQueue();
 
   private Frame currentFrame = new Frame(null, 0, 0L, 0, 0, 0);
 
   protected long startTime = 0L;
   private Boolean isStopped = Boolean.valueOf(true);
 
   public void setSurfaceSize(int width, int height)
   {
   }
 
   public void beginCaptureFrame() {
   }
 
   public void endCaptureFrame() {
     if (this.startTime == 0L)
       this.startTime = System.nanoTime();
   }
 
   public void addSetSurfaceListener(ISurfaceListener listenMe)
   {
   }
 
   public void setOutputSurface(ISurface surface)
   {
   }
 
   public MediaFormatType getMediaFormatType() {
     return null;
   }
 
   public MediaFormat getOutputMediaFormat()
   {
     return null;
   }
 
   public void setTrackId(int trackId)
   {
   }
 
   public void setOutputTrackId(int trackId) {
   }
 
   public void releaseOutputBuffer(int outputBufferIndex) {
   }
 
   public void pull(Frame frame) {
   }
 
   public MediaFormat getMediaFormatByType(MediaFormatType mediaFormatType) {
     return null;
   }
 
   public boolean isLastFile()
   {
     return false;
   }
 
   public void incrementConnectedPluginsCount()
   {
   }
 
   public void close() throws IOException {
   }
 
   public boolean canConnectFirst(IInputRaw connector) {
     return true;
   }
 
   public CommandQueue getOutputCommandQueue()
   {
     return this.commandQueue;
   }
 
   public void fillCommandQueues()
   {
   }
 
   public Frame getFrame() {
     if (this.isStopped.booleanValue()) {
       this.commandQueue.clear();
       return Frame.EOF();
     }
     return this.currentFrame;
   }
 
   public void start()
   {
     this.isStopped = Boolean.valueOf(false);
   }
 
   public void stop()
   {
     if (!this.isStopped.booleanValue()) {
       this.isStopped = Boolean.valueOf(true);
       this.commandQueue.queue(Command.EndOfFile, Integer.valueOf(0));
     }
     this.startTime = 0L;
   }
 
   public ISurface getSurface()
   {
     return null;
   }
   public void waitForSurface(long pts) {
   }
 
   public boolean isStopped() {
     return this.isStopped.booleanValue();
   }
 }

/* Location:           E:\SouceCode\recordGame\gdxDemo\libs\domain-1.2.2415.jar
 * Qualified Name:     com.intel.inde.mp.domain.CaptureSource
 * JD-Core Version:    0.6.1
 */