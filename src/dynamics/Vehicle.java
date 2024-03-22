package dynamics;
import java.util.Vector;
import javax.media.j3d.Transform3D;
import javax.vecmath.Quat4d;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class Vehicle extends RigidBody{
  // in addition to what a vehicle has -being a rigid body- it has the following features:

  // vehicle dimentions:
  //********************
  // vehicle length:
  double xDim;
  // vehicle height:
  double yDim;
  // vehicle width:
  double zDim;
  double []fh = new double[3];
  double []th = new double[3];
  double []zero = new double[3];

  // vehicle is composed of four springs and four wheels:
  //*****************************************************
  Spring[] spring = new Spring[4];
  LuGre[] tire = new LuGre[4];


  // a method for computing position of forces effects
  // for now this method computes only strut in world space (named worldStrut):
  // this worldStrut helps in calculating spring deformation which is the y component of it
  // and is for now considered as the effective point of suspension forces as well as
  // road-tire interaction forces
  void calcPositionVectors(){
    for(int i=0; i<4; i++){
      MatrixAlgebra.mul(spring[i].worldStrut,R,spring[i].localStrut);
      MatrixAlgebra.add(spring[i].worldStrut,spring[i].worldStrut,X);
      MatrixAlgebra.mul(tire[i].effectivePoint,R,tire[i].localEffectivePoint);
      MatrixAlgebra.add(tire[i].effectivePoint,tire[i].effectivePoint,X);

//      MatrixAlgebra.mul(tire[i].worldTstrut,R,tire[i].localTstrut);
//      MatrixAlgebra.add(tire[i].worldTstrut,tire[i].worldTstrut,X);

//      tire[i].worldRoad_tirePoint[2] = tire[i].worldTstrut[2];




//      System.out.println("LOCAL T STRUT:  ");
//      MatrixAlgebra.display(tire[i].localTstrut);
//
//      System.out.println("==========");
//      System.out.println("WORLD STRUT:  ");
//      MatrixAlgebra.display(spring[i].worldStrut);
//      System.out.println("==========");

//      tire[i].worldRoad_tirePoint[0] = spring[i].worldStrut[0];
//      tire[i].worldRoad_tirePoint[1] = 0;
//      tire[i].worldRoad_tirePoint[2] = spring[i].worldStrut[2];

    }
//    System.out.println("*************************");
  }


  public void calculateForceAndTorque(double[] drvTorque,double[] brkTorque,double[] steeringAngle){
    // reset FRCE and TORQUE :
    for(int i=0; i<3; i++){
      FORCE[i]  = 0;
      TORQUE[i] = 0;
    }
//**************************
  calcPositionVectors();
  for(int i=0; i<4; i++){
    spring[i].calcForceAndTorque(this,i);
//    System.out.println("S F");
//    System.out.println(spring[i].forceMagnitude+"Vs"+mass*9.8/4);
    tire[i].calcForceAndTorque(this,drvTorque[i],brkTorque[i],steeringAngle[i],i);

 }

//**************************

    FORCE[1] = -mass*9.8;



    for(int i=0; i<4; i++){

      MatrixAlgebra.add(FORCE,FORCE,spring[i].Force);
      MatrixAlgebra.add(TORQUE,TORQUE,spring[i].Torque);


      MatrixAlgebra.add(FORCE, FORCE, tire[i].Force);
      MatrixAlgebra.add(TORQUE, TORQUE, tire[i].Torque);

    }
//    System.out.println("TR:");
//    MatrixAlgebra.display(tire[0].Vr);
//    System.out.println("Force");
//    MatrixAlgebra.display(tire[0].xyForce);
//    System.out.println("^^^^^^^^^^^^^^^^^^");

//    System.out.println("****************************************");
//    System.out.println("total force:");
//    MatrixAlgebra.display(FORCE);
//    System.out.println("############");
//    System.out.println("total torque");
//    MatrixAlgebra.display(TORQUE);
//    System.out.println("############");

//    System.out.println(TORQUE[0]);
//    System.out.println(TORQUE[1]);
//    System.out.println(TORQUE[2]);

  }
  public Vehicle() {
    // setting vehicle mass:
    mass = SimVals.vehicleMass;
//    mass = 750;
    // setting vehicle dimentions:
    xDim = 4;
    yDim = 1.36;
    zDim = 1.6;
    // initializing IBody
    double scl = mass/12;
    Ibody[0][0] = scl*(yDim*yDim+zDim*zDim);
    Ibody[1][1] = scl*(xDim*xDim+zDim*zDim);
    Ibody[2][2] = scl*(xDim*xDim+yDim*yDim);

    Ibody[0][1] = Ibody[0][2] = 0;
    Ibody[1][0] = Ibody[1][2] = 0;
    Ibody[2][0] = Ibody[2][1] = 0;

    // initializing IbodyInv
//    IbodyInv[0][0] = 0.0022677793904208998;
//    IbodyInv[1][1] = 0.0005720954712922493;
//    IbodyInv[2][2] = 0.0005387931034482759;

    IbodyInv[0][0] = .1360667634e-1;
    IbodyInv[1][1] = .3361419864e-2;
    IbodyInv[2][2] = .3232758622e-2;

    IbodyInv[0][1] = Ibody[0][2] = 0;
    IbodyInv[1][0] = Ibody[1][2] = 0;
    IbodyInv[2][0] = Ibody[2][1] = 0;
    for(int i=0; i<4; i++){
      spring[i] = new Spring();
      tire[i] = new LuGre(this,i);
    }
  }
  public void init(double speed){
    // INITIALIZING SPRING STATE VARIABLES:
    //*************************************
    for (int i = 0; i<4; i++){
      spring[i].init(this,i);
      tire[i].init(this,speed,i);

    }

    // INITIALIZING VEHICLE STATE VARIABLES:
    //**************************************
    // initializing X
    X[0] = 0;
    X[1] = spring[1].rl; //we ought to take the mean of restllength but for the time being this will suffice
    X[2] = 0;
    // initializing R and Q:
//double [][] R2 = new double[3][3];
//    R2[0][0] = Math.cos(Math.PI/40 );
//    R2[1][0] = -Math.sin(Math.PI/40);
//    R2[2][0] = 0;
//
//    R2[0][1] = Math.sin(Math.PI/40);
//    R2[1][1] = Math.cos(Math.PI/40);
//    R2[2][1] = 0;
//
//    R2[0][2] = 0;
//    R2[1][2] = 0;
//    R2[2][2] = 1;
//double [][] R1 = new double[3][3];
//    R1[0][0] = 1;
//    R1[1][0] = 0;
//    R1[2][0] = 0;
//
//    R1[0][1] = 0;
//    R1[1][1] = Math.cos(Math.PI/40);
//    R1[2][1] = Math.sin(Math.PI/40);
//
//    R1[0][2] = 0;
//    R1[1][2] = -Math.sin(Math.PI/40);
//    R1[2][2] = Math.cos(Math.PI/40);
//
//    MatrixAlgebra.mul(R,R2,R1);

    R[0][0] = 1;
    R[1][0] = 0;
    R[2][0] = 0;

    R[0][1] = 0;
    R[1][1] = 1;
    R[2][1] = 0;

    R[0][2] = 0;
    R[1][2] = 0;
    R[2][2] = 1;






//    R[0][0] = 1;
//    R[1][0] = 0;
//    R[2][0] = 0;
//
//    R[0][1] = 0;
//    R[1][1] = 1;
//    R[2][1] = 0;
//
//    R[0][2] = 0;
//    R[1][2] = 0;
//    R[2][2] = 1;

    //double[] koko = new double[4];
    //MatrixAlgebra.rotToQuat(koko,R);
//    Transform3D t = new Transform3D();
//    Transform3D t1 = new Transform3D();
//    Quat4d ququ = new Quat4d();
    //ququ.set(koko[1],koko[2],koko[3],koko[0]);
//    t.setRotation(ququ);
//    t1.rotX(Math.PI/40);
//    t.mul(t1);
//    t.get(ququ);
//    System.out.println(ququ);
//    koko[0] = ququ.w;
//    koko[1] = ququ.x;
//    koko[2] = ququ.y;
//    koko[3] = ququ.z;
//    MatrixAlgebra.quatToRot(R,koko);



    // setting quaternion: since R is initially normalized; there is no need to normalize it
    MatrixAlgebra.rotToQuat(Q,R);
//    System.out.println("R:");
//    MatrixAlgebra.display(R);
//    System.out.println("Q:");
//    MatrixAlgebra.display(Q);
    // initializing V and P:
    // P = mass * V and V has only X[0] components because of longitudinal x motion
    V[0] = speed;
    V[1] = 0;
    V[2] = 0;

    P[0] = mass * speed;
    P[1] = 0;
    P[2] = 0;

    // initializing L:
    // the vehicle suffers no lateral motion in start state:
    L[0] =0;
    L[1] =0;
    L[2] =0;

    // initializing auxiliary variables:
    // we have initialized (V) and (R), there remains (Iinv) and (Omega):

    /*  I exp(-1) = R * Ibody exp(-1) * transpose(R)  */
//    MatrixAlgebra.transpose(Rtrans,R);
//    MatrixAlgebra.mul(Iinv,R,IbodyInv);
//    MatrixAlgebra.mul(Iinv,Iinv,Rtrans);
    MatrixAlgebra.transpose(Rtrans,R);
    MatrixAlgebra.mul(temp,R,IbodyInv);
    MatrixAlgebra.mul(Iinv,temp,Rtrans);
    //   w(t)=Iinv * L
    MatrixAlgebra.mul(Omega,Iinv,L);

    // INITIALIZING TIRE STATE VARIABLES:
    //************************************


  }
  public static void main(String[] args) {
    Vehicle vehicle1 = new Vehicle();
    MatrixAlgebra.display(vehicle1.Ibody);
  }
}
