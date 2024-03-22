package dynamics;

import java.util.Calendar;
import vehiclesimulationcore.SimulateList2;
import vehiclesimulation.BrakingUI;

/**
 *
 * <p>Title: Vehicle Dynamics Simulation Program</p>
 *
 * <p>Description: vehicle dynmaics, braking system simulation</p>
 *
 * <p>Copyright: Copyright (c) 2006 Faculty of Information Technology
 * Engineering at Damascus University</p>
 *
 * <p>Company: FIT</p>
 *
 * @author Yasser Almohammad(Graphics Design and implementation), Ahmad Zoubi,
 *   Usama Rmelawi, Mohammad Askar,Qousai Dabour (vehicle case study, core
 *   functionality)
 *
 * set of samples to be applied
 * by choice
 */
public class Samples {

    /**
     * by calling this method we apply the sample to the program
     */
    public static BrakingUI ui = null;
    public static Calendar prevTime = Calendar.getInstance();
    public static boolean isSample2=false;
    public static void selectSample(String sample) {
        if (sample.equals("sample1"))
            sample1();

        if (sample.equals("sample2"))
            sample2();
        if (sample.equals("sample3"))
            sample3();

        if (sample.equals("sample4"))
            sample4();

        if (sample.equals("sample5"))
            sample5();

        if (sample.equals("sample6"))
            sample6();

        if (sample.equals("sample7"))
            sample7();

    }

    public static void applyInfo() {
        Calendar curTime = Calendar.getInstance();
        if ((curTime.getTimeInMillis() - prevTime.getTimeInMillis()) > 1000) {

            ui.setUIFromSimVals();
            if (SimulateList2.running)
                SimulateList2.running = false;
            SimVals.running = false;
            ui.sim = new SimulationCore();
            SimVals.running = true;
            ui.sim.start();
            prevTime = curTime;
        }

    }


    public static void sample1() {
        SimVals.resetStateVars();
        SimVals.beginSteerAfter = 0.5;
//        SimVals.brakeBL = 0;
//        SimVals.brakeBR = 0;
        SimVals.enableABS = false;
        SimVals.enableInfo = true;
        SimVals.enableOnline = false;
        SimVals.engineTorque = 0;
        SimVals.initialSpeed = 80;
        SimVals.isOptimum = false;

        SimVals.maxBrakeTorqueBL = 450;
        SimVals.maxBrakeTorqueBR = 450;
        SimVals.maxBrakeTorqueFL = 450;
        SimVals.maxBrakeTorqueFR = 450;

        SimVals.maxSteerFL = 0.4;
        SimVals.maxSteerFR = 0.4;
        SimVals.maxSteerTime = 0.5;

        SimVals.vehicleMass=1300;

        SimVals.miuS[0]=347;
        SimVals.miuS[1]=347.5;

        SimVals.miuK[0]=330.5;
        SimVals.miuK[1]=330.7;

        SimVals.segma_0[0]=555;
        SimVals.segma_0[1]=470;

        SimVals.segma_1[0]=30;
        SimVals.segma_1[1]=30;

        SimVals.segma_2[0]=50;
        SimVals.segma_2[1]=50;

        applyInfo();
    }

    public static void sample2() {
        SimVals.resetStateVars();
        SimVals.beginSteerAfter = 0.5;
//        SimVals.brakeBL = 0;
//        SimVals.brakeBR = 0;
        SimVals.enableABS = false;
        SimVals.enableInfo = true;
        SimVals.enableOnline = false;
        SimVals.engineTorque = 0;
        SimVals.initialSpeed = 80;
        SimVals.isOptimum = false;

        SimVals.maxBrakeTorqueBL = 220;
        SimVals.maxBrakeTorqueBR = 220;
        SimVals.maxBrakeTorqueFL = 0;
        SimVals.maxBrakeTorqueFR = 0;

        SimVals.maxSteerFL = 0;
        SimVals.maxSteerFR = 0;
        SimVals.maxSteerTime = 0.5;

        SimVals.vehicleMass=1300;

        SimVals.miuS[0]=96.35;
        SimVals.miuS[1]=96.4;

        SimVals.miuK[0]=90.7516;
        SimVals.miuK[1]=90.75;

        SimVals.segma_0[0]=555;
        SimVals.segma_0[1]=470;

        SimVals.segma_1[0]=30;
        SimVals.segma_1[1]=30;

        SimVals.segma_2[0]=80;
        SimVals.segma_2[1]=80;
        isSample2=true;
        applyInfo();
    }

    public static void sample3() {
        SimVals.resetStateVars();
        SimVals.beginSteerAfter = 0.5;
//        SimVals.brakeBL = 0;
//        SimVals.brakeBR = 0;
        SimVals.enableABS = false;
        SimVals.enableInfo = true;
        SimVals.enableOnline = false;
        SimVals.engineTorque = 0;
        SimVals.initialSpeed = 80;
        SimVals.isOptimum = false;

        SimVals.maxBrakeTorqueBL = 1000;
        SimVals.maxBrakeTorqueBR = 1000;
        SimVals.maxBrakeTorqueFL = 1000;
        SimVals.maxBrakeTorqueFR = 1000;

        SimVals.maxSteerFL = 0;
        SimVals.maxSteerFR = 0;
        SimVals.maxSteerTime = 0.5;

        SimVals.vehicleMass=1300;

        SimVals.miuS[0]=96.35;
        SimVals.miuS[1]=96.4;

        SimVals.miuK[0]=90.7516;
        SimVals.miuK[1]=90.75;

        SimVals.segma_0[0]=555;
        SimVals.segma_0[1]=470;

        SimVals.segma_1[0]=30;
        SimVals.segma_1[1]=30;

        SimVals.segma_2[0]=0.03;
        SimVals.segma_2[1]=0.03;
        applyInfo();
    }

