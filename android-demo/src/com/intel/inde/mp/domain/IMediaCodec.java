 package com.intel.inde.mp.domain;
 
 import java.nio.ByteBuffer;
 
 public abstract interface IMediaCodec
 {
   public static final int CONFIGURE_FLAG_ENCODE = 1;
   public static final int BUFFER_FLAG_END_OF_STREAM = 4;
   public static final int BUFFER_FLAG_CODEC_CONFIG = 2;
   public static final int INFO_OUTPUT_BUFFERS_CHANGED = -3;
   public static final int INFO_OUTPUT_FORMAT_CHANGED = -2;
   public static final int INFO_TRY_AGAIN_LATER = -1;
 
   public abstract void configure(MediaFormat paramMediaFormat, ISurfaceWrapper paramISurfaceWrapper, int paramInt);
 
   public abstract void start();
 
   public abstract void releaseOutputBuffer(int paramInt, boolean paramBoolean);
 
   public abstract ISurface createInputSurface();
 
   public abstract ISurface createSimpleInputSurface(IEglContext paramIEglContext);
 
   public abstract ByteBuffer[] getInputBuffers();
 
   public abstract ByteBuffer[] getOutputBuffers();
 
   public abstract void queueInputBuffer(int paramInt1, int paramInt2, int paramInt3, long paramLong, int paramInt4);
 
   public abstract int dequeueInputBuffer(long paramLong);
 
   public abstract int dequeueOutputBuffer(BufferInfo paramBufferInfo, long paramLong);
 
   public abstract MediaFormat getOutputFormat();
 
   public abstract void signalEndOfInputStream();
 
   public abstract void stop();
 
   public abstract void release();
 
   public abstract void recreate();
 
   public static class BufferInfo
   {
     public int flags;
     public int offset;
     public long presentationTimeUs;
     public int size;
 
     public boolean isEof()
     {
       return (this.flags & 0x4) != 0;
     }
 
     public boolean equals(Object o)
     {
       if (this == o) return true;
       if ((o == null) || (getClass() != o.getClass())) return false;
 
       BufferInfo that = (BufferInfo)o;
 
       if (this.flags != that.flags) return false;
       if (this.offset != that.offset) return false;
       if (this.presentationTimeUs != that.presentationTimeUs) return false;
       if (this.size != that.size) return false;
 
       return true;
     }
 
     public int hashCode()
     {
       int result = this.flags;
       result = 31 * result + this.offset;
       result = 31 * result + (int)(this.presentationTimeUs ^ this.presentationTimeUs >>> 32);
       result = 31 * result + this.size;
       return result;
     }
   }
 }

/* Location:           E:\SouceCode\recordGame\gdxDemo\libs\domain-1.2.2415.jar
 * Qualified Name:     com.intel.inde.mp.domain.IMediaCodec
 * JD-Core Version:    0.6.1
 */