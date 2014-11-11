package com.intel.inde.mp.android;

import java.nio.ByteBuffer;

import android.media.MediaCodec;

import com.intel.inde.mp.domain.IEglContext;
import com.intel.inde.mp.domain.IMediaCodec;
import com.intel.inde.mp.domain.ISurface;
import com.intel.inde.mp.domain.ISurfaceWrapper;
import com.intel.inde.mp.domain.MediaFormat;

public abstract class MediaCodecDecoderPlugin
        implements IMediaCodec
{
    protected MediaCodec mediaCodec;
    private ByteBuffer[] outputBuffers;
    private MediaCodec.BufferInfo outputBufferInfo;
    private ByteBuffer[] inputBuffers;
    private MediaCodec.BufferInfo inputBufferInfo;

    public MediaCodecDecoderPlugin(String mime)
    {
        this.mediaCodec = MediaCodec.createDecoderByType(mime);

        init();
    }

    private void init() {
        this.outputBufferInfo = new MediaCodec.BufferInfo();
        this.inputBufferInfo = new MediaCodec.BufferInfo();
    }

    public abstract void configure(
            MediaFormat paramMediaFormat, ISurfaceWrapper paramISurfaceWrapper,
            int paramInt);

    public void start()
    {
        this.mediaCodec.start();
        this.inputBuffers = null;
        this.outputBuffers = null;
    }

    public void releaseOutputBuffer(int bufferIndex, boolean render)
    {
        this.mediaCodec.releaseOutputBuffer(bufferIndex, render);
    }

    public ISurface createInputSurface()
    {
        return null;
    }

    public ISurface createSimpleInputSurface(IEglContext eglSharedContext)
    {
        return null;
    }

    public ByteBuffer[] getInputBuffers()
    {
        if (this.inputBuffers == null) {
            this.inputBuffers = this.mediaCodec.getInputBuffers();
        }

        return this.inputBuffers;
    }

    public ByteBuffer[] getOutputBuffers()
    {
        if (this.outputBuffers == null) {
            this.outputBuffers = this.mediaCodec.getOutputBuffers();
        }

        return this.outputBuffers;
    }

    public
            void queueInputBuffer(
                    int index, int offset, int size, long presentationTimeUs,
                    int flags)
    {
        this.mediaCodec.queueInputBuffer(index,
                offset,
                size,
                presentationTimeUs,
                flags);
    }

    public int dequeueInputBuffer(long timeout)
    {
        return this.mediaCodec.dequeueInputBuffer(timeout);
    }

    public int dequeueOutputBuffer(
            IMediaCodec.BufferInfo bufferInfo, long timeout) {
        int result = this.mediaCodec.dequeueOutputBuffer(this.outputBufferInfo,
                timeout);

        if (result == -3) {
            this.outputBuffers = null;
            getOutputBuffers();
        }

        BufferInfoTranslator.convertFromAndroid(this.outputBufferInfo,
                bufferInfo);

        return result;
    }

    public MediaFormat getOutputFormat()
    {
        return MediaFormatTranslator.toDomain(this.mediaCodec.getOutputFormat());
    }

    public void signalEndOfInputStream()
    {
    }

    public void stop()
    {
        this.mediaCodec.stop();
    }
}

/*
 * Location: E:\SouceCode\recordGame\gdxDemo\libs\android-1.2.2415.jar
 * Qualified Name: com.intel.inde.mp.android.MediaCodecDecoderPlugin
 * JD-Core Version: 0.6.1
 */
