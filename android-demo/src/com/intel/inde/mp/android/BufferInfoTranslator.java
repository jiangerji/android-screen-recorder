package com.intel.inde.mp.android;

import android.media.MediaCodec;

import com.intel.inde.mp.domain.IMediaCodec;

public class BufferInfoTranslator
{
    public static IMediaCodec.BufferInfo convertFromAndroid(
            MediaCodec.BufferInfo androidBufferInfo,
            IMediaCodec.BufferInfo bufferInfo)
    {
        bufferInfo.flags = androidBufferInfo.flags;
        bufferInfo.presentationTimeUs = androidBufferInfo.presentationTimeUs;
        bufferInfo.offset = androidBufferInfo.offset;
        bufferInfo.size = androidBufferInfo.size;

        return bufferInfo;
    }
}

/*
 * Location: E:\SouceCode\recordGame\gdxDemo\libs\android-1.2.2415.jar
 * Qualified Name: com.intel.inde.mp.android.BufferInfoTranslator
 * JD-Core Version: 0.6.1
 */
