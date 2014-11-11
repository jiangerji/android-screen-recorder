 package com.intel.inde.mp.domain;
 
 import java.util.List;
 
 class PairQueueSpecification
   implements ISpecification<CommandQueue>
 {
   private final PairCommandSpecification pairCommandSpecification;
 
   public PairQueueSpecification(List<Pair<Command, Command>> matchingCommands)
   {
     this.pairCommandSpecification = new PairCommandSpecification(matchingCommands);
   }
 
   public boolean satisfiedBy(CommandQueue sourceQueue, CommandQueue targetQueue)
   {
     Pair sourceCommand = sourceQueue.first();
     Pair targetCommand = targetQueue.first();
     if (targetCommand == null) return false;
     if (targetCommand.left == Command.NextPair) return true;
     if (sourceCommand == null) return false;
     if (sourceCommand.left == Command.NextPair) return true;
     return this.pairCommandSpecification.satisfiedBy(sourceCommand, targetCommand);
   }
 }

/* Location:           E:\SouceCode\recordGame\gdxDemo\libs\domain-1.2.2415.jar
 * Qualified Name:     com.intel.inde.mp.domain.PairQueueSpecification
 * JD-Core Version:    0.6.1
 */