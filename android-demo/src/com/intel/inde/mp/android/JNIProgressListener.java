 package com.intel.inde.mp.android;
 
 import com.intel.inde.mp.IProgressListener;
 
 public class JNIProgressListener
   implements IProgressListener
 {
   long TAG;
 
   public JNIProgressListener(long TAG)
   {
     this.TAG = TAG;
   }
 
   public void onMediaStart()
   {
     onMediaStartJNI(this.TAG);
   }
 
   public void onMediaProgress(float progress)
   {
     onMediaProgressJNI(this.TAG, progress);
   }
 
   public void onMediaDone()
   {
     onMediaDoneJNI(this.TAG);
   }
 
   public void onMediaPause()
   {
     onMediaPauseJNI(this.TAG);
   }
 
   public void onMediaStop()
   {
     onMediaStopJNI(this.TAG);
   }
 
   public void onError(Exception exception)
   {
     onErrorJNI(this.TAG, exception.toString());
   }
 
   private native void onMediaStartJNI(long paramLong);
 
   private native void onMediaProgressJNI(long paramLong, float paramFloat);
 
   private native void onMediaDoneJNI(long paramLong);
 
   private native void onMediaPauseJNI(long paramLong);
 
   private native void onMediaStopJNI(long paramLong);
 
   private native void onErrorJNI(long paramLong, String paramString);
 }

/* Location:           E:\SouceCode\recordGame\gdxDemo\libs\android-1.2.2415.jar
 * Qualified Name:     com.intel.inde.mp.android.JNIProgressListener
 * JD-Core Version:    0.6.1
 */