package com.intel.inde.mp.domain;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

class TopologySolver
{
    public Object getNodes;
    private final LinkedList<LeftNode> sources = new LinkedList();
    private final LinkedList<RightNode> sinks = new LinkedList();
    private final LinkedList<LeftNode> pureSources = new LinkedList();

    private final LinkedList<IsConnectable> rules = new LinkedList();
    private boolean solved;
    private LinkedList<ITopologyTree> trees;

    public void addConnectionRule(IsConnectable rule)
    {
        assertIsNotSolved();
        this.rules.add(rule);
    }

    public void add(ITransform transform) {
        assertIsNotSolved();
        this.sinks.add(new RightNode(transform));
        this.sources.add(new LeftNode(transform));
    }

    public void add(IOutputRaw source) {
        assertIsNotSolved();
        LeftNode leftNode = new LeftNode(source);
        this.pureSources.add(leftNode);
        this.sources.add(leftNode);
    }

    public void add(IInputRaw sink) {
        assertIsNotSolved();
        this.sinks.add(new RightNode(sink));
    }

    public Collection<IOutputRaw> getSources() {
        LinkedList sourceCollection = new LinkedList();
        for (LeftNode source : this.pureSources) {
            sourceCollection.add(source.value());
        }
        return sourceCollection;
    }

    public Collection<IInputRaw> getSinks() {
        LinkedList nodes = new LinkedList();
        for (RightNode source : this.sinks) {
            nodes.add(source.value());
        }
        return nodes;
    }

    public Collection<Pair<IOutputRaw, IInputRaw>> getConnectionsQueue() {
        resolve();

        LinkedList queue = new LinkedList();

        for (ITopologyTree tree : this.trees) {
            buildConnectionQueue(tree, queue, true);
        }

        return queue;
    }

    private void buildConnectionQueue(
            ITopologyTree topologyTree,
            LinkedList<Pair<IOutputRaw, IInputRaw>> queue, boolean bHead) {
        if ((topologyTree == null)
                || (!(topologyTree.current() instanceof IOutputRaw))) {
            return;
        }

        IOutputRaw output = (IOutputRaw) topologyTree.current();
        for (Iterator i$ = topologyTree.next().iterator(); i$.hasNext();) {
            Object o = i$.next();
            ITopologyTree nextTree = (ITopologyTree) o;
            IInputRaw input = (IInputRaw) nextTree.current();

            if ((input.canConnectFirst(output))
                    && ((!bHead) || (output.canConnectFirst(input)))) {
                queue.add(new Pair(output, input));
                buildConnectionQueue(nextTree, queue, false);
            } else {
                buildConnectionQueue(nextTree, queue, false);
                queue.add(new Pair(output, input));
            }
        }
    }

    public Collection<ITopologyTree> resolve() throws RuntimeException
    {
        if (!this.solved) {
            if (!continueResolve()) {
                throw new IllegalStateException("Cannot resolve");
            }

            this.trees = new LinkedList();
            for (LeftNode left : this.pureSources) {
                this.trees.add(buildTree(left));
            }

            this.solved = true;
        }
        return this.trees;
    }

    private void assertIsNotSolved() {
        if (this.solved)
            throw new IllegalStateException("cannot modify topology after solving");
    }

    private ITopologyTree buildTree(IInputRaw input)
    {
        LeftNode lNode = findOutputForTransform(input);
        if (null == lNode) {
            return new TopologyNet(input);
        }

        return buildTree(lNode);
    }

    private ITopologyTree buildTree(LeftNode current) {
        TopologyNet net = new TopologyNet(current.value());

        for (IInputRaw input : current.getConnector())
        {
            if (null == net.next()) {
                net.setNext(new LinkedList());
            }
            net.next().add(buildTree(input));
        }

        return net;
    }

    private LeftNode findOutputForTransform(IInputRaw input) {
        if (!(input instanceof ITransform)) {
            return null;
        }

        for (LeftNode output : this.sources) {
            if (((output.value() instanceof ITransform)) &&
                    (input == output.value())) {
                return output;
            }

        }

        return null;
    }

    private boolean continueResolve() {
        boolean allConnected = true;
        LeftNode source;
        for (Iterator i$ = this.sources.iterator(); i$.hasNext();) {
            source = (LeftNode) i$.next();
            if (!source.isConnected()) {
                allConnected = false;
            }

            for (RightNode sink : this.sinks)
                if (!source.isConnectedTo(sink.value()))
                {
                    source.connect(sink.value());
                    sink.connect(source.value());
                    if ((matchConnectionRules(source, sink)) &&
                            (continueResolve())) {
                        return true;
                    }

                    sink.disconnect(source.value());
                    source.disconnect(sink.value());
                }
        }

        return allConnected;
    }

    private boolean matchConnectionRules(LeftNode output, RightNode sink) {
        for (IsConnectable rule : this.rules) {
            if ((rule.isConnectable((IOutputRaw) output.value(),
                    output.getConnector()))
                    && (rule.isConnectable(sink.getConnector(),
                            (IInputRaw) sink.value())))
            {
                return true;
            }
        }
        return false;
    }

    class RightNode extends ConnectedNode<IInputRaw, IOutputRaw>
    {
        RightNode(IInputRaw node)
        {
            super(node);
        }
    }

    class LeftNode extends ConnectedNode<IOutputRaw, IInputRaw>
    {
        LeftNode(IOutputRaw node)
        {
            super(node);
        }
    }
}

/*
 * Location: E:\SouceCode\recordGame\gdxDemo\libs\domain-1.2.2415.jar
 * Qualified Name: com.intel.inde.mp.domain.TopologySolver
 * JD-Core Version: 0.6.1
 */
