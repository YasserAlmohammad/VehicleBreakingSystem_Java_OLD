package dynamics;

import java.util.ArrayList;
import vehiclesimulationcore.*;

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
 * used to store static values to interface between the dynamics and UI
 *
 */
public class SimVals {
    public static double maxBrakeTorqueFL = 450;
    public static double maxBrakeTorqueFR = 450;
    public static double maxBrakeTorqueBR = 450;
    public static double maxBrakeTorqueBL = 450;

    public static double maxSteerFL = 0.1;
    public static double maxSteerFR = 0.1;


    public static double maxEngineTorque = 0;
    public static double vehicleMass = 1300;
    public static double initialSpeed = 80;

    public static double maxSteerTime = 0.5;
    public static double beginSteerAfter = 0.5;

    public static double elapsedTime = 0.0;
    public static double xDistance = 0.0;

    public static double springLength = 2;
    public static double springDamping = 450;
    public static double springCoeff = 18200;

    public static double engineTorque = 0;

    public static double[] miuS = new double[2];
    public static double[] miuK = new double[2];
    public static double[] segma_0 = new double[2];
    public static double[] segma_1 = new double[2];
    public static double[] segma_2 = new double[2];


    ////////////////////////////////////////////////////
    //////////////////////////////////////////////////
    /////// drawing lists ( don't miss with)
    //stores PointDouble as it's coords
    public static ArrayList listXY = new ArrayList();
    public static ArrayList listXTime = new ArrayList();
    public static ArrayList listYTime = new ArrayList();
    public static ArrayList listXZ = new ArrayList();

    public static ArrayList listSlipTimeFL = new ArrayList();
    public static ArrayList listSlipTractiveFL = new ArrayList();
    public static ArrayList listSlipTractiveLatFL = new ArrayList();

    public static ArrayList listSlipTimeFR = new ArrayList();
    public static ArrayList listSlipTractiveFR = new ArrayList();
    public static ArrayList listSlipTractiveLatFR = new ArrayList();

    public static ArrayList listSlipTimeBL = new ArrayList();
    public static ArrayList listSlipTractiveBL = new ArrayList();
    public static ArrayList listSlipTractiveLatBL = new ArrayList();

    public static ArrayList listSlipTimeBR = new ArrayList();
    public static ArrayList listSlipTractiveBR = new ArrayList();
    public static ArrayList listSlipTractiveLatBR = new ArrayList();


    public static int listCompsitionCount = 0; //only five  tests to be stored in the same list
    public static int listMaxCompose = 5;


    //////////////////////////////////////////////////////
    ///////////////// global variables ///////////////////
    //////////////////////////////////////////////////////
    public static boolean enableOnline = false;
    public static boolean enableABS = false;

    //////////////////////////////////////////////////////
    ////////////////// state variables ///////////////////
    public static double steerFL = 0;
    public static double steerFR = 0;
    public static double steerBL = 0;
    public static double steerBR = 0;
    public static double steerStep = 0;

    public static double brakeBL = 0;
    public static double brakeBR = 0;

    public static boolean running = false;

    //total distance
    public static double distance = 0;

    public static boolean isOptimum = false;
    public static double execTime = 0;
    public static boolean enableInfo = false;

    public static double brakeAfter = 0;
    public static boolean enableNew=false;

    //  public static double maxEngineTorque=0;

    SimVals() {
        reset();
    }

    //reset values to default
    public static void reset() {
        maxBrakeTorqueFL = 450;
        maxBrakeTorqueFR = 450;
        maxBrakeTorqueBR = 450;
        maxBrakeTorqueBL = 450;

        maxSteerFL = 0.1;
        maxSteerFR = 0.1;

        maxEngineTorque = 0;
        vehicleMass = 1300;
        initialSpeed = 80;

        maxSteerTime = 0.5;
        beginSteerAfter = 0.5;

        springLength = 2;
        springDamping = 450;
        springCoeff = 18200;

        engineTorque = 0;

        distance = 0;

        isOptimum = false;
        execTime = 0;
        enableInfo = false;

        miuK[0] = 90.7516;
        miuK[1] = 90.75;

        miuS[0] = 96.35;
        miuS[1] = 96.4;
        segma_0[0] = 555;
        segma_0[1] = 470;

        segma_1[0] = 30;
        segma_1[1] = 30;

        segma_2[0] = 0.1;
        segma_2[1] = 0.1;
        brakeAfter = 0;
        enableNew=false;
        resetLists();
    }

    /**
     * reset drawing lists
     */
    public static void resetLists() {
        listCompsitionCount = 0;
        listXY.clear();
        listXTime.clear();
        listYTime.clear();
        listXZ.clear();

        listSlipTimeFL.clear();
        listSlipTractiveFL.clear();
        listSlipTractiveLatFL.clear();

        listSlipTimeFR.clear();
        listSlipTractiveFR.clear();
        listSlipTractiveLatFR.clear();

        listSlipTimeBL.clear();
        listSlipTractiveBL.clear();
        listSlipTractiveLatBL.clear();

        listSlipTimeBR.clear();
        listSlipTractiveBR.clear();
        listSlipTractiveLatBR.clear();
    }

    public static void resetStateVars() {
        steerFL = 0;
        steerFR = 0;
        steerBL = 0;
        steerBR = 0;
        steerStep = 0;

        brakeBL = 0;
        brakeBR = 0;
    }

    /**
     * -9999999,-9999999 is the separation point between two lists
     */
    public static void addListSeparator() {
        listXY.add(new PointDouble( -9999999, -9999999));
        listXTime.add(new PointDouble( -9999999, -9999999));
        listYTime.add(new PointDouble( -9999999, -9999999));
        listXZ.add(new PointDouble( -9999999, -9999999));

        listSlipTractiveFL.add(new PointDouble( -9999999, -9999999));
        listSlipTimeFL.add(new PointDouble( -9999999, -9999999));

        listSlipTractiveFR.add(new PointDouble( -9999999, -9999999));
        listSlipTimeFR.add(new PointDouble( -9999999, -9999999));

        listSlipTractiveBL.add(new PointDouble( -9999999, -9999999));
        listSlipTimeBL.add(new PointDouble( -9999999, -9999999));

        listSlipTractiveBR.add(new PointDouble( -9999999, -9999999));
        listSlipTimeBR.add(new PointDouble( -9999999, -9999999));
    }
}
