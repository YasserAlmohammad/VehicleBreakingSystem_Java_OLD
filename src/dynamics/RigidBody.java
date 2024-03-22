package dynamics;

/**
 * <p>Title: Vehicle Dynamics Simulation Program</p>
 * <p>Description: vehicle dynmaics, braking system simulation</p>
 * <p>Copyright: Copyright (c) 2006 Faculty of Information Technology Engineering at Damascus University</p>
 * <p>Company: FIT</p>
 * @author Yasser Almohammad(Graphics Design and implementation), Ahmad Zoubi, Usama Rmelawi, Mohammad Askar,Qousai Dabour (vehicle case study, core functionality)
 * @version 1.0
 */

public class RigidBody {

  /*Constant Coantities*/
  double mass;
  double[][] Ibody = new double[3][3];
  double[][] IbodyInv = new double[3][3];

  /*State Variables*/
  double[] X = new double[3];    // centre of mass position
  double[] Q = new double[4];    // rotation quaternion
  double[] P = new double[3];    // linear momentum
  double[] L = new double[3];    // angular momentum

  /*Derived Quantities (auxiliary variables) */
  double[][] Iinv = new double[3][3];     // inverse of inertial tensor
  double[][] R = new double[3][3];        // rotation matrix
  double[][] Rtrans = new double[3][3];   // transpose of rotation matrix
  double[] V = new double[3];             // vehicle linear velocity
  double[] Omega = new double[3];         // vehicle angular velocity
  double[] Qdot = new double[4];          // derivative of quaternion matrix

  /* auxiliary array */
  double[][] temp = new double[3][3];
  double[] Qnorm = new double[4];
  double[] quatOmega = new double[4];
  double[] qTemp = new double[4];

  /* Computed coantities*/
  double[] FORCE = new double[3];
  double[] TORQUE = new double[3];
  double tempo = 0;
  /*METHOD to update state variables*/
  public void updateState(String test){
    tempo+=.01;
    X[0]=tempo;
  }
  public void updateState(){

    /* update X(t)*/
    X[0] = Solver.solve(X[0],V[0],SimulationTuning.timeStep);
    X[1] = Solver.solve(X[1],V[1],SimulationTuning.timeStep);
    X[2] = Solver.solve(X[2],V[2],SimulationTuning.timeStep);


    /* update Q(t)*/
    // first compute Qdot:
    Q[0] = Solver.solve(Q[0],Qdot[0],SimulationTuning.timeStep);
    Q[1] = Solver.solve(Q[1],Qdot[1],SimulationTuning.timeStep);
    Q[2] = Solver.solve(Q[2],Qdot[2],SimulationTuning.timeStep);
    Q[3] = Solver.solve(Q[3],Qdot[3],SimulationTuning.timeStep);

    /*update P(t)*/
    P[0] = Solver.solve(P[0],FORCE[0],SimulationTuning.timeStep);
    P[1] = Solver.solve(P[1],FORCE[1],SimulationTuning.timeStep);
    P[2] = Solver.solve(P[2],FORCE[2],SimulationTuning.timeStep);

    /*update L(t)*/
    L[0] = Solver.solve(L[0],TORQUE[0],SimulationTuning.timeStep);
    L[1] = Solver.solve(L[1],TORQUE[1],SimulationTuning.timeStep);
    L[2] = Solver.solve(L[2],TORQUE[2],SimulationTuning.timeStep);

    /*update auxiliary variables (reset variables related to the previous ones)*/

    /* V(t) = P(t)/M */
    MatrixAlgebra.div(V,P,mass);


    MatrixAlgebra.normalize(Qnorm,Q);
    Q[0] = Qnorm[0];
    Q[1] = Qnorm[1];
    Q[2] = Qnorm[2];
    Q[3] = Qnorm[3];
    /* Qdot = 0.5 * Omega * Q */
    quatOmega[0] = 0;
    quatOmega[1] = Omega[0];
    quatOmega[2] = Omega[1];
    quatOmega[3] = Omega[2];
    MatrixAlgebra.quatMul(qTemp,quatOmega,Q);
    MatrixAlgebra.div(Qdot,qTemp,2);

    /*restor R from its quaternion representation*/


    MatrixAlgebra.quatToRot(R,Q);

    /*  I exp(-1) = R * Ibody exp(-1) * transpose(R)  */
    MatrixAlgebra.transpose(Rtrans,R);
    MatrixAlgebra.mul(temp,R,IbodyInv);
    MatrixAlgebra.mul(Iinv,temp,Rtrans);

    //   w(t)=Iinv * L
    MatrixAlgebra.mul(Omega,Iinv,L);

    /* set changes to vehicle geometry for display */

  }

  public RigidBody() {
  }
  public static void main(String[] args) {
    System.out.println(Math.atan(-1)*180/Math.PI);
  }
}