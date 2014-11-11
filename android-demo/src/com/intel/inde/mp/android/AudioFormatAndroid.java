 package com.intel.inde.mp.android;
 
 import android.media.MediaFormat;
 import com.intel.inde.mp.AudioFormat;
 import java.nio.ByteBuffer;
 
 public class AudioFormatAndroid extends AudioFormat
 {
   private MediaFormat mediaFormat;
 
   AudioFormatAndroid(MediaFormat mediaFormat)
   {
     this.mediaFormat = mediaFormat;
     setAudioCodec(mediaFormat.getString("mime"));
   }
 
   public AudioFormatAndroid(String mimeType, int sampleRate, int channelCount) {
     this.mediaFormat = MediaFormat.createAudioFormat(mimeType, sampleRate, channelCount);
     setAudioCodec(mimeType);
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
 * Qualified Name:     com.intel.inde.mp.android.AudioFormatAndroid
 * JD-Core Version:    0.6.1
 */