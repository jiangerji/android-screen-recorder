package com.intel.inde.mp;

import com.intel.inde.mp.domain.Pair;
import com.intel.inde.mp.domain.Resolution;

public abstract interface IVideoEffect
{
  public abstract void setSegment(Pair<Long, Long> paramPair);

  public abstract Pair<Long, Long> getSegment();

  public abstract void start();

  public abstract void applyEffect(int paramInt, long paramLong, float[] paramArrayOfFloat);

  public abstract void setInputResolution(Resolution paramResolution);

  public abstract boolean fitToCurrentSurface(boolean paramBoolean);
}

/* Location:           E:\SouceCode\recordGame\gdxDemo\libs\domain-1.2.2415.jar
 * Qualified Name:     com.intel.inde.mp.IVideoEffect
 * JD-Core Version:    0.6.1
 */