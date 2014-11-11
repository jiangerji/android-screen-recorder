package com.intel.inde.mp.domain;

abstract interface ISpecification<T>
{
  public abstract boolean satisfiedBy(T paramT1, T paramT2);
}

/* Location:           E:\SouceCode\recordGame\gdxDemo\libs\domain-1.2.2415.jar
 * Qualified Name:     com.intel.inde.mp.domain.ISpecification
 * JD-Core Version:    0.6.1
 */