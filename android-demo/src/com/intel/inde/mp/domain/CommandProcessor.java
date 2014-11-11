 package com.intel.inde.mp.domain;
 
 import com.intel.inde.mp.IProgressListener;
 import java.util.ArrayList;
 
 public class CommandProcessor
   implements ICommandProcessor
 {
   private final ArrayList<OutputInputPair> pairs = new ArrayList();
   private final PairQueueSpecification pairQueueSpecification = new PairQueueSpecification(new MatchingCommands());
   private final IProgressListener progressListener;
   private volatile boolean isPaused = false;
 
   private static final MatchingCommands matchingCommands = new MatchingCommands();
   private boolean stopped = false;
 
   public CommandProcessor(IProgressListener progressListener) {
     this.progressListener = progressListener;
   }
 
   public void add(OutputInputPair pair)
   {
     this.pairs.add(pair);
   }
 
   public void stop()
   {
     this.stopped = true;
   }
 
   public void process()
   {
     for (OutputInputPair pair : this.pairs) {
       pair.output.fillCommandQueues();
       pair.input.fillCommandQueues();
     }
 
     while (!this.stopped)
       for (OutputInputPair pair : this.pairs)
         processCommandPairs(pair);
   }
 
   private void processCommandPairs(OutputInputPair pair)
   {
     pair.output.fillCommandQueues();
     pair.input.fillCommandQueues();
     CommandQueue outputCommandQueue = pair.output.getOutputCommandQueue();
     CommandQueue inputCommandQueue = pair.input.getInputCommandQueue();
 
     while (this.pairQueueSpecification.satisfiedBy(outputCommandQueue, inputCommandQueue)) {
       checkIfPaused();
 
       Pair outputCommand = outputCommandQueue.first();
       Pair inputCommand = inputCommandQueue.first();
 
       if ((outputCommand != null) && (inputCommand != null))
       {
         if (inputCommand.left == Command.NextPair) {
           inputCommandQueue.dequeue();
           break;
         }
         if (outputCommand.left == Command.NextPair) {
           outputCommandQueue.dequeue();
           break;
         }
         process(outputCommandQueue, inputCommandQueue, pair.commandHandlerFactory);
       }
     }
   }
 
   private void process(CommandQueue outputCommandQueue, CommandQueue inputCommandQueue, CommandHandlerFactory commandHandlerFactory) { Pair matchingCommands = dequeMatchingCommands(outputCommandQueue, inputCommandQueue);
     Pair outputCommand = (Pair)matchingCommands.left;
     Pair inputCommand = (Pair)matchingCommands.right;
     process(outputCommand, inputCommand, commandHandlerFactory);
   }
 
   private Pair<Pair<Command, Integer>, Pair<Command, Integer>> dequeMatchingCommands(CommandQueue outputCommandQueue, CommandQueue inputCommandQueue)
   {
     for (Pair matchingCommand : matchingCommands)
     {
       Pair outputCommand = outputCommandQueue.first();
       Pair inputCommand = inputCommandQueue.first();
 
       if ((outputCommand != null) && (inputCommand != null))
       {
         boolean match = ((matchingCommand.left == null) || (matchingCommand.left == outputCommand.left)) && ((matchingCommand.right == null) || (matchingCommand.right == inputCommand.left)) && (outputCommand.right == inputCommand.right);
 
         if (match) {
           if (matchingCommand.left != null) outputCommand = outputCommandQueue.dequeue();
           if (matchingCommand.right != null) inputCommand = inputCommandQueue.dequeue();
           return new Pair(outputCommand, inputCommand);
         }
       }
     }
     throw new UnsupportedOperationException("Pair (" + outputCommandQueue.first() + ", " + inputCommandQueue.first() + ") does not match.");
   }
 
   protected void process(Pair<Command, Integer> outputCommand, Pair<Command, Integer> inputCommand, CommandHandlerFactory commandHandlerFactory) {
     ICommandHandler commandHandler = commandHandlerFactory.create(outputCommand, inputCommand, this.progressListener);
     commandHandler.handle();
   }
 
   private synchronized void checkIfPaused() {
     while (this.isPaused) try {
         this.progressListener.onMediaPause();
         wait();
       } catch (InterruptedException e) {
         e.printStackTrace();
       }
   }
 
   public void pause()
   {
     this.isPaused = true;
   }
 
   public synchronized void resume()
   {
     this.isPaused = false;
     notifyAll();
   }
 }

/* Location:           E:\SouceCode\recordGame\gdxDemo\libs\domain-1.2.2415.jar
 * Qualified Name:     com.intel.inde.mp.domain.CommandProcessor
 * JD-Core Version:    0.6.1
 */