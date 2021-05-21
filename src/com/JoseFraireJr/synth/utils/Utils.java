package com.JoseFraireJr.synth.utils;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

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
