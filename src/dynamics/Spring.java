package dynamics;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class Spring {
  //************ SPRING CHARACTARESTICS ***********
  // spring length:
  double length;
  // spring coefficient:
  double k;
  // damping coefficient:
  double q;
  // strut position in vehicle fixed coordinate system:
  // (we will define its location in world space as a state variables)
  // Being a trailing arm suspnsion the spring always parallel to the vehicle Y axis
  double[] localStrut = new double[3];

  double[] temp1 = new double[3];
  double[] temp2 = new double[3];
  double[] vel = new double[3];
  //************ SPRING STATE VARIABLES ***********
  // spring force in rest situation:
  double restForce;
  // spring length in rest situation:
  double rl;
  // spring current deformation: restLength - crntLength
  double crntDeform;
  // the same as crntSeform but in the previous time step
  double prevDeform;
  // deformation velocity: delta(deformation)/timeStep
  double deformVeloc;
  // magnitude of spring force:
  double forceMagnitude;
  double I;
  double Idot;
  // direction of spring force:
  double[] Force = new double[3];
  double[] Torque = new double[3];
  double[] gravForce = new double[3];
  // strut position in world coordinate system:
  double[] worldStrut = new double[3];

  double[] arm = new double[3];


  //***************** SPRING METHODS **************
  private int sign(double val){
    if (val>0)
      return 1;
    else{
      if (val<0)
        return -1;
      else
        return 0;
    }
  }
  // a method for computing spring forces it takes a vehicle as an argument and
  // returns the forces as vectors
  public void calcForceAndTorque(Vehicle vehicle, int index){

    /*

    double I = y - gt;
    double Idot = yVeloc - gtVeloc;
    int sgnI = sign(I);
    double springForce = -(k*(Math.abs(I)-L0)+miu*Idot*sgnI)*sgnI;
    //double totalForce = springForce - m*9.8;
    double totalForce = springForce ;
    yAccel =totalForce / m;
    yVeloc = Solver.solve(yVeloc,yAccel,TimingConsts.secInterval);
    y = Solver.solve(y,yVeloc,TimingConsts.secInterval);
    return y;

    */

    // calculate deformation:

    // HERE YOU ARE

    I = worldStrut[1]-2.16;

    //I = worldStrut[1];

    //if (I<0) I = .0001;
    MatrixAlgebra.sub(temp1,worldStrut,vehicle.X);
    MatrixAlgebra.crossProd(temp2,vehicle.Omega,temp1);
    MatrixAlgebra.add(vel,temp2,vehicle.V);
    //Idot = vel[1];


    rl = length - ((vehicle.mass*9.8/4)/k);

    Idot = vel[1];

    int sgnI = sign(I);
    forceMagnitude = -(k*(Math.abs(I)-length)+q*Idot*sgnI)*sgnI;
    //forceMagnitude = -(k*(Math.abs(I)-rl)+q*Idot*sgnI)*sgnI;


//    if(forceMagnitude==0)
//      forceMagnitude = vehicle.mass*9.8/4;



    // return spring force in terms of a vector:
    // all spring forces have the direction of the Y axes in the vehicle fixed coordinate system

//    for(int i=0; i<3; i++)
//      Force[i] = forceMagnitude * vehicle.R[1][i];

    Force[0] = 0;
    Force[1] = forceMagnitude;
    Force[2] = 0;


//    Force[0] = 0;
//    Force[1] = 9.8*vehicle.mass/4;
//    Force[2] = 0;
    //MatrixAlgebra.add(Force,Force,gravForce);
    // return spring torque in terms of vectors:
    MatrixAlgebra.sub(arm,worldStrut,vehicle.X);
    MatrixAlgebra.crossProd(Torque,arm,Force);

  }

  // a constructor to build t customised spring
  public Spring(double length,double k, double q){
    this.length = length;
    this.k = k;
    this.q = q;
  }
  // a default spring constructor
  public Spring(){
    length = 2;
    k = 18200;
    q = 450;
  }
  // a method for initializing the spring according to the vehicle on which it will be installed
  // it is responsible for positioning the spring and determining its initial state values
  public void init(Vehicle vehicle, int index){
    restForce = (vehicle.mass/4)*9.8;
    forceMagnitude = restForce;
    crntDeform = prevDeform = 0;

//    switch (index){
//      case 0:
//        localStrut[0] =  vehicle.xDim/2;
//        localStrut[1] =  0;
//        localStrut[2] =  vehicle.zDim/2;
//        break;
//
//      case 1:
//        localStrut[0] =  vehicle.xDim/2;
//        localStrut[1] =  0;
//        localStrut[2] = -vehicle.zDim/2;
//        break;
//
//      case 2:
//        localStrut[0] = -vehicle.xDim/2;
//        localStrut[1] =  0;
//        localStrut[2] = -vehicle.zDim/2;
//        break;
//
//      case 3:
//        localStrut[0] = -vehicle.xDim/2;
//        localStrut[1] =  0;
//        localStrut[2] =  vehicle.zDim/2;
//        break;
//    }
    switch (index){
      case 0:
        localStrut[0] =  vehicle.xDim/2;
        localStrut[1] =  0;
        localStrut[2] = -vehicle.zDim/2;
        break;

      case 1:
        localStrut[0] =  vehicle.xDim/2;
        localStrut[1] =  0;
        localStrut[2] =  vehicle.zDim/2;
        break;

      case 2:
        localStrut[0] = -vehicle.xDim/2;
        localStrut[1] =  0;
        localStrut[2] =  vehicle.zDim/2;
        break;

      case 3:
        localStrut[0] = -vehicle.xDim/2;
        localStrut[1] =  0;
        localStrut[2] = -vehicle.zDim/2;
        break;
    }



  }
  public static void main(String[] args) {

  }
}