 package com.intel.inde.mp.domain.pipeline;
 
 import com.intel.inde.mp.domain.Frame;
 import com.intel.inde.mp.domain.ICommandHandler;
 import com.intel.inde.mp.domain.IPluginOutput;
 import com.intel.inde.mp.domain.SurfaceRender;
 
 public class PushSurfaceCommandHandlerForSurfaceRender
   implements ICommandHandler
 {
   private IPluginOutput decoder;
   private SurfaceRender render;
 
   public PushSurfaceCommandHandlerForSurfaceRender(IPluginOutput decoder, SurfaceRender render)
   {
     this.decoder = decoder;
     this.render = render;
   }
 
   public void handle() {
     Frame frame = this.decoder.getFrame();
     if (!frame.equals(Frame.EOF())) {
       this.decoder.releaseOutputBuffer(frame.getBufferIndex());
     }
 
     this.render.push(frame);
   }
 }

/* Location:           E:\SouceCode\recordGame\gdxDemo\libs\domain-1.2.2415.jar
 * Qualified Name:     com.intel.inde.mp.domain.pipeline.PushSurfaceCommandHandlerForSurfaceRender
 * JD-Core Version:    0.6.1
 */