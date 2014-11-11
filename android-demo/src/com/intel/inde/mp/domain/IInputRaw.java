package com.intel.inde.mp.domain;

public abstract interface IInputRaw
{
  public abstract boolean canConnectFirst(IOutputRaw paramIOutputRaw);

  public abstract CommandQueue getInputCommandQueue();

  public abstract void fillCommandQueues();
}

/* Location:           E:\SouceCode\recordGame\gdxDemo\libs\domain-1.2.2415.jar
 * Qualified Name:     com.intel.inde.mp.domain.IInputRaw
 * JD-Core Version:    0.6.1
 */