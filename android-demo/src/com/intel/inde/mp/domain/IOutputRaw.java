package com.intel.inde.mp.domain;

public abstract interface IOutputRaw
{
  public abstract boolean canConnectFirst(IInputRaw paramIInputRaw);

  public abstract CommandQueue getOutputCommandQueue();

  public abstract void fillCommandQueues();
}

/* Location:           E:\SouceCode\recordGame\gdxDemo\libs\domain-1.2.2415.jar
 * Qualified Name:     com.intel.inde.mp.domain.IOutputRaw
 * JD-Core Version:    0.6.1
 */