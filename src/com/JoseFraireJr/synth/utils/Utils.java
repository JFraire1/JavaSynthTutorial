package com.JoseFraireJr.synth.utils;

import com.JoseFraireJr.synth.SynthControlContainer;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import static java.lang.Math.*;

public class Utils {
    public static void handleProcedure(Procedure procedure, boolean printStackTrace){
        try
        {
            procedure.invoke();
        }
        catch (Exception e)
        {
            if (printStackTrace){
                e.printStackTrace();
            }
        }
    }

    public static class ParameterHandling{
        public static final Robot PARAMETER_ROBOT;

        static{
            try
            {
                PARAMETER_ROBOT = new Robot();
            }
            catch(AWTException e)
            {
                throw new ExceptionInInitializerError("Cannot Construct Robot Instance");
            }
        }
        private ParameterHandling(){}

        public static void addParameterMouseListeners(Component component, SynthControlContainer container, int minVal, int maxVal, int valStep, RefWrapper<Integer> parameter, Procedure onChangeProcedure, Procedure onReleaseProcedure){
            component.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    final Cursor BLANK_CURSOR = Toolkit.getDefaultToolkit().createCustomCursor(new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB),
                            new Point(0,0), "blank_cursor");
                    component.setCursor(BLANK_CURSOR);
                    container.setMouseClickLocation(e.getLocationOnScreen());
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    component.setCursor(Cursor.getDefaultCursor());
                    if (onReleaseProcedure != null){
                        handleProcedure(onReleaseProcedure, true);
                    }
                }
            });
            component.addMouseMotionListener(new MouseAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    if (container.getMouseClickLocation().y != e.getYOnScreen()){
                        boolean mouseMovingUp = container.getMouseClickLocation().y - e.getYOnScreen() > 0;
                        if (mouseMovingUp && parameter.val + valStep <= maxVal){
                            parameter.val += valStep;

                        }
                        else if (!mouseMovingUp && parameter.val - valStep >= minVal){
                            parameter.val -= valStep;
                        }
                        if (onChangeProcedure != null){
                            handleProcedure(onChangeProcedure, true);
                        }
                        PARAMETER_ROBOT.mouseMove(container.getMouseClickLocation().x, container.getMouseClickLocation().y);
                    }
                }
            });
        }

    }
    public static class WindowDesign{
        public static final Border LINE_BORDER = BorderFactory.createLineBorder(Color.BLACK);
    }

    public static class Math{
        public static double frequencyToAngularFrequency(double freq){
            return 2 * PI * freq;
        }
        public static double getKeyFrequency(int keyNum){
            return pow(root(2,12), keyNum - 49) * 440;
        }
        public static double root(double num, double root){
            return pow(E, log(num) / root);
        }
    }
}
