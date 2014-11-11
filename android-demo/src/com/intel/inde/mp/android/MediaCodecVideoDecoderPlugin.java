 package com.intel.inde.mp.android;
 
 import android.media.MediaCodec;
 import android.view.Surface;
 import com.intel.inde.mp.domain.ISurfaceWrapper;
 import com.intel.inde.mp.domain.MediaFormat;
 
 public class MediaCodecVideoDecoderPlugin extends MediaCodecDecoderPlugin
 {
   String mime = "";
 
   public MediaCodecVideoDecoderPlugin(MediaFormat format) { super(getMimeTypeFor(format));
     this.mime = getMimeTypeFor(format);
   }
 
   public void configure(MediaFormat mediaFormat, ISurfaceWrapper surface, int flags)
   {
     Surface surface1 = null;
     if (surface != null) {
       surface1 = (Surface)((SurfaceWrapper)surface).getNativeObject();
     }
     this.mediaCodec.configure(MediaFormatTranslator.from(mediaFormat), surface1, null, flags);
   }
 
   public void release()
   {
     this.mediaCodec.release();
   }
 
   public void recreate()
   {
     release();
     this.mediaCodec = MediaCodec.createDecoderByType(this.mime);
   }
 
   private static String getMimeTypeFor(MediaFormat format) {
     return ((VideoFormatAndroid)format).getString("mime");
   }
 }

/* Location:           E:\SouceCode\recordGame\gdxDemo\libs\android-1.2.2415.jar
 * Qualified Name:     com.intel.inde.mp.android.MediaCodecVideoDecoderPlugin
 * JD-Core Version:    0.6.1
 */