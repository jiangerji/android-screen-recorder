package com.intel.inde.mp.domain;

import java.util.ArrayList;
import java.util.List;

import com.intel.inde.mp.IProgressListener;

public class CommandHandlerFactory
{
    List<entry> handlerCreators;

    public CommandHandlerFactory()
    {
        this.handlerCreators = new ArrayList();
    }

    public ICommandHandler create(
            Pair<Command, Integer> lhsCommand,
            Pair<Command, Integer> rhsCommand,
            IProgressListener progressListener) {
        IHandlerCreator handlerCreator = findHandlerCreator(lhsCommand,
                rhsCommand);
        return handlerCreator.create();
    }

    public void register(
            Pair<Command, Integer> lhsCommand,
            Pair<Command, Integer> rhsCommand, IHandlerCreator handlerCreator) {
        this.handlerCreators.add(new entry(lhsCommand,
                rhsCommand,
                handlerCreator));
    }

    private
            IHandlerCreator findHandlerCreator(
                    Pair<Command, Integer> lhsCommand,
                    Pair<Command, Integer> rhsCommand) {
        for (entry handlerCreator : this.handlerCreators) {
            if ((handlerCreator.leftCommand == null)
                    && (handlerCreator.rightCommand != null)
                    && (handlerCreator.rightCommand.equals(rhsCommand)))
            {
                return handlerCreator.handlerCreator;
            }
            if ((handlerCreator.rightCommand == null)
                    && (handlerCreator.leftCommand != null)
                    && (handlerCreator.leftCommand.equals(lhsCommand)))
            {
                return handlerCreator.handlerCreator;
            }
            if ((handlerCreator.leftCommand != null)
                    && (handlerCreator.rightCommand != null)
                    && (handlerCreator.leftCommand.equals(lhsCommand))
                    && (handlerCreator.rightCommand.equals(rhsCommand)))
            {
                return handlerCreator.handlerCreator;
            }
        }
        throw new IllegalArgumentException("Command handler for pair ("
                + lhsCommand + ", " + rhsCommand + ") not found");
    }

    private class entry {
        public Pair<Command, Integer> leftCommand;
        public Pair<Command, Integer> rightCommand;
        public IHandlerCreator handlerCreator;

        private entry(Pair<Command, Integer> leftCommand,
                Pair<Command, Integer> rightCommand,
                IHandlerCreator handlerCreator) {
            this.leftCommand = leftCommand;
            this.rightCommand = rightCommand;
            this.handlerCreator = handlerCreator;
        }
    }
}

/*
 * Location: E:\SouceCode\recordGame\gdxDemo\libs\domain-1.2.2415.jar
 * Qualified Name: com.intel.inde.mp.domain.CommandHandlerFactory
 * JD-Core Version: 0.6.1
 */
