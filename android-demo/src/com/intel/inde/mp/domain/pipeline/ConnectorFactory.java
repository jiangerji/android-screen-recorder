 package com.intel.inde.mp.domain.pipeline;
 
 import com.intel.inde.mp.AudioFormat;
 import com.intel.inde.mp.domain.AudioDecoder;
 import com.intel.inde.mp.domain.AudioEffector;
 import com.intel.inde.mp.domain.AudioEncoder;
 import com.intel.inde.mp.domain.Encoder;
 import com.intel.inde.mp.domain.ICameraSource;
 import com.intel.inde.mp.domain.ICaptureSource;
 import com.intel.inde.mp.domain.ICommandProcessor;
 import com.intel.inde.mp.domain.IInputRaw;
 import com.intel.inde.mp.domain.IMediaSource;
 import com.intel.inde.mp.domain.IMicrophoneSource;
 import com.intel.inde.mp.domain.IOutputRaw;
 import com.intel.inde.mp.domain.IPluginOutput;
 import com.intel.inde.mp.domain.IsConnectable;
 import com.intel.inde.mp.domain.MediaFormatType;
 import com.intel.inde.mp.domain.MediaSource;
 import com.intel.inde.mp.domain.PassThroughPlugin;
 import com.intel.inde.mp.domain.Plugin;
 import com.intel.inde.mp.domain.Render;
 import com.intel.inde.mp.domain.SurfaceRender;
 import com.intel.inde.mp.domain.VideoDecoder;
 import com.intel.inde.mp.domain.VideoEffector;
 import com.intel.inde.mp.domain.VideoEncoder;
 import java.util.Collection;
 import java.util.LinkedList;
 
 public class ConnectorFactory
 {
   private final ICommandProcessor commandProcessor;
   private final AudioFormat audioMediaFormat;
 
   public ConnectorFactory(ICommandProcessor commandProcessor, AudioFormat audioMediaFormat)
   {
     this.commandProcessor = commandProcessor;
     this.audioMediaFormat = audioMediaFormat;
   }
 
   public void connect(IOutputRaw source, IInputRaw transform)
   {
     if (((source instanceof IMediaSource)) && ((transform instanceof Plugin))) {
       new PluginConnector(this.commandProcessor).connect((IMediaSource)source, (Plugin)transform);
       return;
     }
 
     if (((source instanceof IMediaSource)) && ((transform instanceof Render))) {
       new PluginConnector(this.commandProcessor).connect((IMediaSource)source, (Render)transform);
       return;
     }
 
     if (((source instanceof VideoDecoder)) && ((transform instanceof VideoEncoder))) {
       new PluginConnector(this.commandProcessor).connect((VideoDecoder)source, (VideoEncoder)transform);
       return;
     }
 
     if (((source instanceof AudioDecoder)) && ((transform instanceof AudioEncoder))) {
       new PluginConnector(this.commandProcessor).connect((AudioDecoder)source, (AudioEncoder)transform, this.audioMediaFormat);
       return;
     }
 
     if (((source instanceof VideoEffector)) && ((transform instanceof VideoEncoder))) {
       new PluginConnector(this.commandProcessor).connect((VideoEffector)source, (VideoEncoder)transform);
       return;
     }
 
     if (((source instanceof VideoDecoder)) && ((transform instanceof VideoEffector))) {
       new PluginConnector(this.commandProcessor).connect((VideoDecoder)source, (VideoEffector)transform);
       return;
     }
 
     if (((source instanceof VideoDecoder)) && ((transform instanceof SurfaceRender))) {
       new PluginConnector(this.commandProcessor).connect((VideoDecoder)source, (SurfaceRender)transform);
     }
 
     if (((source instanceof AudioEffector)) && ((transform instanceof AudioEncoder))) {
       new PluginConnector(this.commandProcessor).connect((AudioEffector)source, (AudioEncoder)transform, this.audioMediaFormat);
       return;
     }
 
     if (((source instanceof AudioDecoder)) && ((transform instanceof AudioEffector))) {
       new PluginConnector(this.commandProcessor).connect((AudioDecoder)source, (AudioEffector)transform);
       return;
     }
 
     if (((source instanceof ICaptureSource)) && ((transform instanceof Encoder))) {
       new PluginConnector(this.commandProcessor).connect((ICaptureSource)source, (Encoder)transform);
       return;
     }
 
     if (((source instanceof ICameraSource)) && ((transform instanceof Encoder))) {
       new PluginConnector(this.commandProcessor).connect((ICameraSource)source, (Encoder)transform);
       return;
     }
 
     if (((source instanceof ICameraSource)) && ((transform instanceof VideoEffector))) {
       new PluginConnector(this.commandProcessor).connect((ICameraSource)source, (VideoEffector)transform);
       return;
     }
 
     if (((source instanceof IMicrophoneSource)) && ((transform instanceof AudioEncoder))) {
       new PluginConnector(this.commandProcessor).connect((IMicrophoneSource)source, (AudioEncoder)transform);
       return;
     }
 
     if (((source instanceof IPluginOutput)) && ((transform instanceof Render))) {
       new PluginConnector(this.commandProcessor).connect((IPluginOutput)source, (Render)transform);
       return;
     }
 
     throw new RuntimeException("No connection between " + source.getClass().toString() + " and " + transform.getClass().toString());
   }
 
   public Collection<IsConnectable> createConnectionRules() {
     Collection collection = new LinkedList();
     collection.add(OneToOneConnectable.OneToOneConnection(ICaptureSource.class, VideoEncoder.class));
     collection.add(OneToOneConnectable.OneToOneConnection(ICaptureSource.class, VideoEffector.class));
     collection.add(OneToOneConnectable.OneToOneConnection(ICameraSource.class, VideoEncoder.class));
     collection.add(OneToOneConnectable.OneToOneConnection(ICameraSource.class, VideoEffector.class));
     collection.add(OneToOneConnectable.OneToOneConnection(VideoDecoder.class, VideoEncoder.class));
     collection.add(OneToOneConnectable.OneToOneConnection(VideoDecoder.class, VideoEffector.class));
     collection.add(OneToOneConnectable.OneToOneConnection(VideoDecoder.class, SurfaceRender.class));
     collection.add(OneToOneConnectable.OneToOneConnection(VideoEncoder.class, Render.class));
     collection.add(OneToOneConnectable.OneToOneConnection(VideoEffector.class, VideoEncoder.class));
     collection.add(OneToOneConnectable.OneToOneConnection(AudioEffector.class, AudioEncoder.class));
     collection.add(OneToOneConnectable.OneToOneConnection(IMicrophoneSource.class, AudioEncoder.class));
     collection.add(OneToOneConnectable.OneToOneConnection(IMediaSource.class, Render.class));
 
     collection.add(new OneToOneConnectable(AudioDecoder.class, AudioEffector.class)
     {
       public boolean additionalCheck(IOutputRaw output, IInputRaw input) {
         MediaFormatType mediaFormatType = ((AudioDecoder)output).getMediaFormatType();
         return mediaFormatType == MediaFormatType.AUDIO;
       }
     });
     collection.add(new OneToOneConnectable(AudioDecoder.class, AudioEncoder.class)
     {
       public boolean additionalCheck(IOutputRaw output, IInputRaw input) {
         MediaFormatType mediaFormatType = ((AudioDecoder)output).getMediaFormatType();
         return mediaFormatType == MediaFormatType.AUDIO;
       }
     });
     collection.add(ManyToOneConnectable.ManyToOneConnections(new ManyTypes(new Class[] { PassThroughPlugin.class, Encoder.class, AudioEncoder.class }), Render.class));
 
     collection.add(OneToManyConnectable.OneToManyConnection(MediaSource.class, new ManyTypes(new Class[] { VideoDecoder.class, AudioDecoder.class })));
 
     collection.add(OneToManyConnectable.OneToManyConnection(IMediaSource.class, new ManyTypes(new Class[] { VideoDecoder.class, AudioDecoder.class, PassThroughPlugin.class })));
 
     return collection;
   }
 }

/* Location:           E:\SouceCode\recordGame\gdxDemo\libs\domain-1.2.2415.jar
 * Qualified Name:     com.intel.inde.mp.domain.pipeline.ConnectorFactory
 * JD-Core Version:    0.6.1
 */