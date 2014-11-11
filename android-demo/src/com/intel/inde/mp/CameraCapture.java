package com.intel.inde.mp;

import com.intel.inde.mp.domain.CapturePipeline;
import com.intel.inde.mp.domain.IAndroidMediaObjectFactory;
import com.intel.inde.mp.domain.ICameraSource;
import com.intel.inde.mp.domain.IMicrophoneSource;
import com.intel.inde.mp.domain.IPreview;
import com.intel.inde.mp.domain.Pipeline;
import com.intel.inde.mp.domain.VideoEffector;
import java.util.Collection;
import java.util.LinkedList;

public class CameraCapture extends CapturePipeline
{
  private IMicrophoneSource microphoneSource;
  private ICameraSource cameraSource;
  private IPreview previewRender;
  private Object camera;

  public CameraCapture(IAndroidMediaObjectFactory factory, IProgressListener progressListener)
  {
    super(factory, progressListener);
  }

  public void setTargetAudioFormat(AudioFormat mediaFormat)
  {
    super.setTargetAudioFormat(mediaFormat);
    this.microphoneSource = this.androidMediaObjectFactory.createMicrophoneSource();
    this.microphoneSource.configure(mediaFormat.getAudioSampleRateInHz(), mediaFormat.getAudioChannelCount());
  }

  public void setTargetVideoFormat(VideoFormat mediaFormat)
  {
    super.setTargetVideoFormat(mediaFormat);
  }

  public void setCamera(Object camera)
  {
    if (this.cameraSource == null) {
      this.cameraSource = this.androidMediaObjectFactory.createCameraSource();
    }
    this.cameraSource.setCamera(camera);
    this.camera = camera;
  }

  public IPreview createPreview(Object mGLView, Object camera)
  {
    this.camera = camera;

    if (null == this.previewRender) {
      this.previewRender = this.androidMediaObjectFactory.createPreviewRender(mGLView, camera);
    }

    if (this.videoEffector == null) {
      this.videoEffector = this.androidMediaObjectFactory.createVideoEffector();
    }
    if ((this.previewRender != null) && (camera != null)) {
      this.videoEffector.enablePreview(this.previewRender);
    }

    return this.previewRender;
  }

  public void addVideoEffect(IVideoEffect effect)
  {
    if (this.videoEffector == null) {
      this.videoEffector = this.androidMediaObjectFactory.createVideoEffector();
    }
    this.videoEffector.getVideoEffects().add(effect);
  }

  public void removeVideoEffect(IVideoEffect effect)
  {
    this.videoEffector.getVideoEffects().remove(effect);
  }

  public Collection<IVideoEffect> getVideoEffects()
  {
    return (Collection)this.videoEffector.getVideoEffects().clone();
  }

  public void setTargetConnection(StreamingParameters parameters)
  {
    super.setTargetConnection(parameters);
  }

  protected void setMediaSource()
  {
    setCamera(this.camera);

    if (this.microphoneSource != null) {
      this.pipeline.setMediaSource(this.microphoneSource);
    }

    if (this.cameraSource != null)
      this.pipeline.setMediaSource(this.cameraSource);
  }

  public void stop()
  {
    super.stop();
    if (this.previewRender != null) {
      LinkedList videoEffects = this.videoEffector.getVideoEffects();

      this.videoEffector = this.androidMediaObjectFactory.createVideoEffector();
      this.videoEffector.getVideoEffects().addAll(videoEffects);
      this.videoEffector.enablePreview(this.previewRender);
    }
  }
}

/* Location:           E:\SouceCode\recordGame\gdxDemo\libs\domain-1.2.2415.jar
 * Qualified Name:     com.intel.inde.mp.CameraCapture
 * JD-Core Version:    0.6.1
 */
