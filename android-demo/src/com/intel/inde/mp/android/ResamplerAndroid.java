package com.intel.inde.mp.android;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import com.intel.inde.mp.AudioFormat;
import com.intel.inde.mp.domain.Frame;
import com.intel.inde.mp.domain.Resampler;

public class ResamplerAndroid extends Resampler
{
    public int frameSize = 1024;
    private ByteBuffer internalBuffer;
    private IntBuffer numOutSamples = IntBuffer.allocate(1);

    private final String resamplerLibName = "ippresample";

    public ResamplerAndroid(AudioFormat audioFormat) {
        super(audioFormat);
    }

    private void loadLibrary() {
        try {
            System.loadLibrary("ippresample");
        } catch (LinkageError e) {
            String message = e.getMessage() != null ? e.getMessage()
                    : e.toString();

            throw new IllegalArgumentException("Could not load library: ippresample");
        }
    }

    protected void setup()
    {
        loadLibrary();
    }

    protected void allocateInitInternalBuffers()
    {
        if (this.inputChannelCount == 2)
            this.frameSize = 2048;
        else {
            this.frameSize = 1024;
        }

        int size = ResampleGetSizeJNI(this.inputChannelCount,
                this.inputSampleRate,
                this.frameSize,
                this.targetChannelCount,
                this.targetSampleRate);
        this.internalBuffer = ByteBuffer.allocate(size);
        ResampleInitJNI(this.inputChannelCount,
                this.inputSampleRate,
                this.frameSize,
                this.targetChannelCount,
                this.targetSampleRate,
                this.internalBuffer.array());

        super.allocateInitInternalBuffers();
    }

    public void resampleFrame(Frame frame)
    {
        super.resampleFrame(frame);

        if (resamplingRequired())
        {
            int inSamplesDone = 0;
            int dstOffset;
            int srcOffset = dstOffset = 0;

            int inLenByte = frame.getLength();

            int inLen = inLenByte / 2;
            int outputLen = (int) (inLenByte * this.targetChannelCount
                    / this.inputChannelCount * (this.targetSampleRate / this.inputSampleRate)) * 2 + 2;

            ByteBuffer outputBuffer = ByteBuffer.allocate(outputLen);

            ByteBuffer inputBuffer = ByteBuffer.allocate(frame.getLength());
            inputBuffer.put((ByteBuffer) frame.getByteBuffer().flip());
            while (true)
            {
                int frameLen;
                if (inLen - srcOffset > this.frameSize)
                {
                    frameLen = this.frameSize;
                }
                else
                    frameLen = inLen - srcOffset;

                inSamplesDone = ResampleFrameJNI(inputBuffer.array(),
                        srcOffset,
                        frameLen,
                        outputBuffer.array(),
                        dstOffset,
                        this.numOutSamples.array(),
                        this.internalBuffer.array());

                srcOffset += frameLen;
                dstOffset += this.numOutSamples.get(0);

                if (srcOffset < inLen)
                    if (inSamplesDone == 0) {
                        break;
                    }

            }

            outputBuffer.position(0);

            outputBuffer.limit(2 * dstOffset);
            frame.setLength(outputBuffer.limit());
            frame.getByteBuffer().limit(frame.getByteBuffer().capacity());

            frame.getByteBuffer().position(0);
            frame.getByteBuffer().put(outputBuffer);
        }
    }

    public void resampleBuffer(ByteBuffer frameBuffer, int bufferLength)
    {
        super.resampleBuffer(frameBuffer, bufferLength);

        if (resamplingRequired()) {
            int srcOffset = 0;
            int dstOffset = 0;

            int inLenByte = bufferLength;
            int inLen = inLenByte / 2;
            int outputLen = (int) (inLenByte * this.targetChannelCount
                    / this.inputChannelCount * (this.targetSampleRate / this.inputSampleRate)) * 2 + 2;

            ByteBuffer outputBuffer = ByteBuffer.allocate(outputLen);

            ByteBuffer inputBuffer = ByteBuffer.allocate(inLenByte);
            inputBuffer.put((ByteBuffer) frameBuffer.flip());
            while (true)
            {
                int frameLen;
                if (inLen - srcOffset > this.frameSize)
                {
                    frameLen = this.frameSize;
                }
                else
                    frameLen = inLen - srcOffset;

                int inSamplesDone = ResampleFrameJNI(inputBuffer.array(),
                        srcOffset,
                        frameLen,
                        outputBuffer.array(),
                        dstOffset,
                        this.numOutSamples.array(),
                        this.internalBuffer.array());

                srcOffset += frameLen;
                dstOffset += this.numOutSamples.get(0);

                if ((srcOffset >= inLen) || (inSamplesDone == 0))
                {
                    break;
                }
            }
            outputBuffer.position(0);
            outputBuffer.limit(2 * dstOffset);

            frameBuffer.limit(outputBuffer.limit());
            frameBuffer.position(0);
            frameBuffer.put(outputBuffer);
        }
    }

    private native int ResampleGetSizeJNI(
            int paramInt1, int paramInt2, int paramInt3, int paramInt4,
            int paramInt5);

    private native void ResampleInitJNI(
            int paramInt1, int paramInt2, int paramInt3, int paramInt4,
            int paramInt5, byte[] paramArrayOfByte);

    private native int ResampleFrameJNI(
            byte[] paramArrayOfByte1, int paramInt1, int paramInt2,
            byte[] paramArrayOfByte2, int paramInt3, int[] paramArrayOfInt,
            byte[] paramArrayOfByte3);
}

/*
 * Location: E:\SouceCode\recordGame\gdxDemo\libs\android-1.2.2415.jar
 * Qualified Name: com.intel.inde.mp.android.ResamplerAndroid
 * JD-Core Version: 0.6.1
 */
