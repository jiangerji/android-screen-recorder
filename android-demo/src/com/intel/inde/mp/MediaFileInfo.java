 package com.intel.inde.mp;
 
 import com.intel.inde.mp.domain.Command;
 import com.intel.inde.mp.domain.CommandQueue;
 import com.intel.inde.mp.domain.Frame;
 import com.intel.inde.mp.domain.IAndroidMediaObjectFactory;
 import com.intel.inde.mp.domain.ISurfaceWrapper;
 import com.intel.inde.mp.domain.MediaFormat;
 import com.intel.inde.mp.domain.MediaFormatType;
 import com.intel.inde.mp.domain.MediaSource;
 import com.intel.inde.mp.domain.Pair;
 import com.intel.inde.mp.domain.VideoDecoder;
 import java.io.FileDescriptor;
 import java.io.IOException;
 import java.nio.ByteBuffer;
 
 public class MediaFileInfo
 {
   private IAndroidMediaObjectFactory factory = null;
   MediaFile file;
   MediaSource source;
   VideoDecoder videoDecoder;
   MediaFormat videoFormat = null;
   MediaFormat audioFormat = null;
   private ISurfaceWrapper outputSurface = null;
   private FileDescriptor fileDescriptor;
 
   public MediaFileInfo(IAndroidMediaObjectFactory factory)
   {
     this.factory = factory;
   }
 
   public void setFileName(String fileName)
     throws IOException
   {
     this.source = this.factory.createMediaSource(fileName);
     prepareMediaFile();
   }
 
   public void setFileDescriptor(FileDescriptor fileDescriptor)
     throws IOException
   {
     this.source = this.factory.createMediaSource(fileDescriptor);
     prepareMediaFile();
   }
 
   public void setUri(Uri fileUri)
     throws IOException
   {
     this.source = this.factory.createMediaSource(fileUri);
     prepareMediaFile();
   }
 
   public String getFileName()
   {
     if (null == this.file) {
       return null;
     }
     return this.file.getFilePath();
   }
 
   public FileDescriptor getFileDescriptor()
   {
     if (null == this.file) {
       return null;
     }
     return this.file.getFileDescriptor();
   }
 
   public Uri getUri()
   {
     if (null == this.file) {
       return null;
     }
     return this.file.getUri();
   }
 
   private void prepareMediaFile()
   {
     this.file = new MediaFile(this.source);
     int index = 0;
     for (MediaFormat ignored : this.source.getMediaFormats()) {
       this.source.selectTrack(index++);
     }
 
     this.videoFormat = this.file.getVideoFormat(0);
     this.audioFormat = this.file.getAudioFormat(0);
   }
 
   public void setOutputSurface(ISurfaceWrapper surface)
   {
     this.outputSurface = surface;
   }
 
   public MediaFormat getVideoFormat()
   {
     return this.videoFormat;
   }
 
   public MediaFormat getAudioFormat()
   {
     return this.audioFormat;
   }
 
   public long getDurationInMicroSec()
   {
     return this.file.getDurationInMicroSec();
   }
 
   public void getFrameAtPosition(long time, ByteBuffer buffer)
     throws IOException
   {
     this.videoDecoder = this.factory.createVideoDecoder(this.videoFormat);
     this.videoDecoder.setMediaFormat(this.videoFormat);
     this.videoDecoder.setOutputSurface(this.outputSurface);
     this.videoDecoder.configure();
     this.videoDecoder.setTrackId(this.source.getTrackIdByMediaType(MediaFormatType.VIDEO));
     this.videoDecoder.start();
 
     if (null != this.audioFormat) {
       this.source.unselectTrack(this.source.getTrackIdByMediaType(MediaFormatType.AUDIO));
     }
 
     this.source.start();
     this.source.seek(time);
 
     Frame frame = null;
     Frame outputFrame = new Frame(buffer, 8294400, 0L, 0, 0, 0);
 
     CommandQueue sourceOutputQueue = this.source.getOutputCommandQueue();
 
     while (sourceOutputQueue.size() != 0)
     {
       Pair sourceOutputCommand = sourceOutputQueue.first();
 
       if ((sourceOutputCommand == null) || (sourceOutputCommand.left == Command.EndOfFile))
       {
         break;
       }
       this.videoDecoder.fillCommandQueues();
 
       CommandQueue videoDecoderInputQueue = this.videoDecoder.getInputCommandQueue();
       Pair videoDecoderInputCommand = videoDecoderInputQueue.first();
 
       if ((videoDecoderInputQueue.size() == 0) || (videoDecoderInputCommand == null)) {
         break;
       }
       if (videoDecoderInputCommand.left == Command.NeedData) {
         frame = this.videoDecoder.findFreeFrame();
       } else if (videoDecoderInputCommand.left == Command.NeedInputFormat) {
         videoDecoderInputQueue.dequeue();
         videoDecoderInputQueue.queue(Command.NeedData, Integer.valueOf(this.videoDecoder.getTrackId()));
         continue;
       }
 
       if (frame != null) {
         this.source.pull(frame);
         this.videoDecoder.push(frame);
 
         sourceOutputQueue.dequeue();
         videoDecoderInputQueue.dequeue();
       } else {
         try {
           Thread.sleep(500L);
         } catch (InterruptedException e) {
           e.printStackTrace();
         }
       }
 
       CommandQueue videoDecoderOutputQueue = this.videoDecoder.getOutputCommandQueue();
       Pair videoDecoderOutputCommand = videoDecoderOutputQueue.first();
 
       if ((videoDecoderOutputQueue.size() != 0) && (videoDecoderOutputCommand != null)) {
         if (videoDecoderOutputCommand.left == Command.HasData) {
           if (this.outputSurface != null) {
             Frame decoderFrame = this.videoDecoder.getFrame();
             this.videoDecoder.releaseOutputBuffer(decoderFrame.getBufferIndex());
             break;
           }this.videoDecoder.pull(outputFrame);
 
           break;
         }if (videoDecoderOutputCommand.left == Command.OutputFormatChanged) {
           videoDecoderOutputQueue.dequeue();
         }
       }
     }
 
     sourceOutputQueue.clear();
     this.videoDecoder.close();
   }
 
   public int getRotation()
   {
     return this.file.getRotation();
   }
 }

/* Location:           E:\SouceCode\recordGame\gdxDemo\libs\domain-1.2.2415.jar
 * Qualified Name:     com.intel.inde.mp.MediaFileInfo
 * JD-Core Version:    0.6.1
 */