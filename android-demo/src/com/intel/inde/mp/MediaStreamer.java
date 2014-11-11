 package com.intel.inde.mp;
 
 import com.intel.inde.mp.domain.CommandProcessor;
 import com.intel.inde.mp.domain.IAndroidMediaObjectFactory;
 import com.intel.inde.mp.domain.MediaFormatType;
 import com.intel.inde.mp.domain.MediaSource;
 import com.intel.inde.mp.domain.MultipleMediaSource;
 import com.intel.inde.mp.domain.PassThroughPlugin;
 import com.intel.inde.mp.domain.Pipeline;
 import com.intel.inde.mp.domain.ProgressTracker;
 import com.intel.inde.mp.domain.Render;
 import java.io.IOException;
 import java.io.Serializable;
 
 public class MediaStreamer
   implements Serializable
 {
   private IAndroidMediaObjectFactory factory = null;
   private MultipleMediaSource multipleMediaSource = null;
   private Render sink;
   private PassThroughPlugin videoPipe;
   private PassThroughPlugin audioPipe;
   private ProgressTracker progressTracker = new ProgressTracker();
   private Pipeline pipeline;
   private CommandProcessor commandProcessor;
   private IProgressListener progressListener;
 
   public MediaStreamer(IAndroidMediaObjectFactory factory, IProgressListener progressListener)
   {
     this.factory = factory;
     this.progressListener = progressListener;
     this.multipleMediaSource = new MultipleMediaSource();
   }
 
   public void addSourceFile(String fileName)
     throws IOException
   {
     MediaSource mediaSource = this.factory.createMediaSource(fileName);
     MediaFile mediaFile = new MediaFile(mediaSource);
     this.multipleMediaSource.add(mediaFile);
   }
 
   public void addSourceFile(Uri uri) throws IOException {
     MediaSource mediaSource = this.factory.createMediaSource(uri);
     MediaFile mediaFile = new MediaFile(mediaSource);
     this.multipleMediaSource.add(mediaFile);
   }
 
   public void setTargetConnection(StreamingParameters parameters)
   {
     this.sink = this.factory.createSink(parameters, this.progressListener, this.progressTracker);
   }
 
   public void start()
   {
     this.commandProcessor = new CommandProcessor(this.progressListener);
     this.pipeline = new Pipeline(this.commandProcessor);
     this.videoPipe = new PassThroughPlugin(1024000, MediaFormatType.VIDEO);
     this.audioPipe = new PassThroughPlugin(10240, MediaFormatType.AUDIO);
 
     if (this.videoPipe != null) this.pipeline.addVideoDecoder(this.videoPipe);
     if (this.audioPipe != null) this.pipeline.addAudioDecoder(this.audioPipe);
 
     this.pipeline.setMediaSource(this.multipleMediaSource);
     this.pipeline.setSink(this.sink);
 
     startCommandsProcessingAsync();
   }
 
   private void startCommandsProcessingAsync() {
     Thread thread = new Thread(new Runnable()
     {
       public void run()
       {
         try {
           MediaStreamer.this.pipeline.resolve();
 
           MediaStreamer.this.progressListener.onMediaStart();
           MediaStreamer.this.progressTracker.setFinish((float)MediaStreamer.this.multipleMediaSource.getSegmentsDurationInMicroSec());
           MediaStreamer.this.progressListener.onMediaProgress(0.0F);
 
           MediaStreamer.this.commandProcessor.process();
 
           MediaStreamer.this.progressListener.onMediaDone();
         } catch (Exception e) {
           MediaStreamer.this.progressListener.onError(e);
         }
         try
         {
           MediaStreamer.this.pipeline.release();
         } catch (Exception e) {
           MediaStreamer.this.progressListener.onError(e);
         }
       }
     });
     thread.start();
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
     if (this.commandProcessor != null) {
       this.commandProcessor.stop();
     }
     if (this.pipeline != null) {
       this.pipeline.stop();
     }
     this.progressListener.onMediaStop();
   }
 }

/* Location:           E:\SouceCode\recordGame\gdxDemo\libs\domain-1.2.2415.jar
 * Qualified Name:     com.intel.inde.mp.MediaStreamer
 * JD-Core Version:    0.6.1
 */