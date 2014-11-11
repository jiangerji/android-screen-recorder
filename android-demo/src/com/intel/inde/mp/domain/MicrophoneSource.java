 package com.intel.inde.mp.domain;
 
 import java.io.IOException;
 
 public class MicrophoneSource
   implements IMicrophoneSource
 {
   protected CommandQueue commandQueue = new CommandQueue();
   private boolean isStopped = true;
 
   public void configure(int sampleRate, int channels)
   {
   }
 
   public void pull(Frame frame) {
     if (!isStopped())
     {
       if (frame.getLength() < 0) {
         frame.set(null, 0, 0L, 0, 0, 0);
       }
       this.commandQueue.queue(Command.HasData, Integer.valueOf(0));
     }
     else {
       frame.copyInfoFrom(Frame.EOF());
       frame.copyDataFrom(Frame.EOF());
     }
   }
 
   public MediaFormat getMediaFormatByType(MediaFormatType mediaFormatType)
   {
     return null;
   }
 
   public boolean isLastFile()
   {
     return true;
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
 
   public void start() {
     this.commandQueue.queue(Command.HasData, Integer.valueOf(0));
     this.isStopped = false;
   }
 
   public void stop()
   {
     this.commandQueue.clear();
     this.commandQueue.queue(Command.EndOfFile, Integer.valueOf(0));
     this.isStopped = true;
   }
   public boolean isStopped() {
     return this.isStopped;
   }
 }

/* Location:           E:\SouceCode\recordGame\gdxDemo\libs\domain-1.2.2415.jar
 * Qualified Name:     com.intel.inde.mp.domain.MicrophoneSource
 * JD-Core Version:    0.6.1
 */