 package com.intel.inde.mp.domain;
 
 import com.intel.inde.mp.IVideoEffect;
 import java.io.IOException;
 import java.util.LinkedList;
 
 public class VideoEffector extends MediaCodecPlugin
 {
   private IAndroidMediaObjectFactory factory = null;
   private LinkedList<IVideoEffect> videoEffects = new LinkedList();
   private ISurface encoderSurface;
   private IEffectorSurface internalSurface;
   private float[] matrix = new float[16];
   IPreview previewRender;
   IFrameBuffer frameBuffer;
   private PreviewContext previewContext;
   private Resolution resolution;
 
   public VideoEffector(IMediaCodec mediaCodec, IAndroidMediaObjectFactory factory)
   {
     super(mediaCodec);
     this.factory = factory;
     initInputCommandQueue();
   }
 
   protected void initInputCommandQueue()
   {
     getInputCommandQueue().queue(Command.NeedInputFormat, Integer.valueOf(getTrackId()));
   }
 
   public void enablePreview(IPreview preview) {
     this.previewRender = preview;
   }
 
   public LinkedList<IVideoEffect> getVideoEffects() {
     return this.videoEffects;
   }
 
   protected void feedMeIfNotDraining()
   {
     if ((this.frameCount < 2) && 
       (this.state != PluginState.Draining) && (this.state != PluginState.Drained))
       getInputCommandQueue().queue(Command.NeedData, Integer.valueOf(getTrackId()));
   }
 
   public void push(Frame frame)
   {
     super.push(frame);
     if ((!frame.equals(Frame.empty())) && (!frame.equals(Frame.EOF())))
     {
       initFrameBuffer();
       applyEffects(frame);
       updatePreview();
 
       hasData();
 
       if (this.frameCount < 2)
       {
         this.encoderSurface.setPresentationTime(1000L * frame.getSampleTime());
         this.encoderSurface.swapBuffers();
         this.frameCount += 1;
       }
     }
 
     feedMeIfNotDraining();
   }
 
   private IVideoEffect applyEffects(Frame frame)
   {
     if (this.previewRender == null) {
       this.internalSurface.awaitNewImage();
     }
     if (this.previewRender == null)
       this.internalSurface.getTransformMatrix(this.matrix);
     else {
       this.matrix = this.previewContext.previewTexture.getTransformMatrix();
     }
 
     boolean effectWasApplied = false;
     IVideoEffect appliedEffect = null;
 
     for (IVideoEffect effect : this.videoEffects) {
       Pair segment = effect.getSegment();
       long pts = frame.getSampleTime();
       if (((((Long)segment.left).longValue() <= pts) && (pts <= ((Long)segment.right).longValue())) || ((((Long)segment.left).longValue() == 0L) && (((Long)segment.right).longValue() == 0L))) {
         bindFB();
 
         effect.applyEffect(getInputIndex(), pts, this.matrix);
 
         unbindFB();
 
         effectWasApplied = true;
         appliedEffect = effect;
         break;
       }
     }
     if (!effectWasApplied) {
       bindFB();
       this.internalSurface.drawImage(getInputIndex(), this.matrix);
       unbindFB();
     }
 
     renderOntoEncoderContext();
 
     return appliedEffect;
   }
 
   private void updatePreview() {
     if (this.previewRender == null) {
       return;
     }
     this.previewRender.renderSurfaceFromFrameBuffer(this.frameBuffer.getTextureId());
   }
 
   private void renderOntoEncoderContext() {
     if (this.frameBuffer == null) {
       return;
     }
     this.internalSurface.drawImage2D(this.frameBuffer.getTextureId(), this.matrix);
   }
 
   private int getInputIndex() {
     if (this.previewRender == null) {
       return this.internalSurface.getSurfaceId();
     }
     return this.previewContext.previewTextureId;
   }
 
   private void initFrameBuffer() {
     if ((this.previewRender != null) && (this.frameBuffer == null)) {
       this.frameBuffer = this.factory.createFrameBuffer();
       this.frameBuffer.setResolution(this.resolution);
     }
   }
 
   private void unbindFB() {
     if (this.previewRender != null)
       this.frameBuffer.unbind();
   }
 
   private void bindFB()
   {
     if (this.frameBuffer != null)
       this.frameBuffer.bind();
   }
 
   public void checkIfOutputQueueHasData()
   {
   }
 
   public void releaseOutputBuffer(int outputBufferIndex)
   {
     this.frameCount -= 1;
     feedMeIfNotDraining();
   }
 
   public void pull(Frame frame)
   {
   }
 
   public Frame getFrame() {
     if (this.state == PluginState.Drained) {
       throw new RuntimeException("Out of order operation.");
     }
 
     return new Frame(null, 1, 1L, 0, 0, 0);
   }
 
   public boolean isLastFile()
   {
     return false;
   }
 
   public void start()
   {
     if (this.encoderSurface == null) {
       throw new RuntimeException("Encoder surface not set.");
     }
 
     this.encoderSurface.makeCurrent();
     this.internalSurface = this.factory.createEffectorSurface();
 
     for (IVideoEffect effect : this.videoEffects)
       effect.start();
   }
 
   public void close()
     throws IOException
   {
     stop();
     super.close();
   }
 
   public void stop()
   {
     super.stop();
     if (this.previewRender != null)
     {
       for (IVideoEffect videoEffect : this.videoEffects)
       {
         videoEffect.fitToCurrentSurface(true);
       }
 
       this.previewRender.setListener(null);
 
       this.previewRender.requestRendering();
       this.previewRender = null;
     }
 
     if (this.frameBuffer != null) {
       this.frameBuffer.release();
       this.frameBuffer = null;
     }
 
     if (this.internalSurface != null) {
       this.internalSurface.release();
       this.internalSurface = null;
     }
   }
 
   public void setMediaFormat(MediaFormat mediaFormat)
   {
   }
 
   public void setOutputSurface(ISurface surface)
   {
     this.encoderSurface = surface;
   }
 
   public ISurface getSurface()
   {
     if (this.internalSurface == null) {
       throw new RuntimeException("Effector surface not set.");
     }
     return this.internalSurface;
   }
 
   public void waitForSurface(long pts)
   {
   }
 
   public void configure() {
   }
 
   public boolean canConnectFirst(IOutputRaw connector) {
     return false;
   }
 
   public void onSurfaceAvailable(ISurfaceListener listener)
   {
     if (this.previewRender == null) {
       listener.onSurfaceAvailable(this.factory.getCurrentEglContext());
       return;
     }
     this.previewContext = this.previewRender.getSharedContext();
 
     listener.onSurfaceAvailable(this.previewContext.eglContext);
   }
 
   public void setInputResolution(Resolution resolution)
   {
     this.resolution = resolution;
 
     if (this.previewRender != null)
     {
       for (IVideoEffect videoEffect : this.videoEffects) {
         videoEffect.fitToCurrentSurface(false);
       }
     }
 
     super.setInputResolution(resolution);
 
     for (IVideoEffect videoEffect : this.videoEffects)
       videoEffect.setInputResolution(resolution);
   }
 
   public IPreview getPreview()
   {
     return this.previewRender;
   }
 }

/* Location:           E:\SouceCode\recordGame\gdxDemo\libs\domain-1.2.2415.jar
 * Qualified Name:     com.intel.inde.mp.domain.VideoEffector
 * JD-Core Version:    0.6.1
 */