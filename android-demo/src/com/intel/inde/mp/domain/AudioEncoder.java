 package com.intel.inde.mp.domain;
 
 import com.intel.inde.mp.AudioFormat;
 
 public class AudioEncoder extends Encoder
 {
   private Resampler resampler = null;
   private AudioFormat inputAudioFormat;
 
   public AudioEncoder(IMediaCodec mediaCodec)
   {
     super(mediaCodec);
   }
 
   public void setMediaFormat(MediaFormat mediaFormat)
   {
     this.mediaFormat = mediaFormat;
   }
 
   public void setInputMediaFormat(MediaFormat mediaFormat)
   {
     this.inputAudioFormat = ((AudioFormat)mediaFormat);
     this.resampler.setInputParameters(this.inputAudioFormat);
   }
 
   protected void initInputCommandQueue()
   {
   }
 
   public boolean isLastFile() {
     return true;
   }
 
   public void setOutputSurface(ISurface surface)
   {
   }
 
   public void waitForSurface(long pts) {
   }
 
   public void drain(int bufferIndex) {
     if (this.state != PluginState.Normal) return;
 
     super.drain(bufferIndex);
   }
 
   public void push(Frame frame)
   {
     if (frame.equals(Frame.EOF())) {
       this.mediaCodec.queueInputBuffer(frame.getBufferIndex(), 0, 0, frame.getSampleTime(), frame.getFlags());
     }
     else if (!frame.equals(Frame.empty())) {
       if (this.resampler != null) resampleAudioFrame(frame);
       this.mediaCodec.queueInputBuffer(frame.getBufferIndex(), 0, frame.getLength(), frame.getSampleTime(), 0);
       checkIfOutputQueueHasData();
     }
 
     super.push(frame);
   }
 
   public int getSampleRate() {
     return getAudioFormat().getAudioSampleRateInHz();
   }
 
   public int getChannelCount() {
     return getAudioFormat().getAudioChannelCount();
   }
 
   public int getBitRate() {
     return getAudioFormat().getAudioBitrateInBytes();
   }
 
   public void setSampleRate(int sampleRateInHz) {
     getAudioFormat().setAudioSampleRateInHz(sampleRateInHz);
   }
 
   public void setChannelCount(int channelCount) {
     getAudioFormat().setAudioChannelCount(channelCount);
   }
 
   private AudioFormat getAudioFormat() {
     return (AudioFormat)this.mediaFormat;
   }
 
   public void resampleAudioFrame(Frame frame) {
     this.resampler.resampleFrame(frame);
   }
 
   public void addResampler(Resampler resampler) {
     this.mediaFormat.setInteger("max-input-size", 49152);
     this.resampler = resampler;
   }
 }

/* Location:           E:\SouceCode\recordGame\gdxDemo\libs\domain-1.2.2415.jar
 * Qualified Name:     com.intel.inde.mp.domain.AudioEncoder
 * JD-Core Version:    0.6.1
 */