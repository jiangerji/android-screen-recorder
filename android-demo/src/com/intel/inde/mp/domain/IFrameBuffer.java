package com.intel.inde.mp.domain;

public abstract interface IFrameBuffer
{
  public abstract void setResolution(Resolution paramResolution);

  public abstract int getTextureId();

  public abstract void release();

  public abstract void bind();

  public abstract void unbind();
}

/* Location:           E:\SouceCode\recordGame\gdxDemo\libs\domain-1.2.2415.jar
 * Qualified Name:     com.intel.inde.mp.domain.IFrameBuffer
 * JD-Core Version:    0.6.1
 */