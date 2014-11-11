 package com.intel.inde.mp.domain;
 
 import java.nio.ByteBuffer;
 
 class EofFrame extends Frame
 {
   public EofFrame()
   {
     super(ByteBuffer.allocate(0), -1, 0L, 0, 4, -1);
   }
 
   public boolean equals(Object o)
   {
     if (this == o) return true;
     if (o == null) return false;
 
     Frame frame = (Frame)o;
     return equals(frame);
   }
 
   public boolean equals(Frame frame) {
     return ((frame.getFlags() & 0x4) != 0) || (frame.getLength() == -1);
   }
 
   public int hashCode()
   {
     return 0;
   }
 }

/* Location:           E:\SouceCode\recordGame\gdxDemo\libs\domain-1.2.2415.jar
 * Qualified Name:     com.intel.inde.mp.domain.EofFrame
 * JD-Core Version:    0.6.1
 */