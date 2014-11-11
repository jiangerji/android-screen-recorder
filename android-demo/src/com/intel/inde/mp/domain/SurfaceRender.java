package com.intel.inde.mp.domain;

public abstract class SurfaceRender extends Render
{
  public abstract void onSurfaceAvailable(IOnSurfaceReady paramIOnSurfaceReady);

  public abstract ISurface getSurface();
}

/* Location:           E:\SouceCode\recordGame\gdxDemo\libs\domain-1.2.2415.jar
 * Qualified Name:     com.intel.inde.mp.domain.SurfaceRender
 * JD-Core Version:    0.6.1
 */