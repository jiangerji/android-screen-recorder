 package com.intel.inde.mp.domain;
 
 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.Dictionary;
 import java.util.Hashtable;
 import java.util.Iterator;
 import java.util.List;
 
 class Segments
 {
   private ArrayList<Pair<Long, Long>> segments = new ArrayList();
   private Dictionary<Pair<Long, Long>, Long> lastSegmentSampleTime = new Hashtable();
 
   SegmentListener segmentListener = new SegmentListener();
 
   private Pair<Long, Long> currentSegment = null;
 
   public Segments(List<Pair<Long, Long>> segments) {
     for (Pair segment : segments)
       add(segment);
   }
 
   public boolean isInsideSegment(long sampleTime)
   {
     if (this.segments.isEmpty()) return true;
     return getSegmentByTime(sampleTime) != null;
   }
 
   public void saveSampleTime(long sampleTime) {
     Pair segment = getSegmentByTime(sampleTime);
     if (segment == null) return;
     Long last = (Long)this.lastSegmentSampleTime.get(segment);
     if (last == null) {
       this.lastSegmentSampleTime.put(segment, Long.valueOf(sampleTime));
       return;
     }
     if (last.longValue() < sampleTime)
       this.lastSegmentSampleTime.put(segment, Long.valueOf(sampleTime));
   }
 
   public long shift(long sampleTime)
   {
     long shiftedSampleTime = sampleTime;
     if (getCurrentSegmentTimeShift(sampleTime) != 0L) {
       shiftedSampleTime -= getCurrentSegmentTimeShift(sampleTime);
       shiftedSampleTime += getPreviousSegmentsTimeShift(sampleTime);
     }
     return shiftedSampleTime;
   }
 
   public Pair<Long, Long> getSegmentAfter(long sampleTime) {
     for (Pair segment : this.segments) {
       if (sampleTime < ((Long)segment.left).longValue()) {
         return segment;
       }
     }
     return null;
   }
 
   private Pair<Long, Long> getSegmentByTime(long sampleTime) {
     for (Pair segment : this.segments) {
       if ((((Long)segment.left).longValue() <= sampleTime) && (sampleTime < ((Long)segment.right).longValue())) {
         return segment;
       }
     }
     return null;
   }
 
   private long getCurrentSegmentTimeShift(long sampleTime) {
     long timeShift = 0L;
     Pair segment = getSegmentByTime(sampleTime);
     if (segment != null) {
       timeShift = ((Long)segment.left).longValue();
     }
     return timeShift;
   }
 
   private long getPreviousSegmentsTimeShift(long sampleTime) {
     long timeShift = 0L;
     for (Pair previousSegment : this.segments) {
       if (((Long)previousSegment.right).longValue() < sampleTime) {
         timeShift += ((Long)this.lastSegmentSampleTime.get(previousSegment)).longValue() - ((Long)previousSegment.left).longValue();
       }
     }
     return timeShift;
   }
 
   public boolean isEmpty() {
     return this.segments.isEmpty();
   }
 
   public void add(Pair<Long, Long> pair) {
     Pair arrangedPair = arrange(pair);
     if (arrangedPair != null) {
       this.segments.add(arrangedPair);
       this.segmentListener.segmentAdd();
     }
   }
 
   private Pair<Long, Long> arrange(Pair<Long, Long> pair) {
     Pair arrangedPair = new Pair(pair.left, pair.right);
 
     Iterator segmentIterator = this.segments.iterator();
     while (segmentIterator.hasNext()) {
       Pair segment = (Pair)segmentIterator.next();
       if ((((Long)arrangedPair.left).longValue() <= ((Long)segment.left).longValue()) && (((Long)segment.right).longValue() <= ((Long)arrangedPair.right).longValue())) {
         segmentIterator.remove();
       }
     }
 
     if ((getSegmentByTime(((Long)pair.left).longValue()) == null) && (getSegmentByTime(((Long)pair.right).longValue()) == null)) {
       return arrangedPair;
     }
 
     if (getSegmentByTime(((Long)pair.left).longValue()) == getSegmentByTime(((Long)pair.right).longValue())) {
       return null;
     }
 
     Pair overlappingSegment = getSegmentByTime(((Long)pair.left).longValue());
     if (overlappingSegment != null) {
       arrangedPair.left = overlappingSegment.right;
     }
 
     overlappingSegment = getSegmentByTime(((Long)pair.right).longValue());
     if (overlappingSegment != null) {
       arrangedPair.right = overlappingSegment.left;
     }
 
     return arrangedPair;
   }
 
   public Pair<Long, Long> first() {
     return (Pair)this.segments.get(0);
   }
 
   public Collection<Pair<Long, Long>> asCollection() {
     return new ArrayList(this.segments);
   }
 
   public void add(int index, Pair<Long, Long> segment) {
     this.segments.add(index, segment);
     this.segmentListener.segmentAdd();
   }
 
   public void remove(int index) {
     this.segments.remove(index);
   }
 
   public boolean checkSegmentChanged(long sampleTime) {
     if (this.segments.size() == 1) {
       return false;
     }
     if (this.currentSegment == getSegmentAfter(sampleTime)) {
       this.currentSegment = getSegmentAfter(sampleTime);
       return true;
     }
 
     return false;
   }
 
   public class SegmentListener
   {
     public SegmentListener()
     {
     }
 
     public void segmentAdd()
     {
       if (Segments.this.segments.size() == 1)
         Segments.this.currentSegment = ((Pair)Segments.this.segments.get(0));
     }
 
     public void segmentRemove()
     {
     }
   }
 }

/* Location:           E:\SouceCode\recordGame\gdxDemo\libs\domain-1.2.2415.jar
 * Qualified Name:     com.intel.inde.mp.domain.Segments
 * JD-Core Version:    0.6.1
 */