 package com.intel.inde.mp;
 
 import com.intel.inde.mp.domain.MediaFormat;
 import com.intel.inde.mp.domain.Resolution;
 
 public abstract class VideoFormat extends MediaFormat
 {
   public static final String MIME_TYPE = "video/avc";
   private static final String KEY_BIT_RATE = "bitrate";
   private static final String KEY_COLOR_FORMAT = "color-format";
   private static final String KEY_FRAME_RATE = "frame-rate";
   private static final String KEY_I_FRAME_INTERVAL = "i-frame-interval";
   public static final String KEY_HEIGHT = "height";
   public static final String KEY_WIDTH = "width";
   private static final String NO_INFO_AVAILABLE = "No info available.";
   private String mimeType;
   private int width;
   private int height;
 
   protected void setVideoCodec(String mimeType)
   {
     this.mimeType = mimeType;
   }
 
   public String getVideoCodec()
   {
     return this.mimeType;
   }
 
   public void setVideoFrameSize(int width, int height)
   {
     this.width = width;
     this.height = height;
   }
 
   public Resolution getVideoFrameSize()
   {
     return new Resolution(this.width, this.height);
   }
 
   public int getVideoBitRateInKBytes()
   {
     try
     {
       return getInteger("bitrate") / 1024; } catch (NullPointerException e) {
     }
     throw new RuntimeException("No info available.");
   }
 
   public void setVideoBitRateInKBytes(int bitRate)
   {
     if (this.width * this.height * 30 * 2 * 6.999999999999999E-005D < bitRate) {
       bitRate = (int)(this.width * this.height * 30 * 2 * 6.999999999999999E-005D);
     }
     setInteger("bitrate", bitRate * 1024);
   }
 
   public int getVideoFrameRate()
   {
     try
     {
       return getInteger("frame-rate"); } catch (NullPointerException e) {
     }
     throw new RuntimeException("No info available.");
   }
 
   public void setVideoFrameRate(int bitRate)
   {
     setInteger("frame-rate", bitRate);
   }
 
   public void setVideoIFrameInterval(int iFrameIntervalInSecs)
   {
     setInteger("i-frame-interval", iFrameIntervalInSecs);
   }
 
   public int getVideoIFrameInterval()
   {
     try
     {
       return getInteger("i-frame-interval"); } catch (NullPointerException e) {
     }
     throw new RuntimeException("No info available.");
   }
 
   public void setColorFormat(int colorFormat)
   {
     setInteger("color-format", colorFormat);
   }
 }

/* Location:           E:\SouceCode\recordGame\gdxDemo\libs\domain-1.2.2415.jar
 * Qualified Name:     com.intel.inde.mp.VideoFormat
 * JD-Core Version:    0.6.1
 */