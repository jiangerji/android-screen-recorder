 package com.intel.inde.mp.domain;
 
 public class ProgressTracker
 {
   private float finish = 0.0F;
   private float currentProgress = 0.0F;
 
   public float getProgress() {
     return this.currentProgress / this.finish;
   }
 
   public void setFinish(float finish) {
     this.finish = finish;
   }
 
   public void track(float currentProgress) {
     if (currentProgress > this.currentProgress)
       this.currentProgress = currentProgress;
   }
 }

/* Location:           E:\SouceCode\recordGame\gdxDemo\libs\domain-1.2.2415.jar
 * Qualified Name:     com.intel.inde.mp.domain.ProgressTracker
 * JD-Core Version:    0.6.1
 */