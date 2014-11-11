 package com.intel.inde.mp.android;
 
 import android.media.AudioRecord;
 import com.intel.inde.mp.domain.Frame;
 import com.intel.inde.mp.domain.MediaFormat;
 import com.intel.inde.mp.domain.MediaFormatType;
 import java.nio.ByteBuffer;
 
 public class MicrophoneSource extends com.intel.inde.mp.domain.MicrophoneSource
 {
   private static final long sampleSize = 2L;
   private AudioRecord recorder;
   private int sampleRate;
   private int recordChannels;
   private int androidChannels;
   private final int audioEncoding = 2;
   private int minBufferSize;
   private long startTimeNs;
 
   public void pull(Frame frame)
   {
     if (isStopped()) {
       super.pull(frame);
 
       this.startTimeNs = 0L;
 
       return;
     }
 
     if (this.startTimeNs == 0L) {
       this.startTimeNs = System.nanoTime();
     }
 
     int bufferSize = this.minBufferSize / 2;
 
     if (bufferSize > frame.getByteBuffer().capacity()) {
       bufferSize = frame.getByteBuffer().capacity();
     }
 
     int actualRead = this.recorder.read(frame.getByteBuffer(), bufferSize);
 
     frame.setLength(actualRead);
 
     long presentationTimeNs = actualRead / (this.sampleRate * 2L * this.recordChannels) / 1000000000L;
 
     long sampleTimeMicrosec = (System.nanoTime() - this.startTimeNs + presentationTimeNs) / 1000L;
     frame.setSampleTime(sampleTimeMicrosec);
 
     super.pull(frame);
   }
 
   public void close()
   {
     if (this.recorder != null) {
       this.recorder.release();
     }
     this.recorder = null;
   }
 
   public void start()
   {
     this.recorder = new AudioRecord(1, this.sampleRate, this.androidChannels, 2, this.minBufferSize * 4);
 
     int state = this.recorder.getState();
 
     if (state != 1) {
       throw new IllegalStateException("Failed to start AudioRecord! Used by another application?");
     }
 
     this.recorder.startRecording();
 
     super.start();
   }
 
   public void stop()
   {
     if (this.recorder != null) {
       this.recorder.stop();
       this.recorder.release();
     }
 
     this.recorder = null;
     super.stop();
   }
 
   public MediaFormat getMediaFormatByType(MediaFormatType mediaFormatType)
   {
     if (!mediaFormatType.toString().startsWith("audio")) {
       return null;
     }
 
     return new AudioFormatAndroid("audio/aac", this.sampleRate, this.recordChannels);
   }
 
   public synchronized void configure(int sampleRate, int channels) {
     this.sampleRate = sampleRate;
     this.recordChannels = channels;
 
     switch (this.recordChannels) {
     case 1:
       this.androidChannels = 16;
 
       break;
     case 2:
       this.androidChannels = 12;
     }
 
     this.minBufferSize = AudioRecord.getMinBufferSize(sampleRate, this.androidChannels, 2);
 
     if (this.minBufferSize < 0) {
       this.sampleRate = 8000;
       this.minBufferSize = AudioRecord.getMinBufferSize(sampleRate, this.androidChannels, 2);
     }
   }
 }

/* Location:           E:\SouceCode\recordGame\gdxDemo\libs\android-1.2.2415.jar
 * Qualified Name:     com.intel.inde.mp.android.MicrophoneSource
 * JD-Core Version:    0.6.1
 */