package com.intel.inde.mp.domain;

import java.util.Collection;

public abstract interface IsConnectable
{
  public abstract boolean isConnectable(IOutputRaw paramIOutputRaw, Collection<IInputRaw> paramCollection);

  public abstract boolean isConnectable(Collection<IOutputRaw> paramCollection, IInputRaw paramIInputRaw);
}

/* Location:           E:\SouceCode\recordGame\gdxDemo\libs\domain-1.2.2415.jar
 * Qualified Name:     com.intel.inde.mp.domain.IsConnectable
 * JD-Core Version:    0.6.1
 */