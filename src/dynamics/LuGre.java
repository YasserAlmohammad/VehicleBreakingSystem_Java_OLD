package dynamics;

/**
 * <p>Title: Vehicle Dynamics Simulation Program</p>
 * <p>Description: vehicle dynmaics, braking system simulation</p>
 * <p>Copyright: Copyright (c) 2006 Faculty of Information Technology Engineering at Damascus University</p>
 * <p>Company: FIT</p>
 * @author Yasser Almohammad(Graphics Design and implementation), Ahmad Zoubi, Usama Rmelawi, Mohammad Askar,Qousai Dabour (vehicle case study, core functionality)
 * @version 1.0
 */

public class LuGre extends TTire {
    public double L, xiL, xiR, fmax, vs, raw, alpha1, alpha2, beta2;

    public double[] miuK = new double[2];
    public double[] miuS = new double[2];
    public double[] segma_0 = new double[2];
    public double[] segma_1 = new double[2];
    public double[] segma_2 = new double[2];


    private double gVr, lamdaVr, row1, row2, row3, row4, row5, row6, xiLC2i,
            xiRC2i, LC2i, xiRL, xiLL;
    private double[] coco1 = new double[2];
    private double[] coco = new double[2];
    private double[] CoVr = new double[2];
    private double[] C1 = new double[2];
    private double[] C2 = new double[2];
    private double[] Kss = new double[2];
    private double[] Z = new double[2];
    private double[] Zss = new double[2];
    private double[] Zdot = new double[2];


    public void init(Vehicle vehicle, double speed, int index) {
        super.init(vehicle, speed, index);
        // initialize tire internal deformation state:
//    Z[0] = .01;
//    Z[1] = .001;

        Z[0] = 0;
        Z[1] = 0;

    }

    protected void calculateXYforce1(Vehicle vehicle, double drivingTorque,
                                     double brakingTorque,
                                     double steeringAngle, int index) {

    }


