 package com.intel.inde.mp.domain;
 
 public class OutputInputPair
 {
   public IOutputRaw output;
   public IInputRaw input;
   public CommandHandlerFactory commandHandlerFactory;
 
   public OutputInputPair(IOutputRaw output, IInputRaw input, CommandHandlerFactory commandHandlerFactory)
   {
     this.output = output;
     this.input = input;
     this.commandHandlerFactory = commandHandlerFactory;
   }
 }

/* Location:           E:\SouceCode\recordGame\gdxDemo\libs\domain-1.2.2415.jar
 * Qualified Name:     com.intel.inde.mp.domain.OutputInputPair
 * JD-Core Version:    0.6.1
 */