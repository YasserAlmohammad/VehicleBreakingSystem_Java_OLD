package vehiclesimulationcore;

import javax.vecmath.Vector3d;
import javax.vecmath.Point3d;
import javax.vecmath.Matrix3d;
import javax.vecmath.Matrix3d;
import javax.media.j3d.*;

/**
 * <p>Title: Vehicle Dynamics Simulation Program</p>
 *
 * <p>Description: vehicle dynmaics, braking system simulation</p>
 *
 * <p>Copyright: Copyright (c) 2006 Faculty of Information Technology
 * Engineering at Damascus University</p>
 *
 * <p>
 * camera class, since multiple view are to be available, better to hold it in one class
 * better than spreading it into pieces among several classes
 * in the world, one one camera is to be, but may have several views that makes it like
 * multi camera world.
 *<p>
 * camera position might not change in constant step way, but also in gradual way
 * when applying the simulation
 *<p>
 * one camera multiple views
 * this class enables an easy interface for multiple kinds of camre views over the vehicle and the world
 * it can also integrate camea scenes(moving camera, like a movie production or something) which
 * can moove using a path and steps
 *<p>
 * methods and values of this class are static, since one view port is considerd thus it becomes easy to deal with
 * it without passing it as parameter, when desgining multiViewPort
 * simulation, just remove the static keywords, and add an instance of the class to the World and
 * related classes.
 * <p>
 * the camera in general works in this way:
 *      we got a target object position, target object angles, and also the current camera angle
 *      using appropraite formuals we define the position and lookAt of the camera depening
 *      on the previous params and the current camer code, which defines what's the current view
 *    <p>
 *      we after we got the positions-lookAt, and up vector we invert the transformation matrix and
 *      set it to the view transform group.
 * <p>
 */
