 package com.intel.inde.mp.domain;
 
 import com.intel.inde.mp.VideoFormat;
 
 public class VideoDecoder extends Decoder
   implements IVideoOutput
 {
   public VideoDecoder(IMediaCodec mediaCodec)
   {
     super(mediaCodec, MediaFormatType.VIDEO);
   }
 
   public void drain(int bufferIndex)
   {
     if (this.state != PluginState.Normal) return;
 
     super.drain(bufferIndex);
     this.mediaCodec.signalEndOfInputStream();
   }
 
   public void push(Frame frame)
   {
     if ((this.state == PluginState.Draining) || (this.state == PluginState.Drained)) {
       throw new RuntimeException("Out of order operation.");
     }
     super.push(frame);
   }
 
   public void stop()
   {
     super.stop();
     recreate();
   }
 
   protected void initInputCommandQueue()
   {
     getInputCommandQueue().queue(Command.NeedInputFormat, Integer.valueOf(getTrackId()));
   }
 
   public Resolution getOutputResolution() {
     return ((VideoFormat)getOutputMediaFormat()).getVideoFrameSize();
   }
 }

/* Location:           E:\SouceCode\recordGame\gdxDemo\libs\domain-1.2.2415.jar
 * Qualified Name:     com.intel.inde.mp.domain.VideoDecoder
 * JD-Core Version:    0.6.1
 */