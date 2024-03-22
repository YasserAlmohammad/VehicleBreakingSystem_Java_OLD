package vehiclesimulationcore;

import java.util.ArrayList;
import javax.vecmath.Vector3d;
import javax.vecmath.Matrix3d;
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
public class SimulateList extends Thread {
    VehicleGeom vehicle=null;
    ArrayList coords=null;
    ArrayList vehicleAngles=null;
    ArrayList FLWheelCoords=null; //rotational coordinates (angles)
    ArrayList wheelCoords=null; //temp, 4wheel common coords
    double duration=0;
    private Transform3D transform = new Transform3D();
    boolean oneVal=true; //the input coords list couldcontain only x values
    private Matrix3d rotateMatrix = new Matrix3d();

    /**
     * for thread sake
     * @param str String
     */
    public SimulateList(String str) {
        super(str);
    }
    /**
     * set necessary data for the list application
     * you should also provide the wheel rotational data, a list for each wheel, for now one list for them all
     * <p>
     * for now we make it simple, how ever all data should be provided, even the body positions relative
     * to the whole vehicle when suspension is enables
     * also data of wheel rotation arround it's vertical plane
     * puls data for each wheel
     *
     * @param v VehicleGeom
     * @param c ArrayList            the abscissas of the vehicle(or object)
     * @param vAngles ArrayList      angles arround three axises
     * @param wheel ArrayList        wheel rotainal angles arround it's axon
     * @param d double               time to execute in
     */
    public void set(VehicleGeom v, ArrayList c,ArrayList vAngles,ArrayList wheel,double d){
        vehicle=v;
        coords=c;
        vehicleAngles=vAngles;
        duration=d;
        wheelCoords=wheel;
        if(c.get(0).getClass()==Double.class)
            oneVal=true;
    }

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
        Vector3d angles=vehicle.angles;
        Vector3d pos=new Vector3d();
        //now just make it run, without time synchroniation
        vehicle.reset();
        for(int i=0;i<coords.size();i++){
         //   pos=(Vector3d)coords.get(i);
         //
         if(oneVal){
             double x = ((Double) coords.get(i)).doubleValue();
             pos.set(240 - x, 0, 0);
         }
         else
             pos=(Vector3d)coords.get(i);
         if(vehicleAngles!=null)
             angles=(Vector3d)vehicleAngles.get(i);

         vehicle.setCoords(pos,angles);
         vehicle.spinWheels(((Double)wheelCoords.get(i)).doubleValue());
          try {
              this.sleep((long)(sampleRate*1000));
          } catch (InterruptedException ex) {
              //  System.out.println("error");
          }
          //time time.....
        }
    }
}
