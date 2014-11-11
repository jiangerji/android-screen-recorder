 package com.intel.inde.mp.domain;
 
 import java.nio.ByteBuffer;
 import java.util.Queue;
 
 abstract class Decoder extends MediaCodecPlugin
   implements IFrameAllocator, ITransform
 {
   private final MediaFormatType mediaFormatType;
   private ISurface outputSurface;
   private ISurfaceWrapper clearOutputSurface;
 
   public Decoder(IMediaCodec mediaCodec, MediaFormatType mediaFormatType)
   {
     super(mediaCodec);
     this.mediaFormatType = mediaFormatType;
   }
 
   public void setMediaFormat(MediaFormat mediaFormat)
   {
     this.mediaFormat = mediaFormat;
   }
 
   public void setOutputSurface(ISurface surface)
   {
     this.outputSurface = surface;
     this.clearOutputSurface = surface.getCleanObject();
   }
 
   public void setOutputSurface(ISurfaceWrapper surface) {
     this.outputSurface = null;
     this.clearOutputSurface = surface;
   }
 
   public void push(Frame frame)
   {
     super.push(frame);
 
     this.mediaCodec.queueInputBuffer(frame.getBufferIndex(), 0, frame.getLength(), frame.getSampleTime(), frame.getFlags());
     getOutputBufferIndex();
     feedMeIfNotDraining();
   }
 
   public void pull(Frame frame)
   {
     IMediaCodec.BufferInfo info = new IMediaCodec.BufferInfo();
     int outputBufferIndex = this.mediaCodec.dequeueOutputBuffer(info, this.timeout);
 
     if (outputBufferIndex >= 0) {
       ByteBuffer[] buffers = this.mediaCodec.getOutputBuffers();
 
       frame.setSampleTime(info.presentationTimeUs);
       frame.setFlags(info.flags);
       frame.setLength(info.size);
 
       ByteBuffer fromByteBuffer = buffers[outputBufferIndex].duplicate();
       fromByteBuffer.position(0);
 
       if (frame.getLength() >= 0) {
         fromByteBuffer.limit(frame.getLength());
       }
 
       frame.getByteBuffer().position(0);
       frame.getByteBuffer().put(buffers[outputBufferIndex]);
       this.mediaCodec.releaseOutputBuffer(outputBufferIndex, false);
     }
     else if ((outputBufferIndex == -3) && 
       (outputBufferIndex != -2));
   }
 
   public boolean isLastFile()
   {
     return false;
   }
 
   public void waitForSurface(long pts)
   {
     this.outputSurface.awaitNewImage();
     this.outputSurface.drawImage();
     this.outputSurface.setPresentationTime(pts * 1000L);
   }
 
   public void releaseOutputBuffer(int outputBufferIndex)
   {
     boolean doRender = false;
     if (this.clearOutputSurface != null) {
       doRender = true;
     }
     this.mediaCodec.releaseOutputBuffer(outputBufferIndex, doRender);
   }
 
   public void stop()
   {
     super.stop();
     this.outputBufferInfos.clear();
     this.outputBufferIndexes.clear();
     this.inputBufferIndexes.clear();
     getOutputCommandQueue().clear();
   }
 
   public void configure()
   {
     this.mediaCodec.configure(this.mediaFormat, this.clearOutputSurface, 0);
   }
 
   public ISurface getSurface()
   {
     return this.outputSurface;
   }
 
   public void drain(int bufferIndex)
   {
     super.drain(bufferIndex);
     this.mediaCodec.queueInputBuffer(bufferIndex, 0, 0, 0L, 4);
   }
 
   public MediaFormatType getMediaFormatType()
   {
     return this.mediaFormatType;
   }
 
   public void recreate()
   {
     this.mediaCodec.recreate();
   }
 
   protected void hasData()
   {
     super.hasData();
     getOutputCommandQueue().queue(Command.NextPair, Integer.valueOf(0));
   }
 }

/* Location:           E:\SouceCode\recordGame\gdxDemo\libs\domain-1.2.2415.jar
 * Qualified Name:     com.intel.inde.mp.domain.Decoder
 * JD-Core Version:    0.6.1
 */