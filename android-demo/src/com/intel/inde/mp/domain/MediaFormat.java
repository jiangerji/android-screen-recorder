 package com.intel.inde.mp.domain;
 
 import java.nio.ByteBuffer;
 
 public abstract class MediaFormat
 {
   protected static final String KEY_MIME = "mime";
   protected static final String KEY_DURATION = "durationUs";
 
   public String getMimeType()
   {
     return getString("mime");
   }
 
   public long getDuration() {
     return getLong("durationUs");
   }
 
   public abstract ByteBuffer getByteBuffer(String paramString);
 
   protected abstract void setInteger(String paramString, int paramInt);
 
   protected abstract int getInteger(String paramString);
 
   protected abstract long getLong(String paramString);
 
   protected abstract String getString(String paramString);
 }

/* Location:           E:\SouceCode\recordGame\gdxDemo\libs\domain-1.2.2415.jar
 * Qualified Name:     com.intel.inde.mp.domain.MediaFormat
 * JD-Core Version:    0.6.1
 */