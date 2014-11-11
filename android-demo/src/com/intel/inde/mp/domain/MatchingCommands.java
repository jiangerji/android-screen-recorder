 package com.intel.inde.mp.domain;
 
 import java.util.ArrayList;
 
 public final class MatchingCommands extends ArrayList<Pair<Command, Command>>
 {
   public MatchingCommands()
   {
     add(new Pair(Command.HasData, Command.NeedData));
     add(new Pair(Command.HasData, Command.NeedInputFormat));
     add(new Pair(Command.OutputFormatChanged, Command.NeedInputFormat));
     add(new Pair(Command.OutputFormatChanged, Command.NeedData));
     add(new Pair(Command.EndOfFile, Command.NeedInputFormat));
     add(new Pair(Command.EndOfFile, Command.NeedData));
   }
 }

/* Location:           E:\SouceCode\recordGame\gdxDemo\libs\domain-1.2.2415.jar
 * Qualified Name:     com.intel.inde.mp.domain.MatchingCommands
 * JD-Core Version:    0.6.1
 */