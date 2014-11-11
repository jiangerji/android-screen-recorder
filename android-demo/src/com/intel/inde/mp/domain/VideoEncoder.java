 package com.intel.inde.mp.domain;
 
 import com.intel.inde.mp.VideoFormat;
 
 public class VideoEncoder extends Encoder
   implements ITransform
 {
   public VideoEncoder(IMediaCodec mediaCodec)
   {
     super(mediaCodec);
   }
 
   public void setMediaFormat(MediaFormat inputMediaFormat)
   {
     this.mediaFormat = inputMediaFormat;
     getVideoFormat().setColorFormat(2130708361);
   }
 
   public void setBitRateInKBytes(int bitRate) {
     getVideoFormat().setVideoBitRateInKBytes(bitRate);
   }
 
   public int getBitRateInKBytes() {
     return getVideoFormat().getVideoBitRateInKBytes();
   }
 
   public void setFrameRate(int frameRate) {
     getVideoFormat().setVideoFrameRate(frameRate);
   }
 
   public int getFrameRate() {
     return getVideoFormat().getVideoFrameRate();
   }
 
   public void setIFrameInterval(int iFrameInterval) {
     getVideoFormat().setVideoIFrameInterval(iFrameInterval);
   }
 
   public int getIFrameInterval() {
     return getVideoFormat().getVideoIFrameInterval();
   }
 
   private VideoFormat getVideoFormat() {
     return (VideoFormat)this.mediaFormat;
   }
 
   public boolean isLastFile()
   {
     return false;
   }
 
   public void setOutputSurface(ISurface surface)
   {
   }
 
   public void waitForSurface(long pts) {
   }
 
   public void drain(int bufferIndex) {
     if (this.state != PluginState.Normal) return;
 
     super.drain(bufferIndex);
     this.mediaCodec.signalEndOfInputStream();
   }
 
   protected void feedMeIfNotDraining()
   {
     if ((this.frameCount < 2) && 
       (this.state != PluginState.Draining) && (this.state != PluginState.Drained))
     {
       Pair command = getInputCommandQueue().first();
 
       if ((command == null) || (command.left != Command.NeedData))
         getInputCommandQueue().queue(Command.NeedData, Integer.valueOf(getTrackId()));
     }
   }
 
   public void push(Frame frame)
   {
     super.push(frame);
   }
 
   public void notifySurfaceReady(ISurface surface)
   {
     if (this.frameCount < 2) {
       surface.swapBuffers();
       this.frameCount += 1;
     }
   }
 
   public void releaseOutputBuffer(int outputBufferIndex)
   {
     super.releaseOutputBuffer(outputBufferIndex);
     this.frameCount -= 1;
   }
 }

/* Location:           E:\SouceCode\recordGame\gdxDemo\libs\domain-1.2.2415.jar
 * Qualified Name:     com.intel.inde.mp.domain.VideoEncoder
 * JD-Core Version:    0.6.1
 */