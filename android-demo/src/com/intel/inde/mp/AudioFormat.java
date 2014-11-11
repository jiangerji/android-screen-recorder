package com.intel.inde.mp;

import com.intel.inde.mp.domain.MediaFormat;

public abstract class AudioFormat extends MediaFormat
{
  private static final String KEY_SAMPLE_RATE = "sample-rate";
  private static final String KEY_CHANNEL_COUNT = "channel-count";
  private static final String KEY_IS_ADTS = "is-adts";
  private static final String KEY_CHANNEL_MASK = "channel-mask";
  private static final String KEY_AAC_PROFILE = "aac-profile";
  private static final String KEY_FLAC_COMPRESSION_LEVEL = "flac-compression-level";
  private static final String KEY_MAX_INPUT_SIZE = "max-input-size";
  public static final String KEY_BIT_RATE = "bitrate";
  private static final String NO_INFO_AVAILABLE = "No info available.";
  private String mimeType;

  protected void setAudioCodec(String mimeType)
  {
    this.mimeType = mimeType;
  }

  public String getAudioCodec()
  {
    return this.mimeType;
  }

  public void setAudioSampleRateInHz(int sampleRate)
  {
    setInteger("sample-rate", sampleRate);
  }

  public int getAudioSampleRateInHz()
  {
    try
    {
      return getInteger("sample-rate"); } catch (NullPointerException e) {
    }
    throw new RuntimeException("No info available.");
  }

  public void setAudioChannelCount(int channelCount)
  {
    setInteger("channel-count", channelCount);
  }

  public int getAudioChannelCount()
  {
    try
    {
      return getInteger("channel-count"); } catch (NullPointerException e) {
    }
    throw new RuntimeException("No info available.");
  }

  public void setAudioBitrateInBytes(int bitRate)
  {
    setInteger("bitrate", bitRate);
  }

  public int getAudioBitrateInBytes()
  {
    try
    {
      return getInteger("bitrate"); } catch (NullPointerException e) {
    }
    throw new RuntimeException("No info available.");
  }

  public void setKeyMaxInputSize(int size)
  {
    setInteger("max-input-size", size);
  }

  public void setAudioProfile(int Profile)
  {
    setInteger("aac-profile", Profile);
  }

  public int getAudioProfile()
  {
    try
    {
      return getInteger("aac-profile"); } catch (NullPointerException e) {
    }
    throw new RuntimeException("No info available.");
  }
}

/* Location:           E:\SouceCode\recordGame\gdxDemo\libs\domain-1.2.2415.jar
 * Qualified Name:     com.intel.inde.mp.AudioFormat
 * JD-Core Version:    0.6.1
 */
