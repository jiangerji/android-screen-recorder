package com.intel.inde.mp;

public abstract interface IProgressListener
{
  public abstract void onMediaStart();

  public abstract void onMediaProgress(float paramFloat);

  public abstract void onMediaDone();

  public abstract void onMediaPause();

  public abstract void onMediaStop();

  public abstract void onError(Exception paramException);
}

/* Location:           E:\SouceCode\recordGame\gdxDemo\libs\domain-1.2.2415.jar
 * Qualified Name:     com.intel.inde.mp.IProgressListener
 * JD-Core Version:    0.6.1
 */