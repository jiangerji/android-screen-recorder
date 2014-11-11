 package com.intel.inde.mp.android;
 
 import com.intel.inde.mp.IRecognitionPlugin;
 import com.intel.inde.mp.domain.IAudioContentRecognition;
 import com.intel.inde.mp.domain.RecognitionPipeline;
 
 public class AudioContentRecognition
   implements IAudioContentRecognition
 {
   private RecognitionPipeline pipeline;
   private MicrophoneSource source;
   private IRecognitionPlugin plugin;
   private Thread thread;
 
   public void setRecognizer(IRecognitionPlugin plugin)
   {
     this.plugin = plugin;
   }
 
   private boolean isRunning() {
     return this.thread != null;
   }
 
   public void start() {
     if (this.plugin == null) {
       throw new IllegalStateException("Set recognition plugin using setRecognizer before calling start.");
     }
 
     if (isRunning()) {
       throw new IllegalStateException("Recognition already started.");
     }
 
     this.source = new MicrophoneSource();
     this.source.configure(44100, 1);
 
     this.plugin.start();
 
     this.pipeline = new RecognitionPipeline(this.source, this.plugin);
 
     startThread();
   }
 
   public void stop() {
     if (!isRunning()) {
       return;
     }
 
     this.plugin.stop();
 
     this.pipeline.stop();
     this.thread.interrupt();
 
     this.pipeline = null;
     this.thread = null;
   }
 
   private void startThread() {
     this.thread = new Thread(new Runnable() {
       public void run() {
         AudioContentRecognition.this.pipeline.start();
       }
     }
     , "recordingThread");
 
     this.thread.start();
   }
 }

/* Location:           E:\SouceCode\recordGame\gdxDemo\libs\android-1.2.2415.jar
 * Qualified Name:     com.intel.inde.mp.android.AudioContentRecognition
 * JD-Core Version:    0.6.1
 */