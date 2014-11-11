package com.intel.inde.mp.domain;

import java.nio.ByteBuffer;

public abstract interface IMediaMuxer
{
  public abstract int addTrack(MediaFormat paramMediaFormat);

  public abstract void release();

  public abstract void setOrientationHint(int paramInt);

  public abstract void start();

  public abstract void stop();

  public abstract void writeSampleData(int paramInt, ByteBuffer paramByteBuffer, IMediaCodec.BufferInfo paramBufferInfo);
}

/* Location:           E:\SouceCode\recordGame\gdxDemo\libs\domain-1.2.2415.jar
 * Qualified Name:     com.intel.inde.mp.domain.IMediaMuxer
 * JD-Core Version:    0.6.1
 */