 package com.intel.inde.mp.domain;
 
 import java.util.Queue;
 
 public class AudioDecoder extends Decoder
 {
   public AudioDecoder(IMediaCodec mediaCodec)
   {
     super(mediaCodec, MediaFormatType.AUDIO);
   }
 
   protected int getOutputBufferIndex()
   {
     IMediaCodec.BufferInfo bufferInfo = new IMediaCodec.BufferInfo();
     int outputBufferIndex = this.mediaCodec.dequeueOutputBuffer(bufferInfo, this.timeout);
 
     if ((this.state == PluginState.Draining) && (outputBufferIndex == -1)) {
       this.state = PluginState.Drained;
     }
 
     if ((outputBufferIndex != -1) && (outputBufferIndex != -2)) {
       this.outputBufferIndexes.add(Integer.valueOf(outputBufferIndex));
       this.outputBufferInfos.add(bufferInfo);
     }
 
     if ((outputBufferIndex >= 0) && (!bufferInfo.isEof())) {
       hasData();
     }
 
     if (outputBufferIndex == -2) {
       this.outputMediaFormat = this.mediaCodec.getOutputFormat();
       outputFormatChanged();
     }
 
     return outputBufferIndex;
   }
 
   private void outputFormatChanged() {
     getOutputCommandQueue().queue(Command.OutputFormatChanged, Integer.valueOf(0));
   }
 
   public void stop()
   {
     super.stop();
     recreate();
   }
 
   public MediaFormat getOutputMediaFormat()
   {
     return this.outputMediaFormat;
   }
 }

/* Location:           E:\SouceCode\recordGame\gdxDemo\libs\domain-1.2.2415.jar
 * Qualified Name:     com.intel.inde.mp.domain.AudioDecoder
 * JD-Core Version:    0.6.1
 */