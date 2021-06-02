package com.JoseFraireJr.synth;

import com.JoseFraireJr.synth.utils.RefWrapper;
import com.JoseFraireJr.synth.utils.Utils;

import javax.swing.*;
import java.awt.event.ItemEvent;

public class Oscillator extends SynthControlContainer {

    private static final int TONE_OFFSET_LIMIT = 20;

    private Wavetable wavetable = Wavetable.Sine;
    private int wavetableStepSize;
    private int wavetableIndex;
    private double keyFrequency;
    private RefWrapper<Integer> toneOffset = new RefWrapper<>(0);
    private RefWrapper<Integer> volume = new RefWrapper<>(100);

    public Oscillator(Synthesizer synth){
        super(synth);
        JComboBox<Wavetable> comboBox = new JComboBox<>(Wavetable.values());
        comboBox.setSelectedItem(Wavetable.Sine);
        comboBox.setBounds(10, 10, 75, 25);
        comboBox.addItemListener(l ->
        {
            if (l.getStateChange() == ItemEvent.SELECTED){
                wavetable = (Wavetable)l.getItem();
            }
            synth.updateWaveviewer();
        });
        add(comboBox);
        JLabel toneParameter = new JLabel("x0.0");
        toneParameter.setBounds(177, 65, 30, 25);
        toneParameter.setBorder(Utils.WindowDesign.LINE_BORDER);
        Utils.ParameterHandling.addParameterMouseListeners(toneParameter, this, -TONE_OFFSET_LIMIT, TONE_OFFSET_LIMIT, 1, toneOffset, () ->
                {
                    applyToneOffset();
                    toneParameter.setText("x" + toneOffset.val/10.0);
                    synth.updateWaveviewer();
                    },
                ()->{
            toneOffset.val = 0;
            applyToneOffset();
            toneParameter.setText("x" + toneOffset.val/10.0);
            synth.updateWaveviewer();
        });
        add(toneParameter);
        JLabel toneText = new JLabel("Vibrato");
        toneText.setBounds(172,40,75,25);
        add(toneText);
        setSize(279, 100);
        setBorder(Utils.WindowDesign.LINE_BORDER);
        JLabel volumeParameter = new JLabel(" 100%");
        volumeParameter.setBounds(222, 65, 50, 25);
        volumeParameter.setBorder(Utils.WindowDesign.LINE_BORDER);
        Utils.ParameterHandling.addParameterMouseListeners(volumeParameter, this, 0,100, 1,  volume, () ->
            {
                volumeParameter.setText(" " + volume.val + "%");
                synth.updateWaveviewer();
                },()->{});
        JLabel volumeText = new JLabel("Volume");
        volumeText.setBounds(225, 40, 75, 25);
        add(volumeParameter);
        add(volumeText);
        setLayout(null);
    }

    public double[] getSampleWaveform(int numSamples){
        double[] samples = new double[numSamples];
        double frequency = 1.0 / (numSamples / (double)Synthesizer.AudioInfo.SAMPLE_RATE) * 3.0;
        int index = 0;
        int stepSize = (int)(Wavetable.SIZE * Utils.Math.offsetTone(frequency, getToneOffset()) / Synthesizer.AudioInfo.SAMPLE_RATE);
        for (int i=0; i< numSamples; i++){
            samples[i] = wavetable.getSamples()[index] * getVolumeMultiplier();
            index = (index + stepSize) % Wavetable.SIZE;
        }
        return samples;
    }

    public void setKeyFrequency(double frequency){
       keyFrequency = frequency;
       applyToneOffset();
    }

    private double getToneOffset(){
        return toneOffset.val / 100d;
    }

    private double getVolumeMultiplier(){
        return volume.val / 100.0;
    }

    public double getNextSample(){
        double sample = wavetable.getSamples()[wavetableIndex] * getVolumeMultiplier();
        wavetableIndex = (wavetableIndex + wavetableStepSize) % Wavetable.SIZE;
        return sample;
    }

    private void applyToneOffset(){
        wavetableStepSize = (int)(Wavetable.SIZE * Utils.Math.offsetTone(keyFrequency, getToneOffset()) / Synthesizer.AudioInfo.SAMPLE_RATE);
    }
}
