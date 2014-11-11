 package com.intel.inde.mp.android;
 
 import android.media.MediaFormat;
 import com.intel.inde.mp.domain.IMediaFormatWrapper;
 import com.intel.inde.mp.domain.Wrapper;
 
 public class MediaFormatWrapper extends Wrapper<MediaFormat>
   implements IMediaFormatWrapper
 {
   public MediaFormatWrapper(MediaFormat mediaFormat)
   {
     super(mediaFormat);
   }
 }

/* Location:           E:\SouceCode\recordGame\gdxDemo\libs\android-1.2.2415.jar
 * Qualified Name:     com.intel.inde.mp.android.MediaFormatWrapper
 * JD-Core Version:    0.6.1
 */