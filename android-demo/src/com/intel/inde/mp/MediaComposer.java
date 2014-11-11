 package com.intel.inde.mp;
 
 import com.intel.inde.mp.domain.AudioEffector;
 import com.intel.inde.mp.domain.AudioEncoder;
 import com.intel.inde.mp.domain.CommandProcessor;
 import com.intel.inde.mp.domain.IAndroidMediaObjectFactory;
 import com.intel.inde.mp.domain.MediaFormatType;
 import com.intel.inde.mp.domain.MediaSource;
 import com.intel.inde.mp.domain.MultipleMediaSource;
 import com.intel.inde.mp.domain.Pipeline;
 import com.intel.inde.mp.domain.Plugin;
 import com.intel.inde.mp.domain.ProgressTracker;
 import com.intel.inde.mp.domain.Render;
 import com.intel.inde.mp.domain.Resampler;
 import com.intel.inde.mp.domain.VideoEffector;
 import com.intel.inde.mp.domain.VideoEncoder;
 import java.io.FileDescriptor;
 import java.io.IOException;
 import java.io.Serializable;
 import java.util.Collection;
 import java.util.LinkedList;
 import java.util.List;
 
 public class MediaComposer
   implements Serializable
 {
   private IAndroidMediaObjectFactory factory;
   private MultipleMediaSource multipleMediaSource;
   private Plugin videoDecoder;
   private VideoEncoder videoEncoder;
   private Plugin audioDecoder;
   private AudioEncoder audioEncoder;
   private Render sink;
   private VideoEffector videoEffector;
   private AudioEffector audioEffector;
   private Pipeline pipeline;
   private CommandProcessor commandProcessor;
   private IProgressListener progressListener;
   private ProgressTracker progressTracker = new ProgressTracker();
   private AudioFormat audioFormat;
   private VideoFormat videoFormat;
   private Resampler resampler;
 
   public MediaComposer(IAndroidMediaObjectFactory factory, IProgressListener progressListener)
   {
     this.progressListener = progressListener;
     this.factory = factory;
     this.multipleMediaSource = new MultipleMediaSource();
   }
 
   public void addSourceFile(String fileName)
     throws IOException, RuntimeException
   {
     MediaSource mediaSource = this.factory.createMediaSource(fileName);
     MediaFile mediaFile = new MediaFile(mediaSource);
     this.multipleMediaSource.add(mediaFile);
   }
 
   public void addSourceFile(FileDescriptor fileDescriptor) throws IOException, RuntimeException {
     MediaSource mediaSource = this.factory.createMediaSource(fileDescriptor);
     MediaFile mediaFile = new MediaFile(mediaSource);
     this.multipleMediaSource.add(mediaFile);
   }
 
   public void addSourceFile(Uri uri) throws IOException, RuntimeException {
     MediaSource mediaSource = this.factory.createMediaSource(uri);
     MediaFile mediaFile = new MediaFile(mediaSource);
     this.multipleMediaSource.add(mediaFile);
   }
 
   public void removeSourceFile(MediaFile mediaFile)
   {
     this.multipleMediaSource.remove(mediaFile);
   }
 
   public void insertSourceFile(int index, String fileName)
     throws IOException
   {
     MediaSource mediaSource = this.factory.createMediaSource(fileName);
     MediaFile mediaFile = new MediaFile(mediaSource);
     this.multipleMediaSource.insertAt(index, mediaFile);
   }
 
   public List<MediaFile> getSourceFiles()
   {
     return this.multipleMediaSource.files();
   }
 
   public void setTargetFile(String fileName)
     throws IOException
   {
     this.sink = this.factory.createSink(fileName, this.progressListener, this.progressTracker);
   }
 
   public long getDurationInMicroSec()
   {
     return this.multipleMediaSource.getSegmentsDurationInMicroSec();
   }
 
   public void setTargetVideoFormat(VideoFormat mediaFormat)
   {
     this.videoFormat = mediaFormat;
   }
 
   public VideoFormat getTargetVideoFormat()
   {
     return this.videoFormat;
   }
 
   public void setTargetAudioFormat(AudioFormat mediaFormat)
   {
     this.audioFormat = mediaFormat;
   }
 
   public AudioFormat getTargetAudioFormat()
   {
     return this.audioFormat;
   }
 
   public void addVideoEffect(IVideoEffect effect)
   {
     if (this.videoEffector == null) {
       this.videoEffector = this.factory.createVideoEffector();
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
 
   public void addAudioEffect(IAudioEffect effect)
   {
     if (this.audioEffector == null) {
       this.audioEffector = this.factory.createAudioEffects();
     }
     this.audioEffector.getAudioEffects().add(effect);
   }
 
   public void removeAudioEffect(IAudioEffect effect)
   {
     this.audioEffector.getAudioEffects().remove(effect);
   }
 
   public Collection<IAudioEffect> getAudioEffects()
   {
     return (Collection)this.audioEffector.getAudioEffects().clone();
   }
 
   public void start()
   {
     this.multipleMediaSource.verify();
 
     this.commandProcessor = new CommandProcessor(this.progressListener);
     this.pipeline = new Pipeline(this.commandProcessor);
     this.pipeline.setMediaSource(this.multipleMediaSource);
 
     if ((this.videoFormat != null) && (this.multipleMediaSource.hasTrack(MediaFormatType.VIDEO))) {
       this.videoDecoder = this.factory.createVideoDecoder(this.videoFormat);
       this.videoEncoder = this.factory.createVideoEncoder();
       this.videoEncoder.setMediaFormat(this.videoFormat);
     }
     if (this.videoDecoder != null) this.pipeline.addVideoDecoder(this.videoDecoder);
     if (this.videoEncoder != null) this.pipeline.addVideoEncoder(this.videoEncoder);
     if (this.videoEffector != null) this.pipeline.addVideoEffect(this.videoEffector);
 
     if ((this.audioFormat != null) && (this.multipleMediaSource.hasTrack(MediaFormatType.AUDIO))) {
       this.audioDecoder = this.factory.createAudioDecoder();
       this.audioEncoder = this.factory.createAudioEncoder(this.audioFormat.getAudioCodec());
       this.audioEncoder.setMediaFormat(this.audioFormat);
 
       createResampler(this.audioFormat);
       this.audioEncoder.addResampler(this.resampler);
     }
     if (this.audioDecoder != null) this.pipeline.addAudioDecoder(this.audioDecoder);
     if (this.audioEncoder != null) this.pipeline.addAudioEncoder(this.audioEncoder);
     if (this.audioEffector != null) {
       this.audioEffector.setMediaFormat(this.audioFormat);
       this.pipeline.addAudioEffect(this.audioEffector);
     }
 
     this.pipeline.setSink(this.sink);
 
     startCommandsProcessingAsync();
   }
 
   public void pause()
   {
     this.commandProcessor.pause();
   }
 
   public void resume()
   {
     this.commandProcessor.resume();
   }
 
   public void stop()
   {
     if (this.pipeline != null) {
       this.pipeline.stop();
     }
     notifyOnMediaStop();
   }
 
   private void createResampler(AudioFormat audioFormat) {
     this.resampler = this.factory.createAudioResampler(audioFormat);
   }
 
   private void notifyOnMediaStart() {
     this.progressListener.onMediaStart();
   }
 
   private void notifyOnMediaDone() {
     this.progressListener.onMediaDone();
   }
 
   private void notifyOnMediaStop() {
     this.progressListener.onMediaStop();
   }
 
   private void notifyOnMediaProgress(float progress) {
     this.progressListener.onMediaProgress(progress);
   }
 
   private void notifyOnError(Exception exception) {
     this.progressListener.onError(exception);
   }
 
   private void startCommandsProcessingAsync() {
     new Thread(new Runnable()
     {
       public void run() {
         try {
           MediaComposer.this.pipeline.resolve();
           MediaComposer.this.notifyOnMediaStart();
           MediaComposer.this.notifyOnMediaProgress(0.0F);
           MediaComposer.this.progressTracker.setFinish((float)MediaComposer.this.multipleMediaSource.getSegmentsDurationInMicroSec());
           MediaComposer.this.commandProcessor.process();
         } catch (Exception e) {
           try {
             MediaComposer.this.pipeline.release();
             MediaComposer.this.notifyOnError(e);
           } catch (IOException e1) {
             MediaComposer.this.notifyOnError(e);
             MediaComposer.this.notifyOnError(e1);
           }
           return;
         }
         try
         {
           MediaComposer.this.pipeline.release();
         } catch (IOException e) {
           MediaComposer.this.notifyOnError(e);
           return;
         }
 
         MediaComposer.this.notifyOnMediaProgress(1.0F);
         MediaComposer.this.notifyOnMediaDone();
       }
     }).start();
   }
 }

/* Location:           E:\SouceCode\recordGame\gdxDemo\libs\domain-1.2.2415.jar
 * Qualified Name:     com.intel.inde.mp.MediaComposer
 * JD-Core Version:    0.6.1
 */