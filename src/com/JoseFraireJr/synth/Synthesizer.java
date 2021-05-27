package com.JoseFraireJr.synth;

import com.JoseFraireJr.synth.utils.Utils;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;

public class Synthesizer {

    private static HashMap<Character, Double> KEY_FREQUENCIES = new HashMap<>();
    private boolean shouldGenerate;
    private int startingKey;
    private final int KEY_FREQUENCY_INCREMENT = 1;
    private final char[] octaveSelect = "-+".toCharArray();
    private final char[] KEYS = "zxcvbnm,./asdfghjkl;'qwertyuiop[]".toCharArray();

    private final Oscillator[] oscillators =  new Oscillator[3];
    private final JFrame frame = new JFrame("Synthesizer");
    private final AudioThread audioThread = new AudioThread(() ->
    {
        if (!shouldGenerate){
            return null;
        }
        short[] s = new short[AudioThread.BUFFER_SIZE];
        for (int i=0;i<AudioThread.BUFFER_SIZE;i++){
           double d = 0;
           for (Oscillator o : oscillators){
               d += o.getNextSample() / oscillators.length;
           }
           s[i] = (short)(Short.MAX_VALUE * d);
        }
        return s;
    });

    private final KeyAdapter keyAdapter = new KeyAdapter(){
        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyChar() == '-'){
                setStartingKey(-1);
                setKeys();
            }
            else if (e.getKeyChar() == '='){
                setStartingKey(1);
                setKeys();
            }
            if (!KEY_FREQUENCIES.containsKey(e.getKeyChar())) return;
            if (!audioThread.isRunning()){
                for (Oscillator o : oscillators){
                    o.setKeyFrequency(KEY_FREQUENCIES.get(e.getKeyChar()));
                }
                shouldGenerate = true;
                audioThread.triggerPlayback();
            }
        }
        @Override
        public void keyReleased(KeyEvent e) {
            shouldGenerate = false;
        }
    };

    void setStartingKey(int upDown){
        if(upDown > 0){
            startingKey += 12;
        }
        else{
            startingKey -= 12;
        }
    }

    void setKeys() {
        KEY_FREQUENCIES.clear();
        for (int i = startingKey, key = 0; i < KEYS.length * KEY_FREQUENCY_INCREMENT + startingKey; i += KEY_FREQUENCY_INCREMENT, key++) {
            KEY_FREQUENCIES.put(KEYS[key], Utils.Math.getKeyFrequency(i));
        }
    }

    Synthesizer(){
        setKeys();
        startingKey = 16;
        int y = 0;
        for (int i=0;i< oscillators.length;i++){
            oscillators[i] = new Oscillator(this);
            oscillators[i].setLocation(5,y);
            frame.add(oscillators[i]);
            y += 105;
        }
        frame.addKeyListener(keyAdapter);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                audioThread.close();
            }
        });
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(613, 357);
        frame.setResizable(false);
        frame.setLayout(null);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public KeyAdapter getKeyAdapter(){
        return keyAdapter;
    }

    public static class AudioInfo{
        public static final int SAMPLE_RATE = 44100;
    }
}
