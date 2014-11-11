 package com.intel.inde.mp.domain.pipeline;
 
 import com.intel.inde.mp.domain.Command;
 import com.intel.inde.mp.domain.CommandQueue;
 import com.intel.inde.mp.domain.Frame;
 import com.intel.inde.mp.domain.ICommandHandler;
 import com.intel.inde.mp.domain.IFrameAllocator;
 import com.intel.inde.mp.domain.IOutput;
 import com.intel.inde.mp.domain.MultipleMediaSource;
 import com.intel.inde.mp.domain.Plugin;
 
 class OutputFormatChangedHandler
   implements ICommandHandler
 {
   protected IOutput output;
   protected Plugin plugin;
   private IFrameAllocator inputWithAllocator;
 
   public OutputFormatChangedHandler(IOutput output, Plugin plugin, IFrameAllocator frameAllocator)
   {
     this.output = output;
     this.plugin = plugin;
     this.inputWithAllocator = frameAllocator;
   }
 
   public void handle()
   {
     if ((this.output instanceof MultipleMediaSource)) {
       Frame frame = this.inputWithAllocator.findFreeFrame();
 
       if (frame == null) {
         restoreCommands();
         return;
       }
 
       this.plugin.drain(frame.getBufferIndex());
       this.plugin.stop();
       this.plugin.setMediaFormat(this.output.getMediaFormatByType(this.plugin.getMediaFormatType()));
       this.plugin.configure();
       this.plugin.start();
       this.plugin.setTrackId(this.plugin.getTrackId());
 
       MultipleMediaSource multipleMediaSource = (MultipleMediaSource)this.output;
       int trackId = multipleMediaSource.getTrackIdByMediaType(this.plugin.getMediaFormatType());
       multipleMediaSource.selectTrack(trackId);
       multipleMediaSource.setTrackMap(trackId, this.plugin.getTrackId());
       multipleMediaSource.nextFile();
     }
   }
 
   private void restoreCommands() {
     this.output.getOutputCommandQueue().queue(Command.OutputFormatChanged, Integer.valueOf(this.plugin.getTrackId()));
     this.plugin.getInputCommandQueue().clear();
     this.plugin.skipProcessing();
     this.plugin.getInputCommandQueue().queue(Command.NeedData, Integer.valueOf(this.plugin.getTrackId()));
   }
 }

/* Location:           E:\SouceCode\recordGame\gdxDemo\libs\domain-1.2.2415.jar
 * Qualified Name:     com.intel.inde.mp.domain.pipeline.OutputFormatChangedHandler
 * JD-Core Version:    0.6.1
 */