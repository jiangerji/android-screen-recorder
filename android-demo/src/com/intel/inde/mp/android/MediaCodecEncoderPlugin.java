package com.intel.inde.mp.android;

import java.nio.ByteBuffer;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.opengl.EGLContext;

import com.intel.inde.mp.domain.IEglContext;
import com.intel.inde.mp.domain.IMediaCodec;
import com.intel.inde.mp.domain.ISurface;
import com.intel.inde.mp.domain.ISurfaceWrapper;
import com.intel.inde.mp.domain.IWrapper;
import com.intel.inde.mp.domain.MediaFormat;
import com.intel.inde.mp.domain.graphics.IEglUtil;

public class MediaCodecEncoderPlugin
        implements IMediaCodec
{
    private MediaCodec mediaCodec;
    private ByteBuffer[] outputBuffers;
    private MediaCodec.BufferInfo outputBufferInfo;
    private ByteBuffer[] inputBuffers;
    private MediaCodec.BufferInfo inputBufferInfo;
    private IEglUtil eglUtil;

    private MediaCodecEncoderPlugin(IEglUtil eglUtil)
    {
        this.eglUtil = eglUtil;
        init();
    }

    public MediaCodecEncoderPlugin(String mime, IEglUtil eglUtil) {
        this.eglUtil = eglUtil;
        this.mediaCodec = MediaCodec.createEncoderByType(mime);

        init();
    }

    private void init() {
        this.outputBufferInfo = new MediaCodec.BufferInfo();
        this.inputBufferInfo = new MediaCodec.BufferInfo();
    }

    public static MediaCodecEncoderPlugin createByCodecName(
            String mime, IEglUtil eglUtil) {
        MediaCodecEncoderPlugin plugin = new MediaCodecEncoderPlugin(eglUtil);
        MediaCodecInfo audioCodecInfo = selectCodec(mime);

        String codecName = audioCodecInfo.getName();
        if (codecName != null) {
            plugin.mediaCodec = MediaCodec.createByCodecName(codecName);
        }
        return plugin;
    }

    private static MediaCodecInfo selectCodec(String mimeType)
    {
        int numCodecs = MediaCodecList.getCodecCount();
        for (int i = 0; i < numCodecs; i++) {
            MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(i);

            if (codecInfo.isEncoder())
            {
                String[] types = codecInfo.getSupportedTypes();

                for (String type : types) {
                    if (type.equalsIgnoreCase(mimeType))
                        return codecInfo;
                }
            }
        }
        return null;
    }

    public void configure(
            MediaFormat mediaFormat, ISurfaceWrapper surface, int flags)
    {
        if (mediaFormat.getMimeType().startsWith("video")) {
            this.mediaCodec.configure(MediaFormatTranslator.from(mediaFormat),
                    surface == null ? null
                            : (android.view.Surface) ((SurfaceWrapper) surface).getNativeObject(),
                    null,
                    flags);
        }
        else if (mediaFormat.getMimeType().startsWith("audio"))
            this.mediaCodec.configure(MediaFormatTranslator.from(mediaFormat),
                    null,
                    null,
                    flags);
    }

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
        return new Surface(this.mediaCodec, this.eglUtil);
    }

    public ISurface createSimpleInputSurface(IEglContext eglSharedContext)
    {
        return new SimpleSurface(this.mediaCodec,
                (EGLContext) ((IWrapper) eglSharedContext).getNativeObject());
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
        int index = this.mediaCodec.dequeueInputBuffer(timeout);

        return index;
    }

    public int dequeueOutputBuffer(
            IMediaCodec.BufferInfo bufferInfo, long timeout)
    {
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
        this.mediaCodec.signalEndOfInputStream();
    }

    public void stop()
    {
        this.mediaCodec.stop();
    }

    public void release()
    {
        this.mediaCodec.release();
    }

    public void recreate()
    {
    }
}

/*
 * Location: E:\SouceCode\recordGame\gdxDemo\libs\android-1.2.2415.jar
 * Qualified Name: com.intel.inde.mp.android.MediaCodecEncoderPlugin
 * JD-Core Version: 0.6.1
 */
