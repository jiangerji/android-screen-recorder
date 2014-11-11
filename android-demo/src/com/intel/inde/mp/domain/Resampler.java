 package com.intel.inde.mp.domain;
 
 import com.intel.inde.mp.AudioFormat;
 import java.nio.ByteBuffer;
 
 public class Resampler
 {
   protected int inputChannelCount;
   protected int inputSampleRate;
   protected int targetChannelCount;
   protected int targetSampleRate;
   private boolean configured;
 
   protected void setup()
   {
   }
 
   public Resampler(AudioFormat audioFormat)
   {
     setup();
     setTargetParameters(audioFormat);
   }
 
   public void setTargetParameters(AudioFormat audioFormat)
   {
     int channelCount = audioFormat.getAudioChannelCount();
     int sampleRate = audioFormat.getAudioSampleRateInHz();
 
     if (((channelCount != 1) && (channelCount != 2)) || (!sampleRateSupported(sampleRate))) {
       throw new IllegalArgumentException("Given target audio parameters not supported.");
     }
     if ((this.targetChannelCount != channelCount) || (this.targetSampleRate != sampleRate)) {
       this.targetChannelCount = channelCount;
       this.targetSampleRate = sampleRate;
     }
   }
 
   public void setInputParameters(AudioFormat audioFormat)
   {
     int channelCount = audioFormat.getAudioChannelCount();
     int sampleRate = audioFormat.getAudioSampleRateInHz();
 
     if (((channelCount != 1) && (channelCount != 2)) || (!sampleRateSupported(sampleRate))) {
       throw new IllegalArgumentException("Given input audio parameters not supported.");
     }
     if ((this.inputChannelCount != channelCount) || (this.inputSampleRate != sampleRate)) {
       this.inputChannelCount = channelCount;
       this.inputSampleRate = sampleRate;
 
       allocateInitInternalBuffers();
     }
   }
 
   public boolean resamplingRequired() {
     if ((this.inputChannelCount != this.targetChannelCount) || (this.inputSampleRate != this.targetSampleRate)) {
       return true;
     }
     return false;
   }
 
   public void resampleFrame(Frame frame) {
     if (!this.configured)
       throw new IllegalArgumentException("Resampler not configured.");
   }
 
   public void resampleBuffer(ByteBuffer frameBuffer, int bufferLenght)
   {
     if (!this.configured)
       throw new IllegalArgumentException("Resampler not configured.");
   }
 
   public int getTargetChannelCount()
   {
     return this.targetChannelCount;
   }
 
   public int getTargetSampleRate() {
     return this.targetSampleRate;
   }
 
   public int getInputChannelCount() {
     return this.inputChannelCount;
   }
 
   public int getInputSampleRate() {
     return this.inputSampleRate;
   }
 
   protected void allocateInitInternalBuffers() {
     this.configured = true;
   }
 
   public boolean sampleRateSupported(int sampleRate) {
     for (SampleRate c : SampleRate.values()) {
       if (c.getValue() == sampleRate) {
         return true;
       }
     }
     return false;
   }
 }

/* Location:           E:\SouceCode\recordGame\gdxDemo\libs\domain-1.2.2415.jar
 * Qualified Name:     com.intel.inde.mp.domain.Resampler
 * JD-Core Version:    0.6.1
 */