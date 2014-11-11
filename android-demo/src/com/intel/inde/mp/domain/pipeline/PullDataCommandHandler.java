 package com.intel.inde.mp.domain.pipeline;
 
 import com.intel.inde.mp.domain.Frame;
 import com.intel.inde.mp.domain.ICommandHandler;
 import com.intel.inde.mp.domain.IPluginOutput;
 import com.intel.inde.mp.domain.Render;
 
 class PullDataCommandHandler
   implements ICommandHandler
 {
   protected Render input;
   protected IPluginOutput output;
 
   public PullDataCommandHandler(IPluginOutput output, Render input)
   {
     this.input = input;
     this.output = output;
   }
 
   public void handle()
   {
     Frame frame = this.output.getFrame();
 
     if (Frame.EOF().equals(frame)) {
       this.input.drain(frame.getBufferIndex());
     } else {
       this.input.push(frame);
       this.output.releaseOutputBuffer(frame.getBufferIndex());
     }
   }
 }

/* Location:           E:\SouceCode\recordGame\gdxDemo\libs\domain-1.2.2415.jar
 * Qualified Name:     com.intel.inde.mp.domain.pipeline.PullDataCommandHandler
 * JD-Core Version:    0.6.1
 */