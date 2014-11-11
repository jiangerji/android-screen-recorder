 package com.intel.inde.mp.android;
 
 import android.media.MediaCodec;
 import com.intel.inde.mp.domain.ISurfaceWrapper;
 import com.intel.inde.mp.domain.MediaFormat;
 
 public class MediaCodecAudioDecoderPlugin extends MediaCodecDecoderPlugin
 {
   public MediaCodecAudioDecoderPlugin()
   {
     super("audio/mp4a-latm");
   }
 
   public void configure(MediaFormat mediaFormat, ISurfaceWrapper surface, int flags)
   {
     this.mediaCodec.configure(MediaFormatTranslator.from(mediaFormat), null, null, flags);
   }
 
   public void release()
   {
     this.mediaCodec.release();
   }
 
   public void recreate()
   {
     release();
     this.mediaCodec = MediaCodec.createDecoderByType("audio/mp4a-latm");
   }
 }

/* Location:           E:\SouceCode\recordGame\gdxDemo\libs\android-1.2.2415.jar
 * Qualified Name:     com.intel.inde.mp.android.MediaCodecAudioDecoderPlugin
 * JD-Core Version:    0.6.1
 */