    protected void calculateXYforce(Vehicle vehicle, double drivingTorque,
                                    double brakingTorque,
                                    double steeringAngle, int index) {
        double wr = W * radius;
        Vr[0] = (V_inTireSpace[0] - wr);
        Vr[1] = V_inTireSpace[2];
        //////////////////////////////////////////////////////////////////////////
        if (SimVals.enableNew) {
            fmax = 4 + Math.abs(vehicle.spring[index].forceMagnitude) / 16;
//    if (index==1)
//      System.out.println("front: "+fmax);
//    if (index==2)
//      System.out.println("rear: "+fmax);
            //fmax = 200;
            alpha1 = fmax / xiL;
            alpha2 = -fmax / (L - xiR);
            beta2 = -L * alpha2;
        }
        /////////////////////////////////////////////////////////////////////////


        for (int i = 0; i < 2; i++) {
            if (Vr[i] == 0) {
                xyForce[i] = 0;
            } else {

                //**************************    (2)    **************************
                 // computing gVr:

                 gVr = Math.exp( -MatrixAlgebra.getMagnitude(Vr) / vs);
                MatrixAlgebra.power(coco1, miuK, 2);
                MatrixAlgebra.mul(coco, coco1, Vr);
                double mk2vr = MatrixAlgebra.getMagnitude(coco);
                MatrixAlgebra.mul(coco, miuK, Vr);
                double mkvr = MatrixAlgebra.getMagnitude(coco);
                double a = mk2vr / mkvr;

                //*******************************
                 MatrixAlgebra.power(coco1, miuS, 2);
                MatrixAlgebra.mul(coco, coco1, Vr);
                double ms2vr = MatrixAlgebra.getMagnitude(coco);
                MatrixAlgebra.mul(coco, miuS, Vr);
                double msvr = MatrixAlgebra.getMagnitude(coco);
                double b = ms2vr / msvr;

                gVr *= (b - a);
                gVr += a;

                //**************************    (3)    **************************
                 // computing lamdaVr:
                 lamdaVr = mk2vr / gVr;

                //**************************    (4)    **************************
                 // computing Coi:
                 CoVr[i] = lamdaVr * segma_0[i] / (miuK[i] * miuK[i]);

                //**************************    (5)    **************************
                 // computing Zi'ss

                 C1[i] = Vr[i] * (miuK[i] * miuK[i]) / (lamdaVr * segma_0[i]);
                C2[i] = Math.abs(wr) / CoVr[i];

                row6 = .5 * alpha1 * xiL * xiL + fmax * (xiR - xiL);
                row6 += .5 * alpha2 * (L * L - xiR * xiR) + beta2 * (L - xiR);

                row6 *= segma_2[i] * Vr[i];

                /*
                              row6 = .5*alpha1*xiL*xiL+fmax*(xiR-xiL);
                              row6 += .5*alpha2*(L*L-xiR*xiR)+beta2*(L-xiR);

                              row6 *= segma_2[i]*Vr[i];

                 */
                if (C2[i] == 0) {
                    row1 = alpha1 * (xiL * xiL) / 2;
                    row2 = fmax * (xiR - xiL);
                    row3 = .5 * alpha2 * (L * L - xiR * xiR) + beta2 * (L - xiR);
                    row4 = 0;
                    row5 = 0;

                    Zss[i] = (1 / (vehicle.spring[index].forceMagnitude)) *
                             C1[i] * (row1 + row2 + row3 + row4 + row5);

                    xyForce[i] = -segma_0[i] * C1[i] *
                                 (row1 + row2 + row3 + row4 + row5) - row6;

                } else {
                    xiLC2i = -xiL / C2[i];
                    xiRC2i = -xiR / C2[i];
                    LC2i = -L / C2[i];
                    row1 = alpha1 *
                           ((xiL * xiL) / 2 +
                            C2[i] *
                            (xiL * Math.exp(xiLC2i) - C2[i] +
                             C2[i] * Math.exp(xiLC2i)));
                    row2 = fmax *
                           ((xiR - xiL) +
                            C2[i] * (Math.exp(xiRC2i) - Math.exp(xiLC2i)));
                    row3 = .5 * alpha2 * (L * L - xiR * xiR) + beta2 * (L - xiR);
                    row4 = alpha2 * C2[i] *
                           (L * Math.exp(LC2i) - xiR * Math.exp(xiRC2i));
                    row5 = C2[i] * (beta2 + alpha2 * C2[i]) *
                           (Math.exp(LC2i) - Math.exp(xiRC2i));

                    Zss[i] = (1 / (vehicle.spring[index].forceMagnitude)) *
                             C1[i] * (row1 + row2 + row3 + row4 + row5);

                    xyForce[i] = -segma_0[i] * C1[i] *
                                 (row1 + row2 + row3 + row4 + row5) - row6;

                }
                double Wv = V_inTireSpace[0] / radius;
                double slip = (W - Wv) / Math.max(W, Wv);

                //**************************    (6)    **************************
                 // computing Ki'ss

                 Kss[i] = (Vr[i] / Zss[i] - CoVr[i]);

                //**************************    (7)    **************************
                 // computing Zi dot

                 Zdot[i] = Vr[i] - (CoVr[i] + Kss[i]) * Z[i];

                 //**************************    (8)    **************************
                  // we have all the data needed too compute road-tire interaction:


//        //xyForce[i] = - (vehicle.spring[index].forceMagnitude ) *
//        //(segma_0[i]*Z[i] + segma_1[i]*Zdot[i] + segma_2[i]*Vr[i]);




            } //end of else

            Z[i] = Solver.solve(Z[i], Zdot[i], SimulationTuning.timeStep);
        } // end of for

        // we have to return wheel forces in terms of vector representations:
        // first we compute force vector in tread-fixed coordinate system, then
        // we shift to calculate its position in world space:

    }

    public LuGre(Vehicle vehicle, int index) {
        super(vehicle, index);
        segma_0[0] = SimVals.segma_0[0];
        segma_0[1] = SimVals.segma_0[1];

        segma_1[0] = SimVals.segma_1[0];
        segma_1[1] = SimVals.segma_1[1];

        segma_2[0] = SimVals.segma_2[0]; //error error error
        segma_2[1] = SimVals.segma_2[1]; //error error error
//    segma_2[0] = 4.5;//error error error
//    segma_2[1] = 4.5;//error error error

//    miuK[0] = 15.5;
//    miuK[1] = 15.7;
//
//    miuS[0] = 67;
//    miuS[1] = 67.5;

//    miuK[0] = 330.5;
//    miuK[1] = 330.7;
//
//    miuS[0] = 347;
//    miuS[1] = 347.5;

        miuK[0] = SimVals.miuK[0];
        miuK[1] = SimVals.miuK[1];

        miuS[0] = SimVals.miuS[0];
        miuS[1] = SimVals.miuS[1];

        raw = 1;
        vs = 3.96;
        // tread length et al:
        L = 0.15; /*    L    */
        xiLL = 0.02; /*  xiL/L  */
        xiRL = 0.77; /*  xiR/L  */
        // derived variables from the 3 previous ones:
        fmax = vehicle.mass / 8; //**********Error Error Error Error Error Error Error Error Error Error
        xiL = xiLL * L;
        alpha1 = fmax / xiL;
        xiR = xiRL * L;
        alpha2 = -fmax / (L - xiR);
        beta2 = -L * alpha2;
    }
}
