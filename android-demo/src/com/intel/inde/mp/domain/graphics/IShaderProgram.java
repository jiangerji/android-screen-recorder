package com.intel.inde.mp.domain.graphics;

public abstract interface IShaderProgram
{
  public abstract void create(String paramString1, String paramString2);

  public abstract void use();

  public abstract void unUse();

  public abstract int getAttributeLocation(String paramString);
}

/* Location:           E:\SouceCode\recordGame\gdxDemo\libs\domain-1.2.2415.jar
 * Qualified Name:     com.intel.inde.mp.domain.graphics.IShaderProgram
 * JD-Core Version:    0.6.1
 */