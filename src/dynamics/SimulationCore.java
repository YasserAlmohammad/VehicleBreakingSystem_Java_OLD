package dynamics;

import vehiclesimulationcore.*;
import javax.vecmath.Quat4d;
import javax.vecmath.Vector3d;
import java.util.ArrayList;
import java.util.Calendar;
import vehiclesimulation.BrakingUI;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class SimulationCore extends Thread {
    public SimulateList2 simlist=new SimulateList2("sim");
    public static double t;
    ////////////////////////////////////////////////////////
    public static ArrayList coords = new ArrayList();
    public static ArrayList rots = new ArrayList();
    public static ArrayList flwheel = new ArrayList();
    public static ArrayList frwheel = new ArrayList();
    public static ArrayList blwheel = new ArrayList();
    public static ArrayList brwheel = new ArrayList();
    public static ArrayList flwheelSteer = new ArrayList();
///////////////////////////////////////////////////////////


    public static double[] maxBrake = new double[4];
    public static double[] maxDrive = new double[4];
    public static double[] brake = new double[4];
    public static double[] drive = new double[4];
    public static double[] steer = new double[4];
    public static double ratio0;
    public static double ratio1;
    public static double ratio2;
    public static double ratio3;

    static double steerStep = 0;
    static double maxSteerAngle = 0;

    public static double prevX=0;
    public static double prevZ=0;

    double prevTime=0;
   double curTime=0;
   //double timeStep = 0.01;

   VehicleGeom v=null;
   ABS_simulator[] absS=new ABS_simulator[4];    //askar

   public void run(){

       if (SimVals.enableOnline)
           this.setPriority(Thread.MAX_PRIORITY);

       /////////////////////////////////////////////

       v = World.getCurVehicle();
       //////////////////////////////
       flwheel.clear();
       frwheel.clear();
       blwheel.clear();
       brwheel.clear();
       flwheelSteer.clear();
       coords.clear();
       rots.clear();
       ///////////////////////////////////
       if (SimVals.listCompsitionCount >= SimVals.listMaxCompose) {
           SimVals.resetLists();
       } else {
           SimVals.listCompsitionCount++;
           if (SimVals.listCompsitionCount > 1)
               SimVals.addListSeparator();
       }
       ///////////////////////////////////


       Vehicle Beetle = new Vehicle();
       double Km_Ph_speed = SimVals.initialSpeed;
       double w_u_speed = Km_Ph_speed / 3.6;
       Beetle.init(w_u_speed);

       for (int i = 0; i < 4; i++) { //askar
           absS[i] = new ABS_simulator();
       }

       maxBrake[0] = SimVals.maxBrakeTorqueFL;
       maxBrake[1] = SimVals.maxBrakeTorqueFR;
       maxBrake[2] = SimVals.maxBrakeTorqueBR;
       maxBrake[3] = SimVals.maxBrakeTorqueBL;

       // ratio=((maxBrake[0])/(500))*SimulationTuning.timeStep;
       ratio0 = maxBrake[0] * SimulationTuning.timeStep * 2;
       ratio1 = maxBrake[1] * SimulationTuning.timeStep * 2;
       ratio2 = maxBrake[2] * SimulationTuning.timeStep * 2;
       ratio3 = maxBrake[3] * SimulationTuning.timeStep * 2;

       double maxSteerTime = SimVals.maxSteerTime; //sec

       maxSteerAngle = SimVals.maxSteerFL;
       steerStep = maxSteerTime * maxSteerAngle * SimulationTuning.timeStep;
       SimVals.steerStep = 0.01;

       brake[0] = 1;
       brake[1] = 1;
       brake[2] = 1;
       brake[3] = 1;

       drive[0] = SimVals.engineTorque;
       drive[1] = SimVals.engineTorque;
       drive[2] = SimVals.engineTorque;
       drive[3] = SimVals.engineTorque;

       steer[0] = 0;
       steer[1] = 0;
       steer[2] = 0;
       steer[3] = 0;

       absS[0].Btinput = 220; //askar
       absS[1].Btinput = 220;
       absS[2].Btinput = 220;
       absS[3].Btinput = 220;

       /////////////////////////////////////

       SimVals.distance = 0; //reset
       prevX = 0;
       prevZ = 0;
       double angle = 0;
       double maxTime = 60;
       Calendar prevCal = Calendar.getInstance();
       double speedMagnitude = 0;
       int loops = 0;
           for (t = 0; t < maxTime; t += SimulationTuning.timeStep) {
               loops++;
               if (!SimVals.running)
                   break;
               if (SimVals.enableOnline) {
                   brake[0] = 0;
                   brake[1] = 0;
                   brake[2] = SimVals.brakeBR;
                   brake[3] = SimVals.brakeBL;
               } else {
                   if(curTime>SimVals.brakeAfter){
                       if (brake[0] < maxBrake[0])
                           brake[0] += ratio0;
                       if (brake[1] < maxBrake[1])
                           brake[1] += ratio1;
                       if (brake[2] < maxBrake[2])
                           brake[2] += ratio2;
                       if (brake[3] < maxBrake[3])
                           brake[3] += ratio3;
                   }
               }
               //       System.out.println("brake="+brake[0]);
               Beetle.calculateForceAndTorque(drive, brake, steer);

               Beetle.updateState();
               if(SimVals.enableABS){
                   for (int jk = 0; jk < 4; jk++) { //askar
                       brake[jk] = (absS[jk].tireSlipAlgo(brake[jk],
                               Beetle.tire[jk].V_inTireSpace[0],
                               Beetle.tire[jk].W));

                   }
               }
               curTime += SimulationTuning.timeStep;
               updateSteer(t);
               if (Samples.isSample2)
                   updateSteer1(curTime, maxTime);

               if((!SimVals.isOptimum)&& (curTime - prevTime > 0.03)) {

                         Vector3d vec=new Vector3d(Beetle.X[0], Beetle.X[1], Beetle.X[2]);
                         coords.add(vec);

                         SimVals.listXY.add(new PointDouble(Beetle.X[0], Beetle.X[1]));
                         SimVals.listXTime.add(new PointDouble(curTime,Beetle.X[0]));
                         SimVals.listYTime.add(new PointDouble(curTime,Beetle.X[1] ));
                         SimVals.listXZ.add(new PointDouble(Beetle.X[0], Beetle.X[2]));

                         SimVals.listSlipTimeFL.add(new PointDouble(curTime,Beetle.tire[0].slip));
                         SimVals.listSlipTractiveFL.add(new PointDouble(Beetle.tire[0].slip,Beetle.tire[0].xyForce[0]));
                         SimVals.listSlipTractiveLatFL.add(new PointDouble(Beetle.tire[0].slip,Beetle.tire[0].xyForce[1]));  //lateral

                         SimVals.listSlipTimeFR.add(new PointDouble(curTime,Beetle.tire[1].slip));
                         SimVals.listSlipTractiveFR.add(new PointDouble(Beetle.tire[1].slip,Beetle.tire[1].xyForce[0]));
                         SimVals.listSlipTractiveLatFR.add(new PointDouble(Beetle.tire[1].slip,Beetle.tire[1].xyForce[1]));

                         SimVals.listSlipTimeBL.add(new PointDouble(curTime,Beetle.tire[2].slip));
                         SimVals.listSlipTractiveBL.add(new PointDouble(Beetle.tire[2].slip,Beetle.tire[2].xyForce[0]));
                         SimVals.listSlipTractiveLatBL.add(new PointDouble(Beetle.tire[2].slip,Beetle.tire[2].xyForce[1]));

                         SimVals.listSlipTimeBR.add(new PointDouble(curTime,Beetle.tire[3].slip));
                         SimVals.listSlipTractiveBR.add(new PointDouble(Beetle.tire[3].slip,Beetle.tire[3].xyForce[0]));
                         SimVals.listSlipTractiveLatBR.add(new PointDouble(Beetle.tire[3].slip,Beetle.tire[3].xyForce[1]));



                         Quat4d quat = new Quat4d();
                         quat.set(Beetle.Q[1], Beetle.Q[2], Beetle.Q[3], Beetle.Q[0]);
                         rots.add(quat);
                         flwheel.add(new Double( -Beetle.tire[0].Wa));
                         frwheel.add(new Double( -Beetle.tire[1].Wa));
                         blwheel.add(new Double( -Beetle.tire[3].Wa));
                         brwheel.add(new Double( -Beetle.tire[2].Wa));

                         //steer variant function that updates the static steer matrix above

                         flwheelSteer.add(new Double(steer[0]));

                         if(SimVals.enableOnline){
                             v.setCoords(vec,quat);
                             v.FLWheel.spinWheel(-Beetle.tire[0].Wa);

                             v.rotateFrontWheels(-1*steer[0]);

                             v.FRWheel.spinWheel(-Beetle.tire[1].Wa);
                             v.BLWheel.spinWheel(-Beetle.tire[3].Wa);
                             v.BRWheel.spinWheel(-Beetle.tire[2].Wa);
                             try {
                                 sleep(30);
                             } catch (InterruptedException ex) {
                             }

                             if(SimVals.enableInfo){
                                 BrakingUI.lblPos.setText("["+Beetle.X[0]+"],["+Beetle.X[1]+"],["+Beetle.X[2]+"]");
                                 speedMagnitude=MatrixAlgebra.getMagnitude(Beetle.V);
                                 BrakingUI.lblSpeed.setText(""+speedMagnitude);
                             }

                         }

                        prevTime = curTime;
                     }
                     updateDistance(Beetle.X[0],Beetle.X[2]);

                     //curTime+=timeStep;
                 //    if(!SimVals.enableOnline)

//                if (speedMagnitude < 0.05)
//                    break;

                 }

                 Samples.isSample2=false;
                 SimVals.brakeAfter=0;

                 Calendar afterCal=Calendar.getInstance();
                 double executionTime=(afterCal.getTimeInMillis()-prevCal.getTimeInMillis());
                 System.out.println("execution time="+executionTime+ " for "+loops+" loops");
                 System.out.println("execution time for one loop="+executionTime/loops);
                 BrakingUI.txtXDistance.setText(""+SimVals.distance);
                 BrakingUI.txtTimeElapsed.setText(""+curTime);
                 BrakingUI.txtExecTime.setText(""+executionTime/loops);


                 if((!SimVals.enableOnline) && (!SimVals.isOptimum)){
                     simlist = new SimulateList2("sim");
                     SimulateList2.running = true;
                     simlist.set(coords, rots, flwheel, frwheel, blwheel, brwheel,
                                 flwheelSteer, curTime);
                     System.out.println("calculation finished");
                     System.out.println(Beetle.FORCE[0] + " * " + Beetle.FORCE[1] +
                                        " * " +
                                        Beetle.FORCE[2]);
                     System.out.println("stopping distance: " + Beetle.X[0]);
//    System.out.println("********************");
//    double[] summer = {0,-Beetle.mass*9.8,0};
//    for(int k=0; k<4; k++)
//      MatrixAlgebra.add(summer,summer,Beetle.spring[k].Force);
//
//    System.out.println("********************");
//    MatrixAlgebra.display(summer);


                     simlist.start();
                 }
             }

  public void updateSteer1(double curTime,double maxTime){
   if(curTime>(4)){
     if(Math.rint(curTime)%2==0){
       steer[0] = -.1;
       steer[1] = -.1;
     }
     else{
       steer[0] = .1;
       steer[1] = .1;
     }
   }
  }

  public void updateDistance(double newX,double newZ){
    double step=Math.sqrt(Math.pow((Math.abs(newX)-Math.abs(prevX)),2)+Math.pow((Math.abs(newZ)-Math.abs(prevZ)),2));
    prevX=newX;
    prevZ=newZ;
    SimVals.distance+=step;
}


  public void updateSteer(double curTime) {
    if(SimVals.enableOnline){
        steer[0]=SimVals.steerFL;
        steer[1]=SimVals.steerFR;
        return;
    }

    if (curTime > (SimVals.beginSteerAfter)) {
        if (0 < SimVals.maxSteerFL) {
            if (steer[0] < SimVals.maxSteerFL){
                steer[0] += steerStep;
                steer[1] += steerStep;
            }
      //      if (steer[1] < SimVals.maxSteerFR)
      //          steer[1] += steerStep;
        } else {
            if (steer[0] > SimVals.maxSteerFL){
                steer[0]=steer[0]+steerStep;
                steer[1] =steer[1]+steerStep;

            }
       //     if (steer[1] > SimVals.maxSteerFR)
       //         steer[1] -= steerStep;
        }
    }
}


  public SimulationCore() {

    }
  public static void main(String[] args) {
    (new SimulationCore()).start();
//    (new SimulationCore()).run();
   }
}
