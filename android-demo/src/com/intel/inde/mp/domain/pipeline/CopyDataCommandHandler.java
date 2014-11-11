 package com.intel.inde.mp.domain.pipeline;
 
 import com.intel.inde.mp.domain.Command;
 import com.intel.inde.mp.domain.CommandQueue;
 import com.intel.inde.mp.domain.Frame;
 import com.intel.inde.mp.domain.ICommandHandler;
 import com.intel.inde.mp.domain.IPluginOutput;
 import com.intel.inde.mp.domain.MediaCodecPlugin;
 
 class CopyDataCommandHandler
   implements ICommandHandler
 {
   private final IPluginOutput output;
   private final MediaCodecPlugin input;
 
   public CopyDataCommandHandler(IPluginOutput output, MediaCodecPlugin input)
   {
     this.output = output;
     this.input = input;
   }
 
   public void handle()
   {
     Frame encoderFrame = this.input.findFreeFrame();
     if (encoderFrame == null) {
       restoreCommands();
       return;
     }
 
     Frame decoderFrame = this.output.getFrame();
     if (decoderFrame == null) return;
 
     encoderFrame.copyDataFrom(decoderFrame);
     this.input.push(encoderFrame);
 
     this.output.releaseOutputBuffer(decoderFrame.getBufferIndex());
   }
 
   private void restoreCommands()
   {
     this.output.getOutputCommandQueue().queue(Command.HasData, Integer.valueOf(0));
     this.input.getInputCommandQueue().clear();
     this.input.skipProcessing();
     this.input.getInputCommandQueue().queue(Command.NeedData, Integer.valueOf(0));
   }
 }

/* Location:           E:\SouceCode\recordGame\gdxDemo\libs\domain-1.2.2415.jar
 * Qualified Name:     com.intel.inde.mp.domain.pipeline.CopyDataCommandHandler
 * JD-Core Version:    0.6.1
 */