package com.intel.inde.mp;

import com.intel.inde.mp.domain.CapturePipeline;
import com.intel.inde.mp.domain.IAndroidMediaObjectFactory;
import com.intel.inde.mp.domain.ICaptureSource;
import com.intel.inde.mp.domain.IMicrophoneSource;
import com.intel.inde.mp.domain.Pipeline;

public class GLCapture extends CapturePipeline
{
  private ICaptureSource videoSource;
  private IMicrophoneSource audioSource;
  private boolean frameInProgress;

  public GLCapture(IAndroidMediaObjectFactory factory, IProgressListener progressListener)
  {
    super(factory, progressListener);
  }

  protected void setMediaSource()
  {
    this.videoSource = this.androidMediaObjectFactory.createCaptureSource();
    this.pipeline.setMediaSource(this.videoSource);
    if (this.audioSource != null)
      this.pipeline.setMediaSource(this.audioSource);
  }

  public void setTargetAudioFormat(AudioFormat mediaFormat)
  {
    super.setTargetAudioFormat(mediaFormat);
    this.audioSource = this.androidMediaObjectFactory.createMicrophoneSource();
    this.audioSource.configure(mediaFormat.getAudioSampleRateInHz(), mediaFormat.getAudioChannelCount());
  }

  public void stop()
  {
    super.stop();
  }

  public void setSurfaceSize(int width, int height)
  {
    this.videoSource.setSurfaceSize(width, height);
  }

  public void beginCaptureFrame()
  {
    if (this.frameInProgress) {
      return;
    }

    this.frameInProgress = true;

    this.videoSource.beginCaptureFrame();
  }

  public void endCaptureFrame()
  {
    if (!this.frameInProgress) {
      return;
    }

    this.videoSource.endCaptureFrame();

    this.frameInProgress = false;
  }
}

/* Location:           E:\SouceCode\recordGame\gdxDemo\libs\domain-1.2.2415.jar
 * Qualified Name:     com.intel.inde.mp.GLCapture
 * JD-Core Version:    0.6.1
 */
