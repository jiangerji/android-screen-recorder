 package com.intel.inde.mp.domain;
 
 public abstract class Plugin extends Input
   implements ITransform, IPluginOutput
 {
   private CommandQueue outputQueue = new CommandQueue();
   protected MediaFormat mediaFormat = null;
 
   public void checkIfOutputQueueHasData()
   {
   }
 
   public void notifySurfaceReady(ISurface surface)
   {
   }
 
   public CommandQueue getOutputCommandQueue()
   {
     return this.outputQueue;
   }
 
   public abstract void start();
 
   public abstract void stop();
 
   public void push(Frame frame)
   {
     if (frame.equals(Frame.EOF()))
       drain(frame.getBufferIndex());
   }
 
   public MediaFormatType getMediaFormatType()
   {
     if (this.mediaFormat.getMimeType().startsWith("audio")) {
       return MediaFormatType.AUDIO;
     }
 
     return MediaFormatType.VIDEO;
   }
 
   public MediaFormat getMediaFormatByType(MediaFormatType mediaFormatType)
   {
     if (this.mediaFormat.getMimeType().startsWith(mediaFormatType.toString())) {
       return this.mediaFormat;
     }
     return null;
   }
 
   public MediaFormat getOutputMediaFormat()
   {
     return this.mediaFormat;
   }
 
   public void incrementConnectedPluginsCount()
   {
   }
 
   public boolean canConnectFirst(IInputRaw connector) {
     return true;
   }
 
   public boolean canConnectFirst(IOutputRaw connector) {
     return true;
   }
 
   public void recreate() {
   }
 
   public void setInputResolution(Resolution resolution) {
     getSurface().setInputSize(resolution.width(), resolution.height());
   }
 }

/* Location:           E:\SouceCode\recordGame\gdxDemo\libs\domain-1.2.2415.jar
 * Qualified Name:     com.intel.inde.mp.domain.Plugin
 * JD-Core Version:    0.6.1
 */