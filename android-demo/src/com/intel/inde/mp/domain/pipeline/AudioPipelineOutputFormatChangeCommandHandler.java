 package com.intel.inde.mp.domain.pipeline;
 
 import com.intel.inde.mp.domain.Frame;
 import com.intel.inde.mp.domain.ICommandHandler;
 import com.intel.inde.mp.domain.MediaCodecPlugin;
 import com.intel.inde.mp.domain.MediaFormat;
 
 class AudioPipelineOutputFormatChangeCommandHandler
   implements ICommandHandler
 {
   private MediaCodecPlugin output;
   private MediaCodecPlugin input;
 
   public AudioPipelineOutputFormatChangeCommandHandler(MediaCodecPlugin output, MediaCodecPlugin input)
   {
     this.output = output;
     this.input = input;
   }
 
   public void handle()
   {
     MediaFormat decoderMediaFormat = this.output.getOutputMediaFormat();
     this.input.setInputMediaFormat(decoderMediaFormat);
     this.input.push(Frame.empty());
   }
 }

/* Location:           E:\SouceCode\recordGame\gdxDemo\libs\domain-1.2.2415.jar
 * Qualified Name:     com.intel.inde.mp.domain.pipeline.AudioPipelineOutputFormatChangeCommandHandler
 * JD-Core Version:    0.6.1
 */