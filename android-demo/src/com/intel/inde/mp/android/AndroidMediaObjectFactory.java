package com.intel.inde.mp.android;

import java.io.FileDescriptor;
import java.io.IOException;

import android.content.Context;
import android.hardware.Camera;
import android.opengl.EGL14;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.Surface;

import com.intel.inde.mp.AudioFormat;
import com.intel.inde.mp.IProgressListener;
import com.intel.inde.mp.StreamingParameters;
import com.intel.inde.mp.Uri;
import com.intel.inde.mp.android.graphics.EglUtil;
import com.intel.inde.mp.android.graphics.FrameBuffer;
import com.intel.inde.mp.domain.AudioDecoder;
import com.intel.inde.mp.domain.AudioEffector;
import com.intel.inde.mp.domain.AudioEncoder;
import com.intel.inde.mp.domain.IAndroidMediaObjectFactory;
import com.intel.inde.mp.domain.IAudioContentRecognition;
import com.intel.inde.mp.domain.ICameraSource;
import com.intel.inde.mp.domain.ICaptureSource;
import com.intel.inde.mp.domain.IEffectorSurface;
import com.intel.inde.mp.domain.IEglContext;
import com.intel.inde.mp.domain.IFrameBuffer;
import com.intel.inde.mp.domain.IMediaFormatWrapper;
import com.intel.inde.mp.domain.IMicrophoneSource;
import com.intel.inde.mp.domain.IPreview;
import com.intel.inde.mp.domain.ISurfaceWrapper;
import com.intel.inde.mp.domain.MediaSource;
import com.intel.inde.mp.domain.MuxRender;
import com.intel.inde.mp.domain.ProgressTracker;
import com.intel.inde.mp.domain.Render;
import com.intel.inde.mp.domain.Resampler;
import com.intel.inde.mp.domain.VideoDecoder;
import com.intel.inde.mp.domain.VideoEffector;
import com.intel.inde.mp.domain.VideoEncoder;
import com.intel.inde.mp.domain.graphics.IEglUtil;

public class AndroidMediaObjectFactory
        implements IAndroidMediaObjectFactory
{
    private final Context context;
    MediaCodecEncoderPlugin audioMediaCodec;

    public AndroidMediaObjectFactory(Context context)
    {
        this.context = context;
    }

    public MediaSource createMediaSource(String fileName) throws IOException
    {
        MediaExtractorPlugin mediaExtractor = new MediaExtractorPlugin();
        mediaExtractor.setDataSource(fileName);
        return new MediaSource(mediaExtractor);
    }

    public MediaSource createMediaSource(FileDescriptor fileDescriptor)
            throws IOException
    {
        MediaExtractorPlugin mediaExtractor = new MediaExtractorPlugin();
        mediaExtractor.setDataSource(fileDescriptor);
        return new MediaSource(mediaExtractor);
    }

    public MediaSource createMediaSource(Uri uri) throws IOException
    {
        MediaExtractorPlugin mediaExtractor = new MediaExtractorPlugin();
        mediaExtractor.setDataSource(this.context, uri);
        return new MediaSource(mediaExtractor);
    }

    public VideoDecoder createVideoDecoder(
            com.intel.inde.mp.domain.MediaFormat format)
    {
        VideoDecoder videoDecoder = new VideoDecoder(new MediaCodecVideoDecoderPlugin(format));
        videoDecoder.setTimeout(getDeviceSpecificTimeout());
        return videoDecoder;
    }

    public VideoEncoder createVideoEncoder()
    {
        VideoEncoder videoEncoder = new VideoEncoder(new MediaCodecEncoderPlugin("video/avc",
                getEglUtil()));
        videoEncoder.setTimeout(getDeviceSpecificTimeout());
        return videoEncoder;
    }

    public AudioDecoder createAudioDecoder()
    {
        AudioDecoder audioDecoder = new AudioDecoder(new MediaCodecAudioDecoderPlugin());
        audioDecoder.setTimeout(getDeviceSpecificTimeout());
        return audioDecoder;
    }

    public AudioEncoder createAudioEncoder(String mime)
    {
        this.audioMediaCodec = MediaCodecEncoderPlugin.createByCodecName(mime != null ? mime
                : "audio/mp4a-latm",
                getEglUtil());
        AudioEncoder audioEncoder = new AudioEncoder(this.audioMediaCodec);
        audioEncoder.setTimeout(getDeviceSpecificTimeout());
        return audioEncoder;
    }

    public Resampler createAudioResampler(AudioFormat audioFormat)
    {
        return new ResamplerAndroid(audioFormat);
    }

    public Render createSink(
            String fileName, IProgressListener progressListener,
            ProgressTracker progressTracker) throws IOException
    {
        if (fileName != null) {
            return new MuxRender(new MediaMuxerPlugin(fileName, 0),
                    progressListener,
                    progressTracker);
        }
        return null;
    }

    public Render createSink(
            StreamingParameters parameters, IProgressListener progressListener,
            ProgressTracker progressTracker)
    {
        //TODO: Error cannot use
        Log.d("record",
                "something error hanppend. ");
        throw new RuntimeException();
        //        return new MuxRender(null,
        //                progressListener,
        //                progressTracker);
    }

    public ICaptureSource createCaptureSource()
    {
        return new GameCapturerSource();
    }

    public com.intel.inde.mp.domain.MediaFormat createVideoFormat(
            String mimeType, int width, int height)
    {
        return new VideoFormatAndroid(mimeType, width, height);
    }

    public com.intel.inde.mp.domain.MediaFormat createAudioFormat(
            String mimeType, int channelCount, int sampleRate)
    {
        return new AudioFormatAndroid(mimeType, sampleRate, channelCount);
    }

    public VideoEffector createVideoEffector()
    {
        return new VideoEffector(new MediaCodecEncoderPlugin("video/avc",
                getEglUtil()), this);
    }

    public IEffectorSurface createEffectorSurface() {
        return new EffectorSurface(getEglUtil());
    }

    public IPreview createPreviewRender(Object glView, Object camera)
    {
        return new PreviewRender((GLSurfaceView) glView,
                getEglUtil(),
                (Camera) camera);
    }

    public AudioEffector createAudioEffects()
    {
        return new AudioEffector(null);
    }

    public ICameraSource createCameraSource()
    {
        return new CameraSource(getEglUtil());
    }

    public IMicrophoneSource createMicrophoneSource()
    {
        return new MicrophoneSource();
    }

    public IAudioContentRecognition createAudioContentRecognition()
    {
        return new AudioContentRecognition();
    }

    public IEglContext getCurrentEglContext()
    {
        return new EGLContextWrapper(EGL14.eglGetCurrentContext());
    }

    public IEglUtil getEglUtil()
    {
        return EglUtil.getInstance();
    }

    public IFrameBuffer createFrameBuffer()
    {
        return new FrameBuffer(getEglUtil());
    }

    private int getDeviceSpecificTimeout()
    {
        return 10;
    }

    public static class Converter
    {
        public static ISurfaceWrapper convert(Surface surface) {
            return new SurfaceWrapper(surface);
        }

        public static IMediaFormatWrapper convert(
                android.media.MediaFormat mediaFormat) {
            return new MediaFormatWrapper(mediaFormat);
        }
    }
}

/*
 * Location: E:\SouceCode\recordGame\gdxDemo\libs\android-1.2.2415.jar
 * Qualified Name: com.intel.inde.mp.android.AndroidMediaObjectFactory
 * JD-Core Version: 0.6.1
 */
