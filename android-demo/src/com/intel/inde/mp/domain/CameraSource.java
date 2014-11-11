 package com.intel.inde.mp.domain;
 
 import java.io.IOException;
 
 public class CameraSource
   implements ICameraSource
 {
   private CommandQueue commandQueue = new CommandQueue();
   private boolean isStopped = true;
 
   public void setOutputSurface(ISurface surface) {
   }
 
   public void setPreview(IPreview preview) {
   }
 
   public void setCamera(Object camera) {
   }
 
   public void configure() {
   }
 
   public Frame getFrame() {
     return null;
   }
 
   public ISurface getSurface() {
     return null;
   }
 
   public boolean canConnectFirst(IInputRaw connector)
   {
     return false;
   }
 
   public CommandQueue getOutputCommandQueue()
   {
     return this.commandQueue;
   }
 
   public void fillCommandQueues()
   {
   }
 
   public void start() {
     this.isStopped = false;
   }
 
   public void stop()
   {
     this.commandQueue.clear();
     getOutputCommandQueue().queue(Command.EndOfFile, Integer.valueOf(0));
     this.isStopped = true;
   }
 
   public Resolution getOutputResolution()
   {
     return new Resolution(0, 0);
   }
   public void close() throws IOException {
   }
 
   public Boolean isStopped() {
     return Boolean.valueOf(this.isStopped);
   }
 }

/* Location:           E:\SouceCode\recordGame\gdxDemo\libs\domain-1.2.2415.jar
 * Qualified Name:     com.intel.inde.mp.domain.CameraSource
 * JD-Core Version:    0.6.1
 */