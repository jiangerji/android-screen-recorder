package com.intel.inde.mp.domain;

import java.io.Closeable;

public abstract interface IOutput extends IRunnable, IOutputRaw, Closeable
{
  public abstract void pull(Frame paramFrame);

  public abstract MediaFormat getMediaFormatByType(MediaFormatType paramMediaFormatType);

  public abstract boolean isLastFile();

  public abstract void incrementConnectedPluginsCount();
}

/* Location:           E:\SouceCode\recordGame\gdxDemo\libs\domain-1.2.2415.jar
 * Qualified Name:     com.intel.inde.mp.domain.IOutput
 * JD-Core Version:    0.6.1
 */