 package com.intel.inde.mp.domain.pipeline;
 
 import com.intel.inde.mp.domain.ICommandHandler;
 import com.intel.inde.mp.domain.MediaCodecPlugin;
 
 class DrainCommandHandler
   implements ICommandHandler
 {
   protected final MediaCodecPlugin plugin;
 
   public DrainCommandHandler(MediaCodecPlugin plugin)
   {
     this.plugin = plugin;
   }
 
   public void handle()
   {
     this.plugin.drain(0);
   }
 }

/* Location:           E:\SouceCode\recordGame\gdxDemo\libs\domain-1.2.2415.jar
 * Qualified Name:     com.intel.inde.mp.domain.pipeline.DrainCommandHandler
 * JD-Core Version:    0.6.1
 */