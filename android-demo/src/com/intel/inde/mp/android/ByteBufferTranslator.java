package com.intel.inde.mp.android;

import android.media.MediaCodec;

import com.intel.inde.mp.domain.IMediaCodec;

public class ByteBufferTranslator
{
    public static MediaCodec.BufferInfo from(IMediaCodec.BufferInfo bufferInfo)
    {
        MediaCodec.BufferInfo result = new MediaCodec.BufferInfo();
        result.flags = bufferInfo.flags;
        result.offset = bufferInfo.offset;
        result.size = bufferInfo.size;
        result.presentationTimeUs = bufferInfo.presentationTimeUs;
        return result;
    }
}

/*
 * Location: E:\SouceCode\recordGame\gdxDemo\libs\android-1.2.2415.jar
 * Qualified Name: com.intel.inde.mp.android.ByteBufferTranslator
 * JD-Core Version: 0.6.1
 */
