 package com.intel.inde.mp.domain;
 
 import com.intel.inde.mp.domain.pipeline.IOnStopListener;
 
 public abstract class Render extends Input
 {
   protected IOnStopListener onStopListener;
 
   public abstract int getTrackIdByMediaFormat(MediaFormat paramMediaFormat);
 
   public abstract void start();
 
   public void addOnStopListener(IOnStopListener onStopListener)
   {
     this.onStopListener = onStopListener;
   }
 }

/* Location:           E:\SouceCode\recordGame\gdxDemo\libs\domain-1.2.2415.jar
 * Qualified Name:     com.intel.inde.mp.domain.Render
 * JD-Core Version:    0.6.1
 */