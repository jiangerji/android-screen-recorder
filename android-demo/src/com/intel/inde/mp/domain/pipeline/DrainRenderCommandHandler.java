 package com.intel.inde.mp.domain.pipeline;
 
 import com.intel.inde.mp.domain.ICommandHandler;
 import com.intel.inde.mp.domain.Render;
 
 public class DrainRenderCommandHandler
   implements ICommandHandler
 {
   protected final Render render;
 
   public DrainRenderCommandHandler(Render render)
   {
     this.render = render;
   }
 
   public void handle()
   {
     this.render.drain(0);
   }
 }

/* Location:           E:\SouceCode\recordGame\gdxDemo\libs\domain-1.2.2415.jar
 * Qualified Name:     com.intel.inde.mp.domain.pipeline.DrainRenderCommandHandler
 * JD-Core Version:    0.6.1
 */