 package com.intel.inde.mp.domain;
 
 import java.io.IOException;
 import java.util.ArrayList;
 
 public abstract class Encoder extends MediaCodecPlugin
   implements ITransform, ISurfaceCreator
 {
   private ISurface surface;
   ArrayList<IOnSurfaceReady> listeners = new ArrayList();
 
   public Encoder(IMediaCodec mediaCodec) {
     super(mediaCodec);
     initInputCommandQueue();
   }
 
   public ISurface getSurface()
   {
     if (this.surface == null) {
       this.surface = this.mediaCodec.createInputSurface();
 
       for (IOnSurfaceReady listener : this.listeners) {
         listener.onSurfaceReady();
       }
     }
     return this.surface;
   }
 
   public ISurface getSimpleSurface(IEglContext eglContext)
   {
     if (this.surface == null) {
       this.surface = this.mediaCodec.createSimpleInputSurface(eglContext);
     }
     return this.surface;
   }
 
   public void checkIfOutputQueueHasData()
   {
     while (-1 != getOutputBufferIndex());
   }
 
   public void push(Frame frame)
   {
     super.push(frame);
     feedMeIfNotDraining();
   }
 
   public void configure()
   {
     this.mediaCodec.configure(this.mediaFormat, null, 1);
   }
 
   public void onSurfaceAvailable(IOnSurfaceReady listener)
   {
     this.listeners.add(listener);
   }
 
   public void pull(Frame frame)
   {
     throw new UnsupportedOperationException("Unexpected call of pull() in Encoder.");
   }
 
   public void releaseOutputBuffer(int outputBufferIndex)
   {
     this.mediaCodec.releaseOutputBuffer(outputBufferIndex, false);
   }
 
   public void setTrackId(int trackId)
   {
     this.trackId = trackId;
   }
 
   public void close() throws IOException
   {
     super.close();
 
     if (this.surface != null) {
       this.surface.release();
       this.surface = null;
     }
   }
 }

/* Location:           E:\SouceCode\recordGame\gdxDemo\libs\domain-1.2.2415.jar
 * Qualified Name:     com.intel.inde.mp.domain.Encoder
 * JD-Core Version:    0.6.1
 */