 package com.intel.inde.mp.domain;
 
 public enum SampleRate
 {
   SAMPLE_RATE_8000(8000), 
   SAMPLE_RATE_16000(16000), 
   SAMPLE_RATE_24000(24000), 
   SAMPLE_RATE_22050(22050), 
   SAMPLE_RATE_32000(32000), 
   SAMPLE_RATE_44100(44100), 
   SAMPLE_RATE_48000(48000);
 
   private final int sampleRate;
 
   private SampleRate(int sampleRate) {
     this.sampleRate = sampleRate;
   }
 
   public int getValue() {
     return this.sampleRate;
   }
 }

/* Location:           E:\SouceCode\recordGame\gdxDemo\libs\domain-1.2.2415.jar
 * Qualified Name:     com.intel.inde.mp.domain.SampleRate
 * JD-Core Version:    0.6.1
 */