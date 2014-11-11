 package com.intel.inde.mp.domain.pipeline;
 
 import com.intel.inde.mp.domain.Command;
 import com.intel.inde.mp.domain.CommandQueue;
 import com.intel.inde.mp.domain.ICommandHandler;
 import com.intel.inde.mp.domain.IVideoOutput;
 import com.intel.inde.mp.domain.Plugin;
 
 class ConfigureVideoDecoderCommandHandler
   implements ICommandHandler
 {
   protected final IVideoOutput output;
   private final Plugin input;
 
   public ConfigureVideoDecoderCommandHandler(IVideoOutput output, Plugin decoder)
   {
     this.output = output;
     this.input = decoder;
   }
 
   public void handle()
   {
     this.output.getOutputCommandQueue().queue(Command.HasData, Integer.valueOf(this.input.getTrackId()));
     this.input.setInputResolution(this.output.getOutputResolution());
   }
 }

/* Location:           E:\SouceCode\recordGame\gdxDemo\libs\domain-1.2.2415.jar
 * Qualified Name:     com.intel.inde.mp.domain.pipeline.ConfigureVideoDecoderCommandHandler
 * JD-Core Version:    0.6.1
 */