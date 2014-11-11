 package com.intel.inde.mp.domain;
 
 abstract class Input
   implements IInput
 {
   private CommandQueue inputQueue = new CommandQueue();
   protected PluginState state;
   protected int trackId;
 
   Input()
   {
     this.state = PluginState.Starting;
   }
   protected void initInputCommandQueue() {
   }
 
   void setState(PluginState state) {
     this.state = state;
   }
 
   public CommandQueue getInputCommandQueue()
   {
     return this.inputQueue;
   }
 
   public void drain(int bufferIndex)
   {
     setState(PluginState.Draining);
     getInputCommandQueue().clear();
   }
 
   protected void feedMeIfNotDraining() {
     if ((this.state != PluginState.Draining) && (this.state != PluginState.Drained))
       getInputCommandQueue().queue(Command.NeedData, Integer.valueOf(getTrackId()));
   }
 
   public abstract void configure();
 
   public void skipProcessing()
   {
     getInputCommandQueue().clear();
     getInputCommandQueue().queue(Command.NextPair, Integer.valueOf(getTrackId()));
   }
 
   public int getTrackId()
   {
     return this.trackId;
   }
 
   public void setTrackId(int trackId)
   {
     this.trackId = trackId;
     initInputCommandQueue();
   }
 }

/* Location:           E:\SouceCode\recordGame\gdxDemo\libs\domain-1.2.2415.jar
 * Qualified Name:     com.intel.inde.mp.domain.Input
 * JD-Core Version:    0.6.1
 */