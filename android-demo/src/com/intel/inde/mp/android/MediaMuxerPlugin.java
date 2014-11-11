package com.intel.inde.mp.android;

import java.io.IOException;
import java.nio.ByteBuffer;

import android.media.MediaMuxer;

import com.intel.inde.mp.domain.IMediaCodec;
import com.intel.inde.mp.domain.IMediaMuxer;
import com.intel.inde.mp.domain.MediaFormat;

public class MediaMuxerPlugin
        implements IMediaMuxer
{
    private final MediaMuxer mediaMuxer;
    private long[] lastPresentationTime = new long[2];

    public MediaMuxerPlugin(String filename, int outputFormat)
            throws IOException {
        this.mediaMuxer = new MediaMuxer(filename, outputFormat);
    }

    public int addTrack(MediaFormat mediaFormat)
    {
        return this.mediaMuxer.addTrack(MediaFormatTranslator.from(mediaFormat));
    }

    public void release()
    {
        this.mediaMuxer.release();
    }

    public void setOrientationHint(int degrees)
    {
        this.mediaMuxer.setOrientationHint(degrees);
    }

    public void start()
    {
        this.mediaMuxer.start();
    }

    public void stop()
    {
        this.mediaMuxer.stop();
    }

    public
            void writeSampleData(
                    int trackIndex, ByteBuffer buffer,
                    IMediaCodec.BufferInfo bufferInfo)
    {
        if (bufferInfo.size == 0)
        {
            return;
        }

        if (this.lastPresentationTime[trackIndex] > bufferInfo.presentationTimeUs)
        {
            return;
        }

        if ((bufferInfo.flags & 0x2) != 0)
        {
            return;
        }

        this.lastPresentationTime[trackIndex] = bufferInfo.presentationTimeUs;

        this.mediaMuxer.writeSampleData(trackIndex,
                buffer,
                ByteBufferTranslator.from(bufferInfo));
    }
}

/*
 * Location: E:\SouceCode\recordGame\gdxDemo\libs\android-1.2.2415.jar
 * Qualified Name: com.intel.inde.mp.android.MediaMuxerPlugin
 * JD-Core Version: 0.6.1
 */
