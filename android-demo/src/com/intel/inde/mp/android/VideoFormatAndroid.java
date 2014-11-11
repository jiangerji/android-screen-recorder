 package com.intel.inde.mp.android;
 
 import android.media.MediaFormat;
 import com.intel.inde.mp.VideoFormat;
 import java.nio.ByteBuffer;
 
 public class VideoFormatAndroid extends VideoFormat
 {
   private MediaFormat mediaFormat;
 
   VideoFormatAndroid(MediaFormat mediaFormat)
   {
     this.mediaFormat = mediaFormat;
     setVideoFrameSize(mediaFormat.getInteger("width"), mediaFormat.getInteger("height"));
     setVideoCodec(mediaFormat.getString("mime"));
   }
 
   public VideoFormatAndroid(String mimeType, int width, int height) {
     if ((width > 1280) || (height > 1280)) {
       if (width > height) {
         width = 1280;
         height = 720;
       } else {
         width = 720;
         height = 1280;
       }
     }
     this.mediaFormat = MediaFormat.createVideoFormat(mimeType, width, height);
     setVideoFrameSize(width, height);
     setVideoCodec(mimeType);
   }
 
   public MediaFormat getNativeFormat() {
     return this.mediaFormat;
   }
 
   public ByteBuffer getByteBuffer(String key)
   {
     return this.mediaFormat.getByteBuffer(key);
   }
 
   public void setInteger(String key, int value)
   {
     this.mediaFormat.setInteger(key, value);
   }
 
   public int getInteger(String key)
   {
     return this.mediaFormat.getInteger(key);
   }
 
   protected long getLong(String key)
   {
     return this.mediaFormat.getLong(key);
   }
 
   protected String getString(String key)
   {
     return this.mediaFormat.getString(key);
   }
 }

/* Location:           E:\SouceCode\recordGame\gdxDemo\libs\android-1.2.2415.jar
 * Qualified Name:     com.intel.inde.mp.android.VideoFormatAndroid
 * JD-Core Version:    0.6.1
 */