public class Camera {
    public Camera() {
        try {
            jbInit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    static Point3d pos=new Point3d(1,1,1); //camera position
    static Point3d lookAt=new Point3d(); //camera looak at
    static Vector3d up=new Vector3d(0,1,0);  //the up vector

    static  TransformGroup camera=null; //the ViewPlatform of the VirtualUniverse it self
    static Transform3D transform=new Transform3D(); //manipulator

    static int cameraCode=0; //current camera code
    static final int tvPos1Code=0;
    static final int tvPos2Code=1;
    static final int aboveCode2=2;
  //  static final int tvPos4Code=3; //rotate car but not the vehicle
    static final int driverCode=3;
    static final int centerCode=4;
    static final int unBindCode=5;
    static final int wheelCode=6;
    static final int aboveCode1=7;
    static final int aboveCode=8;



    //tv position 1: looking at the vehicle, behid it with DISTANCE value, over the vehicle
    //with HEIGHT value
    static final double tvPos1Distance=10; //10 meters
    static final double tvPos1Height=2;

    //Driver Position: looking at the same direction as the driver, in his position
    static final double driverPosX=0;
    static final double driverPosY=1.2;
    static final double driverPosZ=0;
    static final double driverCameraDistance=0.5;


    //Wheel position: near the wheel to have a look at both the wheel and the view
    static final double wheelPosX=0;
    static final double wheelPosY=0;
    static final double wheelPosZ=5;  //relative to vehicle origin
    static final double wheelCameraDistance=5;

    //origin camer position: fixed camer position near the center of the world, looking at
    //the vehicle
    static Point3d originPos=new Point3d(5,2,0); //

    //camera looking down, from above 20m for example
    static final double aboveDistance=20; //centered


    //unbind the camera: return to origin and unbind
    static Point3d unBindPos=new Point3d(0,2,10); //camera position
    static Point3d unBindlookAt=new Point3d(0,0,0); //camera look at


     static private Matrix3d look=new Matrix3d();
     static Vector3d center=new Vector3d(0,2,0);
     static double angle=0; //camera specific angle, is set for free rotating camera arround the object
     private static double curDistance=0; //current distance from the lookAt point
     static boolean isCameraBinded=false;

     /**
      * passing the view platform transform we create this camera, the initial view is TVPos1
      * @param vp TransformGroup
      */
     public Camera(TransformGroup vp) {
        camera=vp;
        setTVPos1CameraType(); //default: follow the vehicle
    }

    /**
     * not usually used, how ever!
     * @param vp TransformGroup
     */
    public static void setViewPlatformTransformGroup(TransformGroup vp){
        camera=vp;
    }

    /**
     * using the vehicle position(or new position), and angle update the camera position
     * of the current camera type
     *
     * @param vehiclePos Vector3d   position of target object
     * @param angles Vector3d       rotations (angles) arround the three axises of the target object
     */
    public static void updatePos(Vector3d vehiclePos,Vector3d angles){
         if(!isCameraBinded)
             return;
        switch(cameraCode){
        case tvPos1Code:

            //we need to calculate the sin-cos only when angle changes
            pos.set(vehiclePos.x + Math.cos(angles.y + angle) * curDistance,
                    tvPos1Height,
                    vehiclePos.z - Math.sin(angles.y + angle) * curDistance);
            lookAt.set(vehiclePos.x, vehiclePos.y, vehiclePos.z);
            transform.lookAt(pos, lookAt, up);
            transform.invert();
            camera.setTransform(transform);
            break;
        case centerCode:
            lookAt.set(vehiclePos.x+1, vehiclePos.y, vehiclePos.z);
            transform.lookAt(pos, lookAt, up);

            //we got a view matrix, inverse of this matrix can be used with
            //the viewPlatform view
            transform.invert();
            camera.setTransform(transform);
            break;
        case driverCode:
            pos.set(vehiclePos.x,
                    driverPosY,
                    vehiclePos.z);
            lookAt.set(vehiclePos.x - Math.cos(angles.y + angle) * curDistance,
                       driverPosY,
                       vehiclePos.z + Math.sin(angles.y + angle) * curDistance);
            transform.lookAt(pos, lookAt, up);
            transform.invert();
            camera.setTransform(transform);
            break;
        case wheelCode:
            pos.set(vehiclePos.x + Math.sin(angles.y + angle) * curDistance,
                    vehiclePos.y,
                    vehiclePos.z + Math.cos(angles.y + angle) * curDistance);

            lookAt.set(vehiclePos.x, vehiclePos.y, vehiclePos.z);
            transform.lookAt(pos, lookAt, up);
            transform.invert();
            camera.setTransform(transform);
            break;

        case aboveCode:
            lookAt.set(0, 0, 0);
            pos.set(1,curDistance,0);
            transform.lookAt(pos, lookAt, up);
            transform.invert();
            camera.setTransform(transform);
            break;
        case aboveCode1:
            lookAt.set(vehiclePos.x, vehiclePos.y, vehiclePos.z);
            pos.set(1, curDistance, 0);
            transform.lookAt(pos, lookAt, up);
            transform.invert();
            camera.setTransform(transform);
            break;
        case aboveCode2:
            /*
            starnge thing when the x component of pos and lookAt are matched the camera doesn't
            work, even when it's the time tp flip the direction, or it might have something to
            do with the inversion i don't know */
            lookAt.set(vehiclePos.x, vehiclePos.y, vehiclePos.z);
            pos.set(vehiclePos.x-1, curDistance, vehiclePos.z);
            transform.lookAt(pos, lookAt, up);
            transform.invert();
            camera.setTransform(transform);
            break;
        }
        int x=0;

    }

    /**
     * the camera is behind the vehicle looking at it
     * this camera enable free rotation arround the vehicle also changing the tracking distace
     */
    public static void setTVPos1CameraType(){
        curDistance=tvPos1Distance;
        cameraCode=tvPos1Code;
        isCameraBinded=true;
    }

    /**
     * the driver view: like the eyes of the driver and in his position looking at the frond window
     * this camera enable free rotaion inside the vehicle looking through any window
     */
    public static void setDriverCameraType(){
        curDistance=driverCameraDistance;
        cameraCode=driverCode;
        isCameraBinded=true;
    }

    /**
     * near the side wheels, looking at them, to take a good look at what's happening there
     * the camera moves with the vehicle
     * this camera enable moving freely arround the vehicle also changing the tracking distace
     */
    public static void setWheelCameraType(){
        curDistance=wheelCameraDistance;
        cameraCode = wheelCode;
        isCameraBinded=true;
    }

    /**
     * the camera is almost centerd in the world, fixed position and looking at the vehicle
     */
    public static void setOriginCameraType(){
        cameraCode=centerCode;
        pos.set(1,2,1);
        isCameraBinded=true;
    }

    /**
     * fixed acamera centerd in the world looking down
     * this camera enable moving up-down
     */
    public static void setaBoveCameraType() {
        cameraCode = aboveCode;
       curDistance=aboveDistance;
       isCameraBinded=true;
    }

    /**
     * the same as previous but the camera tracks the vehile while holdin in place
     * this camera enable moving up-down
     */
    public static void setaBove1CameraType() {
       cameraCode = aboveCode1;
       curDistance=aboveDistance;
       isCameraBinded=true;
    }

    /**
     * the camera is above the vehicle with some distance looking down to it and moving with the
     * vehicle
     * this camera enable moving up-down
     */
    public static void setaBove2CameraType() {
        cameraCode = aboveCode2;
        curDistance = aboveDistance;
        isCameraBinded=true;
    }

    /**
     * changing the tracking distace to increase to some extent
     */
    public static void increaseCurDistance(){
        if ((cameraCode == aboveCode)||(cameraCode == aboveCode1)||(cameraCode == aboveCode2)){
            if (curDistance < 65)
                curDistance += 1;
        }
        else
        if (curDistance < 20) //for example
            curDistance += 0.5;
    }

    /**
     * changing the tracking distace to decrease to some extent
     */
    public static void decreaseCurDistance(){
        if((cameraCode == aboveCode)||(cameraCode == aboveCode1)||(cameraCode == aboveCode2)){
            if (curDistance > 0.1)
                curDistance -= 1;
        }
        else
        if(curDistance>0.5)
            curDistance-=0.5;
    }

    /**
     * unbind the camera from it's target
     */
    public static void setUnBindCameraType(){
        cameraCode=unBindCode;
        pos.set(unBindPos);
        transform.lookAt(unBindPos,unBindlookAt,up);
        transform.invert(); //to apply to viewPlatform
        camera.setTransform(transform);
        isCameraBinded=false;

    }

    private void jbInit() throws Exception {
    }

}
