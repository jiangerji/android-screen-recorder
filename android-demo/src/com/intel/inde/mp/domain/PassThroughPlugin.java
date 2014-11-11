 package com.intel.inde.mp.domain;
 
 import java.io.IOException;
 import java.nio.ByteBuffer;
 
 public class PassThroughPlugin extends Plugin
   implements IFrameAllocator
 {
   Frame frame;
   int outputTrackId = 0;
   private boolean frameDelivered = true;
   private MediaFormatType mediaFormatType;
 
   public PassThroughPlugin(int size, MediaFormatType mediaFormatType)
   {
     this.frame = new Frame(ByteBuffer.allocate(size), size, 0L, 0, 0, 0);
     this.mediaFormatType = mediaFormatType;
     getOutputCommandQueue().queue(Command.OutputFormatChanged, Integer.valueOf(getTrackId()));
   }
 
   protected void initInputCommandQueue()
   {
     feedMeIfNotDraining();
   }
 
   public Frame findFreeFrame()
   {
     return this.frame;
   }
 
   public MediaFormatType getMediaFormatType()
   {
     return this.mediaFormatType;
   }
 
   public void drain(int bufferIndex)
   {
     super.drain(bufferIndex);
     getOutputCommandQueue().queue(Command.EndOfFile, Integer.valueOf(0));
   }
 
   public void push(Frame frame)
   {
     super.push(frame);
     if (!frame.equals(Frame.EOF())) {
       this.frameDelivered = false;
       this.frame = frame;
       getOutputCommandQueue().queue(Command.HasData, Integer.valueOf(0));
     }
   }
 
   public void pull(Frame frame)
   {
     frame.copyInfoFrom(getFrame());
   }
 
   public Frame getFrame()
   {
     if (!this.frameDelivered) {
       this.frameDelivered = true;
       feedMeIfNotDraining();
       this.frame.setTrackId(this.outputTrackId);
       return this.frame;
     }
     if (this.state == PluginState.Draining) {
       return Frame.EOF();
     }
     throw new UnsupportedOperationException("Attempt to pull a frame twice.");
   }
 
   public boolean isLastFile()
   {
     return false;
   }
 
   public void skipProcessing()
   {
     getInputCommandQueue().queue(Command.NextPair, Integer.valueOf(getTrackId()));
   }
 
   public void start()
   {
   }
 
   public void stop()
   {
   }
 
   public void configure()
   {
   }
 
   public void checkIfOutputQueueHasData()
   {
   }
 
   public void setMediaFormat(MediaFormat mediaFormat)
   {
     this.mediaFormat = mediaFormat;
   }
 
   public void setOutputSurface(ISurface surface)
   {
   }
 
   public void setOutputTrackId(int trackId)
   {
     this.outputTrackId = trackId;
   }
 
   public void releaseOutputBuffer(int outputBufferIndex)
   {
   }
 
   public ISurface getSurface()
   {
     return null;
   }
 
   public void waitForSurface(long pts)
   {
   }
 
   public void fillCommandQueues()
   {
   }
 
   public void close()
     throws IOException
   {
   }
 }

/* Location:           E:\SouceCode\recordGame\gdxDemo\libs\domain-1.2.2415.jar
 * Qualified Name:     com.intel.inde.mp.domain.PassThroughPlugin
 * JD-Core Version:    0.6.1
 */