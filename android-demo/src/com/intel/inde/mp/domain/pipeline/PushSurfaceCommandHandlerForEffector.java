 package com.intel.inde.mp.domain.pipeline;
 
 import com.intel.inde.mp.domain.Frame;
 import com.intel.inde.mp.domain.ICommandHandler;
 import com.intel.inde.mp.domain.IPluginOutput;
 import com.intel.inde.mp.domain.Plugin;
 
 class PushSurfaceCommandHandlerForEffector
   implements ICommandHandler
 {
   protected final IPluginOutput output;
   protected final Plugin input;
 
   public PushSurfaceCommandHandlerForEffector(IPluginOutput output, Plugin input)
   {
     this.output = output;
     this.input = input;
   }
 
   public void handle()
   {
     Frame frame = this.output.getFrame();
     if (!frame.equals(Frame.EOF())) {
       this.output.releaseOutputBuffer(frame.getBufferIndex());
     }
 
     this.input.push(frame);
     this.input.checkIfOutputQueueHasData();
   }
 }

/* Location:           E:\SouceCode\recordGame\gdxDemo\libs\domain-1.2.2415.jar
 * Qualified Name:     com.intel.inde.mp.domain.pipeline.PushSurfaceCommandHandlerForEffector
 * JD-Core Version:    0.6.1
 */