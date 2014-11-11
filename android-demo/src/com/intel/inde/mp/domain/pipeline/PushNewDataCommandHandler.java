 package com.intel.inde.mp.domain.pipeline;
 
 import com.intel.inde.mp.domain.Frame;
 import com.intel.inde.mp.domain.ICommandHandler;
 import com.intel.inde.mp.domain.IOutput;
 import com.intel.inde.mp.domain.Render;
 import java.nio.ByteBuffer;
 
 class PushNewDataCommandHandler
   implements ICommandHandler
 {
   private IOutput output;
   private Render render;
 
   public PushNewDataCommandHandler(IOutput output, Render render)
   {
     this.output = output;
     this.render = render;
   }
 
   public void handle()
   {
     Frame frame = new Frame(ByteBuffer.allocate(1048576), 1048576, 0L, 0, 0, 0);
     this.output.pull(frame);
     this.render.push(frame);
   }
 }

/* Location:           E:\SouceCode\recordGame\gdxDemo\libs\domain-1.2.2415.jar
 * Qualified Name:     com.intel.inde.mp.domain.pipeline.PushNewDataCommandHandler
 * JD-Core Version:    0.6.1
 */