 package com.intel.inde.mp.domain;
 
 import java.nio.ByteBuffer;
 
 public class Frame
 {
   private static final Frame eofFrame = new EofFrame();
   private static final Frame emptyFrame = new Frame(null, 0, 0L, 0, 0, 0);
   private final IMediaCodec.BufferInfo bufferInfo = new IMediaCodec.BufferInfo();
   protected ByteBuffer byteBuffer;
   protected int trackId;
   private int bufferIndex;
 
   public Frame(ByteBuffer byteBuffer, int length, long sampleTime, int bufferIndex, int flags, int trackId)
   {
     this.byteBuffer = byteBuffer;
     this.trackId = trackId;
     this.bufferInfo.flags = flags;
     this.bufferInfo.presentationTimeUs = sampleTime;
     this.bufferIndex = bufferIndex;
     this.bufferInfo.size = length;
   }
 
   public void set(ByteBuffer byteBuffer, int length, long sampleTime, int bufferIndex, int flags, int trackId)
   {
     this.byteBuffer = byteBuffer;
     this.trackId = trackId;
     this.bufferInfo.flags = flags;
     this.bufferInfo.presentationTimeUs = sampleTime;
     this.bufferIndex = bufferIndex;
     this.bufferInfo.size = length;
   }
 
   public ByteBuffer getByteBuffer() {
     return this.byteBuffer;
   }
 
   public int getLength() {
     return this.bufferInfo.size;
   }
 
   public void setLength(int length) {
     this.bufferInfo.size = length;
   }
 
   public long getSampleTime() {
     return this.bufferInfo.presentationTimeUs;
   }
 
   public void setSampleTime(long sampleTime) {
     this.bufferInfo.presentationTimeUs = sampleTime;
   }
 
   public int getTrackId() {
     return this.trackId;
   }
 
   public void setTrackId(int trackId) {
     this.trackId = trackId;
   }
 
   public static Frame EOF() {
     return eofFrame;
   }
 
   public static Frame empty()
   {
     return emptyFrame;
   }
 
   public void copyInfoFrom(Frame frame) {
     copyBufferInfoFrom(frame);
   }
 
   public void copyDataFrom(Frame frame) {
     copyBufferInfoFrom(frame);
 
     ByteBuffer fromByteBuffer = frame.getByteBuffer().duplicate();
     fromByteBuffer.rewind();
     if (frame.getLength() >= 0) {
       fromByteBuffer.limit(frame.getLength());
     }
 
     this.byteBuffer.rewind();
     this.byteBuffer.put(fromByteBuffer);
   }
 
   private void copyBufferInfoFrom(Frame frame) {
     this.bufferInfo.size = frame.getLength();
     this.bufferInfo.presentationTimeUs = frame.getSampleTime();
     this.bufferInfo.flags = frame.getFlags();
     this.trackId = frame.getTrackId();
   }
 
   public boolean equals(Object o)
   {
     if (this == o) return true;
     if (!(o instanceof Frame)) return false;
 
     Frame frame = (Frame)o;
 
     return equals(frame);
   }
 
   private boolean equals(Frame frame) {
     if ((frame instanceof EofFrame)) return ((EofFrame)frame).equals(this);
 
     if ((this.bufferInfo.size == 0) && (frame.bufferInfo.size == 0)) return true;
     if (this.bufferInfo.size != frame.bufferInfo.size) return false;
     if (this.bufferInfo.presentationTimeUs != frame.bufferInfo.presentationTimeUs) return false;
     if (!this.byteBuffer.equals(frame.byteBuffer)) return false;
     if (this.trackId != frame.trackId) return false;
 
     return true;
   }
 
   public int hashCode()
   {
     int result = this.bufferInfo.hashCode();
     if (this.byteBuffer != null) result = 31 * result + this.byteBuffer.hashCode();
     result = 31 * result + this.trackId;
     result = 31 * result + this.bufferIndex;
     return result;
   }
 
   public int getBufferIndex() {
     return this.bufferIndex;
   }
 
   public int getFlags() {
     return this.bufferInfo.flags;
   }
 
   public void setFlags(int flags) {
     this.bufferInfo.flags = flags;
   }
 }

/* Location:           E:\SouceCode\recordGame\gdxDemo\libs\domain-1.2.2415.jar
 * Qualified Name:     com.intel.inde.mp.domain.Frame
 * JD-Core Version:    0.6.1
 */