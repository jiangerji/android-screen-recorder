 package com.intel.inde.mp.android;
 
 import android.content.Context;
 import android.media.MediaExtractor;
 import android.media.MediaMetadataRetriever;
 import com.intel.inde.mp.domain.IMediaExtractor;
 import java.io.FileDescriptor;
 import java.io.IOException;
 import java.nio.ByteBuffer;
 
 public class MediaExtractorPlugin
   implements IMediaExtractor
 {
   private MediaExtractor mediaExtractor = new MediaExtractor();
   private String path;
   private FileDescriptor fileDescriptor;
   private Context context;
   private com.intel.inde.mp.Uri uri;
 
   public void setDataSource(String path)
     throws IOException
   {
     this.path = path;
     this.mediaExtractor.setDataSource(path);
   }
 
   public void setDataSource(FileDescriptor fileDescriptor) throws IOException {
     this.fileDescriptor = fileDescriptor;
     this.mediaExtractor.setDataSource(fileDescriptor);
   }
 
   public void setDataSource(Context context, com.intel.inde.mp.Uri uri) throws IOException {
     this.context = context;
     this.uri = uri;
     this.mediaExtractor.setDataSource(context, android.net.Uri.parse(uri.getString()), null);
   }
 
   public int getTrackCount()
   {
     return this.mediaExtractor.getTrackCount();
   }
 
   public com.intel.inde.mp.domain.MediaFormat getTrackFormat(int index)
   {
     if (this.mediaExtractor.getTrackFormat(index).getString("mime").contains("video"))
       return new VideoFormatAndroid(this.mediaExtractor.getTrackFormat(index));
     if (this.mediaExtractor.getTrackFormat(index).getString("mime").contains("audio")) {
       return new AudioFormatAndroid(this.mediaExtractor.getTrackFormat(index));
     }
     return null;
   }
 
   public void selectTrack(int index)
   {
     this.mediaExtractor.selectTrack(index);
   }
 
   public void unselectTrack(int index)
   {
     this.mediaExtractor.unselectTrack(index);
   }
 
   public int getSampleTrackIndex()
   {
     return this.mediaExtractor.getSampleTrackIndex();
   }
 
   public boolean advance()
   {
     return this.mediaExtractor.advance();
   }
 
   public void release()
   {
     this.mediaExtractor.release();
   }
 
   public int getSampleFlags()
   {
     return this.mediaExtractor.getSampleFlags();
   }
 
   public void seekTo(long timeUs, int mode)
   {
     this.mediaExtractor.seekTo(timeUs, mode);
   }
 
   public int getRotation()
   {
     MediaMetadataRetriever retriever = new MediaMetadataRetriever();
     if (this.path != null)
       retriever.setDataSource(this.path);
     else if (this.fileDescriptor != null)
       retriever.setDataSource(this.fileDescriptor);
     else if (this.uri != null)
       retriever.setDataSource(this.context, android.net.Uri.parse(this.uri.getString()));
     else {
       throw new IllegalStateException("File not set");
     }
     String rotation = retriever.extractMetadata(24);
     retriever.release();
     return Integer.parseInt(rotation);
   }
 
   public int readSampleData(ByteBuffer inputBuffer)
   {
     return this.mediaExtractor.readSampleData(inputBuffer, 0);
   }
 
   public long getSampleTime()
   {
     return this.mediaExtractor.getSampleTime();
   }
 
   public String getFilePath()
   {
     return this.path;
   }
 
   public FileDescriptor getFileDescriptor() {
     return this.fileDescriptor;
   }
 
   public com.intel.inde.mp.Uri getUri() {
     return this.uri;
   }
 }

/* Location:           E:\SouceCode\recordGame\gdxDemo\libs\android-1.2.2415.jar
 * Qualified Name:     com.intel.inde.mp.android.MediaExtractorPlugin
 * JD-Core Version:    0.6.1
 */