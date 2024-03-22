package vehiclesimulationcore;

import java.util.ArrayList;
import javax.vecmath.Vector3d;
import javax.vecmath.Matrix3d;
import javax.vecmath.Quat4d;
import javax.media.j3d.Transform3D;

/**
 * <p>Title: Vehicle Dynamics Simulation Program</p>
 *
 * <p>Description: vehicle dynmaics, braking system simulation</p>
 *
 * <p>Copyright: Copyright (c) 2006 Faculty of Information Technology
 * Engineering at Damascus University</p>
 *<p>
 * test class for list simulation application on vehicle
 *<p>
 * Coordinate List Application:<p>
 *       We got a sampled linked list of coordinate values, apply it to the vehicle model
 *       in time continuous form, make it match a real world time model.
 *       For example: 100 samples, in 5 seconds, 20 sample per second, 20 transformations per second.
 *  <p>this is done through simulateList( ) method

 */

public class SimulateList2 extends Thread {
        /**
         * vehicle positions
         * of type Vector3d
         */
        ArrayList coords=null;

        /**
         * rotational quats
         * of type Quat4d
         */
        ArrayList vehicleAngles=null;

        /**
         * angles of type Double
         */
    ArrayList FLWheelCoords=null; //rotational coordinates (angles)
    ArrayList FRWheelCoords=null;
    ArrayList BLWheelCoords=null;
    ArrayList BRWheelCoords=null;

    ArrayList FLWheelSteer=null; //rotational coordinates (angles)
    ArrayList FRWheelSteer=null;
    ArrayList BLWheelSteer=null;
    ArrayList BRWheelSteer=null;

    public static boolean running=false;

    /**
     * simulation duration like 5sec
     */
    double duration=0;

    private Transform3D transform = new Transform3D();
    boolean oneVal=true; //the input coords list couldcontain only x values

    private Quat4d rot = new Quat4d();

    /**
     * for thread sake
     * @param str String
     */
    public SimulateList2(String str) {
        super(str);
    }

    /**
     *
     * @param c ArrayList     coords of the vehicleposition
     * @param vAngles ArrayList    rotation matrix at each moment
     * @param FLWheel ArrayList
     * @param FRWheel ArrayList
     * @param BLWheel ArrayList
     * @param BRWheel ArrayList
     * @param d double       duration of simulation
     */
    public void set( ArrayList c,ArrayList vAngles,ArrayList FLWheel,ArrayList FRWheel,ArrayList BLWheel,ArrayList BRWheel,ArrayList FWheelSteer,double d){
        coords=c;
        vehicleAngles=vAngles;
        duration=d;

        FLWheelCoords=FLWheel;
        FRWheelCoords=FRWheel;
        BLWheelCoords=BLWheel;
        BRWheelCoords=BRWheel;
        FLWheelSteer=FWheelSteer;

//      pos.add(new Vector3d(x,y,z));
//      vAngles.add(new Quat4d());
//      FLWheel.add(aabssica);

    }

    /**
     * steering angle list  for all wheels
     * @param FLWheel
     * @param FRWheel
     * @param BLWheel
     * @param BRWheel

    public void set2(ArrayList FLWheel,ArrayList FRWheel,ArrayList BLWheel,ArrayList BRWheel){
      FLWheelSteer=FLWheel; //rotational coordinates (angles)
      FRWheelSteer=FRWheel;
      BLWheelSteer=BLWheel;
      BRWheelSteer=BRWheel;
    }
    */

    /**
     * a core method to apply a list of coordinates coming from outside appication or
     * another scientific simulation, the list is time-data samples, COORDS in the list
     * are to be simulated uniformly in DURATION time   (frame-sample rate)
     * <p>
     * for now we use simple simulation, since data is not yet provided
     */
    public void run(){
        //necessary to set max priority or it will run like in the background
        //and get flicker effects
        this.setPriority(Thread.MAX_PRIORITY);
        double sampleRate=duration/coords.size();
        Vector3d pos=new Vector3d();
        //now just make it run, without time synchroniation
        VehicleGeom v=World.getCurVehicle();
        v.reset();

        int listSize=coords.size();
        int i=0;
        while(running && i<listSize){
 //       for(int i=0;i<coords.size();i++/*i++*/){
             rot=(Quat4d)vehicleAngles.get(i);
             pos=(Vector3d)coords.get(i);
             v.setCoords(pos,rot);
             v.FLWheel.spinWheel(((Double)FLWheelCoords.get(i)).doubleValue());

             v.rotateFrontWheels(-1*((Double)FLWheelSteer.get(i)).doubleValue());

             v.FRWheel.spinWheel(((Double)FRWheelCoords.get(i)).doubleValue());
             v.BLWheel.spinWheel(((Double)BLWheelCoords.get(i)).doubleValue());
             v.BRWheel.spinWheel(((Double)BRWheelCoords.get(i)).doubleValue());

          try {
              this.sleep((long)(sampleRate*1000));
          } catch (InterruptedException ex) {
          }
          ++i;
        }
    }
}
