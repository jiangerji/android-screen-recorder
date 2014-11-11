 package com.intel.inde.mp.domain;
 
 import com.intel.inde.mp.AudioFormat;
 import com.intel.inde.mp.IProgressListener;
 import com.intel.inde.mp.VideoFormat;
 import com.intel.inde.mp.domain.pipeline.IOnStopListener;
 import java.io.IOException;
 
 public class MuxRender extends Render
 {
   private final IMediaMuxer notReadyMuxer;
   private IMediaMuxer muxer;
   private final IProgressListener progressListener;
   private final ProgressTracker progressTracker;
   private int connectedPluginsCount = 0;
   private int tracksCount = 0;
   private int drainCount = 0;
   private int videoTrackId = -1;
   private int audioTrackId = -1;
   private FrameBuffer frameBuffer = new FrameBuffer(0);
   private boolean zeroFramesReceived = true;
 
   public MuxRender(IMediaMuxer muxer, IProgressListener progressListener, ProgressTracker progressTracker)
   {
     this.notReadyMuxer = muxer;
     this.progressListener = progressListener;
     this.progressTracker = progressTracker;
   }
 
   protected void initInputCommandQueue()
   {
   }
 
   public void push(Frame frame)
   {
     if (this.zeroFramesReceived == true) {
       this.zeroFramesReceived = false;
     }
 
     if (this.frameBuffer.areAllTracksConfigured()) {
       writeBufferedFrames();
       writeSampleData(frame);
       feedMeIfNotDraining();
     } else {
       this.frameBuffer.push(frame);
       getInputCommandQueue().queue(Command.NeedInputFormat, Integer.valueOf(0));
     }
   }
 
   private void writeBufferedFrames() {
     while (this.frameBuffer.canPull()) {
       Frame bufferedFrame = this.frameBuffer.pull();
       writeSampleData(bufferedFrame);
     }
   }
 
   private void writeSampleData(Frame frame) {
     IMediaCodec.BufferInfo bufferInfo = new IMediaCodec.BufferInfo();
     bufferInfo.flags = frame.getFlags();
     bufferInfo.presentationTimeUs = frame.getSampleTime();
     bufferInfo.size = frame.getLength();
 
     this.muxer.writeSampleData(frame.getTrackId(), frame.getByteBuffer(), bufferInfo);
 
     this.progressTracker.track((float)frame.getSampleTime());
     this.progressListener.onMediaProgress(this.progressTracker.getProgress());
   }
 
   public void drain(int bufferIndex)
   {
     this.drainCount += 1;
 
     if (this.drainCount == this.connectedPluginsCount)
     {
       closeRender();
 
       this.progressListener.onMediaStop();
       if (this.onStopListener != null) {
         this.onStopListener.onStop();
       }
       setState(PluginState.Drained);
     }
 
     if (this.frameBuffer.areAllTracksConfigured())
       feedMeIfNotDraining();
     else
       getInputCommandQueue().queue(Command.NeedInputFormat, Integer.valueOf(0));
   }
 
   public void configure()
   {
     this.connectedPluginsCount += 1;
     getInputCommandQueue().queue(Command.NeedInputFormat, Integer.valueOf(0));
     this.frameBuffer.addTrack();
   }
 
   public void setMediaFormat(MediaFormat mediaFormat)
   {
     int trackIndex = this.notReadyMuxer.addTrack(mediaFormat);
     if ((mediaFormat instanceof VideoFormat)) this.videoTrackId = trackIndex;
     if ((mediaFormat instanceof AudioFormat)) this.audioTrackId = trackIndex;
 
     this.frameBuffer.configure(this.tracksCount);
     this.tracksCount += 1;
   }
 
   public int getTrackIdByMediaFormat(MediaFormat mediaFormat)
   {
     if ((mediaFormat instanceof VideoFormat)) {
       if (this.videoTrackId == -1) throw new IllegalStateException("Video track not initialised");
       return this.videoTrackId;
     }if ((mediaFormat instanceof AudioFormat)) {
       if (this.audioTrackId == -1) throw new IllegalStateException("Audio track not initialised");
       return this.audioTrackId;
     }
 
     return -1;
   }
 
   public void start()
   {
     if (this.connectedPluginsCount == this.tracksCount)
     {
       this.notReadyMuxer.start();
       this.muxer = this.notReadyMuxer;
 
       for (int track = 0; track < this.tracksCount; track++)
         feedMeIfNotDraining();
     }
   }
 
   public boolean canConnectFirst(IOutputRaw connector)
   {
     return true;
   }
 
   public void fillCommandQueues() {
   }
 
   public void close() throws IOException {
     closeRender();
   }
 
   private void closeRender() {
     if (this.muxer != null)
       try {
         this.muxer.stop();
         this.muxer.release();
         this.muxer = null;
       }
       catch (Exception e) {
         if (!this.zeroFramesReceived)
         {
           throw new RuntimeException("Failed to close the render.", e);
         }
       }
   }
 }

/* Location:           E:\SouceCode\recordGame\gdxDemo\libs\domain-1.2.2415.jar
 * Qualified Name:     com.intel.inde.mp.domain.MuxRender
 * JD-Core Version:    0.6.1
 */