    public static void sample4() {
        SimVals.resetStateVars();
        SimVals.beginSteerAfter = 0.5;
        SimVals.brakeBL = 0;
        SimVals.brakeBR = 0;
        SimVals.enableABS = false;
        SimVals.enableInfo = true;
        SimVals.enableOnline = false;
        SimVals.engineTorque = 0;
        SimVals.initialSpeed = 80;
        SimVals.isOptimum = false;

        SimVals.maxBrakeTorqueBL = 450;
        SimVals.maxBrakeTorqueBR = 450;
        SimVals.maxBrakeTorqueFL = 450;
        SimVals.maxBrakeTorqueFR = 450;

        SimVals.maxSteerFL = -0.4;
        SimVals.maxSteerFR = -0.4;
        SimVals.maxSteerTime = 0.5;

        SimVals.vehicleMass=1300;

        SimVals.miuS[0]=347;
        SimVals.miuS[1]=347.5;

        SimVals.miuK[0]=330.5;
        SimVals.miuK[1]=330.7;

        SimVals.segma_0[0]=555;
        SimVals.segma_0[1]=470;

        SimVals.segma_1[0]=30;
        SimVals.segma_1[1]=30;

        SimVals.segma_2[0]=50;
        SimVals.segma_2[1]=50;

        applyInfo();
    }

    public static void sample5() {
        SimVals.resetStateVars();
        SimVals.brakeAfter = 0.5;
        SimVals.beginSteerAfter = 0.1;
        SimVals.brakeBL = 0;
        SimVals.brakeBR = 0;
        SimVals.enableABS = false;
        SimVals.enableInfo = true;
        SimVals.enableOnline = false;
        SimVals.engineTorque = 0;
        SimVals.initialSpeed = 80;
        SimVals.isOptimum = false;

        SimVals.maxBrakeTorqueBL = 450;
        SimVals.maxBrakeTorqueBR = 0;
        SimVals.maxBrakeTorqueFL = 450;
        SimVals.maxBrakeTorqueFR = 0;

        SimVals.maxSteerFL = 0.0;
        SimVals.maxSteerFR = 0.0;
        SimVals.maxSteerTime = 0.3;

        SimVals.vehicleMass=1300;

        SimVals.miuS[0]=347;
        SimVals.miuS[1]=347.5;

        SimVals.miuK[0]=330.5;
        SimVals.miuK[1]=330.7;

        SimVals.segma_0[0]=555;
        SimVals.segma_0[1]=470;

        SimVals.segma_1[0]=30;
        SimVals.segma_1[1]=30;

        SimVals.segma_2[0]=50;
        SimVals.segma_2[1]=50;

        applyInfo();


        applyInfo();
    }

    public static void sample6() {
        SimVals.resetStateVars();
        SimVals.brakeAfter = 0.0;
        SimVals.beginSteerAfter = 2;
        SimVals.brakeBL = 0;
        SimVals.brakeBR = 0;
        SimVals.enableABS = false;
        SimVals.enableInfo = true;
        SimVals.enableOnline = false;
        SimVals.engineTorque = 0;
        SimVals.initialSpeed = 80;
        SimVals.isOptimum = false;

        SimVals.maxBrakeTorqueBL = 450;
        SimVals.maxBrakeTorqueBR = 0;
        SimVals.maxBrakeTorqueFL = 450;
        SimVals.maxBrakeTorqueFR = 0;

        SimVals.maxSteerFL = 0.3;
        SimVals.maxSteerFR = 0.3;
        SimVals.maxSteerTime = 0.3;

        SimVals.vehicleMass=1300;

        SimVals.miuS[0]=347;
        SimVals.miuS[1]=347.5;

        SimVals.miuK[0]=330.5;
        SimVals.miuK[1]=330.7;

        SimVals.segma_0[0]=555;
        SimVals.segma_0[1]=470;

        SimVals.segma_1[0]=30;
        SimVals.segma_1[1]=30;

        SimVals.segma_2[0]=50;
        SimVals.segma_2[1]=50;

        applyInfo();
    }

    public static void sample7() {
        SimVals.resetStateVars();
        SimVals.beginSteerAfter = 0.5;
//        SimVals.brakeBL = 0;
//        SimVals.brakeBR = 0;
        SimVals.enableABS = true;
        SimVals.enableInfo = true;
        SimVals.enableOnline = false;
        SimVals.engineTorque = 0;
        SimVals.initialSpeed = 80;
        SimVals.isOptimum = false;

        SimVals.maxBrakeTorqueBL = 1000;
        SimVals.maxBrakeTorqueBR = 1000;
        SimVals.maxBrakeTorqueFL = 1000;
        SimVals.maxBrakeTorqueFR = 1000;

        SimVals.maxSteerFL = 0;
        SimVals.maxSteerFR = 0;
        SimVals.maxSteerTime = 0.5;

        SimVals.vehicleMass=1300;

        SimVals.miuS[0]=96.35;
        SimVals.miuS[1]=96.4;

        SimVals.miuK[0]=90.7516;
        SimVals.miuK[1]=90.75;

        SimVals.segma_0[0]=555;
        SimVals.segma_0[1]=470;

        SimVals.segma_1[0]=30;
        SimVals.segma_1[1]=30;

        SimVals.segma_2[0]=0.03;
        SimVals.segma_2[1]=0.03;
        applyInfo();
    }


}
