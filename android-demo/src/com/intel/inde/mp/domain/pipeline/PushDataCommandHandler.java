 package com.intel.inde.mp.domain.pipeline;
 
 import com.intel.inde.mp.domain.Command;
 import com.intel.inde.mp.domain.CommandQueue;
 import com.intel.inde.mp.domain.Frame;
 import com.intel.inde.mp.domain.ICommandHandler;
 import com.intel.inde.mp.domain.IFrameAllocator;
 import com.intel.inde.mp.domain.IOutput;
 import com.intel.inde.mp.domain.Plugin;
 
 class PushDataCommandHandler
   implements ICommandHandler
 {
   protected IOutput output;
   protected Plugin plugin;
   private IFrameAllocator inputWithAllocator;
 
   public PushDataCommandHandler(IOutput output, Plugin plugin, IFrameAllocator frameAllocator)
   {
     this.output = output;
     this.plugin = plugin;
     this.inputWithAllocator = frameAllocator;
   }
 
   public void handle()
   {
     Frame frame = this.inputWithAllocator.findFreeFrame();
 
     if (frame == null)
     {
       restoreCommands();
       return;
     }
 
     this.output.pull(frame);
     this.plugin.push(frame);
     this.plugin.checkIfOutputQueueHasData();
   }
 
   private void restoreCommands() {
     this.output.getOutputCommandQueue().queue(Command.HasData, Integer.valueOf(this.plugin.getTrackId()));
     this.plugin.skipProcessing();
     this.plugin.getInputCommandQueue().queue(Command.NeedData, Integer.valueOf(this.plugin.getTrackId()));
   }
 }

/* Location:           E:\SouceCode\recordGame\gdxDemo\libs\domain-1.2.2415.jar
 * Qualified Name:     com.intel.inde.mp.domain.pipeline.PushDataCommandHandler
 * JD-Core Version:    0.6.1
 */