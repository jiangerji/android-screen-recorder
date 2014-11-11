 package com.intel.inde.mp.android;
 
 import com.intel.inde.mp.IVideoEffect;
 import com.intel.inde.mp.domain.Pair;
 import com.intel.inde.mp.domain.Resolution;
 
 public class JNIVideoEffect
   implements IVideoEffect
 {
   private long tag;
 
   public JNIVideoEffect(long tag)
   {
     this.tag = tag;
   }
 
   public Pair<Long, Long> getSegment() {
     return getSegmentJNI(this.tag);
   }
 
   public void setSegment(Pair<Long, Long> segment)
   {
   }
 
   public void start()
   {
     startJNI(this.tag);
   }
 
   public void applyEffect(int inTextureId, long timeProgress, float[] transformMatrix)
   {
     applyEffectJNI(this.tag, inTextureId, timeProgress, transformMatrix);
   }
 
   public void setInputResolution(Resolution resolution)
   {
     setInputResolutionJNI(this.tag, resolution);
   }
 
   public boolean fitToCurrentSurface(boolean should)
   {
     return fitToCurrentSurfaceJNI(this.tag, should);
   }
 
   private native Pair<Long, Long> getSegmentJNI(long paramLong);
 
   private native void startJNI(long paramLong);
 
   private native void applyEffectJNI(long paramLong1, int paramInt, long paramLong2, float[] paramArrayOfFloat);
 
   private native void setInputResolutionJNI(long paramLong, Resolution paramResolution);
 
   private native boolean fitToCurrentSurfaceJNI(long paramLong, boolean paramBoolean);
 }

/* Location:           E:\SouceCode\recordGame\gdxDemo\libs\android-1.2.2415.jar
 * Qualified Name:     com.intel.inde.mp.android.JNIVideoEffect
 * JD-Core Version:    0.6.1
 */