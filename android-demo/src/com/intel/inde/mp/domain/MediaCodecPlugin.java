package com.intel.inde.mp.domain;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Logger;

public abstract class MediaCodecPlugin extends Plugin
{
    protected int timeout = 10;
    protected final IMediaCodec mediaCodec;
    protected Queue<Integer> outputBufferIndexes = new LinkedList();
    protected Queue<Integer> inputBufferIndexes = new LinkedList();
    protected Queue<IMediaCodec.BufferInfo> outputBufferInfos = new LinkedList();
    protected MediaFormat outputMediaFormat = null;
    protected ByteBuffer[] inputBuffers = null;
    protected int outputTrackId;
    protected int frameCount;
    protected HashMap<Integer, Frame> bufferIndexToFrame = new HashMap();

    public MediaCodecPlugin(IMediaCodec mediaCodec) {
        this.mediaCodec = mediaCodec;
    }

    public void checkIfOutputQueueHasData()
    {
        getOutputBufferIndex();
    }

    protected void feedMeIfNotDraining()
    {
        if ((this.state != PluginState.Draining)
                && (this.state != PluginState.Drained)) {
            int inputBufferIndex = this.mediaCodec.dequeueInputBuffer(this.timeout);
            if (inputBufferIndex >= 0) {
                this.inputBufferIndexes.add(Integer.valueOf(inputBufferIndex));
                super.feedMeIfNotDraining();
            }
            else if (this.inputBufferIndexes.size() > 0) {
                Pair command = getInputCommandQueue().first();
                if ((command == null) || (command.left != Command.NeedData))
                    super.feedMeIfNotDraining();
            }
        }
    }

    public Frame getFrame()
    {
        feedMeIfNotDraining();

        Integer outputBufferIndex = (Integer) this.outputBufferIndexes.poll();
        IMediaCodec.BufferInfo outputBufferInfo = (IMediaCodec.BufferInfo) this.outputBufferInfos.poll();
        if (((this.state == PluginState.Draining) || (this.state == PluginState.Drained))
                && (outputBufferIndex == null)) {
            if (getOutputBufferIndex() >= 0) {
                outputBufferIndex = (Integer) this.outputBufferIndexes.poll();
                outputBufferInfo = (IMediaCodec.BufferInfo) this.outputBufferInfos.poll();
            } else {
                return Frame.EOF();
            }
        }

        if (outputBufferIndex == null) {
            return Frame.empty();
        }

        while ((isStatusToSkip(outputBufferIndex))
                && (this.outputBufferIndexes.size() > 0)) {
            outputBufferIndex = (Integer) this.outputBufferIndexes.poll();
            outputBufferInfo = (IMediaCodec.BufferInfo) this.outputBufferInfos.poll();
        }

        ByteBuffer outputBuffer = this.mediaCodec.getOutputBuffers()[outputBufferIndex.intValue()];
        Frame frame;
        if (this.bufferIndexToFrame.containsKey(outputBufferIndex)) {
            frame = (Frame) this.bufferIndexToFrame.get(outputBufferIndex);

            frame.set(outputBuffer,
                    outputBufferInfo.size,
                    outputBufferInfo.presentationTimeUs,
                    outputBufferIndex.intValue(),
                    outputBufferInfo.flags,
                    this.outputTrackId);
        } else {
            frame = new Frame(outputBuffer,
                    outputBufferInfo.size,
                    outputBufferInfo.presentationTimeUs,
                    outputBufferIndex.intValue(),
                    outputBufferInfo.flags,
                    this.outputTrackId);

            this.bufferIndexToFrame.put(outputBufferIndex, frame);

            Logger.getLogger("AMP").info("New frame allocated for buffer "
                    + outputBufferIndex);
        }

        checkIfOutputQueueHasData();

        return frame;
    }

    protected int getOutputBufferIndex()
    {
        IMediaCodec.BufferInfo bufferInfo = new IMediaCodec.BufferInfo();
        int outputBufferIndex = this.mediaCodec.dequeueOutputBuffer(bufferInfo,
                this.timeout);

        if ((this.state == PluginState.Draining) && (outputBufferIndex == -1)) {
            this.state = PluginState.Drained;
        }

        if ((outputBufferIndex != -1) && (outputBufferIndex != -2))
        {
            this.outputBufferIndexes.add(Integer.valueOf(outputBufferIndex));
            this.outputBufferInfos.add(bufferInfo);
        }

        if ((outputBufferIndex >= 0) && (!bufferInfo.isEof())) {
            hasData();
        }

        if (outputBufferIndex == -2) {
            this.outputMediaFormat = this.mediaCodec.getOutputFormat();
            outputFormatChanged();
        }

        return outputBufferIndex;
    }

    protected boolean isStatusToSkip(Integer outputBufferIndex) {
        return (outputBufferIndex.intValue() == -3)
                || (outputBufferIndex.intValue() == -2);
    }

    private void outputFormatChanged()
    {
        getOutputCommandQueue().queue(Command.OutputFormatChanged,
                Integer.valueOf(0));
    }

    protected void hasData()
    {
        getOutputCommandQueue().queue(Command.HasData, Integer.valueOf(0));
    }

    public void drain(int bufferIndex)
    {
        super.drain(bufferIndex);
        getOutputCommandQueue().queue(Command.EndOfFile, Integer.valueOf(0));
    }

    public Frame findFreeFrame()
    {
        if ((this.state == PluginState.Draining)
                || (this.state == PluginState.Drained)) {
            return Frame.EOF();
        }

        if (this.inputBufferIndexes.size() == 0) {
            return null;
        }
        int inputBufferIndex = ((Integer) this.inputBufferIndexes.poll()).intValue();
        return new Frame(this.inputBuffers[inputBufferIndex],
                0,
                0L,
                inputBufferIndex,
                0,
                0);
    }

    public void setOutputTrackId(int trackId)
    {
        this.outputTrackId = trackId;
    }

    public MediaFormat getOutputMediaFormat()
    {
        return this.mediaCodec.getOutputFormat();
    }

    public void fillCommandQueues()
    {
        if (this.state != PluginState.Normal) {
            return;
        }

        checkIfOutputQueueHasData();
        feedMeIfNotDraining();
    }

    public void start()
    {
        this.mediaCodec.start();
        this.inputBuffers = this.mediaCodec.getInputBuffers();
        setState(PluginState.Normal);
    }

    public void stop()
    {
        setState(PluginState.Paused);
        this.mediaCodec.stop();
    }

    public void close() throws IOException
    {
        this.mediaCodec.release();
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public void setInputMediaFormat(MediaFormat mediaFormat) {
        this.mediaFormat = mediaFormat;
    }
}

/*
 * Location: E:\SouceCode\recordGame\gdxDemo\libs\domain-1.2.2415.jar
 * Qualified Name: com.intel.inde.mp.domain.MediaCodecPlugin
 * JD-Core Version: 0.6.1
 */
