package com.intel.inde.mp.domain;

public abstract interface ISurface
{
  public abstract void awaitNewImage();

  public abstract void drawImage();

  public abstract void setPresentationTime(long paramLong);

  public abstract void swapBuffers();

  public abstract void makeCurrent();

  public abstract ISurfaceWrapper getCleanObject();

  public abstract void setProjectionMatrix(float[] paramArrayOfFloat);

  public abstract void setViewport();

  public abstract void setInputSize(int paramInt1, int paramInt2);

  public abstract Resolution getInputSize();

  public abstract void release();
}

/* Location:           E:\SouceCode\recordGame\gdxDemo\libs\domain-1.2.2415.jar
 * Qualified Name:     com.intel.inde.mp.domain.ISurface
 * JD-Core Version:    0.6.1
 */