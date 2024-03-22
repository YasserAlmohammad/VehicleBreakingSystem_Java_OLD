package dynamics;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class TireOld {
  //**************** TIRE CHARACTARESTICS ****************
  double[] segma_0 = new double[2];
  double[] segma_1 = new double[2];
  double[] segma_2 = new double[2];
  // kinematic and static friction coefficients in both x and y directions:
  double[] miuK = new double[2];
  double[] miuS = new double[2];
  // tread length et al:
  double L = 0.15;      /*    L    */
  double xiLL = 0.02;   /*  xiL/L  */
  double xiRL = 0.77;   /*  xiR/L  */
  // derived variables from the 3 previous ones:
  double fmax = 200;//**********Error Error Error Error Error Error Error Error Error Error
  double xiL = xiLL * L;
  double alpha1 = fmax/xiL;
  double xiR = xiRL * L;
  double alpha2 = -fmax/(L-xiR);
  double beta2 = -L * alpha2;
  // tire space in vehicle space:
  double[] TXv = new double[3];
  double[] TYv = new double[3];
  double[] TZv = new double[3];

  // tire space in world space:
  double[] TXw = new double[3];
  double[] TYw = new double[3];
  double[] TZw = new double[3];
  // tire space in world space normalized:
  double[] NTXw = new double[3];
//  double[] NTYw = new double[3];
  double[] NTZw = new double[3];

  // world tire connection:
  double TR[][] = new double[3][3];
  double TRinv[][] = new double[3][3];

  //
  double[] V_intTireSpace = new double[3];


  double xShiftFront;
  double xShiftRear;
  double yShift;
  double zShift;

  double raw = 1;
  double vs = 3.96;
  // wheel radius:
  double r = 0.30;
  // wheel moment of inertia:
  double jw = .5*10*r*r;

  // tire rolling resistance parameters:
  double Cr0 = 0.01;
  double Crv = 0.00014;
  // tire rolling resistance force:
  double RR;

  //**************** TIRE STATE VARIABLES ****************

  // slip velocity: x,y components:
  double[] Vr = new double[2];
  // tire angular velocity
  double W;
  // tire angular abcissa:
  double Wa;
  // tire vector speed in world space:
  double[] V = new double[3];

  double worldRoad_tirePoint[] = new double[3];

  //an alternative road tire interaction point in vehicle and world respectively:
  double[] localTstrut = new double[3];
  double[] worldTstrut = new double[3];

  double treadCoordSystemX[] = new double[3];
  double[] soso = new double[3];
  double[][] riri = new double[3][3];

  double[] arm = new double[3];

  double[] coco1 = new double[2];
  double[] coco2 = new double[2];
  double[] coco3 = new double[2];
  double[] temp1 = new double[3];
  double[] temp2 = new double[3];
  double[] temp3 = new double[3];

  double gVr;                     /*   (2)   */
  double lamdaVr;                 /*   (3)   */
  double[] CoVr = new double[2];  /*   (4)   */
  double[] C1 = new double[2];    /*   (5)   */   /*   C1i   */
  double[] C2 = new double[2];    /*   (5)   */   /*   C2i   */
  double[] Zss = new double[2];   /*   (5)   */
  double[] Kss = new double[2];   /*   (6)   */
  double[] Z = new double[2];     /*   (7)   */
  double[] Zdot = new double[2];  /*   (7)   */

  // an auxiliary variable:
  double[] coco = new double[2];
  //*********** TIRE FORCES ****************
  double[] xyForce = new double[2];
  //***** FORCE VECTOR IN TREAD SPACE ******
  double[] ForceInTreadSpace = new double[3];
  //***** FORCE VECTOR IN WORLD SPACE ******
  double[] Force = new double[3];
  //***** FORCE VECTOR IN WORLD SPACE ******
  double[] Torque = new double[3];

  //an auxiliary function
  private int sign(double value){
    if (value > .001)
      return 1;
    else
      if (value < -.001)
        return -1;
      else
        return 0;
  }
  //********* GENERATING TIRE FORCES *******
  public void calcForceAndTorque (Vehicle vehicle, double drivingTorque, double brakingTorque,
                       double steeringAngle,  int index){

    double row1 = 1;
    double row2 = 1;
    double row3 = 1;
    double row4 = 1;
    double row5 = 1;
    double row6 = 1;
    double xiLC2i =1;
    double xiRC2i = 1;
    double LC2i = 1 ;
    //**************************    (1)    **************************


    // slip can now be easily determined: but we have to know the velocity vector
    // of the chassis corner where the wheel exists:
    MatrixAlgebra.sub(temp1,worldRoad_tirePoint,vehicle.X);
    MatrixAlgebra.crossProd(temp2,vehicle.Omega,temp1);
    MatrixAlgebra.add(V,temp2,vehicle.V);

    // obtaining the X of the wheel space- in vehicle space:
    TXv[0] = Math.cos(steeringAngle);
    TXv[1] = 0;
    TXv[2] = Math.sin(steeringAngle);

    // obtaining the X of the wheel space- in world space:
    MatrixAlgebra.mul(TXw,vehicle.R,TXv);
    // projecting TXv on the road surface:
    TXw[1] = 0;
    // Normalizing TXw:
    MatrixAlgebra.normalize(NTXw,TXw);
    // computeing NTZw:
    NTZw[0] = -NTXw[2];
    NTZw[1] =  0;
    NTZw[2] =  NTXw[0];

    // computing TR:
    TR[0][0] = NTXw[0];
    TR[1][0] = NTXw[1];
    TR[2][0] = NTXw[2];

    TR[0][1] = 0;
    TR[1][1] = 1;
    TR[2][1] = 0;

    TR[0][2] = NTZw[0];
    TR[1][2] = NTZw[1];
    TR[2][2] = NTZw[2];

    // computing TRinv:
    TRinv[0][0] =  NTXw[0];
    TRinv[1][0] =  NTXw[1];
    TRinv[2][0] = -NTXw[2];

    TRinv[0][1] = 0;
    TRinv[1][1] = 1;
    TRinv[2][1] = 0;

    TRinv[0][2] = -NTZw[0];
    TRinv[1][2] =  NTZw[1];
    TRinv[2][2] =  NTZw[2];

    MatrixAlgebra.mul(V_intTireSpace,TRinv,V);


//    double vm = MatrixAlgebra.getMagnitude(VgroundProjectionInTreadCoordSystem);

    // we now have all the data to compute Vri (slip velocity)
    double wr = W * r;

    Vr[0] =  (V_intTireSpace[0] - wr);
//    System.out.println("Vr[0] is : "+Vr[0]);
//    System.out.println("Vin tread [0] is : "+V_intTireSpace[0]);
//    System.out.println("W is : "+wr);
    Vr[1] =  V_intTireSpace[2];

//    Vr[0] =  (V_intTireSpace[0]);
//    Vr[1] =  V_intTireSpace[2];

    if((Vr[0]==0)&&(Vr[1]==0)){
      xyForce[0] = xyForce[1] = 0;
    }
      else{


      //**************************    (2)    **************************
      // computing gVr:

      gVr = Math.exp(-MatrixAlgebra.getMagnitude(Vr) / vs);
      MatrixAlgebra.power(coco1,miuK,2);
      MatrixAlgebra.mul(coco,coco1,Vr);
      double mk2vr = MatrixAlgebra.getMagnitude(coco);
      MatrixAlgebra.mul(coco,miuK,Vr);
      double mkvr = MatrixAlgebra.getMagnitude(coco);
      double a = mk2vr/mkvr;

      //*******************************
      MatrixAlgebra.power(coco1,miuS,2);
      MatrixAlgebra.mul(coco,coco1,Vr);
      double ms2vr = MatrixAlgebra.getMagnitude(coco);
      MatrixAlgebra.mul(coco,miuS,Vr);
      double msvr = MatrixAlgebra.getMagnitude(coco);
      double b = ms2vr/msvr;

      gVr *= (b-a);
      gVr += a;

      //**************************    (3)    **************************
      // computing lamdaVr:
      lamdaVr = mk2vr / gVr;

      //**************************    (4)    **************************
      // computing Coi:
      CoVr[0] = lamdaVr * segma_0[0] / (miuK[0]*miuK[0]);
      CoVr[1] = lamdaVr * segma_0[1] / (miuK[1]*miuK[1]);

      //**************************    (5)    **************************
      // computing Zi'ss
      for (int i=0; i<2; i++){
        C1[i] = Vr[i] * (miuK[i]*miuK[i])   /   (lamdaVr * segma_0[i]);
        C2[i] = Math.abs(wr)/ CoVr[i];
      }
      for (int i=0; i<2; i++){
        row6 = .5*alpha1*xiL*xiL+fmax*(xiR-xiL);
        row6 += .5*alpha2*(L*L-xiR*xiR)+beta2*(L-xiR);

        row6 *= segma_2[i]*Vr[i];
       // System.out.println("row 6 is : "+row6);
        if(C2[i]==0){


          row1 = alpha1 * (xiL*xiL)/2;
          row2 = fmax * (xiR-xiL);
          row3 = .5 * alpha2 * ( L*L - xiR*xiR ) + beta2 * (L-xiR);
          row4 = 0;
          row5 = 0;

          Zss[i] = (1/(vehicle.spring[index].forceMagnitude)) * C1[i] * (row1+row2+row3+row4+row5);


          xyForce[i] = -segma_0[i] * C1[i] * (row1+row2+row3+row4+row5) - row6;

          }
        else{
          xiLC2i = -xiL / C2[i];
          xiRC2i = -xiR / C2[i];
          LC2i = -L / C2[i];
          row1 = alpha1 * ((xiL*xiL)/2 + C2[i]* (xiL*Math.exp(xiLC2i) - C2[i] + C2[i]*Math.exp(xiLC2i)));
          row2 = fmax * ((xiR-xiL) + C2[i]*(Math.exp(xiRC2i) - Math.exp(xiLC2i)));
          row3 = .5 * alpha2 * ( L*L - xiR*xiR ) + beta2 * (L-xiR);
          row4 = alpha2 * C2[i] * ( L*Math.exp(LC2i) - xiR*Math.exp(xiRC2i));
          row5 = C2[i] * (beta2 + alpha2*C2[i]) * (Math.exp(LC2i)-Math.exp(xiRC2i));

          Zss[i] = (1/(vehicle.spring[index].forceMagnitude)) * C1[i] * (row1+row2+row3+row4+row5);


          xyForce[i] = -segma_0[i] * C1[i] * (row1+row2+row3+row4+row5) - row6;


          }
          double Wv=V_intTireSpace[0]/r;
          double slip=(W-Wv)/Math.max(W,Wv);

      }

      //**************************    (6)    **************************
      // computing Ki'ss
      for(int i=0;i<2;i++)
        Kss[i] =  (Vr[i]/Zss[i]-CoVr[i]);

      //**************************    (7)    **************************
      // computing Zi dot
      for(int i=0;i<2;i++)
        Zdot[i] = Vr[i] - (CoVr[i] + Kss[i])*Z[i];



      //**************************    (8)    **************************
      // we have all the data needed too compute road-tire interaction:

//      for(int i=0;i<2;i++)
//        xyForce[i] = - (vehicle.spring[index].forceMagnitude ) *
//        (segma_0[i]*Z[i] + segma_1[i]*Zdot[i] + segma_2[i]*Vr[i]);




      }//end of else


    // we have to return wheel forces in terms of vector representations:
    // first we compute force vector in tread-fixed coordinate system, then
    // we shift to calculate its position in world space:

    // obtaining force vector in tread-fixed coordinate system:
    ForceInTreadSpace[0] = xyForce[0];
    ForceInTreadSpace[1] = 0;
    ForceInTreadSpace[2] = xyForce[1];
    // obtaining force vector in world space:
    MatrixAlgebra.mul(Force,TR,ForceInTreadSpace);

    // obtaining torque:
    MatrixAlgebra.sub(arm,worldRoad_tirePoint,vehicle.X);
    MatrixAlgebra.crossProd(Torque,arm,Force);




    // we have to update Zi(t) for the next time step:
    for (int i=0; i<2; i++)
      Z[i] = Solver.solve(Z[i],Zdot[i],SimulationTuning.timeStep);



    //*************************


    // first we have to determine wheel angular velocity in order to determine slip
    // determining W:
    // first we determine rolling resistance, the term Mv.g can be substituted
    //with spring reaction force (Newton's 3rd law)


    RR = (vehicle.mass/4 ) *(Cr0+Crv*W*r);

    // computing total torque:
    // driving torque is positive counterclockwise
    // both BRAKING TORQUE and ROLLIG RESISTANCE always oppposites velocity
    // driving force generates a positive torqure when positive and vice versa:
    if(SimulationCore.brake[index]==0)
      W = V_intTireSpace[0]/r;
    else{
//      double totalTorque = drivingTorque - sign(W)*brakingTorque - sign(W)*RR + xyForce[0]*r;
      double totalTorque = drivingTorque - sign(W)*brakingTorque - sign(W)*RR + sign(W)*xyForce[0]*r;
      double Wdot;
      Wdot = totalTorque / jw;

      W = Solver.solve(W,Wdot,SimulationTuning.timeStep);

    }
    Wa = Solver.solve(Wa,W,SimulationTuning.timeStep);
  }


  public TireOld() {
    segma_0[0] = 555;
    segma_0[1] = 470;

    segma_1[0] = 300;//error error error
    segma_1[1] = 300;//error error error

    segma_2[0] = 5;//error error error
    segma_2[1] = 5;//error error error

    miuK[0] = 0.7516;
    miuK[1] = 0.75;

    miuS[0] = 1.35;
    miuS[1] = 1.4;


//    miuK[0] = 2.5;
//    miuK[1] = 2.7;
//
//    miuS[0] = 3;
//    miuS[1] = 3.5;




    Z[0]=Z[1]=0;

    r = 0.30;
    //fmax =
        try {
            jbInit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    public void init(Vehicle vehicle,double speed,int index){
        // slip angle zero:
        //tire[i].alpha = 0;
        // slip velocity vector in wheel space:
        Vr[0] =.01;
        Vr[1] =.0000000000001;
        // tire angular velocity:
        W = speed/r;
        // tire vector speed in wheel space:
        V[0] = speed; // this is because there is no steering in start state.
        V[1] = 0;
        // initialize tire internal deformation state:
        Z[0] = .01;
        Z[1] = .001;
        fmax = vehicle.mass/8;
        xyForce[0] = .1;
        xyForce[1] = .1;
        xShiftFront = .7;
        xShiftRear = .3;
        yShift = .1;
        zShift = .25;
//        switch (index){
//          case 0:
//            localTstrut[0] =  vehicle.xDim/2-xShiftFront;
//            localTstrut[1] = -vehicle.yDim/2+yShift;
//            localTstrut[2] = -vehicle.zDim/2+zShift;
//            break;
//
//          case 1:
//            localTstrut[0] =  vehicle.xDim/2-xShiftFront;
//            localTstrut[1] = -vehicle.yDim/2+yShift;
//            localTstrut[2] =  vehicle.zDim/2-zShift;
//            break;
//
//          case 2:
//            localTstrut[0] = -vehicle.xDim/2+xShiftRear;
//            localTstrut[1] = -vehicle.yDim/2+yShift;
//            localTstrut[2] =  vehicle.zDim/2-zShift;
//            break;
//
//          case 3:
//            localTstrut[0] = -vehicle.xDim/2+xShiftRear;
//            localTstrut[1] = -vehicle.yDim/2+yShift;
//            localTstrut[2] = -vehicle.zDim/2+zShift;
//            break;
//        }

        switch (index){
          case 0:
            localTstrut[0] =  vehicle.xDim/2;
            localTstrut[1] = -vehicle.yDim/2;
            localTstrut[2] = -vehicle.zDim/2;
            break;

          case 1:
            localTstrut[0] =  vehicle.xDim/2;
            localTstrut[1] = -vehicle.yDim/2;
            localTstrut[2] =  vehicle.zDim/2;
            break;

          case 2:
            localTstrut[0] = -vehicle.xDim/2;
            localTstrut[1] = -vehicle.yDim/2;
            localTstrut[2] =  vehicle.zDim/2;
            break;

          case 3:
            localTstrut[0] = -vehicle.xDim/2;
            localTstrut[1] = -vehicle.yDim/2;
            localTstrut[2] = -vehicle.zDim/2;
            break;
        }

    }
  public static void main(String[] args) {
    TireOld tire1 = new TireOld();

  }

    private void jbInit() throws Exception {
    }
    public class NumException extends Exception{

    }
}
