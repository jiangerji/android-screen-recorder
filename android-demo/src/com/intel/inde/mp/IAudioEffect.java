package com.intel.inde.mp;

import com.intel.inde.mp.domain.MediaFormat;
import com.intel.inde.mp.domain.Pair;
import java.nio.ByteBuffer;

public abstract interface IAudioEffect
{
  public abstract void setSegment(Pair<Long, Long> paramPair);

  public abstract Pair<Long, Long> getSegment();

  public abstract void applyEffect(ByteBuffer paramByteBuffer, long paramLong);

  public abstract MediaFormat getMediaFormat();
}

/* Location:           E:\SouceCode\recordGame\gdxDemo\libs\domain-1.2.2415.jar
 * Qualified Name:     com.intel.inde.mp.IAudioEffect
 * JD-Core Version:    0.6.1
 */