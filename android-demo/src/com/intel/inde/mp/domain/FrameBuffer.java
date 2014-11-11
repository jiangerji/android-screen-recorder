 package com.intel.inde.mp.domain;
 
 import java.util.HashSet;
 import java.util.LinkedList;
 import java.util.Queue;
 import java.util.Set;
 
 class FrameBuffer
 {
   private int numberOfTracks;
   private final Set<Integer> configuredTrackIndexes = new HashSet();
   private final Queue<Frame> frames = new LinkedList();
 
   public FrameBuffer(int numberOfTracks) {
     this.numberOfTracks = numberOfTracks;
   }
 
   public void configure(int trackIndex) {
     this.configuredTrackIndexes.add(Integer.valueOf(trackIndex));
   }
 
   public boolean areAllTracksConfigured() {
     return this.numberOfTracks == this.configuredTrackIndexes.size();
   }
 
   public void push(Frame frame) {
     this.frames.add(frame);
   }
 
   public boolean canPull() {
     return (areAllTracksConfigured()) && (!this.frames.isEmpty());
   }
 
   public Frame pull() {
     return (Frame)this.frames.poll();
   }
 
   public void addTrack() {
     this.numberOfTracks += 1;
   }
 }

/* Location:           E:\SouceCode\recordGame\gdxDemo\libs\domain-1.2.2415.jar
 * Qualified Name:     com.intel.inde.mp.domain.FrameBuffer
 * JD-Core Version:    0.6.1
 */