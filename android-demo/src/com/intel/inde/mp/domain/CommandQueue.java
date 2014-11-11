 package com.intel.inde.mp.domain;
 
 import java.util.Iterator;
 import java.util.LinkedList;
 
 public class CommandQueue
   implements Iterable<Pair<Command, Integer>>
 {
   protected LinkedList<Pair<Command, Integer>> queue = new LinkedList();
 
   public CommandQueue() {
   }
 
   public CommandQueue(CommandQueue commandQueue) {
     for (Pair pair : commandQueue)
       queue((Command)pair.left, (Integer)pair.right);
   }
 
   public void queue(Command command, Integer trackId)
   {
     Pair pair = new Pair(command, trackId);
     this.queue.add(pair);
   }
 
   public Pair<Command, Integer> dequeue() {
     return (Pair)this.queue.poll();
   }
 
   public Iterator<Pair<Command, Integer>> iterator()
   {
     return this.queue.iterator();
   }
 
   public Pair<Command, Integer> first() {
     if (size() == 0) {
       return null;
     }
 
     return (Pair)this.queue.peek();
   }
 
   public Pair<Command, Integer> last() {
     if (size() == 0) return null;
     return (Pair)this.queue.getLast();
   }
 
   public void clear() {
     this.queue.clear();
   }
 
   public int size() {
     return this.queue.size();
   }
 }

/* Location:           E:\SouceCode\recordGame\gdxDemo\libs\domain-1.2.2415.jar
 * Qualified Name:     com.intel.inde.mp.domain.CommandQueue
 * JD-Core Version:    0.6.1
 */