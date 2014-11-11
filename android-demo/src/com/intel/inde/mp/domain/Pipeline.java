package com.intel.inde.mp.domain;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collection;

import com.intel.inde.mp.AudioFormat;
import com.intel.inde.mp.domain.pipeline.ConnectorFactory;
import com.intel.inde.mp.domain.pipeline.IOnStopListener;

public class Pipeline
{
    private final TopologySolver topologySolver = new TopologySolver();
    private final ICommandProcessor commandProcessor;
    private IOnStopListener onStopListener = new IOnStopListener()
    {
        public void onStop() {
            Pipeline.this.commandProcessor.stop();
        }
    };

    public Pipeline(ICommandProcessor commandProcessor)
    {
        this.commandProcessor = commandProcessor;
    }

    public void setMediaSource(IOutput mediaSource) {
        this.topologySolver.add(mediaSource);
    }

    public void setMediaSource(ICaptureSource mediaSource) {
        this.topologySolver.add(mediaSource);
    }

    public void setMediaSource(ICameraSource cameraSource) {
        this.topologySolver.add(cameraSource);
    }

    public void setMediaSource(IMicrophoneSource microphoneSource) {
        this.topologySolver.add(microphoneSource);
    }

    public void addTransform(ITransform transform) {
        this.topologySolver.add(transform);
    }

    public void addVideoDecoder(Plugin videoDecoder) {
        this.topologySolver.add(videoDecoder);
    }

    public void addVideoEncoder(VideoEncoder videoEncoder) {
        this.topologySolver.add(videoEncoder);
    }

    public void addAudioDecoder(Plugin audioDecoder) {
        this.topologySolver.add(audioDecoder);
    }

    public void addAudioEncoder(AudioEncoder audioEncoder) {
        this.topologySolver.add(audioEncoder);
    }

    public void addVideoEffect(VideoEffector effect) {
        this.topologySolver.add(effect);
    }

    public void addAudioEffect(AudioEffector effect) {
        this.topologySolver.add(effect);
    }

    public void setSink(Render sink) {
        this.topologySolver.add(sink);
        if (sink != null)
            sink.addOnStopListener(this.onStopListener);
    }

    public void resolve()
    {
        AudioFormat audioFormat = getAudioFormat();

        ConnectorFactory connectorFactory = new ConnectorFactory(this.commandProcessor,
                audioFormat);

        Collection<IsConnectable> connectionRules = connectorFactory.createConnectionRules();
        for (IsConnectable connectionRule : connectionRules) {
            this.topologySolver.addConnectionRule(connectionRule);
        }

        Collection<Pair<IOutputRaw, IInputRaw>> connectionQueue = this.topologySolver.getConnectionsQueue();
        for (Pair rawPair : connectionQueue) {
            connectorFactory.connect((IOutputRaw) rawPair.left,
                    (IInputRaw) rawPair.right);
        }

        startSource();
    }

    private void startSource() {
        for (IOutputRaw iOutputRaw : this.topologySolver.getSources()) {
            IRunnable mediaSource = (IRunnable) iOutputRaw;
            mediaSource.start();
        }
    }

    private AudioFormat getAudioFormat() {
        AudioFormat audioFormat = null;

        for (IOutputRaw iOutputRaw : this.topologySolver.getSources()) {
            if ((iOutputRaw instanceof IOutput))
            {
                IOutput mediaSource = (IOutput) iOutputRaw;

                audioFormat = (AudioFormat) mediaSource.getMediaFormatByType(MediaFormatType.AUDIO);

                if (audioFormat != null)
                    break;
            }
        }
        return audioFormat;
    }

    public void stop() {
        for (IOutputRaw source : this.topologySolver.getSources()) {
            IRunnable mediaSource = (IRunnable) source;
            mediaSource.stop();
        }
    }

    public void release() throws IOException {
        for (IOutputRaw node : this.topologySolver.getSources()) {
            ((Closeable) node).close();
        }

        for (IInputRaw node : this.topologySolver.getSinks())
            ((Closeable) node).close();
    }
}

/*
 * Location: E:\SouceCode\recordGame\gdxDemo\libs\domain-1.2.2415.jar
 * Qualified Name: com.intel.inde.mp.domain.Pipeline
 * JD-Core Version: 0.6.1
 */
