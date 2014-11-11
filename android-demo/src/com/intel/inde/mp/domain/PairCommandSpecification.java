package com.intel.inde.mp.domain;

import java.util.Iterator;
import java.util.List;

class PairCommandSpecification
        implements ISpecification<Command>
{
    private final List<Pair<Command, Command>> matchingCommands;

    public PairCommandSpecification(
            List<Pair<Command, Command>> matchingCommands)
    {
        this.matchingCommands = matchingCommands;
    }

    public boolean satisfiedBy(Command source, Command target)
    {
        if ((source == null) && (target == null))
            return false;
        for (Iterator<Pair<Command, Command>> i$ = this.matchingCommands.iterator(); i$.hasNext();)
        {
            Pair<Command, Command> matchingCommand = (Pair<Command, Command>) i$.next();
            if ((matchingCommand.left != null)
                    && (matchingCommand.left == source)
                    && (matchingCommand.right != null)
                    && (matchingCommand.right == target))
                return true;
            if ((matchingCommand.left == null)
                    && (matchingCommand.right == target))
                return true;
            if ((matchingCommand.right != null)
                    || (matchingCommand.left != source))
                return true;
        }
        return false;
    }

    public boolean satisfiedBy(
            Pair<Command, Integer> sourcePair,
            Pair<Command, Integer> targetPair) {
        if ((sourcePair == null) || (targetPair == null))
            return false;

        for (Pair matchingCommand : this.matchingCommands) {
            if ((matchingCommand.left != null)
                    && (matchingCommand.left == sourcePair.left)
                    && (matchingCommand.right != null)
                    && (matchingCommand.right == targetPair.left)
                    && (sourcePair.right == targetPair.right))
            {
                return true;
            }
            if ((matchingCommand.left == null)
                    && (matchingCommand.right == targetPair.left)
                    && (sourcePair.right == targetPair.right))
                return true;
            if ((matchingCommand.right == null)
                    && (matchingCommand.left == targetPair.left)
                    && (sourcePair.right == targetPair.right))
                return true;
        }
        return false;
    }
}

/*
 * Location: E:\SouceCode\recordGame\gdxDemo\libs\domain-1.2.2415.jar
 * Qualified Name: com.intel.inde.mp.domain.PairCommandSpecification
 * JD-Core Version: 0.6.1
 */
