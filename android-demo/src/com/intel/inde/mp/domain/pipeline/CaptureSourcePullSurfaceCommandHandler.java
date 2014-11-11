 package com.intel.inde.mp.domain.pipeline;
 
 import com.intel.inde.mp.domain.Frame;
 import com.intel.inde.mp.domain.ICameraSource;
 import com.intel.inde.mp.domain.ICommandHandler;
 import com.intel.inde.mp.domain.MediaCodecPlugin;
 
 class CaptureSourcePullSurfaceCommandHandler
   implements ICommandHandler
 {
   private ICameraSource source;
   private MediaCodecPlugin plugin;
 
   public CaptureSourcePullSurfaceCommandHandler(ICameraSource source, MediaCodecPlugin plugin)
   {
     this.source = source;
     this.plugin = plugin;
   }
 
   public void handle()
   {
     Frame frame = this.source.getFrame();
 
     if ((!frame.equals(Frame.EOF())) && (0 != frame.getLength())) {
       this.plugin.notifySurfaceReady(this.source.getSurface());
     }
     this.plugin.push(frame);
     this.plugin.checkIfOutputQueueHasData();
   }
 }

/* Location:           E:\SouceCode\recordGame\gdxDemo\libs\domain-1.2.2415.jar
 * Qualified Name:     com.intel.inde.mp.domain.pipeline.CaptureSourcePullSurfaceCommandHandler
 * JD-Core Version:    0.6.1
 */