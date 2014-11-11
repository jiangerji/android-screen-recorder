 package com.intel.inde.mp.domain;
 
 import com.intel.inde.mp.Uri;
 import com.intel.inde.mp.VideoFormat;
 import java.io.FileDescriptor;
 import java.io.IOException;
 import java.nio.ByteBuffer;
 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.LinkedList;
 import java.util.Set;
 
 public class MediaSource
   implements IMediaSource
 {
   private final IMediaExtractor mediaExtractor;
   private CommandQueue commandQueue = new CommandQueue();
   private int lastTrackId = 0;
   private Set<Integer> selectedTracks = new HashSet();
   private PluginState state = PluginState.Drained;
   private Segments segments = new Segments(new ArrayList());
 
   public MediaSource(IMediaExtractor mediaExtractor)
   {
     this.mediaExtractor = mediaExtractor;
   }
 
   public void selectTrack(int trackIndex) {
     if (trackIndex > this.mediaExtractor.getTrackCount() - 1) {
       throw new RuntimeException("Attempt to select non-existing track.");
     }
     this.mediaExtractor.selectTrack(trackIndex);
     this.selectedTracks.add(Integer.valueOf(trackIndex));
   }
 
   public void unselectTrack(int trackIndex) {
     if (trackIndex > this.mediaExtractor.getTrackCount() - 1) {
       throw new RuntimeException("Attempt to unselect non-existing track.");
     }
     this.mediaExtractor.unselectTrack(trackIndex);
     this.selectedTracks.remove(Integer.valueOf(trackIndex));
   }
 
   public CommandQueue getOutputCommandQueue()
   {
     return this.commandQueue;
   }
 
   public void fillCommandQueues()
   {
   }
 
   public void close() throws IOException
   {
     this.mediaExtractor.release();
   }
 
   public void pull(Frame frame)
   {
     if (this.state != PluginState.Normal) {
       throw new IllegalStateException("Attempt to pull frame from not started media source or after EOF.");
     }
 
     readSampleData(frame);
 
     if (!frame.equals(Frame.EOF())) {
       this.mediaExtractor.advance();
       checkIfHasData();
     }
   }
 
   private void readSampleData(Frame frame) {
     frame.setSampleTime(getSampleTime());
     frame.setTrackId(getTrackId());
     frame.setFlags(this.mediaExtractor.getSampleFlags());
     frame.setLength(this.mediaExtractor.readSampleData(frame.getByteBuffer()));
     frame.getByteBuffer().position(0);
   }
 
   private void checkIfHasData() {
     if (this.mediaExtractor.getSampleTrackIndex() == -1) {
       drain();
       return;
     }
 
     if (this.segments.isInsideSegment(this.mediaExtractor.getSampleTime())) {
       this.commandQueue.queue(Command.HasData, Integer.valueOf(this.mediaExtractor.getSampleTrackIndex()));
       this.lastTrackId = this.mediaExtractor.getSampleTrackIndex();
       return;
     }
 
     Pair segmentAfter = this.segments.getSegmentAfter(this.mediaExtractor.getSampleTime());
 
     if (segmentAfter == null) {
       drain();
       return;
     }
 
     seek(((Long)segmentAfter.left).longValue());
     this.lastTrackId = this.mediaExtractor.getSampleTrackIndex();
   }
 
   private int getTrackId()
   {
     int trackId = this.mediaExtractor.getSampleTrackIndex();
     return trackId == -1 ? this.lastTrackId : trackId;
   }
 
   private long getSampleTime() {
     long sampleTime = this.mediaExtractor.getSampleTime();
     this.segments.saveSampleTime(sampleTime);
     return this.segments.shift(sampleTime);
   }
 
   private void drain() {
     this.state = PluginState.Draining;
     this.commandQueue.clear();
     this.commandQueue.queue(Command.EndOfFile, Integer.valueOf(this.lastTrackId));
   }
 
   public Iterable<MediaFormat> getMediaFormats() {
     LinkedList result = new LinkedList();
     for (int i = 0; i < this.mediaExtractor.getTrackCount(); i++) {
       result.add(this.mediaExtractor.getTrackFormat(i));
     }
     return result;
   }
 
   public MediaFormat getMediaFormatByType(MediaFormatType mediaFormatType)
   {
     for (MediaFormat mediaFormat : getMediaFormats()) {
       if (mediaFormat.getMimeType().startsWith(mediaFormatType.toString())) {
         return mediaFormat;
       }
     }
     return null;
   }
 
   public boolean isLastFile()
   {
     return true;
   }
 
   public void incrementConnectedPluginsCount()
   {
   }
 
   public void start()
   {
     this.state = PluginState.Normal;
     if (this.segments.isEmpty()) {
       this.segments.add(new Pair(Long.valueOf(0L), Long.valueOf(getDurationInMicroSec())));
     }
     seek(((Long)this.segments.first().left).longValue());
   }
 
   public int getTrackIdByMediaType(MediaFormatType mediaFormatType) {
     for (int i = 0; i < this.mediaExtractor.getTrackCount(); i++) {
       if ((this.mediaExtractor.getTrackFormat(i) != null) && (this.mediaExtractor.getTrackFormat(i).getMimeType() != null) && (this.mediaExtractor.getTrackFormat(i).getMimeType().startsWith(mediaFormatType.toString())))
       {
         return i;
       }
     }
     return -1;
   }
 
   public long getDurationInMicroSec() {
     long duration = getMaxSelectedTracksDuration();
     if (duration == 0L) {
       duration = getMaxAllTracksDuration();
     }
     return duration;
   }
 
   private long getMaxSelectedTracksDuration() {
     long maxDuration = 0L;
     for (Iterator i$ = this.selectedTracks.iterator(); i$.hasNext(); ) { int trackIndex = ((Integer)i$.next()).intValue();
       if ((this.mediaExtractor.getTrackFormat(trackIndex) != null) && (this.mediaExtractor.getTrackFormat(trackIndex).getDuration() > maxDuration))
       {
         maxDuration = this.mediaExtractor.getTrackFormat(trackIndex).getDuration();
       }
     }
     return maxDuration;
   }
 
   private long getMaxAllTracksDuration() {
     long maxDuration = 0L;
     int i = 0;
     for (MediaFormat ignored : getMediaFormats()) {
       if (this.mediaExtractor.getTrackFormat(i).getDuration() > maxDuration) {
         maxDuration = this.mediaExtractor.getTrackFormat(i).getDuration();
       }
       i++;
     }
     return maxDuration;
   }
 
   public Set<Integer> getSelectedTracks() {
     return this.selectedTracks;
   }
 
   public void seek(long seekPosition) {
     this.mediaExtractor.seekTo(seekPosition, 1);
     this.commandQueue.clear();
 
     if (hasVideoTrack()) {
       while (!isVideoTrack()) {
         this.mediaExtractor.advance();
       }
     }
 
     checkIfHasData();
   }
 
   private boolean hasVideoTrack() {
     for (Integer selectedTrack : this.selectedTracks) {
       if (isVideoTrack(selectedTrack.intValue())) return true;
     }
     return false;
   }
 
   private boolean isVideoTrack() {
     return isVideoTrack(getTrackId());
   }
 
   private boolean isVideoTrack(int trackId) {
     String mimeType = this.mediaExtractor.getTrackFormat(trackId).getMimeType();
     return mimeType.startsWith("video");
   }
 
   public void stop()
   {
     drain();
   }
 
   public boolean canConnectFirst(IInputRaw connector)
   {
     return true;
   }
 
   public int getRotation() {
     return this.mediaExtractor.getRotation();
   }
 
   public String getFilePath() {
     return this.mediaExtractor.getFilePath();
   }
 
   public FileDescriptor getFileDescriptor() {
     return this.mediaExtractor.getFileDescriptor();
   }
 
   public Uri getUri() {
     return this.mediaExtractor.getUri();
   }
 
   public void add(Pair<Long, Long> segment) {
     this.segments.add(segment);
   }
 
   public Collection<Pair<Long, Long>> getSegments() {
     return this.segments.asCollection();
   }
 
   public void insert(Pair<Long, Long> segment, int index) {
     this.segments.add(index, segment);
   }
 
   public void removeSegment(int index) {
     this.segments.remove(index);
   }
 
   public long getSegmentsDurationInMicroSec() {
     if (this.segments.isEmpty()) {
       return getDurationInMicroSec();
     }
 
     long totalDuration = 0L;
     for (Pair segment : this.segments.asCollection()) {
       totalDuration += ((Long)segment.right).longValue() - ((Long)segment.left).longValue();
     }
     return totalDuration;
   }
 
   public Resolution getOutputResolution()
   {
     VideoFormat videoFormat = (VideoFormat)getMediaFormatByType(MediaFormatType.VIDEO);
 
     if (videoFormat == null) {
       throw new UnsupportedOperationException("Failed to get output resolution.");
     }
 
     return videoFormat.getVideoFrameSize();
   }
 }

/* Location:           E:\SouceCode\recordGame\gdxDemo\libs\domain-1.2.2415.jar
 * Qualified Name:     com.intel.inde.mp.domain.MediaSource
 * JD-Core Version:    0.6.1
 */