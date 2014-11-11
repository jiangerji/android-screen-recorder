package com.intel.inde.mp.domain;

public abstract interface ICommandProcessor
{
  public abstract void process();

  public abstract void add(OutputInputPair paramOutputInputPair);

  public abstract void stop();
}

/* Location:           E:\SouceCode\recordGame\gdxDemo\libs\domain-1.2.2415.jar
 * Qualified Name:     com.intel.inde.mp.domain.ICommandProcessor
 * JD-Core Version:    0.6.1
 */