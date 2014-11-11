package com.intel.inde.mp.domain;

public abstract interface ICaptureSource extends IPluginOutput
{
  public abstract void setSurfaceSize(int paramInt1, int paramInt2);

  public abstract void beginCaptureFrame();

  public abstract void endCaptureFrame();

  public abstract void addSetSurfaceListener(ISurfaceListener paramISurfaceListener);
}

/* Location:           E:\SouceCode\recordGame\gdxDemo\libs\domain-1.2.2415.jar
 * Qualified Name:     com.intel.inde.mp.domain.ICaptureSource
 * JD-Core Version:    0.6.1
 */