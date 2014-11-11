 package com.intel.inde.mp.domain.pipeline;
 
 import com.intel.inde.mp.domain.Frame;
 import com.intel.inde.mp.domain.ICommandHandler;
 import com.intel.inde.mp.domain.IInput;
 
 class SkipOutputFormatChangeCommandHandler
   implements ICommandHandler
 {
   private IInput encoder;
 
   public SkipOutputFormatChangeCommandHandler(IInput encoder)
   {
     this.encoder = encoder;
   }
 
   public void handle()
   {
     this.encoder.push(Frame.empty());
   }
 }

/* Location:           E:\SouceCode\recordGame\gdxDemo\libs\domain-1.2.2415.jar
 * Qualified Name:     com.intel.inde.mp.domain.pipeline.SkipOutputFormatChangeCommandHandler
 * JD-Core Version:    0.6.1
 */