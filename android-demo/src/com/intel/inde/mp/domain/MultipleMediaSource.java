 package com.intel.inde.mp.domain;
 
 import com.intel.inde.mp.AudioFormat;
 import com.intel.inde.mp.MediaFile;
 import com.intel.inde.mp.VideoFormat;
 import java.io.IOException;
 import java.util.Collection;
 import java.util.Dictionary;
 import java.util.Hashtable;
 import java.util.Iterator;
 import java.util.LinkedList;
 import java.util.List;
 import java.util.Set;
 
 public class MultipleMediaSource
   implements IMediaSource
 {
   private LinkedList<MediaFile> mediaFiles = new LinkedList();
   private Iterator<MediaFile> mediaFileIterator = null;
   private MediaFile currentMediaFile = null;
   private Hashtable<Integer, Long> sampleTimeOffsets = new Hashtable();
   private Hashtable<Integer, Long> currentSampleTimes = new Hashtable();
   private CommandQueue commandQueue = new CommandQueue();
   private boolean isLastFile = true;
   private int connectedPluginsCount = 0;
   private int nextFileRequest = 0;
   private Dictionary<Integer, Integer> trackIdMap = new Hashtable();
 
   public CommandQueue getOutputCommandQueue()
   {
     return this.commandQueue;
   }
 
   public void fillCommandQueues()
   {
   }
 
   public void close() throws IOException
   {
     for (MediaFile mediaFile : this.mediaFiles)
       mediaFile.getMediaSource().close();
   }
 
   public void pull(Frame frame)
   {
     if (this.currentMediaFile == this.mediaFiles.getLast()) {
       this.isLastFile = true;
     }
 
     pullFrameFromMediaSource(frame);
     hasData();
 
     this.currentSampleTimes.put(Integer.valueOf(frame.getTrackId()), Long.valueOf(frame.getSampleTime()));
 
     if ((isLastFrame()) && (!isLastFile()))
       switchToNextFile();
   }
 
   public void nextFile()
   {
     this.nextFileRequest += 1;
 
     if (this.nextFileRequest == this.connectedPluginsCount) {
       hasData();
       this.nextFileRequest = 0;
     }
   }
 
   public MediaFormat getMediaFormatByType(MediaFormatType mediaFormatType)
   {
     for (MediaFormat mediaFormat : this.currentMediaFile.getMediaSource().getMediaFormats()) {
       if (mediaFormat.getMimeType().startsWith(mediaFormatType.toString())) {
         return mediaFormat;
       }
     }
     return null;
   }
 
   private void pullFrameFromMediaSource(Frame frame) {
     Pair firstCommand = this.currentMediaFile.getMediaSource().getOutputCommandQueue().dequeue();
 
     if (firstCommand.left == Command.HasData) {
       this.currentMediaFile.getMediaSource().pull(frame);
       frame.trackId = mapTrackId(frame.trackId);
       frame.setSampleTime(safeGet((Long)this.sampleTimeOffsets.get(Integer.valueOf(frame.getTrackId()))) + frame.getSampleTime());
     }
   }
 
   private int mapTrackId(int sourceTrackId) {
     if (this.trackIdMap.get(Integer.valueOf(sourceTrackId)) != null) {
       return ((Integer)this.trackIdMap.get(Integer.valueOf(sourceTrackId))).intValue();
     }
     return sourceTrackId;
   }
 
   private long safeGet(Long value) {
     return value == null ? 0L : value.longValue();
   }
 
   private void switchToNextFile() {
     long maxCurrentTimeOffset = getMaxCurrentTimeOffset();
     for (Iterator i$ = this.currentSampleTimes.keySet().iterator(); i$.hasNext(); ) { int key = ((Integer)i$.next()).intValue();
       this.sampleTimeOffsets.put(Integer.valueOf(key), Long.valueOf(maxCurrentTimeOffset + 1L));
     }
 
     this.currentMediaFile = ((MediaFile)this.mediaFileIterator.next());
     this.currentMediaFile.start();
   }
 
   private boolean isLastFrame()
   {
     CommandQueue queue = this.currentMediaFile.getMediaSource().getOutputCommandQueue();
     Pair command = queue.first();
 
     if (command == null) return false;
 
     return (queue.size() == 1) && (command.left == Command.EndOfFile);
   }
 
   public boolean isLastFile() {
     return this.isLastFile;
   }
 
   public void incrementConnectedPluginsCount()
   {
     this.connectedPluginsCount += 1;
   }
 
   public void start()
   {
     this.currentMediaFile.start();
     hasData();
   }
 
   public List<MediaFile> files() {
     return this.mediaFiles;
   }
 
   public void add(MediaFile mediaFile) throws RuntimeException {
     validate(mediaFile);
 
     this.mediaFiles.add(mediaFile);
     this.mediaFileIterator = this.mediaFiles.iterator();
     this.currentMediaFile = ((MediaFile)this.mediaFileIterator.next());
     this.isLastFile = (this.mediaFiles.size() == 1);
   }
 
   private void validate(MediaFile mediaFile) throws RuntimeException {
     if (this.mediaFiles.size() == 0) return;
 
     AudioFormat newFileAudioFormat = (AudioFormat)mediaFile.getMediaSource().getMediaFormatByType(MediaFormatType.AUDIO);
     AudioFormat firstFileAudioFormat = (AudioFormat)((MediaFile)this.mediaFiles.getFirst()).getMediaSource().getMediaFormatByType(MediaFormatType.AUDIO);
 
     if (firstFileAudioFormat == null) return;
 
     if (newFileAudioFormat == null)
       throw new RuntimeException("The stream you are trying to add has no audio track, but the first added stream has audio track. Please select a stream with audio track.");
   }
 
   public long getMaxCurrentTimeOffset()
   {
     long max = 0L;
     for (Iterator i$ = this.currentSampleTimes.values().iterator(); i$.hasNext(); ) { long currentTimeOffset = ((Long)i$.next()).longValue();
       if (currentTimeOffset > max) {
         max = currentTimeOffset;
       }
     }
     return max;
   }
 
   private void hasData() {
     Pair firstCommand = this.currentMediaFile.getMediaSource().getOutputCommandQueue().first();
     if (firstCommand == null) return;
 
     firstCommand.right = Integer.valueOf(mapTrackId(((Integer)firstCommand.right).intValue()));
 
     if (firstCommand.left != Command.EndOfFile)
       this.commandQueue.queue((Command)firstCommand.left, (Integer)firstCommand.right);
     else if (!this.isLastFile)
       queueCommand(Command.OutputFormatChanged);
     else
       queueCommand(Command.EndOfFile);
   }
 
   private void queueCommand(Command command)
   {
     for (Iterator i$ = this.currentMediaFile.getMediaSource().getSelectedTracks().iterator(); i$.hasNext(); ) { int trackId = ((Integer)i$.next()).intValue();
       this.commandQueue.queue(command, Integer.valueOf(mapTrackId(trackId)));
     }
   }
 
   public int getTrackIdByMediaType(MediaFormatType mediaFormatType)
   {
     return this.currentMediaFile.getMediaSource().getTrackIdByMediaType(mediaFormatType);
   }
 
   public void selectTrack(int trackId)
   {
     for (MediaFile mediaFile : this.mediaFiles)
       mediaFile.getMediaSource().selectTrack(trackId);
   }
 
   public void setTrackMap(int source, int target)
   {
     this.trackIdMap.put(Integer.valueOf(source), Integer.valueOf(target));
   }
 
   public void stop()
   {
     this.commandQueue.clear();
     queueCommand(Command.EndOfFile);
   }
 
   public boolean canConnectFirst(IInputRaw connector)
   {
     return true;
   }
 
   public long getSegmentsDurationInMicroSec() {
     long totalDuration = 0L;
     for (MediaFile mediaFile : this.mediaFiles) {
       totalDuration += mediaFile.getSegmentsDurationInMicroSec();
     }
     return totalDuration;
   }
 
   public void remove(MediaFile mediaFile) {
     this.mediaFiles.remove(mediaFile);
   }
 
   public void insertAt(int index, MediaFile mediaFile) {
     this.mediaFiles.add(index, mediaFile);
   }
 
   public Resolution getOutputResolution()
   {
     VideoFormat videoFormat = (VideoFormat)getMediaFormatByType(MediaFormatType.VIDEO);
     return videoFormat == null ? new Resolution(0, 0) : videoFormat.getVideoFrameSize();
   }
 
   public boolean hasTrack(MediaFormatType mediaFormatType) {
     return getTrackIdByMediaType(mediaFormatType) != -1;
   }
 
   public void verify()
   {
     for (MediaFile mediaFile : this.mediaFiles)
     {
       boolean withAudio = true;
       boolean withVideo = true;
 
       if (mediaFile.getMediaSource().getTrackIdByMediaType(MediaFormatType.VIDEO) == -1) {
         withVideo = false;
       }
 
       if (mediaFile.getMediaSource().getTrackIdByMediaType(MediaFormatType.AUDIO) == -1) {
         withAudio = true;
       }
 
       boolean videoNoAudio = false;
       boolean videoAudio = false;
       boolean audioNoVideo = false;
 
       if ((withVideo == true) && (!withAudio)) videoNoAudio = true;
       if ((withVideo == true) && (withAudio == true)) videoAudio = true;
       if ((withAudio == true) && (!withVideo)) audioNoVideo = true;
 
       if ((videoAudio == true) && (audioNoVideo == true)) {
         throw new RuntimeException("Cannot process files with and without video in the same pipeline.");
       }
       if ((videoAudio == true) && (videoNoAudio == true)) {
         throw new RuntimeException("Cannot process files with and without audio in the same pipeline.");
       }
       if ((videoNoAudio == true) && (audioNoVideo == true))
         throw new RuntimeException("Cannot process files with and without video in the same pipeline.");
     }
   }
 }

/* Location:           E:\SouceCode\recordGame\gdxDemo\libs\domain-1.2.2415.jar
 * Qualified Name:     com.intel.inde.mp.domain.MultipleMediaSource
 * JD-Core Version:    0.6.1
 */