package com.intel.inde.mp.domain;

import java.io.Closeable;

public abstract interface IInput extends IInputRaw, Closeable
{
  public abstract void push(Frame paramFrame);

  public abstract void drain(int paramInt);

  public abstract void setMediaFormat(MediaFormat paramMediaFormat);

  public abstract void skipProcessing();

  public abstract int getTrackId();

  public abstract void setTrackId(int paramInt);
}

/* Location:           E:\SouceCode\recordGame\gdxDemo\libs\domain-1.2.2415.jar
 * Qualified Name:     com.intel.inde.mp.domain.IInput
 * JD-Core Version:    0.6.1
 */