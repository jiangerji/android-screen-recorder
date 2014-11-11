 package com.intel.inde.mp.domain;
 
 import com.intel.inde.mp.AudioFormat;
 import com.intel.inde.mp.IProgressListener;
 import com.intel.inde.mp.StreamingParameters;
 import com.intel.inde.mp.VideoFormat;
 import java.io.IOException;
 import java.util.concurrent.ExecutorService;
 import java.util.concurrent.Executors;
 import java.util.concurrent.TimeUnit;
 
 public abstract class CapturePipeline
 {
   VideoEncoder videoEncoder;
   AudioEncoder audioEncoder;
   protected VideoEffector videoEffector;
   protected final IAndroidMediaObjectFactory androidMediaObjectFactory;
   protected Pipeline pipeline;
   Render sink;
   AudioFormat audioFormat;
   protected ExecutorService pools;
   private final IProgressListener progressListener;
   private boolean started;
 
   public CapturePipeline(IAndroidMediaObjectFactory androidMediaObjectFactory, IProgressListener progressListener)
   {
     this.androidMediaObjectFactory = androidMediaObjectFactory;
     this.progressListener = progressListener;
   }
 
   public void setTargetFile(String fileName)
     throws IOException
   {
     this.sink = this.androidMediaObjectFactory.createSink(fileName, this.progressListener, new ProgressTracker());
   }
 
   public void setTargetConnection(StreamingParameters parameters)
   {
     this.sink = this.androidMediaObjectFactory.createSink(parameters, this.progressListener, new ProgressTracker());
   }
 
   public void setTargetVideoFormat(VideoFormat mediaFormat)
   {
     if (this.videoEncoder == null) {
       this.videoEncoder = this.androidMediaObjectFactory.createVideoEncoder();
     }
     this.videoEncoder.setMediaFormat(this.androidMediaObjectFactory.createVideoFormat(mediaFormat.getVideoCodec(), mediaFormat.getVideoFrameSize().width(), mediaFormat.getVideoFrameSize().height()));
 
     this.videoEncoder.setBitRateInKBytes(mediaFormat.getVideoBitRateInKBytes());
     this.videoEncoder.setFrameRate(mediaFormat.getVideoFrameRate());
     this.videoEncoder.setIFrameInterval(mediaFormat.getVideoIFrameInterval());
   }
 
   public void setTargetAudioFormat(AudioFormat mediaFormat)
   {
     if (this.audioEncoder == null) {
       this.audioEncoder = this.androidMediaObjectFactory.createAudioEncoder(null);
     }
     this.audioFormat = mediaFormat;
 
     int channelCount = mediaFormat.getAudioChannelCount();
     int sampleRate = mediaFormat.getAudioSampleRateInHz();
 
     AudioFormat audioFormat = (AudioFormat)this.androidMediaObjectFactory.createAudioFormat(mediaFormat.getAudioCodec(), channelCount, sampleRate);
     audioFormat.setAudioBitrateInBytes(22050);
     audioFormat.setAudioProfile(2);
 
     this.audioEncoder.setMediaFormat(audioFormat);
   }
 
   public void start()
   {
     CommandProcessor commandProcessor = new CommandProcessor(this.progressListener);
     this.pipeline = new Pipeline(commandProcessor);
     this.pools = Executors.newSingleThreadExecutor();
 
     buildPipeline();
     executeProcessor(commandProcessor);
 
     this.started = true;
   }
 
   protected void buildPipeline() {
     setMediaSource();
 
     if (this.videoEffector != null) {
       this.pipeline.addVideoEffect(this.videoEffector);
     }
     if (this.audioEncoder != null) {
       this.pipeline.addAudioEncoder(this.audioEncoder);
     }
     if (this.videoEncoder != null) {
       this.pipeline.addVideoEncoder(this.videoEncoder);
     }
     this.pipeline.setSink(this.sink);
   }
 
   private void waitForTermination()
   {
     while (!Thread.currentThread().isInterrupted())
     {
       try
       {
         Thread.sleep(5L);
       } catch (Exception e) {
       }
     }
   }
 
   protected void executeProcessor(final CommandProcessor commandProcessor) {
     this.pools.execute(new Runnable()
     {
       public void run() {
         try {
           CapturePipeline.this.pipeline.resolve();
           CapturePipeline.this.notifyOnStart();
           commandProcessor.process();
           CapturePipeline.this.notifyOnDone();
         } catch (Exception e) {
           CapturePipeline.this.notifyOnError(e);
         } finally {
           try {
             CapturePipeline.this.pipeline.release();
           } catch (Exception e) {
             CapturePipeline.this.notifyOnError(e);
           }
           CapturePipeline.this.waitForTermination();
         }
       }
     });
   }
 
   public void stop()
   {
     if (!this.started) {
       return;
     }
 
     try
     {
       this.pipeline.stop();
 
       notifyOnStop();
 
       this.pools.shutdownNow();
       this.pools.awaitTermination(10L, TimeUnit.SECONDS);
     }
     catch (Exception e) {
       notifyOnError(e);
     }
 
     this.audioEncoder = null;
     this.videoEncoder = null;
 
     this.started = false;
   }
 
   private void notifyOnDone() {
     this.progressListener.onMediaDone();
   }
 
   protected void notifyOnError(Exception e) {
     this.progressListener.onError(e);
   }
 
   protected void notifyOnStart() {
     this.progressListener.onMediaStart();
   }
 
   protected void notifyOnStop() {
     this.progressListener.onMediaStop();
   }
 
   protected abstract void setMediaSource();
 }

/* Location:           E:\SouceCode\recordGame\gdxDemo\libs\domain-1.2.2415.jar
 * Qualified Name:     com.intel.inde.mp.domain.CapturePipeline
 * JD-Core Version:    0.6.1
 */