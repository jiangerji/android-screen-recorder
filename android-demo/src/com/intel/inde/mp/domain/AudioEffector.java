 package com.intel.inde.mp.domain;
 
 import com.intel.inde.mp.IAudioEffect;
 import java.io.IOException;
 import java.nio.ByteBuffer;
 import java.util.Iterator;
 import java.util.LinkedList;
 
 public class AudioEffector extends MediaCodecPlugin
 {
   private LinkedList<IAudioEffect> audioEffects = new LinkedList();
 
   private LinkedList<Frame> framesPool = new LinkedList();
   private LinkedList<Frame> framesOutput = new LinkedList();
 
   public AudioEffector(IMediaCodec mediaCodec) {
     super(mediaCodec);
     initInputCommandQueue();
 
     ByteBuffer buffer = ByteBuffer.allocate(24576);
     ByteBuffer buffer1 = ByteBuffer.allocate(24576);
     ByteBuffer buffer2 = ByteBuffer.allocate(24576);
     this.framesPool.add(new Frame(buffer, 24576, 0L, 0, 0, 0));
     this.framesPool.add(new Frame(buffer1, 24576, 0L, 0, 0, 0));
     this.framesPool.add(new Frame(buffer2, 24576, 0L, 0, 0, 0));
   }
 
   public LinkedList<IAudioEffect> getAudioEffects() {
     return this.audioEffects;
   }
 
   protected void initInputCommandQueue()
   {
     feedMeIfNotDraining();
   }
 
   protected void feedMeIfNotDraining()
   {
     if ((this.state != PluginState.Draining) && (this.state != PluginState.Drained))
       getInputCommandQueue().queue(Command.NeedData, Integer.valueOf(getTrackId()));
   }
 
   public void push(Frame frame)
   {
     super.push(frame);
 
     if (!frame.equals(Frame.empty())) {
       applyEffects(frame);
     }
 
     if (this.framesPool.size() > 0) {
       feedMeIfNotDraining();
     }
 
     if ((!frame.equals(Frame.empty())) && (!frame.equals(Frame.EOF())))
       hasData();
   }
 
   private void applyEffects(Frame frame)
   {
     for (IAudioEffect effect : this.audioEffects) {
       Pair segment = effect.getSegment();
 
       if ((segment == null) || ((((Long)segment.left).longValue() <= frame.getSampleTime()) && (((Long)segment.right).longValue() >= frame.getSampleTime())))
       {
         effect.applyEffect(frame.getByteBuffer(), frame.getSampleTime());
         frame.setLength(frame.getByteBuffer().limit());
 
         this.mediaFormat = effect.getMediaFormat();
       }
     }
   }
 
   public void checkIfOutputQueueHasData()
   {
   }
 
   public void releaseOutputBuffer(int outputBufferIndex) {
   }
 
   public void pull(Frame frame) {
   }
 
   public Frame findFreeFrame() {
     if (this.framesPool.size() > 0) {
       Iterator iterator = this.framesPool.iterator();
       Frame frame = (Frame)iterator.next();
       this.framesOutput.add(frame);
       iterator.remove();
       return frame;
     }
     return null;
   }
 
   public Frame getFrame()
   {
     Frame frame = null;
     if (this.framesOutput.size() > 0) {
       Iterator iterator = this.framesOutput.iterator();
       frame = (Frame)iterator.next();
       this.framesPool.add(frame);
       iterator.remove();
     }
 
     if (this.framesPool.size() > 0) {
       feedMeIfNotDraining();
     }
     return frame;
   }
 
   private void outputFormatChanged() {
     getOutputCommandQueue().queue(Command.OutputFormatChanged, Integer.valueOf(0));
   }
 
   public void setInputMediaFormat(MediaFormat mediaFormat)
   {
     this.outputMediaFormat = mediaFormat;
     outputFormatChanged();
   }
 
   public boolean isLastFile()
   {
     return false;
   }
 
   public void start()
   {
     setState(PluginState.Normal);
   }
 
   public void stop()
   {
     setState(PluginState.Paused);
   }
 
   public void setMediaFormat(MediaFormat mediaFormat)
   {
     this.mediaFormat = mediaFormat;
   }
 
   public void configure()
   {
   }
 
   public void setOutputSurface(ISurface surface) {
   }
 
   public ISurface getSurface() {
     return null;
   }
 
   public void waitForSurface(long pts)
   {
   }
 
   public void close() throws IOException
   {
   }
 
   public MediaFormat getOutputMediaFormat()
   {
     return this.mediaFormat;
   }
 }

/* Location:           E:\SouceCode\recordGame\gdxDemo\libs\domain-1.2.2415.jar
 * Qualified Name:     com.intel.inde.mp.domain.AudioEffector
 * JD-Core Version:    0.6.1
 */