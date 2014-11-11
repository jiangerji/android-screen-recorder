 package com.intel.inde.mp.domain;
 
 import com.intel.inde.mp.IRecognitionPlugin;
 import com.intel.inde.mp.IRecognitionPlugin.RecognitionInput;
 import java.nio.ByteBuffer;
 
 public class RecognitionPipeline
 {
   private State state;
   private IRecognitionPlugin recognitionPlugin;
   private IOutput mediaSource;
   private Frame frame;
   private ByteBuffer buffer;
   private IRecognitionPlugin.RecognitionInput pluginInput;
   private final int bufferSize = 16384;
 
   public RecognitionPipeline(IOutput source, IRecognitionPlugin plugin) {
     if ((plugin == null) || (source == null)) {
       throw new IllegalArgumentException("Plugin or Source can't be null");
     }
 
     this.state = State.NotInitialized;
     this.buffer = ByteBuffer.allocateDirect(16384);
     this.frame = new Frame(this.buffer, 16384, 0L, 0, 0, 0);
     this.pluginInput = new IRecognitionPlugin.RecognitionInput();
     this.recognitionPlugin = plugin;
     this.mediaSource = source;
     this.pluginInput.setMediaFormat(this.mediaSource.getMediaFormatByType(MediaFormatType.AUDIO));
   }
 
   public void start() {
     setState(State.Running);
     this.mediaSource.start();
     while (this.state == State.Running) {
       this.mediaSource.pull(this.frame);
       this.pluginInput.setFrame(this.frame);
       this.recognitionPlugin.recognize(this.pluginInput);
     }
     this.mediaSource.stop();
 
     setState(State.Initialized);
   }
 
   public void stop() {
     setState(State.Stopping);
   }
 
   private void setState(State state) {
     this.state = state;
   }
 
   static enum State
   {
     NotInitialized, 
     Initialized, 
     Running, 
     Stopping;
   }
 }

/* Location:           E:\SouceCode\recordGame\gdxDemo\libs\domain-1.2.2415.jar
 * Qualified Name:     com.intel.inde.mp.domain.RecognitionPipeline
 * JD-Core Version:    0.6.1
 */