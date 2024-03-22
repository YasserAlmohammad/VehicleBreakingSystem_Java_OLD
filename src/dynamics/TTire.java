package dynamics;

/**
 * <p>Title: Vehicle Dynamics Simulation Program</p>
 * <p>Description: vehicle dynmaics, braking system simulation</p>
 * <p>Copyright: Copyright (c) 2006 Faculty of Information Technology Engineering at Damascus University</p>
 * <p>Company: FIT</p>
 * @author Yasser Almohammad(Graphics Design and implementation), Ahmad Zoubi, Usama Rmelawi, Mohammad Askar,Qousai Dabour (vehicle case study, core functionality)
 * @version 1.0
 */

public abstract class TTire {

  //               DATA MEMBERS
  //*********************************************
  //*********************************************

  // ************* tire specifics *******************8
  public double Wdot,W,Wa,radius,tireMass,inertia,slip;
  protected double RR,Cr0,Crv;

  //************ velocity from world space to tire space ************

  // tire velocity vector in world space
  public double[] V = new double[3];
  // tire velocity vector in tire space
  public double[] V_inTireSpace = new double[3];
  // slip long/lat velocity in tire space
  public double[] Vr = new double[2];

  //************** force from tire space to world space *************

  // force long/lat components in tire space
  public double[] xyForce = new double[2];
  // force vector in tire space
  public double[] localForce = new double[3];
  // force vector in world space
  public double[] Force = new double[3];

  //*********************** torque calculation **********************
  public double[] effectivePoint = new double[3];
  public double[] localEffectivePoint = new double[3];
  public double[] Torque = new double[3];

  // tire to world connection
  public double[][] TR = new double[3][3];
  public double[][] TRinv = new double[3][3];

  private double[] TXv = new double[3];
  private double[] TXw = new double[3];
  private double[] NTZw = new double[3];
  private double[] NTXw = new double[3];
  private double[] temp1 = new double[3];
  private double[] temp2 = new double[3];
  private double[] arm = new double[3];



  //               tire constructor
  //*********************************************
  TTire(Vehicle vehicle,int index){
    radius = .3;
    tireMass = 10;
    inertia = .5*tireMass*radius*radius;
    Cr0 = 0.01;
    Crv = 0.00014;
    switch (index){
      case 0:
        localEffectivePoint[0] =  vehicle.xDim/2;
        localEffectivePoint[1] = -vehicle.yDim/2;
        localEffectivePoint[2] = -vehicle.zDim/2;
        break;

      case 1:
        localEffectivePoint[0] =  vehicle.xDim/2;
        localEffectivePoint[1] = -vehicle.yDim/2;
        localEffectivePoint[2] =  vehicle.zDim/2;
        break;

      case 2:
        localEffectivePoint[0] = -vehicle.xDim/2;
        localEffectivePoint[1] = -vehicle.yDim/2;
        localEffectivePoint[2] =  vehicle.zDim/2;
        break;

      case 3:
        localEffectivePoint[0] = -vehicle.xDim/2;
        localEffectivePoint[1] = -vehicle.yDim/2;
        localEffectivePoint[2] = -vehicle.zDim/2;
        break;
        }

  }
  private int sign(double value){
    if (value > .1)  //.1
      return 1;
    else
      if (value < -.1)   //.1
        return -1;
      else
        return 0;
  }
  protected void init(Vehicle vehicle,double speed, int index){

    // slip angle zero:
    //tire[i].alpha = 0;
    // slip velocity vector in wheel space:
    Vr[0] =.01;
    Vr[1] =.0000000000001;
    // tire angular velocity:
    W = speed/radius;
    // tire vector speed in wheel space:
    V[0] = speed; // this is because there is no steering in start state.
    V[1] = 0;
    // initialize tire internal deformation state:

    xyForce[0] = .1;
    xyForce[1] = .1;








//    // slip velocity vector in wheel space:
//    Vr[0] =.01;
//    Vr[1] =.0000000000001;
//    // tire angular velocity:
//    W = speed/radius;
//    // tire vector speed in wheel space:
//    V[0] = speed; // this is because there is no steering in start state.
//    V[1] = 0;
//
//    xyForce[0] = .1;
//    xyForce[1] = .1;
  }









  protected void calculateXYforce(Vehicle vehicle, double drivingTorque, double brakingTorque,
                       double steeringAngle,  int index){}
  // a method to calculate TR and TRinv
  private void calculateTireWorldSwitches(Vehicle vehicle,double steeringAngle){
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

  }
  private void calculateV_inTireSpace(Vehicle vehicle){

    MatrixAlgebra.sub(temp1,effectivePoint,vehicle.X);
    MatrixAlgebra.crossProd(temp2,vehicle.Omega,temp1);

    MatrixAlgebra.add(V,temp2,vehicle.V);
    V[1] = 0;
    MatrixAlgebra.mul(V_inTireSpace,TRinv,V);

  }
  public void calcForceAndTorque(Vehicle vehicle, double drivingTorque, double brakingTorque,
                       double steeringAngle,  int index){




    calculateTireWorldSwitches(vehicle,steeringAngle);

    calculateV_inTireSpace(vehicle);
    V_inTireSpace[1] = 0;
    calculateXYforce(vehicle,drivingTorque,brakingTorque,steeringAngle,index);
    localForce[0] = xyForce[0];
    localForce[1] = 0;
    localForce[2] = xyForce[1];


    MatrixAlgebra.mul(Force,TR,localForce);

    // obtaining torque:
    MatrixAlgebra.sub(arm,effectivePoint,vehicle.X);
    MatrixAlgebra.crossProd(Torque,arm,Force);


    RR = (vehicle.mass/4 ) *(Cr0+Crv*W*radius);
    RR = 0;
    // computing total torque:
    // driving torque is positive counterclockwise
    // both BRAKING TORQUE and ROLLIG RESISTANCE always oppposites velocity
    // driving force generates a positive torqure when positive and vice versa:

    if((SimulationCore.brake[index]==0)&&(SimulationCore.drive[index]==0))
      W = V_inTireSpace[0]/radius;
    else{
      //double totalTorque = drivingTorque - sign(W)*brakingTorque - sign(W)*RR + sign(W)*xyForce[0]*radius;
      double totalTorque = drivingTorque - sign(W)*brakingTorque - sign(W)*RR
                           - sign(drivingTorque + brakingTorque)*(Math.abs(sign(W)))*xyForce[0]*radius;
//      double totalTorque = drivingTorque - sign(W)*brakingTorque + sign(W)*xyForce[0]*radius;

      Wdot = totalTorque / inertia;

      W = Solver.solve(W,Wdot,SimulationTuning.timeStep);

    }
    Wa = Solver.solve(Wa,W,SimulationTuning.timeStep);
    double Wv=V_inTireSpace[0]/radius;
    slip=(W-Wv)/Math.max(W,Wv);
//    System.out.println("slip"+slip);

  }
}